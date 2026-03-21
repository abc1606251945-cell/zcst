package com.zcst.manage.mapper;

import com.zcst.manage.domain.StudentSchedule;

import java.util.List;
import java.util.Date;

/**
 * 学生课表Mapper接口
 * 
 * @author zcst
 * @date 2026-03-21
 */
public interface StudentScheduleMapper
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
     * @param params 包含学号、开始时间和结束时间的Map
     * @return 学生课表集合
     */
    public List<StudentSchedule> selectStudentScheduleByTimeRange(java.util.Map<String, Object> params);

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
}
