package com.zcst.manage.domain.Vo;

import com.zcst.manage.domain.AttendanceStatistics;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考勤统计 Vo
 * 用于返回前端展示数据，包含关联信息
 * 
 * @author zcst
 * @date 2026-03
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class AttendanceStatisticsVo extends AttendanceStatistics {
    private static final long serialVersionUID = 1L;

    /** 学生姓名 */
    private String studentName;

    /** 场馆名称 */
    private String venueName;
}
