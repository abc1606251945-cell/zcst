package com.zcst.manage.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 自习场地占用情况查询 Service
 */
public interface IStudyRoomService
{
    /**
     * 查询指定日期范围内的自习场地占用记录。
     *
     * @param cjid 场地号，可为空
     * @param startDate 查询开始日期
     * @param endDate 查询结束日期
     * @return 占用记录，字段为 SYJSSJ 和 CJID
     */
    List<Map<String, String>> selectStudyRoomOccupancy(String cjid, LocalDate startDate, LocalDate endDate);
}
