package com.zcst.manage.mapper;

import com.zcst.manage.domain.AttendanceRecord;
import com.zcst.manage.domain.Vo.AttendanceRecordVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考勤记录 Mapper 接口
 * 对应数据库表：attendance_record
 * 
 * 提供考勤记录表的 CRUD 操作接口
 * 包括：打卡、签退、查询、统计等功能
 * 
 * SQL 语句位置：resources/mapper/manage/AttendanceRecordMapper.xml
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
public interface AttendanceRecordMapper {
    /**
     * 根据记录 ID 查询考勤记录
     * 
     * @param recordId 考勤记录 ID（主键）
     * @return 考勤记录对象，如果不存在则返回 null
     */
    public AttendanceRecord selectAttendanceRecordById(Long recordId);

    /**
     * 查询考勤记录列表（支持条件过滤）
     * 支持按学生 ID、值班 ID、年月、状态等条件过滤
     * 
     * @param attendanceRecord 查询条件对象
     * @return 考勤记录列表
     */
    public List<AttendanceRecord> selectAttendanceRecordList(AttendanceRecord attendanceRecord);

    /**
     * 新增考勤记录
     * 用于创建新的打卡记录
     * 
     * @param attendanceRecord 考勤记录对象
     * @return 影响行数（1 表示成功）
     */
    public int insertAttendanceRecord(AttendanceRecord attendanceRecord);

    /**
     * 修改考勤记录
     * 用于更新签退时间、状态等信息
     * 
     * @param attendanceRecord 考勤记录对象
     * @return 影响行数（1 表示成功）
     */
    public int updateAttendanceRecord(AttendanceRecord attendanceRecord);

    /**
     * 根据记录 ID 删除考勤记录
     * 
     * @param recordId 考勤记录 ID
     * @return 影响行数（1 表示成功）
     */
    public int deleteAttendanceRecordById(Long recordId);

    /**
     * 批量删除考勤记录
     * 用于批量删除多条考勤记录
     * 
     * @param recordIds 考勤记录 ID 数组
     * @return 删除的记录数
     */
    public int deleteAttendanceRecordByIds(Long[] recordIds);

    /**
     * 根据值班 ID 查询考勤记录
     * 用于检查某个值班是否已经打卡
     * 
     * @param dutyId 值班 ID
     * @return 考勤记录对象，如果不存在则返回 null
     */
    public AttendanceRecord selectAttendanceRecordByDutyId(Integer dutyId);

    public AttendanceRecord selectUnCheckedOutRecordByStudentAndDuty(
        @Param("studentId") String studentId,
        @Param("dutyId") Integer dutyId
    );

    /**
     * 根据值班 ID 列表批量查询考勤记录
     * 用于批量获取多个值班的考勤情况
     * 
     * @param dutyIds 值班 ID 列表
     * @return 考勤记录列表
     */
    public List<AttendanceRecord> selectAttendanceRecordByDutyIds(@Param("dutyIds") List<Integer> dutyIds);

    /**
     * 根据学生 ID 和月份查询考勤记录
     * 用于月度考勤统计
     * 
     * @param studentId 学生 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 该学生该月的考勤记录列表
     */
    public List<AttendanceRecord> selectAttendanceRecordByStudentIdAndMonth(@Param("studentId") String studentId, @Param("yearMonth") String yearMonth);

    /**
     * 根据学生 ID 和月份查询考勤记录 VO
     * 用于月度考勤统计，返回包含关联信息的 VO 对象
     * 
     * @param studentId 学生 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 该学生该月的考勤记录 VO 列表
     */
    public List<AttendanceRecordVo> selectAttendanceRecordVoByStudentIdAndMonth(@Param("studentId") String studentId, @Param("yearMonth") String yearMonth);

    /**
     * 查询考勤记录 VO 列表（支持条件过滤）
     * 支持按学生 ID、值班 ID、年月、状态等条件过滤，返回包含关联信息的 VO 对象
     * 
     * @param attendanceRecord 查询条件对象
     * @return 考勤记录 VO 列表
     */
    public List<AttendanceRecordVo> selectAttendanceRecordVoList(AttendanceRecord attendanceRecord);
}
