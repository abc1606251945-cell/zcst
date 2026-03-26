package com.zcst.manage.service.impl;

import com.zcst.manage.domain.AttendanceRecord;
import com.zcst.manage.domain.AttendanceStatistics;
import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.mapper.AttendanceRecordMapper;
import com.zcst.manage.mapper.AttendanceStatisticsMapper;
import com.zcst.manage.mapper.DutyScheduleMapper;
import com.zcst.manage.service.IAttendanceStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 考勤统计Service实现类
 */
@Service
public class AttendanceStatisticsServiceImpl implements IAttendanceStatisticsService {
    @Autowired
    private AttendanceStatisticsMapper attendanceStatisticsMapper;

    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    @Autowired
    private DutyScheduleMapper dutyScheduleMapper;

    @Override
    public AttendanceStatistics selectAttendanceStatisticsById(Long statId) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsById(statId);
    }

    @Override
    public List<AttendanceStatistics> selectAttendanceStatisticsList(AttendanceStatistics attendanceStatistics) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsList(attendanceStatistics);
    }

    @Override
    public List<AttendanceStatistics> selectAttendanceStatisticsListByStudentId(String studentId) {
        AttendanceStatistics attendanceStatistics = new AttendanceStatistics();
        attendanceStatistics.setStudentId(studentId);
        return attendanceStatisticsMapper.selectAttendanceStatisticsList(attendanceStatistics);
    }

    @Override
    public List<AttendanceStatistics> selectAttendanceStatisticsListByVenueId(Integer venueId) {
        AttendanceStatistics attendanceStatistics = new AttendanceStatistics();
        attendanceStatistics.setVenueId(venueId);
        return attendanceStatisticsMapper.selectAttendanceStatisticsList(attendanceStatistics);
    }

    @Override
    public int insertAttendanceStatistics(AttendanceStatistics attendanceStatistics) {
        attendanceStatistics.setCreatedAt(new Date());
        attendanceStatistics.setUpdatedAt(new Date());
        return attendanceStatisticsMapper.insertAttendanceStatistics(attendanceStatistics);
    }

    @Override
    public int updateAttendanceStatistics(AttendanceStatistics attendanceStatistics) {
        attendanceStatistics.setUpdatedAt(new Date());
        return attendanceStatisticsMapper.updateAttendanceStatistics(attendanceStatistics);
    }

    @Override
    public int deleteAttendanceStatisticsById(Long statId) {
        return attendanceStatisticsMapper.deleteAttendanceStatisticsById(statId);
    }

    @Override
    public int deleteAttendanceStatisticsByIds(Long[] statIds) {
        return attendanceStatisticsMapper.deleteAttendanceStatisticsByIds(statIds);
    }

    @Override
    public int calculateMonthlyAttendance(String yearMonth) {
        // 解析年月
        LocalDate date = LocalDate.parse(yearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int year = date.getYear();
        int month = date.getMonthValue();

        // 查询该月份的所有值班记录
        DutySchedule dutyScheduleQuery = new DutySchedule();
        // 这里需要根据实际情况实现按月查询值班记录的逻辑
        List<DutySchedule> dutySchedules = dutyScheduleMapper.selectDutyScheduleList(dutyScheduleQuery);

        // 按学生ID分组统计
        for (DutySchedule dutySchedule : dutySchedules) {
            String studentId = dutySchedule.getStudentId();
            Integer venueId = dutySchedule.getVenueId();

            // 查询学生该月的考勤记录
            List<AttendanceRecord> attendanceRecords = attendanceRecordMapper.selectAttendanceRecordByStudentIdAndMonth(studentId, yearMonth);

            // 计算统计数据
            int checkInCount = attendanceRecords.size();
            int attendanceCount = 0;
            int absenceCount = 0;
            int lateCount = 0;
            int earlyLeaveCount = 0;
            BigDecimal totalDutyHours = BigDecimal.ZERO;

            for (AttendanceRecord record : attendanceRecords) {
                if ("0".equals(record.getStatus())) {
                    attendanceCount++;
                } else if ("1".equals(record.getStatus())) {
                    lateCount++;
                    attendanceCount++;
                } else if ("2".equals(record.getStatus())) {
                    earlyLeaveCount++;
                    attendanceCount++;
                } else if ("3".equals(record.getStatus())) {
                    absenceCount++;
                }

                // 计算值班时长
                DutySchedule duty = dutyScheduleMapper.selectDutyScheduleByDutyId(record.getDutyId());
                if (duty != null) {
                    long duration = duty.getEndTime().getTime() - duty.getStartTime().getTime();
                    BigDecimal hours = BigDecimal.valueOf(duration / (1000.0 * 60 * 60));
                    totalDutyHours = totalDutyHours.add(hours);
                }
            }

            // 计算缺勤次数（总值班次数 - 出勤次数）
            long totalDutyCount = dutySchedules.stream().filter(d -> studentId.equals(d.getStudentId())).count();
            absenceCount = (int) (totalDutyCount - attendanceCount);

            // 查找或创建考勤统计记录
            AttendanceStatistics existingStats = attendanceStatisticsMapper.selectAttendanceStatisticsByStudentIdAndMonth(studentId, yearMonth);
            AttendanceStatistics stats;

            if (existingStats != null) {
                stats = existingStats;
            } else {
                stats = new AttendanceStatistics();
                stats.setStudentId(studentId);
                stats.setVenueId(venueId);
                stats.setYearMonth(yearMonth);
            }

            // 更新统计数据
            stats.setTotalDutyHours(totalDutyHours);
            stats.setCheckInCount(checkInCount);
            stats.setAttendanceCount(attendanceCount);
            stats.setAbsenceCount(absenceCount);
            stats.setLateCount(lateCount);
            stats.setEarlyLeaveCount(earlyLeaveCount);

            // 保存统计记录
            if (existingStats != null) {
                attendanceStatisticsMapper.updateAttendanceStatistics(stats);
            } else {
                attendanceStatisticsMapper.insertAttendanceStatistics(stats);
            }
        }

        return 1;
    }

    @Override
    public AttendanceStatistics selectAttendanceStatisticsByStudentIdAndMonth(String studentId, String yearMonth) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsByStudentIdAndMonth(studentId, yearMonth);
    }

    @Override
    public List<AttendanceStatistics> selectAttendanceStatisticsByVenueIdAndMonth(Integer venueId, String yearMonth) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsByVenueIdAndMonth(venueId, yearMonth);
    }
}