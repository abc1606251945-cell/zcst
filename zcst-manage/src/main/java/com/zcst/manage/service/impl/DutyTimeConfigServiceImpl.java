package com.zcst.manage.service.impl;

import com.zcst.manage.domain.DutyTimeConfig;
import com.zcst.manage.mapper.DutyScheduleMapper;
import com.zcst.manage.mapper.DutyTimeConfigMapper;
import com.zcst.manage.service.IDutyTimeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 值班时间配置Service实现
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Service
public class DutyTimeConfigServiceImpl implements IDutyTimeConfigService
{
    @Autowired
    private DutyTimeConfigMapper dutyTimeConfigMapper;

    @Autowired
    private DutyScheduleMapper dutyScheduleMapper;

    @Override
    public List<DutyTimeConfig> selectDutyTimeConfigList(DutyTimeConfig dutyTimeConfig)
    {
        return dutyTimeConfigMapper.selectDutyTimeConfigList(dutyTimeConfig);
    }

    @Override
    public List<DutyTimeConfig> selectDutyTimeConfigByVenueId(Integer venueId)
    {
        return dutyTimeConfigMapper.selectDutyTimeConfigByVenueId(venueId);
    }

    @Override
    public DutyTimeConfig selectDutyTimeConfigByConfigId(Integer configId)
    {
        return dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(configId);
    }

    @Override
    public int insertDutyTimeConfig(DutyTimeConfig dutyTimeConfig)
    {
        // 格式化时间，防止前端传ISO格式
        dutyTimeConfig.setStartTime(formatTime(dutyTimeConfig.getStartTime()));
        dutyTimeConfig.setEndTime(formatTime(dutyTimeConfig.getEndTime()));
        dutyTimeConfig.setIsEnable(1); // 默认启用
        return dutyTimeConfigMapper.insertDutyTimeConfig(dutyTimeConfig);
    }

    @Override
    @Transactional
    public int updateDutyTimeConfig(DutyTimeConfig dutyTimeConfig)
    {
        // 格式化时间，防止前端传ISO格式
        dutyTimeConfig.setStartTime(formatTime(dutyTimeConfig.getStartTime()));
        dutyTimeConfig.setEndTime(formatTime(dutyTimeConfig.getEndTime()));
        
        // 检查是否从启用变为禁用
        DutyTimeConfig oldConfig = dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(dutyTimeConfig.getConfigId());
        Integer oldEnable = oldConfig != null ? oldConfig.getIsEnable() : null;
        Integer newEnable = dutyTimeConfig.getIsEnable();
        if (Integer.valueOf(1).equals(oldEnable) && Integer.valueOf(0).equals(newEnable)) {
            // 当从启用变为禁用时，删除该时段的所有值班信息
            dutyScheduleMapper.deleteDutyScheduleByVenueAndTime(dutyTimeConfig.getVenueId(), normalizeTime(dutyTimeConfig.getStartTime()), normalizeTime(dutyTimeConfig.getEndTime()));
        }
        
        return dutyTimeConfigMapper.updateDutyTimeConfig(dutyTimeConfig);
    }

    /**
     * 格式化时间字符串，如果是ISO格式则提取时间部分
     */
    private String formatTime(String timeStr) {
        if (timeStr != null && timeStr.contains("T")) {
            // ISO格式: 2026-03-22T00:00:53.000Z
            try {
                Instant instant = Instant.parse(timeStr);
                return DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of("Asia/Shanghai")).format(instant) + ":00";
            } catch (Exception ignored) {
            }
            String[] parts = timeStr.split("T");
            if (parts.length > 1) {
                String timePart = parts[1];
                if (timePart.contains(".")) {
                    return timePart.split("\\.")[0];
                }
                if (timePart.endsWith("Z")) {
                    return timePart.substring(0, timePart.length() - 1);
                }
                return timePart;
            }
        }
        if (timeStr != null && timeStr.length() == 5) {
            return timeStr + ":00";
        }
        if (timeStr != null && timeStr.length() == 8 && timeStr.charAt(2) == ':' && timeStr.charAt(5) == ':') {
            return timeStr.substring(0, 6) + "00";
        }
        return timeStr;
    }

    private String normalizeTime(String timeStr)
    {
        if (timeStr == null) {
            return null;
        }
        String t = timeStr.trim();
        if (t.length() == 5) {
            return t + ":00";
        }
        return t;
    }

    @Override
    @Transactional
    public int deleteDutyTimeConfigByConfigId(Integer configId)
    {
        DutyTimeConfig config = dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(configId);
        if (config != null) {
            dutyScheduleMapper.deleteDutyScheduleByVenueAndTime(config.getVenueId(), normalizeTime(config.getStartTime()), normalizeTime(config.getEndTime()));
        }
        return dutyTimeConfigMapper.deleteDutyTimeConfigByConfigId(configId);
    }

    @Override
    @Transactional
    public int deleteDutyTimeConfigByConfigIds(Integer[] configIds)
    {
        if (configIds != null) {
            for (Integer configId : configIds) {
                DutyTimeConfig config = dutyTimeConfigMapper.selectDutyTimeConfigByConfigId(configId);
                if (config != null) {
                    dutyScheduleMapper.deleteDutyScheduleByVenueAndTime(config.getVenueId(), normalizeTime(config.getStartTime()), normalizeTime(config.getEndTime()));
                }
            }
        }
        return dutyTimeConfigMapper.deleteDutyTimeConfigByConfigIds(configIds);
    }
}
