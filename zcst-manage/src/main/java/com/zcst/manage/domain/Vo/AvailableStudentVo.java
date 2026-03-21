package com.zcst.manage.domain.Vo;

import lombok.Data;

/**
 * 可用学生Vo
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Data
public class AvailableStudentVo
{
    /** 学号 */
    private String studentId;

    /** 姓名 */
    private String name;

    /** 性别 */
    private String gender;

    /** 专业ID */
    private Long majorId;

    /** 年级 */
    private String grade;

    /** 手机号码 */
    private String phone;

    /** 场馆ID */
    private Long venueId;

    /** 总值班时长（毫秒） */
    private long totalDutyTime;

    /** 专业名称 */
    private String majorName;

    /** 场馆名称 */
    private String venueName;
}
