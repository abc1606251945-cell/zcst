package com.zcst.manage.domain;

import com.zcst.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 考勤统计表实体类
 * 对应数据库表：attendance_statistics
 * 
 * 用于统计学生每月的考勤数据，包括：
 * - 值班总时长（total_duty_hours）
 * - 打卡次数（check_in_count）
 * - 出勤次数（attendance_count）
 * - 缺勤次数（absence_count）
 * - 迟到次数（late_count）
 * - 早退次数（early_leave_count）
 * - 请假次数（leave_count）
 * - 调班次数（exchange_count）
 * 
 * 统计周期：按月统计（yearMonth 字段，格式：yyyy-MM）
 * 统计维度：按学生和场馆分组
 * 更新方式：每月调用 Service 层的 calculateMonthlyAttendance 方法计算
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class AttendanceStatistics extends BaseEntity {
    /**
     * 序列化版本 UID
     * 用于对象序列化时的版本控制
     */
    private static final long serialVersionUID = 1L;

    /**
     * 统计 ID（主键自增）
     * 对应数据库字段：stat_id
     */
    private Long statId;

    /**
     * 学号（学生唯一标识）
     * 对应数据库字段：student_id
     * 外键关联：sys_user 表
     * 联合索引：(student_id, year_month)
     */
    private String studentId;

    /**
     * 场馆 ID
     * 对应数据库字段：venue_id
     * 外键关联：venue 表
     * 用于按场馆统计考勤数据
     */
    private Integer venueId;

    /**
     * 年月（统计周期）
     * 对应数据库字段：year_month
     * 格式：yyyy-MM（如：2026-03）
     * 联合索引：(student_id, year_month)、(venue_id, year_month)
     */
    private String yearMonth;

    /**
     * 值班总时长（单位：小时）
     * 对应数据库字段：total_duty_hours
     * 精度：保留 2 位小数
     * 计算方式：累加该月所有考勤记录的实际值班时长
     * 用途：统计学生月度工作总量
     */
    private BigDecimal totalDutyHours;

    /**
     * 打卡次数
     * 对应数据库字段：check_in_count
     * 计算方式：该月考勤记录的总数
     * 用途：反映学生实际到岗打卡的次数
     */
    private Integer checkInCount;

    /**
     * 出勤次数
     * 对应数据库字段：attendance_count
     * 计算方式：状态为正常 (0) + 迟到 (1) + 早退 (2) 的次数
     * 用途：反映学生实际出勤的次数（包含非正常出勤）
     */
    private Integer attendanceCount;

    /**
     * 缺勤次数
     * 对应数据库字段：absence_count
     * 计算方式：该月总值班次数 - 出勤次数
     * 用途：反映学生无故缺勤的次数
     */
    private Integer absenceCount;

    /**
     * 迟到次数
     * 对应数据库字段：late_count
     * 计算方式：状态为迟到 (1) 的次数
     * 用途：反映学生迟到的频率
     */
    private Integer lateCount;

    /**
     * 早退次数
     * 对应数据库字段：early_leave_count
     * 计算方式：状态为早退 (2) 的次数
     * 用途：反映学生早退的频率
     */
    private Integer earlyLeaveCount;

    /**
     * 请假次数
     * 对应数据库字段：leave_count
     * 计算方式：该月请假申请通过的次数
     * 用途：反映学生请假的频率
     */
    private Integer leaveCount;

    /**
     * 调班次数
     * 对应数据库字段：exchange_count
     * 计算方式：该月调班申请通过的次数
     * 用途：反映学生调班的频率
     */
    private Integer exchangeCount;

    /**
     * 创建时间
     * 对应数据库字段：created_at
     * 记录统计记录创建时的时间戳
     */
    private Date createdAt;

    /**
     * 更新时间
     * 对应数据库字段：updated_at
     * 记录统计记录最后一次修改的时间戳
     */
    private Date updatedAt;
}
