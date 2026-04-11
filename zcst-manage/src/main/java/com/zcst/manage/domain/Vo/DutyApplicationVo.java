package com.zcst.manage.domain.Vo;

import lombok.Data;

import java.util.Date;

@Data
public class DutyApplicationVo
{
    private String applicationType;

    private Integer id;

    private Integer dutyId;

    private String studentId;

    private String studentName;

    private String relatedStudentId;

    private String relatedStudentName;

    private Integer venueId;

    private String venueName;

    private Date startTime;

    private Date endTime;

    private Long startTimeTs;

    private Long endTimeTs;

    private String reason;

    private String proofImage;

    private String status;

    private String statusCode;

    private Date approveTime;

    private String approveRemark;

    private Long approveTimeTs;

    private Date cancelTime;

    private String cancelReason;

    private Long cancelTimeTs;

    private Date createdAt;

    private Long createdAtTs;
}
