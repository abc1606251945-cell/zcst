package com.zcst.manage.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zcst.common.annotation.Excel;
import com.zcst.common.core.domain.BaseEntity;

/**
 * 场馆信息管理对象 venue
 * 
 * @author ji
 * @date 2026-03-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class Venue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 场馆ID */
    private Long venueId;

    /** 场馆名称 */
    @Excel(name = "场馆名称")
    private String venueName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

}
