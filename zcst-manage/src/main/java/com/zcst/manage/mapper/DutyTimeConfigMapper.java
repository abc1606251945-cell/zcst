package com.zcst.manage.mapper;

import com.zcst.manage.domain.DutyTimeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 值班时间配置Mapper接口
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Mapper
public interface DutyTimeConfigMapper
{
    /**
     * 查询值班时间配置列表
     * 
     * @param dutyTimeConfig 值班时间配置
     * @return 值班时间配置集合
     */
    public List<DutyTimeConfig> selectDutyTimeConfigList(DutyTimeConfig dutyTimeConfig);

    /**
     * 按场馆ID查询值班时间配置
     * 
     * @param venueId 场馆ID
     * @return 值班时间配置集合
     */
    public List<DutyTimeConfig> selectDutyTimeConfigByVenueId(@Param("venueId") Integer venueId);

    /**
     * 查询值班时间配置详细信息
     * 
     * @param configId 配置ID
     * @return 值班时间配置
     */
    public DutyTimeConfig selectDutyTimeConfigByConfigId(@Param("configId") Integer configId);

    /**
     * 新增值班时间配置
     * 
     * @param dutyTimeConfig 值班时间配置
     * @return 结果
     */
    public int insertDutyTimeConfig(DutyTimeConfig dutyTimeConfig);

    /**
     * 修改值班时间配置
     * 
     * @param dutyTimeConfig 值班时间配置
     * @return 结果
     */
    public int updateDutyTimeConfig(DutyTimeConfig dutyTimeConfig);

    /**
     * 删除值班时间配置
     * 
     * @param configId 配置ID
     * @return 结果
     */
    public int deleteDutyTimeConfigByConfigId(@Param("configId") Integer configId);

    /**
     * 批量删除值班时间配置
     * 
     * @param configIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteDutyTimeConfigByConfigIds(@Param("configIds") Integer[] configIds);

    public int countByVenueAndTime(@Param("venueId") Integer venueId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    public int countByVenueAndTimeExcludeId(@Param("configId") Integer configId, @Param("venueId") Integer venueId, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
