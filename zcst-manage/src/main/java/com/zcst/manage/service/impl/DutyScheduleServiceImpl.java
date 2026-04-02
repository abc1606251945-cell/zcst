package com.zcst.manage.service.impl;

import com.zcst.common.exception.ServiceException;
import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.domain.DutyTimeConfig;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.AvailableStudentVo;
import com.zcst.manage.mapper.DutyScheduleMapper;
import com.zcst.manage.mapper.StudentMapper;
import com.zcst.manage.service.IDutyScheduleService;
import com.zcst.manage.service.IDutyTimeConfigService;
import com.zcst.manage.service.IStudentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 值班表Service实现
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Service
public class DutyScheduleServiceImpl implements IDutyScheduleService
{
    @Autowired
    private DutyScheduleMapper dutyScheduleMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private IStudentScheduleService studentScheduleService;

    @Autowired
    private IDutyTimeConfigService dutyTimeConfigService;

    @Override
    public List<DutySchedule> selectDutyScheduleList(DutySchedule dutySchedule)
    {
        return dutyScheduleMapper.selectDutyScheduleList(dutySchedule);
    }

    @Override
    public List<DutySchedule> selectDutyScheduleByStudentId(String studentId)
    {
        return dutyScheduleMapper.selectDutyScheduleByStudentId(studentId);
    }

    @Override
    public List<DutySchedule> selectDutyScheduleByVenueId(Integer venueId)
    {
        return dutyScheduleMapper.selectDutyScheduleByVenueId(venueId);
    }

    @Override
    public DutySchedule selectDutyScheduleByDutyId(Integer dutyId)
    {
        return dutyScheduleMapper.selectDutyScheduleByDutyId(dutyId);
    }

    @Override
    public int insertDutySchedule(DutySchedule dutySchedule)
    {
        return dutyScheduleMapper.insertDutySchedule(dutySchedule);
    }

    @Override
    public int updateDutySchedule(DutySchedule dutySchedule)
    {
        return dutyScheduleMapper.updateDutySchedule(dutySchedule);
    }

    @Override
    public int deleteDutyScheduleByDutyId(Integer dutyId)
    {
        return dutyScheduleMapper.deleteDutyScheduleByDutyId(dutyId);
    }

    @Override
    public int deleteDutyScheduleByDutyIds(Integer[] dutyIds)
    {
        return dutyScheduleMapper.deleteDutyScheduleByDutyIds(dutyIds);
    }

