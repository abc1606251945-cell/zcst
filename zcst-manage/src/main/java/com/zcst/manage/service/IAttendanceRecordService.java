package com.zcst.manage.service;

import com.zcst.manage.domain.AttendanceRecord;
import com.zcst.manage.domain.Vo.AttendanceRecordVo;

import java.util.List;

/**
 * 考勤记录 Service 接口
 * 提供考勤记录的业务逻辑处理
 * 
 * 主要功能：
 * 1. 打卡操作（checkIn）- 学生到达场馆后打卡
 * 2. 签退操作（checkOut）- 学生值班结束后签退
 * 3. 考勤记录 CRUD 操作
 * 4. 考勤记录查询（按学生、场馆、年月）
 * 
 * 核心业务逻辑：
 * - 打卡：验证值班信息、检查重复打卡、判断是否迟到
 * - 签退：验证考勤记录、计算值班时长、判断是否早退
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
public interface IAttendanceRecordService {
    /**
     * 根据记录 ID 查询考勤记录详细信息
     * 
     * @param recordId 考勤记录 ID
     * @return 考勤记录对象
     */
    public AttendanceRecord selectAttendanceRecordById(Long recordId);

    /**
     * 查询考勤记录列表（支持条件过滤）
     * 支持按学生 ID、值班 ID、场馆 ID、年月、状态等条件过滤
     * 
     * @param attendanceRecord 查询条件对象
     * @return 考勤记录列表
     */
    public List<AttendanceRecord> selectAttendanceRecordList(AttendanceRecord attendanceRecord);

    /**
     * 新增考勤记录
     * 用于手动创建考勤记录（一般由系统自动创建）
     * 
     * @param attendanceRecord 考勤记录对象
     * @return 影响行数（1 表示成功）
     */
    public int insertAttendanceRecord(AttendanceRecord attendanceRecord);

    /**
     * 修改考勤记录
     * 用于更新考勤记录信息（如备注、状态等）
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
     * 打卡操作（核心业务方法）
     * 
     * 业务逻辑步骤：
     * 1. 验证值班信息是否存在
     * 2. 检查是否已经打卡（防止重复打卡）
     * 3. 创建考勤记录
     * 4. 判断打卡状态（正常/迟到）
     * 5. 更新值班表的考勤状态
     * 
     * 事务控制：保证考勤记录和值班表状态更新的原子性
     * 
     * @param studentId 学生 ID
     * @param dutyId 值班 ID
     * @return 影响行数（大于 0 表示成功）
     */
    public int checkIn(String studentId, Integer dutyId);

    /**
     * 签退操作（核心业务方法）
     * 
     * 业务逻辑步骤：
     * 1. 验证考勤记录是否存在
     * 2. 检查是否已经签退（防止重复签退）
     * 3. 验证值班信息
     * 4. 设置签退时间并计算实际值班时长
     * 5. 判断是否早退
     * 6. 更新考勤记录状态
     * 7. 更新值班表的考勤状态
     * 
     * 事务控制：保证考勤记录更新和值班表状态更新的原子性
     * 
     * @param recordId 考勤记录 ID
     * @param dutyId 值班 ID
     * @return 影响行数（大于 0 表示成功）
     */
    public int checkOut(Long recordId, Integer dutyId);

    /**
     * 根据学生 ID 和月份查询考勤记录
     * 用于月度考勤统计
     * 
     * @param studentId 学生 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤记录列表
     */
    public List<AttendanceRecord> selectAttendanceRecordByStudentIdAndMonth(String studentId, String yearMonth);

    /**
     * 根据学生 ID 查询考勤记录列表
     * 用于查询某个学生的所有考勤记录
     * 
     * @param studentId 学生 ID
     * @return 考勤记录列表
     */
    public List<AttendanceRecord> selectAttendanceRecordListByStudentId(String studentId);

    /**
     * 根据场馆 ID 查询考勤记录列表
     * 用于查询某个场馆的所有考勤记录
     * 
     * @param venueId 场馆 ID
     * @return 考勤记录列表
     */
    public List<AttendanceRecord> selectAttendanceRecordListByVenueId(Integer venueId);

    /**
     * 查询考勤记录 VO 列表（支持条件过滤）
     * 支持按学生 ID、值班 ID、场馆 ID、年月、状态等条件过滤，返回包含关联信息的 VO 对象
     * 
     * @param attendanceRecord 查询条件对象
     * @return 考勤记录 VO 列表
     */
    public List<AttendanceRecordVo> selectAttendanceRecordVoList(AttendanceRecord attendanceRecord);

    /**
     * 根据学生 ID 和月份查询考勤记录 VO
     * 用于月度考勤统计，返回包含关联信息的 VO 对象
     * 
     * @param studentId 学生 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤记录 VO 列表
     */
    public List<AttendanceRecordVo> selectAttendanceRecordVoByStudentIdAndMonth(String studentId, String yearMonth);
}