package com.zcst.manage.domain.dto;

import lombok.Data;

@Data
public class AutoScheduleByConfigDTO
{
    private Integer venueId;

    private String startDate;

    private String endDate;

    private Integer weeks;
}
