package com.zcst.manage.service;

import com.zcst.manage.domain.AttendanceRecord;

import java.util.List;

/**
 * 考勤记录Service接口
 */
public interface IAttendanceRecordService {
    /**
     * 查询考勤记录
     *
     * @param recordId 考勤记录ID
     * @return 考勤记录
     */
    public AttendanceRecord selectAttendanceRecordById(Long recordId);

    /**
     * 查询考勤记录列表
     *
     * @param attendanceRecord 考勤记录
     * @return 考勤记录集合
     */
    public List<AttendanceRecord> selectAttendanceRecordList(AttendanceRecord attendanceRecord);

    /**
     * 新增考勤记录
     *
     * @param attendanceRecord 考勤记录
     * @return 结果
     */
    public int insertAttendanceRecord(AttendanceRecord attendanceRecord);

    /**
     * 修改考勤记录
     *
     * @param attendanceRecord 考勤记录
     * @return 结果
     */
    public int updateAttendanceRecord(AttendanceRecord attendanceRecord);

    /**
     * 删除考勤记录
     *
     * @param recordId 考勤记录ID
     * @return 结果
     */
    public int deleteAttendanceRecordById(Long recordId);

    /**
     * 批量删除考勤记录
     *
     * @param recordIds 需要删除的考勤记录ID
     * @return 结果
     */
    public int deleteAttendanceRecordByIds(Long[] recordIds);

    /**
     * 打卡操作
     *
     * @param studentId 学生ID
     * @param dutyId 值班ID
     * @return 结果
     */
    public int checkIn(String studentId, Integer dutyId);

    /**
     * 根据学生ID和月份查询考勤记录
     *
     * @param studentId 学生ID
     * @param yearMonth 年月
     * @return 考勤记录集合
     */
    public List<AttendanceRecord> selectAttendanceRecordByStudentIdAndMonth(String studentId, String yearMonth);

    /**
     * 根据学生ID查询考勤记录列表
     *
     * @param studentId 学生ID
     * @return 考勤记录集合
     */
    public List<AttendanceRecord> selectAttendanceRecordListByStudentId(String studentId);

    /**
     * 根据场馆ID查询考勤记录列表
     *
     * @param venueId 场馆ID
     * @return 考勤记录集合
     */
    public List<AttendanceRecord> selectAttendanceRecordListByVenueId(Integer venueId);
}