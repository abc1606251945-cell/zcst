package com.zcst.manage.mapper;

import com.zcst.manage.domain.DutySchedule;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 值班表Mapper接口
 * 
 * @author zcst
 * @date 2026-03-21
 */
public interface DutyScheduleMapper
{
    /**
     * 查询值班表列表
     * 
     * @param dutySchedule 值班表
     * @return 值班表集合
     */
    public List<DutySchedule> selectDutyScheduleList(DutySchedule dutySchedule);

    /**
     * 按学号查询值班表
     * 
     * @param studentId 学号
     * @return 值班表集合
     */
    public List<DutySchedule> selectDutyScheduleByStudentId(String studentId);

    /**
     * 按场馆ID查询值班表
     * 
     * @param venueId 场馆ID
     * @return 值班表集合
     */
    public List<DutySchedule> selectDutyScheduleByVenueId(Integer venueId);

    /**
     * 查询值班表详细信息
     * 
     * @param dutyId 值班表ID
     * @return 值班表
     */
    public DutySchedule selectDutyScheduleByDutyId(Integer dutyId);

    /**
     * 新增值班表
     * 
     * @param dutySchedule 值班表
     * @return 结果
     */
    public int insertDutySchedule(DutySchedule dutySchedule);

    /**
     * 修改值班表
     * 
     * @param dutySchedule 值班表
     * @return 结果
     */
    public int updateDutySchedule(DutySchedule dutySchedule);

    /**
     * 删除值班表
     * 
     * @param dutyId 值班表ID
     * @return 结果
     */
    public int deleteDutyScheduleByDutyId(Integer dutyId);

    /**
     * 查询指定时间内是否有重复排班
     * 
     * @param studentId 学号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 结果
     */
    public List<DutySchedule> selectOverlappingDuty(@Param("studentId") String studentId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 查询指定场馆在指定时间内的排班数量
     * 
     * @param venueId 场馆ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 结果
     */
    public int countVenueDuty(@Param("venueId") Integer venueId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 批量查询学生的总值班时长
     * 
     * @param studentIds 学生ID列表
     * @return 结果
     */
    public List<Map<String, Object>> selectStudentsTotalDutyTime(@Param("studentIds") List<String> studentIds);

    public int deleteDutyScheduleByVenueAndTime(@Param("venueId") Integer venueId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    public int deleteDutyScheduleByVenueAndRange(@Param("venueId") Integer venueId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 批量删除值班表
     * 
     * @param dutyIds 需要删除的数据 ID
     * @return 结果
     */
    public int deleteDutyScheduleByDutyIds(Integer[] dutyIds);

    /**
     * 查询学生当前可签到的值班信息
     * 
     * @param studentId 学号
     * @param currentTime 当前时间
     * @return 可签到的值班信息列表
     */
    public List<DutySchedule> selectCurrentAvailableDuty(@Param("studentId") String studentId, @Param("currentTime") Date currentTime);
}
