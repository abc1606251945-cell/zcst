package com.zcst.manage.service;

import com.zcst.manage.domain.AttendanceStatistics;

import java.util.List;

/**
 * 考勤统计 Service 接口
 * 提供考勤统计的业务逻辑处理
 * 
 * 主要功能：
 * 1. 月度考勤统计计算（calculateMonthlyAttendance）
 * 2. 考勤统计 CRUD 操作
 * 3. 按学生/场馆查询考勤统计
 * 
 * 核心业务逻辑：
 * - 月度统计：按年月过滤值班记录，统计出勤、迟到、早退、缺勤次数
 * - 时长计算：使用 BigDecimal 精确计算值班总时长
 * - 数据更新：支持统计数据的更新和插入
 * 
 * 统计维度：
 * - 按学生统计：每个学生的月度考勤数据
 * - 按场馆统计：每个场馆的月度考勤数据
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
public interface IAttendanceStatisticsService {
    /**
     * 根据统计 ID 查询考勤统计详细信息
     * 
     * @param statId 统计 ID
     * @return 考勤统计对象
     */
    public AttendanceStatistics selectAttendanceStatisticsById(Long statId);

    /**
     * 查询考勤统计列表（支持条件过滤）
     * 支持按学生 ID、场馆 ID、年月等条件过滤
     * 
     * @param attendanceStatistics 查询条件对象
     * @return 考勤统计列表
     */
    public List<AttendanceStatistics> selectAttendanceStatisticsList(AttendanceStatistics attendanceStatistics);

    /**
     * 新增考勤统计记录
     * 用于手动创建考勤统计（一般由系统自动计算）
     * 
     * @param attendanceStatistics 考勤统计对象
     * @return 影响行数（1 表示成功）
     */
    public int insertAttendanceStatistics(AttendanceStatistics attendanceStatistics);

    /**
     * 修改考勤统计记录
     * 用于更新考勤统计数据
     * 
     * @param attendanceStatistics 考勤统计对象
     * @return 影响行数（1 表示成功）
     */
    public int updateAttendanceStatistics(AttendanceStatistics attendanceStatistics);

    /**
     * 根据统计 ID 删除考勤统计记录
     * 
     * @param statId 统计 ID
     * @return 影响行数（1 表示成功）
     */
    public int deleteAttendanceStatisticsById(Long statId);

    /**
     * 批量删除考勤统计记录
     * 用于批量删除多条统计记录
     * 
     * @param statIds 统计 ID 数组
     * @return 删除的记录数
     */
    public int deleteAttendanceStatisticsByIds(Long[] statIds);

    /**
     * 按月统计考勤数据（核心业务方法）
     * 
     * 业务逻辑步骤：
     * 1. 按年月过滤值班记录
     * 2. 按学生 ID 分组
     * 3. 查询每个学生的考勤记录
     * 4. 统计出勤、迟到、早退、缺勤次数
     * 5. 计算值班总时长（使用 BigDecimal 精确计算）
     * 6. 更新或插入考勤统计记录
     * 
     * 统计规则：
     * - 打卡次数：实际打卡的次数
     * - 出勤次数：状态为正常 (0) + 迟到 (1) + 早退 (2) 的次数
     * - 迟到次数：状态为迟到 (1) 的次数
     * - 早退次数：状态为早退 (2) 的次数
     * - 缺勤次数：总值班次数 - 出勤次数
     * - 值班总时长：累加实际值班时长，如无则使用计划时长
     * 
     * 事务控制：保证统计过程中数据的一致性
     * 
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 1 表示成功，0 表示失败
     */
    public int calculateMonthlyAttendance(String yearMonth);

    /**
     * 根据学生 ID 和年月查询考勤统计
     * 用于查询某个学生在指定月份的考勤统计详细信息
     * 
     * @param studentId 学生 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计对象，如果不存在则返回 null
     */
    public AttendanceStatistics selectAttendanceStatisticsByStudentIdAndMonth(String studentId, String yearMonth);

    /**
     * 根据场馆 ID 和年月查询考勤统计列表
     * 用于查询某个场馆在指定月份所有学生的考勤统计数据
     * 
     * @param venueId 场馆 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计列表
     */
    public List<AttendanceStatistics> selectAttendanceStatisticsByVenueIdAndMonth(Integer venueId, String yearMonth);

    /**
     * 根据学生 ID 查询考勤统计列表
     * 用于查询某个学生的所有月份考勤统计
     * 
     * @param studentId 学生 ID
     * @return 考勤统计列表
     */
    public List<AttendanceStatistics> selectAttendanceStatisticsListByStudentId(String studentId);

    /**
     * 根据场馆 ID 查询考勤统计列表
     * 用于查询某个场馆所有学生的考勤统计
     * 
     * @param venueId 场馆 ID
     * @return 考勤统计列表
     */
    public List<AttendanceStatistics> selectAttendanceStatisticsListByVenueId(Integer venueId);
}