package com.zcst.manage.service;

import com.zcst.manage.domain.StudentSchedule;

import java.util.List;
import java.util.Date;

/**
 * 学生课表Service接口
 * 
 * @author zcst
 * @date 2026-03-21
 */
public interface IStudentScheduleService
{
    /**
     * 查询学生课表列表
     * 
     * @param studentSchedule 学生课表
     * @return 学生课表集合
     */
    public List<StudentSchedule> selectStudentScheduleList(StudentSchedule studentSchedule);

    /**
     * 按学号查询学生课表
     * 
     * @param studentId 学号
     * @return 学生课表集合
     */
    public List<StudentSchedule> selectStudentScheduleByStudentId(String studentId);

    /**
     * 查询指定时间段内的学生课表
     * 
     * @param studentId 学号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 学生课表集合
     */
    public List<StudentSchedule> selectStudentScheduleByTimeRange(String studentId, Date startTime, Date endTime);

    /**
     * 查询学生课表详细信息
     * 
     * @param scheduleId 课表ID
     * @return 学生课表
     */
    public StudentSchedule selectStudentScheduleByScheduleId(Integer scheduleId);

    /**
     * 新增学生课表
     * 
     * @param studentSchedule 学生课表
     * @return 结果
     */
    public int insertStudentSchedule(StudentSchedule studentSchedule);

    /**
     * 修改学生课表
     * 
     * @param studentSchedule 学生课表
     * @return 结果
     */
    public int updateStudentSchedule(StudentSchedule studentSchedule);

    /**
     * 删除学生课表
     * 
     * @param scheduleId 课表ID
     * @return 结果
     */
    public int deleteStudentScheduleByScheduleId(Integer scheduleId);

    /**
     * 批量删除学生课表
     * 
     * @param scheduleIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteStudentScheduleByScheduleIds(Integer[] scheduleIds);

    /**
     * 检查学生在指定时间段是否有课
     * 
     * @param studentId 学号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否有课
     */
    public boolean hasClass(String studentId, Date startTime, Date endTime);
}
