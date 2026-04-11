package com.zcst.manage.domain.Vo;

import com.zcst.manage.domain.DutySchedule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

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

    /** 场馆名称 */
    private String venueName;

    private Boolean canCheckIn;

    private Boolean canCheckOut;

    private Long startTimeTs;

    private Long endTimeTs;

    private Long recordId;

    private Date checkInTime;

    private Date checkOutTime;
}
