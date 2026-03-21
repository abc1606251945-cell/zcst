package com.zcst.manage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 值班时间配置表
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Data
public class DutyTimeConfig
{
    /** 配置ID */
    private Integer configId;

    /** 场馆ID */
    private Integer venueId;

    /** 开始时间 */
    private String startTime;

    /** 结束时间 */
    private String endTime;

    /** 是否启用 */
    private Integer isEnable;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
}
