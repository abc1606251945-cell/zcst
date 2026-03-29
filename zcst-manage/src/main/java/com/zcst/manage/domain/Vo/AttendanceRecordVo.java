package com.zcst.manage.domain.Vo;

import com.zcst.manage.domain.AttendanceRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 考勤记录 Vo
 * 用于返回前端展示数据，包含关联信息
 * 
 * @author zcst
 * @date 2026-03
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class AttendanceRecordVo extends AttendanceRecord {
    private static final long serialVersionUID = 1L;

    /** 学生姓名 */
    private String studentName;

    /** 场馆名称 */
    private String venueName;

    /** 值班开始时间 */
    private Date startTime;

    /** 值班结束时间 */
    private Date endTime;
}
