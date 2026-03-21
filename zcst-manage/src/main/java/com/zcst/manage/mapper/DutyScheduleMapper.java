package com.zcst.manage.mapper;

import com.zcst.manage.domain.DutySchedule;

import java.util.List;

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
     * 批量删除值班表
     * 
     * @param dutyIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteDutyScheduleByDutyIds(Integer[] dutyIds);
}
