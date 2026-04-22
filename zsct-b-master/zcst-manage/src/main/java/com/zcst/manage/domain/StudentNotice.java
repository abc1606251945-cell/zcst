package com.zcst.manage.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=true)
/**
 * 学生通知实体
 *
 * 对应表：student_notice
 * 用途：小程序“通知中心”，支持按人已读/未读。
 */
public class StudentNotice extends com.zcst.common.core.domain.BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 通知ID（主键） */
    private Long noticeId;

    /** 接收人学号 */
    private String studentId;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 类型（如 system/approve/duty 等） */
    private String type;

    /** 业务类型（如 leave/swap/duty 等） */
    private String bizType;

    /** 业务ID（如申请ID、值班ID等） */
    private String bizId;

    /** 发布时间 */
    private Date publishTime;

    private Long publishTimeTs;

    /** 是否已读：0 未读 / 1 已读 */
    private String readFlag;

    /** 已读时间 */
    private Date readTime;

    private Long readTimeTs;

    /** 创建时间（业务字段） */
    private Date createdAt;

    private Long createdAtTs;

    /** 更新时间（业务字段） */
    private Date updatedAt;

    private Long updatedAtTs;
}
