package com.zcst.manage.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 自习场地占用情况查询 Mapper
 */
public interface StudyRoomMapper
{
    /**
     * 查询指定时间范围内发生重叠的场地占用记录。
     *
     * @param cjid 场地号，可为空
     * @param queryStartTime 查询开始时间
     * @param queryEndTime 查询结束时间，使用开区间
     * @return 字段为 SYJSSJ 和 CJID 的记录列表
     */
    List<Map<String, String>> selectStudyRoomOccupancy(@Param("cjid") String cjid,
                                                       @Param("queryStartTime") Date queryStartTime,
                                                       @Param("queryEndTime") Date queryEndTime);
}
