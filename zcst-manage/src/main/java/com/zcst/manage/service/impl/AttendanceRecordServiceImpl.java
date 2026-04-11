package com.zcst.manage.service.impl;

import com.zcst.common.constant.HttpStatus;
import com.zcst.common.exception.ServiceException;
import com.zcst.manage.constant.AttendanceStatusEnum;
import com.zcst.manage.domain.AttendanceRecord;
import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.domain.Vo.AttendanceRecordVo;
import com.zcst.manage.mapper.AttendanceRecordMapper;
import com.zcst.manage.mapper.DutyScheduleMapper;
import com.zcst.manage.service.IAttendanceRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * 考勤记录 Service 实现类
 * 提供考勤记录的增删改查、打卡、签退等业务逻辑实现
 * 
 * @author zcst
 * @date 2026-03-27
 */
@Service
public class AttendanceRecordServiceImpl implements IAttendanceRecordService {
    
    /**
     * 日志记录器
     * 用于记录业务操作日志和异常信息
     */
    private static final Logger log = LoggerFactory.getLogger(AttendanceRecordServiceImpl.class);

    /**
     * 考勤记录 Mapper
     */
    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    /**
     * 值班表 Mapper
     */
    @Autowired
    private DutyScheduleMapper dutyScheduleMapper;

    /**
     * 根据 ID 查询考勤记录
     * 
     * @param recordId 考勤记录 ID
     * @return 考勤记录
     */
    @Override
    public AttendanceRecord selectAttendanceRecordById(Long recordId) {
        return attendanceRecordMapper.selectAttendanceRecordById(recordId);
    }

    /**
     * 查询考勤记录列表
     * 支持多条件组合查询
     * 
     * @param attendanceRecord 考勤记录查询条件
     * @return 考勤记录列表
     */
    @Override
    public List<AttendanceRecord> selectAttendanceRecordList(AttendanceRecord attendanceRecord) {
        return attendanceRecordMapper.selectAttendanceRecordList(attendanceRecord);
    }

    /**
     * 根据学生 ID 查询考勤记录列表
     * 
     * @param studentId 学号
     * @return 考勤记录列表
     */
    @Override
    public List<AttendanceRecord> selectAttendanceRecordListByStudentId(String studentId) {
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setStudentId(studentId);
        return attendanceRecordMapper.selectAttendanceRecordList(attendanceRecord);
    }

