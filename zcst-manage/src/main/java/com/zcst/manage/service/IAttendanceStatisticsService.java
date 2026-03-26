package com.zcst.manage.service;

import com.zcst.manage.domain.AttendanceStatistics;

import java.util.List;

/**
 * 考勤统计Service接口
 */
public interface IAttendanceStatisticsService {
    /**
     * 查询考勤统计
     *
     * @param statId 考勤统计ID
     * @return 考勤统计
     */
    public AttendanceStatistics selectAttendanceStatisticsById(Long statId);

    /**
     * 查询考勤统计列表
     *
     * @param attendanceStatistics 考勤统计
     * @return 考勤统计集合
     */
    public List<AttendanceStatistics> selectAttendanceStatisticsList(AttendanceStatistics attendanceStatistics);

    /**
     * 新增考勤统计
     *
     * @param attendanceStatistics 考勤统计
     * @return 结果
     */
    public int insertAttendanceStatistics(AttendanceStatistics attendanceStatistics);

    /**
     * 修改考勤统计
     *
     * @param attendanceStatistics 考勤统计
     * @return 结果
     */
    public int updateAttendanceStatistics(AttendanceStatistics attendanceStatistics);

    /**
     * 删除考勤统计
     *
     * @param statId 考勤统计ID
     * @return 结果
     */
    public int deleteAttendanceStatisticsById(Long statId);

    /**
     * 批量删除考勤统计
     *
     * @param statIds 需要删除的考勤统计ID
     * @return 结果
     */
    public int deleteAttendanceStatisticsByIds(Long[] statIds);

    /**
     * 按月统计考勤数据
     *
     * @param yearMonth 年月
     * @return 结果
     */
    public int calculateMonthlyAttendance(String yearMonth);

    /**
     * 根据学生ID和年月查询考勤统计
     *
     * @param studentId 学生ID
     * @param yearMonth 年月
     * @return 考勤统计
     */
    public AttendanceStatistics selectAttendanceStatisticsByStudentIdAndMonth(String studentId, String yearMonth);

    /**
     * 根据场馆ID和年月查询考勤统计
     *
     * @param venueId 场馆ID
     * @param yearMonth 年月
     * @return 考勤统计集合
     */
    public List<AttendanceStatistics> selectAttendanceStatisticsByVenueIdAndMonth(Integer venueId, String yearMonth);

    /**
     * 根据学生ID查询考勤统计列表
     *
     * @param studentId 学生ID
     * @return 考勤统计集合
     */
    public List<AttendanceStatistics> selectAttendanceStatisticsListByStudentId(String studentId);

    /**
     * 根据场馆ID查询考勤统计列表
     *
     * @param venueId 场馆ID
     * @return 考勤统计集合
     */
    public List<AttendanceStatistics> selectAttendanceStatisticsListByVenueId(Integer venueId);
}