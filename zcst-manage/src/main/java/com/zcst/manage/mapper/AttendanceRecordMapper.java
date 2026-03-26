package com.zcst.manage.mapper;

import com.zcst.manage.domain.AttendanceRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考勤记录Mapper接口
 */
public interface AttendanceRecordMapper {
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
     * 根据值班ID查询考勤记录
     *
     * @param dutyId 值班ID
     * @return 考勤记录
     */
    public AttendanceRecord selectAttendanceRecordByDutyId(Integer dutyId);

    /**
     * 根据值班ID列表查询考勤记录
     *
     * @param dutyIds 值班ID列表
     * @return 考勤记录集合
     */
    public List<AttendanceRecord> selectAttendanceRecordByDutyIds(@Param("dutyIds") List<Integer> dutyIds);

    /**
     * 根据学生ID和月份查询考勤记录
     *
     * @param studentId 学生ID
     * @param yearMonth 年月
     * @return 考勤记录集合
     */
    public List<AttendanceRecord> selectAttendanceRecordByStudentIdAndMonth(@Param("studentId") String studentId, @Param("yearMonth") String yearMonth);
}