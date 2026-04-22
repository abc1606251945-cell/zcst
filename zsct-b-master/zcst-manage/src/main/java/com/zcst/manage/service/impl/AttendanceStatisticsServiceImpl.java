package com.zcst.manage.service.impl;

import com.zcst.manage.constant.AttendanceStatusEnum;
import com.zcst.manage.domain.AttendanceRecord;
import com.zcst.manage.domain.AttendanceStatistics;
import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.domain.Vo.AttendanceStatisticsVo;
import com.zcst.manage.mapper.AttendanceRecordMapper;
import com.zcst.manage.mapper.AttendanceStatisticsMapper;
import com.zcst.manage.mapper.DutyScheduleMapper;
import com.zcst.manage.mapper.LeaveApplicationMapper;
import com.zcst.manage.mapper.ShiftExchangeMapper;
import com.zcst.manage.service.IAttendanceStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 考勤统计 Service 实现类
 * 负责处理考勤数据的统计业务逻辑
 * 
 * 主要功能：
 * 1. 月度考勤统计计算（按月计算出勤、迟到、早退、缺勤次数）
 * 2. 按学生查询考勤统计
 * 3. 按场馆查询考勤统计
 * 4. 值班时长统计（使用 BigDecimal 精确计算）
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
@Service
public class AttendanceStatisticsServiceImpl implements IAttendanceStatisticsService {
    /**
     * 日志记录器
     * 用于记录业务操作日志和异常信息
     */
    private static final Logger log = LoggerFactory.getLogger(AttendanceStatisticsServiceImpl.class);

    /**
     * 考勤统计 Mapper 接口
     * 用于操作考勤统计表（attendance_statistics）
     */
    @Autowired
    private AttendanceStatisticsMapper attendanceStatisticsMapper;

    /**
     * 考勤记录 Mapper 接口
     * 用于操作考勤记录表（attendance_record）
     */
    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    /**
     * 值班安排 Mapper 接口
     * 用于操作值班安排表（duty_schedule）
     */
    @Autowired
    private DutyScheduleMapper dutyScheduleMapper;

    /**
     * 请假申请 Mapper 接口
     * 用于统计请假次数
     */
    @Autowired
    private LeaveApplicationMapper leaveApplicationMapper;

    /**
     * 调班申请 Mapper 接口
     * 用于统计调班次数
     */
    @Autowired
    private ShiftExchangeMapper shiftExchangeMapper;