    @Override
    public List<AvailableStudentVo> getAvailableStudents(Integer venueId, Date startTime, Date endTime)
    {
        // 查询该场馆的所有学生
        Student studentParam = new Student();
        studentParam.setVenueId(venueId.longValue());
        List<Student> students = studentMapper.selectStudentList(studentParam);

        if (students.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询学生当前值班时长 (秒)
        List<String> studentIds = students.stream().map(Student::getStudentId).collect(Collectors.toList());
        List<Map<String, Object>> dutyTimeList = dutyScheduleMapper.selectStudentsTotalDutyTime(studentIds);
        Map<String, Long> studentDutyTimes = new HashMap<>();
        for (Map<String, Object> map : dutyTimeList) {
            String sid = (String) map.get("studentId");
            Object total = map.get("totalTime");
            studentDutyTimes.put(sid, total == null ? 0L : ((Number) total).longValue()); // 使用秒为单位
        }

        // 筛选出在指定时间段内没有课 且 没有其他值班 的学生
        List<AvailableStudentVo> availableStudents = new ArrayList<>();
        for (Student s : students) {
            // 检查是否有课
            if (studentScheduleService.hasClass(s.getStudentId(), startTime, endTime)) {
                continue;
            }
            // 检查是否有其他值班
            if (!dutyScheduleMapper.selectOverlappingDuty(s.getStudentId(), startTime, endTime).isEmpty()) {
                continue;
            }

            AvailableStudentVo vo = new AvailableStudentVo();
            vo.setStudentId(s.getStudentId());
            vo.setName(s.getName());
            vo.setGender(s.getGender());
            vo.setMajorId(s.getMajorId());
            vo.setGrade(s.getGrade());
            vo.setPhone(s.getPhone());
            vo.setVenueId(s.getVenueId());
            vo.setTotalDutyTime(studentDutyTimes.getOrDefault(s.getStudentId(), 0L));
            availableStudents.add(vo);
        }

        // 按值班总时长正序排列
        availableStudents.sort(Comparator.comparingLong(AvailableStudentVo::getTotalDutyTime));

        return availableStudents;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean autoSchedule(Integer venueId, Date startDate, Date endDate, List<IDutyScheduleService.TimeSlot> timeSlots)
    {
        if (venueId == null) {
            throw new ServiceException("场馆不能为空");
        }
        if (startDate == null) {
            throw new ServiceException("开始日期不能为空");
        }
        if (endDate == null) {
            throw new ServiceException("结束日期不能为空");
        }
        if (timeSlots == null || timeSlots.isEmpty()) {
            throw new ServiceException("值班时段不能为空");
        }
        // TODO: 添加并发控制，防止同时为同一场馆排班
        // 建议使用 Redis 分布式锁：String lockKey = "duty_schedule_lock_" + venueId + "_" + startTime;
        
        // 获取该场馆的所有学生
        Student studentParam = new Student();
        studentParam.setVenueId(venueId.longValue());
        List<Student> students = studentMapper.selectStudentList(studentParam);

        if (students.isEmpty()) {
            return false;
        }

        // 批量查询学生当前值班时长 (秒)
        List<String> studentIds = students.stream().map(Student::getStudentId).collect(Collectors.toList());
        List<Map<String, Object>> dutyTimeList = dutyScheduleMapper.selectStudentsTotalDutyTime(studentIds);
        Map<String, Long> studentDutyTimes = new HashMap<>();
        for (Map<String, Object> map : dutyTimeList) {
            String sid = (String) map.get("studentId");
            Object total = map.get("totalTime");
            studentDutyTimes.put(sid, total == null ? 0L : ((Number) total).longValue()); // 使用秒为单位
        }

        // 初始化缺失的学生时长为0
        for (Student s : students) {
            studentDutyTimes.putIfAbsent(s.getStudentId(), 0L);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate startDay = startDate.toInstant().atZone(zoneId).toLocalDate();
        LocalDate endDay = endDate.toInstant().atZone(zoneId).toLocalDate();

        Calendar rangeStartCal = Calendar.getInstance();
        rangeStartCal.setTime(Date.from(startDay.minusDays(1).atStartOfDay(zoneId).toInstant()));
        rangeStartCal.set(Calendar.HOUR_OF_DAY, 0);
        rangeStartCal.set(Calendar.MINUTE, 0);
        rangeStartCal.set(Calendar.SECOND, 0);
        rangeStartCal.set(Calendar.MILLISECOND, 0);

        Calendar rangeEndCal = Calendar.getInstance();
        rangeEndCal.setTime(Date.from(endDay.atStartOfDay(zoneId).toInstant()));
        rangeEndCal.set(Calendar.HOUR_OF_DAY, 23);
        rangeEndCal.set(Calendar.MINUTE, 59);
        rangeEndCal.set(Calendar.SECOND, 59);
        rangeEndCal.set(Calendar.MILLISECOND, 999);

        List<Map<String, Object>> existingDutyDates = dutyScheduleMapper.selectStudentDutyDatesInRange(studentIds, rangeStartCal.getTime(), rangeEndCal.getTime());
        Set<String> studentDutyDayKeys = new HashSet<>();
        for (Map<String, Object> row : existingDutyDates) {
            Object sidObj = row.get("studentId");
            Object dateObj = row.get("dutyDate");
            if (sidObj == null || dateObj == null) {
                continue;
            }
            String sid = String.valueOf(sidObj);
            LocalDate dutyDate;
            if (dateObj instanceof Date) {
                dutyDate = ((Date) dateObj).toInstant().atZone(zoneId).toLocalDate();
            } else {
                dutyDate = LocalDate.parse(String.valueOf(dateObj));
            }
            studentDutyDayKeys.add(sid + "|" + dutyDate);
        }

        for (LocalDate day = startDay; !day.isAfter(endDay); day = day.plusDays(1)) {
            for (IDutyScheduleService.TimeSlot slot : timeSlots) {
                String[] startParts = slot.getStartTime().split(":");
                String[] endParts = slot.getEndTime().split(":");

                int startHour = Integer.parseInt(startParts[0]);
                int startMinute = Integer.parseInt(startParts[1]);
                int startSecond = startParts.length >= 3 ? Integer.parseInt(startParts[2]) : 0;
                int endHour = Integer.parseInt(endParts[0]);
                int endMinute = Integer.parseInt(endParts[1]);
                int endSecond = endParts.length >= 3 ? Integer.parseInt(endParts[2]) : 0;

                if (startHour == endHour && startMinute == endMinute && startSecond == endSecond) {
                    continue;
                }

                Date slotStart = Date.from(day.atTime(startHour, startMinute, startSecond).atZone(zoneId).toInstant());
                LocalDate slotEndDay = (endHour < startHour
                        || (endHour == startHour && (endMinute < startMinute || (endMinute == startMinute && endSecond < startSecond))))
                        ? day.plusDays(1)
                        : day;
                Date slotEnd = Date.from(slotEndDay.atTime(endHour, endMinute, endSecond).atZone(zoneId).toInstant());

                // 1. 检查场馆该时段是否已经排满（暂时硬编码为2人，建议从配置读取）
                // TODO: 从配置中读取每个时段的最大人数
                int maxStudentsPerSlot = 2;
                int existingCount = dutyScheduleMapper.countVenueDuty(venueId, slotStart, slotEnd);
                if (existingCount >= maxStudentsPerSlot) {
                    continue;
                }
                int needed = maxStudentsPerSlot - existingCount;

                // 2. 筛选可用学生 (无课 且 该时段没在其他地方值班)
                List<Student> baseCandidates = students.stream()
                        .filter(s -> !studentScheduleService.hasClass(s.getStudentId(), slotStart, slotEnd))
                        .filter(s -> dutyScheduleMapper.selectOverlappingDuty(s.getStudentId(), slotStart, slotEnd).isEmpty())
                        .collect(Collectors.toList());

                if (!baseCandidates.isEmpty()) {
                    String dayStr = day.toString();
                    String yesterdayStr = day.minusDays(1).toString();

                    List<Student> strictCandidates = baseCandidates.stream()
                            .filter(s -> !studentDutyDayKeys.contains(s.getStudentId() + "|" + dayStr))
                            .filter(s -> !studentDutyDayKeys.contains(s.getStudentId() + "|" + yesterdayStr))
                            .collect(Collectors.toList());

                    List<Student> availableStudents;
                    if (strictCandidates.size() >= needed) {
                        availableStudents = strictCandidates;
                    } else {
                        availableStudents = baseCandidates.stream()
                                .filter(s -> !studentDutyDayKeys.contains(s.getStudentId() + "|" + dayStr))
                                .collect(Collectors.toList());
                    }

                    if (availableStudents.size() < needed) {
                        availableStudents = baseCandidates;
                    }

                    availableStudents.sort(Comparator
                            .comparingLong((Student s) -> studentDutyTimes.getOrDefault(s.getStudentId(), 0L))
                            .thenComparing(Student::getStudentId));
                    int assignCount = Math.min(needed, availableStudents.size());
                    
                    for (int i = 0; i < assignCount; i++) {
                        Student selectedStudent = availableStudents.get(i);

                        // 创建值班记录
                        DutySchedule dutySchedule = new DutySchedule();
                        dutySchedule.setStudentId(selectedStudent.getStudentId());
                        dutySchedule.setVenueId(venueId);
                        dutySchedule.setStartTime(slotStart);
                        dutySchedule.setEndTime(slotEnd);
                        dutySchedule.setRemark(selectedStudent.getName() + "的自动排班");

                        // 保存值班记录
                        dutyScheduleMapper.insertDutySchedule(dutySchedule);

                        // 更新学生的值班时长（使用秒为单位）
                        long dutyDuration = (slotEnd.getTime() - slotStart.getTime()) / 1000; // 毫秒转秒
                        studentDutyTimes.put(selectedStudent.getStudentId(), 
                                studentDutyTimes.get(selectedStudent.getStudentId()) + dutyDuration);

                        studentDutyDayKeys.add(selectedStudent.getStudentId() + "|" + dayStr);
                    }
                }
            }
        }

        return true;
    }

    /**
     * 根据值班时间配置进行自动排班
     * 
     * @param venueId 场馆ID
     * @param startDate 开始日期
     * @param weeks 排班周数
     * @return 排班结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean autoScheduleByConfig(Integer venueId, Date startDate, int weeks)
    {
        if (startDate == null) {
            throw new ServiceException("开始日期不能为空");
        }
        if (weeks <= 0) {
            throw new ServiceException("排班周数必须大于0");
        }
        // 计算结束日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.WEEK_OF_YEAR, weeks);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // 包含开始日期，所以减1天
        Date endDate = calendar.getTime();

        // 获取该场馆的值班时间配置
        List<DutyTimeConfig> configs = dutyTimeConfigService.selectDutyTimeConfigByVenueId(venueId);
        if (configs.isEmpty()) {
            return false;
        }

        // 转换为TimeSlot列表
        List<IDutyScheduleService.TimeSlot> timeSlots = new ArrayList<>();
        for (DutyTimeConfig config : configs) {
            IDutyScheduleService.TimeSlot slot = new IDutyScheduleService.TimeSlot();
            slot.setStartTime(config.getStartTime());
            slot.setEndTime(config.getEndTime());
            timeSlots.add(slot);
        }

        // 执行自动排班
        return autoSchedule(venueId, startDate, endDate, timeSlots);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean autoScheduleByConfig(Integer venueId, Date startDate, Date endDate)
    {
        if (startDate == null || endDate == null) {
            throw new ServiceException("开始日期和结束日期不能为空");
        }
        List<DutyTimeConfig> configs = dutyTimeConfigService.selectDutyTimeConfigByVenueId(venueId);
        if (configs.isEmpty()) {
            return false;
        }

        dutyScheduleMapper.deleteDutyScheduleByVenueAndRange(venueId, startDate, endDate);

        List<IDutyScheduleService.TimeSlot> timeSlots = new ArrayList<>();
        for (DutyTimeConfig config : configs) {
            IDutyScheduleService.TimeSlot slot = new IDutyScheduleService.TimeSlot();
            slot.setStartTime(config.getStartTime());
            slot.setEndTime(config.getEndTime());
            timeSlots.add(slot);
        }

        return autoSchedule(venueId, startDate, endDate, timeSlots);
    }

    /**
     * 计算学生的总值班时长（秒）
     */
    private long calculateTotalDutyTime(String studentId)
    {
        List<DutySchedule> dutySchedules = dutyScheduleMapper.selectDutyScheduleByStudentId(studentId);
        long totalTime = 0;
        for (DutySchedule schedule : dutySchedules) {
            totalTime += (schedule.getEndTime().getTime() - schedule.getStartTime().getTime()) / 1000; // 毫秒转秒
        }
        return totalTime;
    }

    @Override
    public List<DutySchedule> selectCurrentAvailableDuty(String studentId, Date currentTime)
    {
        if (currentTime == null) {
            currentTime = new Date();
        }
        return dutyScheduleMapper.selectCurrentAvailableDuty(studentId, currentTime);
    }
}
