package com.zcst.manage.mapper;

import com.zcst.manage.domain.AttendanceStatistics;
import com.zcst.manage.domain.Vo.AttendanceStatisticsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考勤统计 Mapper 接口
 * 对应数据库表：attendance_statistics
 * 
 * 提供考勤统计表的 CRUD 操作接口
 * 包括：月度统计查询、按学生/场馆统计等功能
 * 
 * SQL 语句位置：resources/mapper/manage/AttendanceStatisticsMapper.xml
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
public interface AttendanceStatisticsMapper {
    /**
     * 根据统计 ID 查询考勤统计详细信息
     * 
     * @param statId 统计 ID（主键）
     * @return 考勤统计对象，如果不存在则返回 null
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
     * 用于创建新的月度统计记录
     * 
     * @param attendanceStatistics 考勤统计对象
     * @return 影响行数（1 表示成功）
     */
    public int insertAttendanceStatistics(AttendanceStatistics attendanceStatistics);

    /**
     * 修改考勤统计记录
     * 用于更新月度统计数据
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
     * 根据学生 ID 和年月查询考勤统计
     * 用于查询某个学生在指定月份的考勤统计数据
     * 
     * @param studentId 学生 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计对象，如果不存在则返回 null
     */
    public AttendanceStatistics selectAttendanceStatisticsByStudentIdAndMonth(@Param("studentId") String studentId, @Param("yearMonth") String yearMonth);

    /**
     * 根据场馆 ID 和年月查询考勤统计列表
     * 用于查询某个场馆在指定月份所有学生的考勤统计数据
     * 
     * @param venueId 场馆 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计列表
     */
    public List<AttendanceStatistics> selectAttendanceStatisticsByVenueIdAndMonth(@Param("venueId") Integer venueId, @Param("yearMonth") String yearMonth);

    /**
     * 根据学生 ID 和年月查询考勤统计 VO
     * 用于查询某个学生在指定月份的考勤统计详细信息，返回包含关联信息的 VO 对象
     * 
     * @param studentId 学生 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计 VO 对象，如果不存在则返回 null
     */
    public AttendanceStatisticsVo selectAttendanceStatisticsVoByStudentIdAndMonth(@Param("studentId") String studentId, @Param("yearMonth") String yearMonth);

    /**
     * 根据场馆 ID 和年月查询考勤统计 VO 列表
     * 用于查询某个场馆在指定月份所有学生的考勤统计数据，返回包含关联信息的 VO 对象
     * 
     * @param venueId 场馆 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计 VO 列表
     */
    public List<AttendanceStatisticsVo> selectAttendanceStatisticsVoByVenueIdAndMonth(@Param("venueId") Integer venueId, @Param("yearMonth") String yearMonth);

    /**
     * 查询考勤统计 VO 列表（支持条件过滤）
     * 支持按学生 ID、场馆 ID、年月等条件过滤，返回包含关联信息的 VO 对象
     * 
     * @param attendanceStatistics 查询条件对象
     * @return 考勤统计 VO 列表
     */
    public List<AttendanceStatisticsVo> selectAttendanceStatisticsVoList(AttendanceStatistics attendanceStatistics);
}