    /**
     * 根据统计 ID 查询考勤统计详细信息
     * 
     * @param statId 统计 ID
     * @return 考勤统计对象
     */
    @Override
    public AttendanceStatistics selectAttendanceStatisticsById(Long statId) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsById(statId);
    }

    /**
     * 查询考勤统计列表
     * 支持按学生 ID、场馆 ID、年月等条件过滤
     * 
     * @param attendanceStatistics 查询条件
     * @return 考勤统计列表
     */
    @Override
    public List<AttendanceStatistics> selectAttendanceStatisticsList(AttendanceStatistics attendanceStatistics) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsList(attendanceStatistics);
    }

    /**
     * 根据学生 ID 查询考勤统计列表
     * 用于查询某个学生的所有月份考勤统计
     * 
     * @param studentId 学号
     * @return 该学生的考勤统计列表
     */
    @Override
    public List<AttendanceStatistics> selectAttendanceStatisticsListByStudentId(String studentId) {
        AttendanceStatistics attendanceStatistics = new AttendanceStatistics();
        attendanceStatistics.setStudentId(studentId);
        return attendanceStatisticsMapper.selectAttendanceStatisticsList(attendanceStatistics);
    }

    /**
     * 根据场馆 ID 查询考勤统计列表
     * 用于查询某个场馆所有学生的考勤统计
     * 
     * @param venueId 场馆 ID
     * @return 该场馆的考勤统计列表
     */
    @Override
    public List<AttendanceStatistics> selectAttendanceStatisticsListByVenueId(Integer venueId) {
        AttendanceStatistics attendanceStatistics = new AttendanceStatistics();
        attendanceStatistics.setVenueId(venueId);
        return attendanceStatisticsMapper.selectAttendanceStatisticsList(attendanceStatistics);
    }

    /**
     * 新增考勤统计记录
     * 自动设置创建时间和更新时间
     * 
     * @param attendanceStatistics 考勤统计对象
     * @return 影响行数
     */
    @Override
    public int insertAttendanceStatistics(AttendanceStatistics attendanceStatistics) {
        attendanceStatistics.setCreatedAt(new Date());
        attendanceStatistics.setUpdatedAt(new Date());
        return attendanceStatisticsMapper.insertAttendanceStatistics(attendanceStatistics);
    }

    /**
     * 修改考勤统计记录
     * 自动更新更新时间
     * 
     * @param attendanceStatistics 考勤统计对象
     * @return 影响行数
     */
    @Override
    public int updateAttendanceStatistics(AttendanceStatistics attendanceStatistics) {
        attendanceStatistics.setUpdatedAt(new Date());
        return attendanceStatisticsMapper.updateAttendanceStatistics(attendanceStatistics);
    }

    /**
     * 根据统计 ID 删除考勤统计记录
     * 
     * @param statId 统计 ID
     * @return 影响行数
     */
    @Override
    public int deleteAttendanceStatisticsById(Long statId) {
        return attendanceStatisticsMapper.deleteAttendanceStatisticsById(statId);
    }

    /**
     * 批量删除考勤统计记录
     * 
     * @param statIds 统计 ID 数组
     * @return 影响行数
     */
    @Override
    public int deleteAttendanceStatisticsByIds(Long[] statIds) {
        return attendanceStatisticsMapper.deleteAttendanceStatisticsByIds(statIds);
    }

    /**
     * 计算月度考勤统计
     * 核心业务逻辑：
     * 1. 按年月过滤值班记录
     * 2. 按学生 ID 分组
     * 3. 查询每个学生的考勤记录
     * 4. 统计出勤、迟到、早退、缺勤次数
     * 5. 计算值班总时长（使用 BigDecimal 精确计算）
     * 6. 更新或插入考勤统计记录
     * 
     * 统计规则：
     * - 打卡次数：实际打卡的次数
     * - 出勤次数：状态为正常 (0) + 迟到 (1) + 早退 (2) 的次数
     * - 迟到次数：状态为迟到 (1) 的次数
     * - 早退次数：状态为早退 (2) 的次数
     * - 缺勤次数：总值班次数 - 出勤次数
     * - 值班总时长：累加实际值班时长，如无则使用计划时长
     * 
     * 事务控制：保证统计过程中数据的一致性
     * 
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 1 表示成功，0 表示失败
     */
    @Transactional
    @Override
    public int calculateMonthlyAttendance(String yearMonth) {
        try {
            log.info("开始计算 {} 月份的考勤统计", yearMonth);
            
            // 步骤 1：解析年月，获取该月的年份、月份和天数
            LocalDate date = LocalDate.parse(yearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int year = date.getYear();
            int month = date.getMonthValue();
            int daysInMonth = date.lengthOfMonth();

            // 步骤 2：查询所有值班记录（后续按年月过滤）
            DutySchedule dutyScheduleQuery = new DutySchedule();
            List<DutySchedule> allDutySchedules = dutyScheduleMapper.selectDutyScheduleList(dutyScheduleQuery);
            
            // 步骤 3：过滤出指定月份的值班记录
            List<DutySchedule> monthDutySchedules = new ArrayList<>();
            for (DutySchedule duty : allDutySchedules) {
                if (duty.getStartTime() != null) {
                    LocalDate dutyDate = duty.getStartTime().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                    if (dutyDate.getYear() == year && dutyDate.getMonthValue() == month) {
                        monthDutySchedules.add(duty);
                    }
                }
            }
            
            log.info("{} 月份共有 {} 条值班记录", yearMonth, monthDutySchedules.size());

            // 步骤 4：按学生 ID 分组，便于按学生统计
            java.util.Map<String, List<DutySchedule>> studentDutyMap = monthDutySchedules.stream()
                .collect(java.util.stream.Collectors.groupingBy(DutySchedule::getStudentId));

            // 步骤 5：按学生逐个统计考勤数据
            int successCount = 0;
            for (java.util.Map.Entry<String, List<DutySchedule>> entry : studentDutyMap.entrySet()) {
                String studentId = entry.getKey();
                List<DutySchedule> studentDuties = entry.getValue();
                
                if (studentDuties.isEmpty()) {
                    continue;
                }
                
                // 步骤 5.1：获取场馆 ID，添加 null 判断防止空指针
                Integer venueId = null;
                if (studentDuties.get(0) != null) {
                    venueId = studentDuties.get(0).getVenueId();
                }
                if (venueId == null) {
                    log.warn("学生 {} 的场馆 ID 为空，跳过统计", studentId);
                    continue;
                }

                // 步骤 5.2：查询学生该月的考勤记录
                List<AttendanceRecord> attendanceRecords = attendanceRecordMapper.selectAttendanceRecordByStudentIdAndMonth(studentId, yearMonth);

                // 步骤 5.3：初始化统计数据
                int checkInCount = attendanceRecords.size();
                int attendanceCount = 0;
                int lateCount = 0;
                int earlyLeaveCount = 0;
                BigDecimal totalDutyHours = BigDecimal.ZERO;

                // 步骤 5.4：遍历考勤记录，统计各状态次数和总时长
                for (AttendanceRecord record : attendanceRecords) {
                    if (AttendanceStatusEnum.NORMAL.equals(record.getStatus())) {
                        // 正常
                        attendanceCount++;
                    } else if (AttendanceStatusEnum.LATE.equals(record.getStatus())) {
                        // 迟到
                        lateCount++;
                        attendanceCount++;
                    } else if (AttendanceStatusEnum.EARLY_LEAVE.equals(record.getStatus())) {
                        // 早退
                        earlyLeaveCount++;
                        attendanceCount++;
                    }

                    // 计算值班总时长
                    if (record.getActualDutyHours() != null) {
                        // 优先使用实际值班时长
                        totalDutyHours = totalDutyHours.add(BigDecimal.valueOf(record.getActualDutyHours()));
                    } else {
                        // 否则使用计划时长（从值班安排表计算）
                        DutySchedule duty = dutyScheduleMapper.selectDutyScheduleByDutyId(record.getDutyId());
                        if (duty != null && duty.getStartTime() != null && duty.getEndTime() != null) {
                            long duration = duty.getEndTime().getTime() - duty.getStartTime().getTime();
                            // 使用 BigDecimal 精确计算小时数，保留 2 位小数，四舍五入
                            BigDecimal hours = new BigDecimal(duration)
                                .divide(new BigDecimal(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP);
                            totalDutyHours = totalDutyHours.add(hours);
                        }
                    }
                }

                // 步骤 5.5：计算缺勤次数（总值班次数 - 出勤次数）
                int totalDutyCount = studentDuties.size();
                int absenceCount = totalDutyCount - attendanceCount;

                // 步骤 5.6：统计请假和调班次数
                int leaveCount = leaveApplicationMapper.countByStudentIdAndMonth(studentId, yearMonth);
                int exchangeCount = shiftExchangeMapper.countByStudentIdAndMonth(studentId, yearMonth);
                log.info("学生 {} 在 {} 月份的请假次数：{}, 调班次数：{}", studentId, yearMonth, leaveCount, exchangeCount);

                // 步骤 5.7：查找或创建考勤统计记录
                AttendanceStatistics existingStats = attendanceStatisticsMapper.selectAttendanceStatisticsByStudentIdAndMonth(studentId, yearMonth);
                AttendanceStatistics stats;

                if (existingStats != null) {
                    // 已存在统计记录，使用现有记录
                    stats = existingStats;
                } else {
                    // 不存在，创建新记录
                    stats = new AttendanceStatistics();
                    stats.setStudentId(studentId);
                    stats.setVenueId(venueId);
                    stats.setYearMonth(yearMonth);
                }

                // 步骤 5.8：更新统计数据（包括请假和调班次数）
                stats.setTotalDutyHours(totalDutyHours);
                stats.setCheckInCount(checkInCount);
                stats.setAttendanceCount(attendanceCount);
                stats.setAbsenceCount(absenceCount);
                stats.setLateCount(lateCount);
                stats.setEarlyLeaveCount(earlyLeaveCount);
                stats.setLeaveCount(leaveCount);
                stats.setExchangeCount(exchangeCount);

                // 步骤 5.8：保存统计记录（更新或插入）
                if (existingStats != null) {
                    attendanceStatisticsMapper.updateAttendanceStatistics(stats);
                } else {
                    attendanceStatisticsMapper.insertAttendanceStatistics(stats);
                }
                
                successCount++;
            }
            
            log.info("{} 月份考勤统计完成，共统计 {} 名学生", yearMonth, successCount);
            return 1;
        } catch (Exception e) {
            log.error("计算月度考勤统计失败，yearMonth: {}", yearMonth, e);
            throw new RuntimeException("计算月度考勤统计失败：" + e.getMessage(), e);
        }
    }

    /**
     * 根据学生 ID 和年月查询考勤统计
     * 用于查询某个学生在指定月份的考勤统计数据
     * 
     * @param studentId 学号
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计对象，如果不存在则返回 null
     */
    @Override
    public AttendanceStatistics selectAttendanceStatisticsByStudentIdAndMonth(String studentId, String yearMonth) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsByStudentIdAndMonth(studentId, yearMonth);
    }

    /**
     * 根据场馆 ID 和年月查询考勤统计列表
     * 用于查询某个场馆在指定月份所有学生的考勤统计数据
     * 
     * @param venueId 场馆 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计列表
     */
    @Override
    public List<AttendanceStatistics> selectAttendanceStatisticsByVenueIdAndMonth(Integer venueId, String yearMonth) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsByVenueIdAndMonth(venueId, yearMonth);
    }

    /**
     * 查询考勤统计 VO 列表（支持条件过滤）
     * 
     * @param attendanceStatistics 查询条件对象
     * @return 考勤统计 VO 列表
     */
    @Override
    public List<AttendanceStatisticsVo> selectAttendanceStatisticsVoList(AttendanceStatistics attendanceStatistics) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsVoList(attendanceStatistics);
    }

    /**
     * 根据学生 ID 和年月查询考勤统计 VO
     * 
     * @param studentId 学号
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计 VO 对象
     */
    @Override
    public AttendanceStatisticsVo selectAttendanceStatisticsVoByStudentIdAndMonth(String studentId, String yearMonth) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsVoByStudentIdAndMonth(studentId, yearMonth);
    }

    /**
     * 根据场馆 ID 和年月查询考勤统计 VO 列表
     * 
     * @param venueId 场馆 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计 VO 列表
     */
    @Override
    public List<AttendanceStatisticsVo> selectAttendanceStatisticsVoByVenueIdAndMonth(Integer venueId, String yearMonth) {
        return attendanceStatisticsMapper.selectAttendanceStatisticsVoByVenueIdAndMonth(venueId, yearMonth);
    }
}