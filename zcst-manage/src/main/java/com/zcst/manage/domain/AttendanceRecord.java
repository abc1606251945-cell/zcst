package com.zcst.manage.domain;

import com.zcst.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 考勤记录表
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class AttendanceRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long recordId;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 值班表ID
     */
    private Integer dutyId;

    /**
     * 打卡时间
     */
    private Date checkInTime;

    /**
     * 状态（0正常 1迟到 2早退 3缺勤）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}