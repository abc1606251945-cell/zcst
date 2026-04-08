package com.zcst.manage.service.impl;

import com.zcst.manage.domain.StudentSchedule;
import com.zcst.manage.mapper.StudentScheduleMapper;
import com.zcst.manage.service.IStudentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生课表Service实现
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Service
public class StudentScheduleServiceImpl implements IStudentScheduleService
{
    @Autowired
    private StudentScheduleMapper studentScheduleMapper;

    @Override
    public List<StudentSchedule> selectStudentScheduleList(StudentSchedule studentSchedule)
    {
        return studentScheduleMapper.selectStudentScheduleList(studentSchedule);
    }

    @Override
    public List<StudentSchedule> selectStudentScheduleByStudentId(String studentId)
    {
        return studentScheduleMapper.selectStudentScheduleByStudentId(studentId);
    }

    @Override
    public List<StudentSchedule> selectStudentScheduleByTimeRange(String studentId, Date startTime, Date endTime)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("studentId", studentId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return studentScheduleMapper.selectStudentScheduleByTimeRange(params);
    }

    @Override
    public StudentSchedule selectStudentScheduleByScheduleId(Integer scheduleId)
    {
        return studentScheduleMapper.selectStudentScheduleByScheduleId(scheduleId);
    }

    @Override
    public int insertStudentSchedule(StudentSchedule studentSchedule)
    {
        return studentScheduleMapper.insertStudentSchedule(studentSchedule);
    }

    @Override
    public int updateStudentSchedule(StudentSchedule studentSchedule)
    {
        return studentScheduleMapper.updateStudentSchedule(studentSchedule);
    }

    @Override
    public int deleteStudentScheduleByScheduleId(Integer scheduleId)
    {
        return studentScheduleMapper.deleteStudentScheduleByScheduleId(scheduleId);
    }

    @Override
    public int deleteStudentScheduleByScheduleIds(Integer[] scheduleIds)
    {
        return studentScheduleMapper.deleteStudentScheduleByScheduleIds(scheduleIds);
    }

    @Override
    public boolean hasClass(String studentId, Date startTime, Date endTime)
    {
        List<StudentSchedule> schedules = selectStudentScheduleByTimeRange(studentId, startTime, endTime);
        return !schedules.isEmpty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int replaceStudentSchedule(String studentId, List<StudentSchedule> schedules)
    {
        studentScheduleMapper.deleteStudentScheduleByStudentId(studentId);
        if (schedules == null || schedules.isEmpty())
        {
            return 0;
        }
        return studentScheduleMapper.batchInsertStudentSchedule(schedules);
    }
}
