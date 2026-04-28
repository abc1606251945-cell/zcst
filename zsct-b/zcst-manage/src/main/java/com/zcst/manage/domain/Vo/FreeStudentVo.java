package com.zcst.manage.domain.Vo;

import lombok.Data;

@Data
public class FreeStudentVo
{
    private String studentId;

    private String studentName;

    private Integer venueId;

    private String venueName;

    private String period;

    private String dutyDate;

    private Long startTimeTs;

    private Long endTimeTs;
}
