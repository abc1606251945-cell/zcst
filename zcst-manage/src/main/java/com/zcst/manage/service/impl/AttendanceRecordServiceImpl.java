package com.zcst.manage.service.impl;

import com.zcst.manage.domain.AttendanceRecord;
import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.mapper.AttendanceRecordMapper;
import com.zcst.manage.mapper.DutyScheduleMapper;
import com.zcst.manage.service.IAttendanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * 考勤记录Service实现类
 */
@Service
public class AttendanceRecordServiceImpl implements IAttendanceRecordService {
    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    @Autowired
    private DutyScheduleMapper dutyScheduleMapper;

    @Override
    public AttendanceRecord selectAttendanceRecordById(Long recordId) {
        return attendanceRecordMapper.selectAttendanceRecordById(recordId);
    }

    @Override
    public List<AttendanceRecord> selectAttendanceRecordList(AttendanceRecord attendanceRecord) {
        return attendanceRecordMapper.selectAttendanceRecordList(attendanceRecord);
    }

    @Override
    public List<AttendanceRecord> selectAttendanceRecordListByStudentId(String studentId) {
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setStudentId(studentId);
        return attendanceRecordMapper.selectAttendanceRecordList(attendanceRecord);
    }

    @Override
    public List<AttendanceRecord> selectAttendanceRecordListByVenueId(Integer venueId) {
        // 先查询该场馆的所有值班记录
        DutySchedule dutySchedule = new DutySchedule();
        dutySchedule.setVenueId(venueId);
        List<DutySchedule> dutySchedules = dutyScheduleMapper.selectDutyScheduleList(dutySchedule);
        
        // 提取所有值班ID
        List<Integer> dutyIds = new ArrayList<>();
        for (DutySchedule duty : dutySchedules) {
            dutyIds.add(duty.getDutyId());
        }
        
        // 如果没有值班记录，返回空列表
        if (dutyIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 根据值班ID查询考勤记录
        return attendanceRecordMapper.selectAttendanceRecordByDutyIds(dutyIds);
    }

    @Override
    public int insertAttendanceRecord(AttendanceRecord attendanceRecord) {
        attendanceRecord.setCreatedAt(new Date());
        attendanceRecord.setUpdatedAt(new Date());
        return attendanceRecordMapper.insertAttendanceRecord(attendanceRecord);
    }

    @Override
    public int updateAttendanceRecord(AttendanceRecord attendanceRecord) {
        attendanceRecord.setUpdatedAt(new Date());
        return attendanceRecordMapper.updateAttendanceRecord(attendanceRecord);
    }

    @Override
    public int deleteAttendanceRecordById(Long recordId) {
        return attendanceRecordMapper.deleteAttendanceRecordById(recordId);
    }

    @Override
    public int deleteAttendanceRecordByIds(Long[] recordIds) {
        return attendanceRecordMapper.deleteAttendanceRecordByIds(recordIds);
    }

    @Override
    public int checkIn(String studentId, Integer dutyId) {
        // 查询值班信息
        DutySchedule dutySchedule = dutyScheduleMapper.selectDutyScheduleByDutyId(dutyId);
        if (dutySchedule == null) {
            return 0;
        }

        // 检查是否已经打卡
        AttendanceRecord existingRecord = attendanceRecordMapper.selectAttendanceRecordByDutyId(dutyId);
        if (existingRecord != null) {
            return 0;
        }

        // 创建考勤记录
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setStudentId(studentId);
        attendanceRecord.setDutyId(dutyId);
        attendanceRecord.setCheckInTime(new Date());

        // 判断打卡状态
        Date now = new Date();
        if (now.before(dutySchedule.getStartTime())) {
            attendanceRecord.setStatus("0"); // 正常
        } else {
            attendanceRecord.setStatus("1"); // 迟到
        }

        // 保存考勤记录
        int result = attendanceRecordMapper.insertAttendanceRecord(attendanceRecord);

        // 更新值班表的考勤状态
        if (result > 0) {
            dutySchedule.setAttendanceStatus(attendanceRecord.getStatus());
            dutyScheduleMapper.updateDutySchedule(dutySchedule);
        }

        return result;
    }

    @Override
    public List<AttendanceRecord> selectAttendanceRecordByStudentIdAndMonth(String studentId, String yearMonth) {
        return attendanceRecordMapper.selectAttendanceRecordByStudentIdAndMonth(studentId, yearMonth);
    }
}