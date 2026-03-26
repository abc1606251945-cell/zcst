package com.zcst.manage.domain;

import com.zcst.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 考勤统计表
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class AttendanceStatistics extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    private Long statId;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 场馆ID
     */
    private Integer venueId;

    /**
     * 年月（格式：2026-04）
     */
    private String yearMonth;

    /**
     * 值班总时长
     */
    private BigDecimal totalDutyHours;

    /**
     * 打卡次数
     */
    private Integer checkInCount;

    /**
     * 出勤次数
     */
    private Integer attendanceCount;

    /**
     * 缺勤次数
     */
    private Integer absenceCount;

    /**
     * 迟到次数
     */
    private Integer lateCount;

    /**
     * 早退次数
     */
    private Integer earlyLeaveCount;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}