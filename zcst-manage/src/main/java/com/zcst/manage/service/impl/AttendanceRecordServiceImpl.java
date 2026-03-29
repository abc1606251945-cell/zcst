package com.zcst.manage.service.impl;

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
        try {
            // 查询值班信息
            DutySchedule dutySchedule = dutyScheduleMapper.selectDutyScheduleByDutyId(dutyId);
            if (dutySchedule == null) {
                log.warn("值班信息不存在，dutyId: {}", dutyId);
                return 0;
            }

            // 检查是否已经打卡
            AttendanceRecord existingRecord = attendanceRecordMapper.selectAttendanceRecordByDutyId(dutyId);
            if (existingRecord != null) {
                log.warn("该值班已打卡，dutyId: {}", dutyId);
                return 0;
            }

            // 创建考勤记录
            AttendanceRecord attendanceRecord = new AttendanceRecord();
            attendanceRecord.setStudentId(studentId);
            attendanceRecord.setDutyId(dutyId);
            Date checkInTime = new Date();
            attendanceRecord.setCheckInTime(checkInTime);

            // 判断打卡状态（使用打卡时间判断）
            if (checkInTime.before(dutySchedule.getStartTime())) {
                attendanceRecord.setStatus(AttendanceStatusEnum.NORMAL); // 正常
            } else {
                attendanceRecord.setStatus(AttendanceStatusEnum.LATE); // 迟到
            }

            // 保存考勤记录
            int result = attendanceRecordMapper.insertAttendanceRecord(attendanceRecord);

            // 更新值班表的考勤状态
            if (result > 0) {
                dutySchedule.setAttendanceStatus(attendanceRecord.getStatus());
                dutyScheduleMapper.updateDutySchedule(dutySchedule);
                log.info("打卡成功，studentId: {}, dutyId: {}, status: {}", studentId, dutyId, attendanceRecord.getStatus());
            }

            return result;
        } catch (Exception e) {
            log.error("打卡失败，studentId: {}, dutyId: {}", studentId, dutyId, e);
            throw new RuntimeException("打卡失败：" + e.getMessage(), e);
        }
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
        try {
            // 查询考勤记录
            AttendanceRecord attendanceRecord = attendanceRecordMapper.selectAttendanceRecordById(recordId);
            if (attendanceRecord == null) {
                log.warn("考勤记录不存在，recordId: {}", recordId);
                return 0;
            }

            // 检查是否已经签退
            if (attendanceRecord.getCheckOutTime() != null) {
                log.warn("该考勤记录已签退，recordId: {}", recordId);
                return 0;
            }

            // 查询值班信息
            DutySchedule dutySchedule = dutyScheduleMapper.selectDutyScheduleByDutyId(dutyId);
            if (dutySchedule == null) {
                log.warn("值班信息不存在，dutyId: {}", dutyId);
                return 0;
            }

            // 设置签退时间和计算时长
            Date checkOutTime = new Date();
            attendanceRecord.setCheckOutTime(checkOutTime);
            
            // 计算实际值班时长（小时），使用 BigDecimal 精确计算
            long durationMillis = checkOutTime.getTime() - attendanceRecord.getCheckInTime().getTime();
            BigDecimal actualHours = new BigDecimal(durationMillis)
                .divide(new BigDecimal(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP);
            attendanceRecord.setActualDutyHours(actualHours.doubleValue());

            // 判断是否早退
            if (checkOutTime.before(dutySchedule.getEndTime())) {
                // 早退，更新状态为 2
                attendanceRecord.setStatus(AttendanceStatusEnum.EARLY_LEAVE);
                log.info("早退，recordId: {}, studentId: {}", recordId, attendanceRecord.getStudentId());
            } else {
                // 正常签退，保持原状态或更新为 0
                if (AttendanceStatusEnum.LATE.equals(attendanceRecord.getStatus())) {
                    // 如果打卡时迟到，保持迟到状态
                    attendanceRecord.setStatus(AttendanceStatusEnum.LATE);
                } else {
                    attendanceRecord.setStatus(AttendanceStatusEnum.NORMAL);
                }
            }

            // 更新考勤记录
            int result = attendanceRecordMapper.updateAttendanceRecord(attendanceRecord);

            // 更新值班表的考勤状态
            if (result > 0) {
                dutySchedule.setAttendanceStatus(attendanceRecord.getStatus());
                dutyScheduleMapper.updateDutySchedule(dutySchedule);
                log.info("签退成功，recordId: {}, studentId: {}, status: {}", recordId, attendanceRecord.getStudentId(), attendanceRecord.getStatus());
            }

            return result;
        } catch (Exception e) {
            log.error("签退失败，recordId: {}, dutyId: {}", recordId, dutyId, e);
            throw new RuntimeException("签退失败：" + e.getMessage(), e);
        }
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