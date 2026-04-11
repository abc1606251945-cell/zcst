package com.zcst.manage.domain.dto;

import lombok.Data;

@Data
/**
 * 学生端提交值班申请 DTO
 *
 * applicationType：
 * - leave：请假
 * - swap：调班（语义为替班）
 */
public class SubmitDutyApplicationDTO
{
    /** leave | swap */
    private String applicationType;

    /** 值班ID（必填；必须属于当前登录学生） */
    private Integer dutyId;

    /** 申请原因（必填） */
    private String reason;

    /** 替班人学号（swap 必填） */
    private String relatedStudentId;

    /** 请假类型（leave 可选：0病假 1事假 2其他） */
    private String leaveType;

    /** 证明材料图片 URL（leave 可选） */
    private String proofImage;
}
