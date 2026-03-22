package com.zcst.manage.domain;

import com.zcst.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 值班表
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Data
public class DutySchedule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 值班表ID */
    private Integer dutyId;

    /** 学号 */
    private String studentId;

    /** 姓名 */
    private String studentName;

    /** 性别 */
    private String gender;

    /** 手机号码 */
    private String studentPhone;

    /** 场馆ID */
    private Integer venueId;

    /** 值班开始时间 */
    private Date startTime;

    /** 值班结束时间 */
    private Date endTime;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private Date createdAt;

    /** 更新时间 */
    private Date updatedAt;
}
