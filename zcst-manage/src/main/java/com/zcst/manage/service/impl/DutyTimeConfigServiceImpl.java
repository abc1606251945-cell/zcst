package com.zcst.manage.service.impl;

import com.zcst.manage.domain.DutyTimeConfig;
import com.zcst.manage.mapper.DutyTimeConfigMapper;
import com.zcst.manage.service.IDutyTimeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        dutyTimeConfig.setIsEnable(1); // 默认启用
        return dutyTimeConfigMapper.insertDutyTimeConfig(dutyTimeConfig);
    }

    @Override
    public int updateDutyTimeConfig(DutyTimeConfig dutyTimeConfig)
    {
        return dutyTimeConfigMapper.updateDutyTimeConfig(dutyTimeConfig);
    }

    @Override
    public int deleteDutyTimeConfigByConfigId(Integer configId)
    {
        return dutyTimeConfigMapper.deleteDutyTimeConfigByConfigId(configId);
    }

    @Override
    public int deleteDutyTimeConfigByConfigIds(Integer[] configIds)
    {
        return dutyTimeConfigMapper.deleteDutyTimeConfigByConfigIds(configIds);
    }
}
