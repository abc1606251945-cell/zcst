package com.zcst.manage.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=true)
public class Major extends com.zcst.common.core.domain.BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long majorId;

    private String majorName;

    private Long collegeId;

    private Date createdAt;

    private Date updatedAt;
}
