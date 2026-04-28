package com.zcst.manage.domain;

import com.zcst.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 学生课表
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class StudentSchedule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 课表ID */
    private Integer scheduleId;

    /** 学号 */
    private String studentId;

    /** 课程名称 */
    private String courseName;

    /** 上课地点 */
    private String location;

    /** 课程开始时间 */
    private Date startTime;

    /** 课程结束时间 */
    private Date endTime;

    /** 创建时间 */
    private Date createdAt;

    /** 更新时间 */
    private Date updatedAt;
}
