package com.zcst.manage.domain.Vo;

import com.zcst.manage.domain.DutySchedule;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 值班表Vo
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class DutyScheduleVo extends DutySchedule
{
    private static final long serialVersionUID = 1L;

    /** 学生姓名 */
    private String studentName;

    /** 场馆名称 */
    private String venueName;
}
