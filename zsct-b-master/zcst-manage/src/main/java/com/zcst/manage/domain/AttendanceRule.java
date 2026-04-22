package com.zcst.manage.domain;

import com.zcst.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考勤规则配置表
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class AttendanceRule extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 规则 ID
     */
    private Integer ruleId;

    /**
     * 场馆 ID
     */
    private Integer venueId;

    /**
     * 场馆名称
     */
    private String venueName;

    /**
     * 迟到阈值（分钟）
     */
    private Integer lateThresholdMinutes;

    /**
     * 早退阈值（分钟）
     */
    private Integer earlyLeaveThresholdMinutes;

    /**
     * 最早提前打卡时间（分钟）
     */
    private Integer minCheckinBeforeMinutes;

    /**
     * 最晚延后打卡时间（分钟）
     */
    private Integer maxCheckinAfterMinutes;

    /**
     * 状态（0 正常 1 停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}
