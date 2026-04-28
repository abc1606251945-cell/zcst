package com.zcst.manage.service.impl;

import com.zcst.manage.mapper.StudyRoomMapper;
import com.zcst.manage.service.IStudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 自习场地占用情况查询 Service 实现
 */
@Service
public class StudyRoomServiceImpl implements IStudyRoomService
{
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    @Autowired
    private StudyRoomMapper studyRoomMapper;

    @Override
    public List<Map<String, String>> selectStudyRoomOccupancy(String cjid, LocalDate startDate, LocalDate endDate)
    {
        Date queryStartTime = Date.from(startDate.atStartOfDay(ZONE_ID).toInstant());
        Date queryEndTime = Date.from(endDate.plusDays(1).atStartOfDay(ZONE_ID).toInstant());
        return studyRoomMapper.selectStudyRoomOccupancy(cjid, queryStartTime, queryEndTime);
    }
}
