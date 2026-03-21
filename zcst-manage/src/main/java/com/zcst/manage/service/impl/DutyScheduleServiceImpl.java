package com.zcst.manage.service.impl;

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
        Student student = new Student();
        student.setVenueId(venueId.longValue());
        List<Student> students = studentMapper.selectStudentList(student);

        // 筛选出在指定时间段内没有课的学生
        List<AvailableStudentVo> availableStudents = new ArrayList<>();
        for (Student s : students) {
            if (!studentScheduleService.hasClass(s.getStudentId(), startTime, endTime)) {
                // 计算该学生的总值班时长
                long totalDutyTime = calculateTotalDutyTime(s.getStudentId());
                AvailableStudentVo vo = new AvailableStudentVo();
                vo.setStudentId(s.getStudentId());
                vo.setName(s.getName());
                vo.setGender(s.getGender());
                vo.setMajorId(s.getMajorId());
                vo.setGrade(s.getGrade());
                vo.setPhone(s.getPhone());
                vo.setVenueId(s.getVenueId());
                vo.setTotalDutyTime(totalDutyTime);
                availableStudents.add(vo);
            }
        }

        // 按值班总时长正序排列
        availableStudents.sort(Comparator.comparingLong(AvailableStudentVo::getTotalDutyTime));

        return availableStudents;
    }

    @Override
    public boolean autoSchedule(Integer venueId, Date startDate, Date endDate, List<IDutyScheduleService.TimeSlot> timeSlots)
    {
        // 获取该场馆的所有学生
        Student student = new Student();
        student.setVenueId(venueId.longValue());
        List<Student> students = studentMapper.selectStudentList(student);

        if (students.isEmpty()) {
            return false;
        }

        // 计算学生的当前值班时长
        Map<String, Long> studentDutyTimes = new HashMap<>();
        for (Student s : students) {
            studentDutyTimes.put(s.getStudentId(), calculateTotalDutyTime(s.getStudentId()));
        }

        // 生成排班计划
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            // 对每个时间段进行排班
            for (IDutyScheduleService.TimeSlot slot : timeSlots) {
                // 解析时间段
                String[] startParts = slot.getStartTime().split(":");
                String[] endParts = slot.getEndTime().split(":");

                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startParts[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(startParts[1]));
                Date slotStart = calendar.getTime();

                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endParts[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(endParts[1]));
                Date slotEnd = calendar.getTime();

                // 筛选可用学生
                List<Student> availableStudents = students.stream()
                        .filter(s -> !studentScheduleService.hasClass(s.getStudentId(), slotStart, slotEnd))
                        .collect(Collectors.toList());

                if (!availableStudents.isEmpty()) {
                    // 选择值班时长最少的1-2名学生
                    availableStudents.sort(Comparator.comparingLong(s -> studentDutyTimes.getOrDefault(s.getStudentId(), 0L)));
                    int assignCount = Math.min(2, availableStudents.size());
                    
                    for (int i = 0; i < assignCount; i++) {
                        Student selectedStudent = availableStudents.get(i);

                        // 创建值班记录
                        DutySchedule dutySchedule = new DutySchedule();
                        dutySchedule.setStudentId(selectedStudent.getStudentId());
                        dutySchedule.setVenueId(venueId);
                        dutySchedule.setStartTime(slotStart);
                        dutySchedule.setEndTime(slotEnd);
                        dutySchedule.setRemark(selectedStudent.getName() + "的值班");

                        // 保存值班记录
                        dutyScheduleMapper.insertDutySchedule(dutySchedule);

                        // 更新学生的值班时长
                        long dutyDuration = slotEnd.getTime() - slotStart.getTime();
                        studentDutyTimes.put(selectedStudent.getStudentId(), 
                                studentDutyTimes.getOrDefault(selectedStudent.getStudentId(), 0L) + dutyDuration);
                    }
                }
            }

            // 移到下一天
            calendar.add(Calendar.DAY_OF_MONTH, 1);
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
    public boolean autoScheduleByConfig(Integer venueId, Date startDate, int weeks)
    {
        // 计算结束日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.WEEK_OF_YEAR, weeks);
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

    /**
     * 计算学生的总值班时长（毫秒）
     */
    private long calculateTotalDutyTime(String studentId)
    {
        List<DutySchedule> dutySchedules = dutyScheduleMapper.selectDutyScheduleByStudentId(studentId);
        long totalTime = 0;
        for (DutySchedule schedule : dutySchedules) {
            totalTime += schedule.getEndTime().getTime() - schedule.getStartTime().getTime();
        }
        return totalTime;
    }
}