    /**
     * 根据场馆 ID 查询考勤记录列表
     * 先查询该场馆的所有值班记录，再根据值班 ID 查询考勤记录
     * 
     * @param venueId 场馆 ID
     * @return 考勤记录列表
     */
    @Override
    public List<AttendanceRecord> selectAttendanceRecordListByVenueId(Integer venueId) {
        // 查询该场馆的所有值班记录
        DutySchedule dutySchedule = new DutySchedule();
        dutySchedule.setVenueId(venueId);
        List<DutySchedule> dutySchedules = dutyScheduleMapper.selectDutyScheduleList(dutySchedule);
        
        // 提取所有值班 ID
        List<Integer> dutyIds = new ArrayList<>();
        for (DutySchedule duty : dutySchedules) {
            dutyIds.add(duty.getDutyId());
        }
        
        // 如果没有值班记录，返回空列表
        if (dutyIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 根据值班 ID 查询考勤记录
        return attendanceRecordMapper.selectAttendanceRecordByDutyIds(dutyIds);
    }

    /**
     * 新增考勤记录
     * 自动设置创建时间和更新时间
     * 
     * @param attendanceRecord 考勤记录信息
     * @return 影响行数
     */
    @Override
    public int insertAttendanceRecord(AttendanceRecord attendanceRecord) {
        attendanceRecord.setCreatedAt(new Date());
        attendanceRecord.setUpdatedAt(new Date());
        return attendanceRecordMapper.insertAttendanceRecord(attendanceRecord);
    }

    /**
     * 修改考勤记录
     * 自动更新更新时间
     * 
     * @param attendanceRecord 考勤记录信息
     * @return 影响行数
     */
    @Override
    public int updateAttendanceRecord(AttendanceRecord attendanceRecord) {
        attendanceRecord.setUpdatedAt(new Date());
        return attendanceRecordMapper.updateAttendanceRecord(attendanceRecord);
    }

    /**
     * 根据 ID 删除考勤记录
     * 
     * @param recordId 考勤记录 ID
     * @return 影响行数
     */
    @Override
    public int deleteAttendanceRecordById(Long recordId) {
        return attendanceRecordMapper.deleteAttendanceRecordById(recordId);
    }

    /**
     * 批量删除考勤记录
     * 
     * @param recordIds 考勤记录 ID 数组
     * @return 影响行数
     */
    @Override
    public int deleteAttendanceRecordByIds(Long[] recordIds) {
        return attendanceRecordMapper.deleteAttendanceRecordByIds(recordIds);
    }

    /**
     * 打卡业务逻辑
     * 1. 验证值班信息是否存在
     * 2. 检查是否已经打卡（防止重复打卡）
     * 3. 创建考勤记录
     * 4. 判断打卡状态（正常/迟到）
     * 5. 更新值班表的考勤状态
     * 
     * 事务控制：保证考勤记录和值班表状态更新的原子性
     * 
     * @param studentId 学号
     * @param dutyId 值班 ID
     * @return 影响行数
     */
    @Transactional
    @Override
    public int checkIn(String studentId, Integer dutyId) {
        DutySchedule dutySchedule = dutyScheduleMapper.selectDutyScheduleByDutyId(dutyId);
        if (dutySchedule == null) {
            throw new ServiceException("参数错误：值班信息不存在", HttpStatus.BAD_REQUEST);
        }
        if (dutySchedule.getStudentId() == null || !dutySchedule.getStudentId().equals(studentId)) {
            throw new ServiceException("无权限操作该值班", HttpStatus.FORBIDDEN);
        }

        AttendanceRecord existingRecord = attendanceRecordMapper.selectAttendanceRecordByDutyId(dutyId);
        if (existingRecord != null) {
            throw new ServiceException("已打卡，不能重复打卡", HttpStatus.CONFLICT);
        }

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setStudentId(studentId);
        attendanceRecord.setDutyId(dutyId);
        Date checkInTime = new Date();
        attendanceRecord.setCheckInTime(checkInTime);

        if (checkInTime.before(dutySchedule.getStartTime())) {
            attendanceRecord.setStatus(AttendanceStatusEnum.NORMAL);
        } else {
            attendanceRecord.setStatus(AttendanceStatusEnum.LATE);
        }

        int result = attendanceRecordMapper.insertAttendanceRecord(attendanceRecord);
        if (result > 0) {
            dutySchedule.setAttendanceStatus(attendanceRecord.getStatus());
            dutyScheduleMapper.updateDutySchedule(dutySchedule);
        }

        return result;
    }

    /**
     * 签退业务逻辑
     * 1. 验证考勤记录是否存在
     * 2. 检查是否已经签退（防止重复签退）
     * 3. 验证值班信息
     * 4. 设置签退时间并计算实际值班时长
     * 5. 判断是否早退
     * 6. 更新考勤记录状态
     * 7. 更新值班表的考勤状态
     * 
     * 事务控制：保证考勤记录更新和值班表状态更新的原子性
     * 
     * @param recordId 考勤记录 ID
     * @param dutyId 值班 ID
     * @return 影响行数
     */
    @Transactional
    @Override
    public int checkOut(Long recordId, Integer dutyId) {
        AttendanceRecord attendanceRecord = attendanceRecordMapper.selectAttendanceRecordById(recordId);
        if (attendanceRecord == null) {
            throw new ServiceException("参数错误：考勤记录不存在", HttpStatus.BAD_REQUEST);
        }
        if (attendanceRecord.getCheckOutTime() != null) {
            throw new ServiceException("已签退，不能重复签退", HttpStatus.CONFLICT);
        }
        if (attendanceRecord.getCheckInTime() == null) {
            throw new ServiceException("业务冲突：未打卡不能签退", HttpStatus.CONFLICT);
        }

        Integer recordDutyId = attendanceRecord.getDutyId();
        if (dutyId == null)
        {
            dutyId = recordDutyId;
        }
        if (recordDutyId != null && dutyId != null && !recordDutyId.equals(dutyId))
        {
            throw new ServiceException("参数错误：recordId 与 dutyId 不匹配", HttpStatus.BAD_REQUEST);
        }

        DutySchedule dutySchedule = dutyScheduleMapper.selectDutyScheduleByDutyId(dutyId);
        if (dutySchedule == null) {
            throw new ServiceException("参数错误：值班信息不存在", HttpStatus.BAD_REQUEST);
        }
        if (dutySchedule.getStudentId() != null && attendanceRecord.getStudentId() != null
            && !dutySchedule.getStudentId().equals(attendanceRecord.getStudentId()))
        {
            throw new ServiceException("参数错误：考勤记录与值班归属不一致", HttpStatus.BAD_REQUEST);
        }

        Date checkOutTime = new Date();
        attendanceRecord.setCheckOutTime(checkOutTime);

        long durationMillis = checkOutTime.getTime() - attendanceRecord.getCheckInTime().getTime();
        BigDecimal actualHours = new BigDecimal(durationMillis)
            .divide(new BigDecimal(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP);
        attendanceRecord.setActualDutyHours(actualHours.doubleValue());

        if (checkOutTime.before(dutySchedule.getEndTime())) {
            attendanceRecord.setStatus(AttendanceStatusEnum.EARLY_LEAVE);
        } else {
            if (AttendanceStatusEnum.LATE.equals(attendanceRecord.getStatus())) {
                attendanceRecord.setStatus(AttendanceStatusEnum.LATE);
            } else {
                attendanceRecord.setStatus(AttendanceStatusEnum.NORMAL);
            }
        }

        int result = attendanceRecordMapper.updateAttendanceRecord(attendanceRecord);
        if (result > 0) {
            dutySchedule.setAttendanceStatus(attendanceRecord.getStatus());
            dutyScheduleMapper.updateDutySchedule(dutySchedule);
        }

        return result;
    }

    @Override
    public int checkOutByDuty(String studentId, Integer dutyId)
    {
        AttendanceRecord record = attendanceRecordMapper.selectUnCheckedOutRecordByStudentAndDuty(studentId, dutyId);
        if (record == null || record.getRecordId() == null) {
            throw new ServiceException("当前无可签退记录", HttpStatus.CONFLICT);
        }
        return checkOut(record.getRecordId(), dutyId);
    }

    @Override
    public int checkOutByRecordId(String studentId, Long recordId)
    {
        AttendanceRecord record = attendanceRecordMapper.selectAttendanceRecordById(recordId);
        if (record == null)
        {
            throw new ServiceException("参数错误：考勤记录不存在", HttpStatus.BAD_REQUEST);
        }
        if (record.getStudentId() == null || !record.getStudentId().equals(studentId))
        {
            throw new ServiceException("该记录不属于当前用户", HttpStatus.FORBIDDEN);
        }
        if (record.getDutyId() == null)
        {
            throw new ServiceException("参数错误：考勤记录未关联值班", HttpStatus.BAD_REQUEST);
        }
        return checkOut(recordId, record.getDutyId());
    }

    /**
     * 根据学生 ID 和月份查询考勤记录
     * 
     * @param studentId 学号
     * @param yearMonth 年月（格式：2026-04）
     * @return 考勤记录列表
     */
    @Override
    public List<AttendanceRecord> selectAttendanceRecordByStudentIdAndMonth(String studentId, String yearMonth) {
        return attendanceRecordMapper.selectAttendanceRecordByStudentIdAndMonth(studentId, yearMonth);
    }

    /**
     * 查询考勤记录 VO 列表（支持条件过滤）
     * 
     * @param attendanceRecord 查询条件对象
     * @return 考勤记录 VO 列表
     */
    @Override
    public List<AttendanceRecordVo> selectAttendanceRecordVoList(AttendanceRecord attendanceRecord) {
        return attendanceRecordMapper.selectAttendanceRecordVoList(attendanceRecord);
    }

    /**
     * 根据学生 ID 和月份查询考勤记录 VO
     * 
     * @param studentId 学号
     * @param yearMonth 年月（格式：2026-04）
     * @return 考勤记录 VO 列表
     */
    @Override
    public List<AttendanceRecordVo> selectAttendanceRecordVoByStudentIdAndMonth(String studentId, String yearMonth) {
        return attendanceRecordMapper.selectAttendanceRecordVoByStudentIdAndMonth(studentId, yearMonth);
    }
}
