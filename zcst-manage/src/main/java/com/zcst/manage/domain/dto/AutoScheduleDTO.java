package com.zcst.manage.domain.dto;

import com.zcst.manage.service.IDutyScheduleService;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 自动排班DTO
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Data
public class AutoScheduleDTO
{
    /** 场馆ID */
    private Integer venueId;

    /** 开始日期 */
    private Date startDate;

    /** 结束日期 */
    private Date endDate;

    /** 时间段配置 */
    private List<IDutyScheduleService.TimeSlot> timeSlots;
}
