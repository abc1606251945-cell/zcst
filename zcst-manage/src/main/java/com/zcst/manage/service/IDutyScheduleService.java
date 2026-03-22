package com.zcst.manage.service;

import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.domain.Vo.AvailableStudentVo;

import java.util.List;
import java.util.Date;

/**
 * 值班表Service接口
 * 
 * @author zcst
 * @date 2026-03-21
 */
public interface IDutyScheduleService
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

    /**
     * 获取指定场馆和时间段内的可用学生列表
     * 
     * @param venueId 场馆ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 可用学生列表
     */
    public List<AvailableStudentVo> getAvailableStudents(Integer venueId, Date startTime, Date endTime);

    /**
     * 自动排班
     * 
     * @param venueId 场馆ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param timeSlots 时间段配置
     * @return 排班结果
     */
    public boolean autoSchedule(Integer venueId, Date startDate, Date endDate, List<TimeSlot> timeSlots);

    /**
     * 根据值班时间配置进行自动排班
     * 
     * @param venueId 场馆ID
     * @param startDate 开始日期
     * @param weeks 排班周数
     * @return 排班结果
     */
    public boolean autoScheduleByConfig(Integer venueId, Date startDate, int weeks);

    public boolean autoScheduleByConfig(Integer venueId, Date startDate, Date endDate);

    /**
     * 时间段配置类
     */
    public static class TimeSlot {
        private String startTime; // 格式：HH:mm
        private String endTime;   // 格式：HH:mm

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
