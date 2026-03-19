package com.zcst.manage.service;

import java.util.List;
import com.zcst.manage.domain.Venue;

/**
 * 场馆信息管理Service接口
 * 
 * @author ji
 * @date 2026-03-19
 */
public interface IVenueService 
{
    /**
     * 查询场馆信息管理
     * 
     * @param venueId 场馆信息管理主键
     * @return 场馆信息管理
     */
    public Venue selectVenueByVenueId(Long venueId);

    /**
     * 查询场馆信息管理列表
     * 
     * @param venue 场馆信息管理
     * @return 场馆信息管理集合
     */
    public List<Venue> selectVenueList(Venue venue);

    /**
     * 新增场馆信息管理
     * 
     * @param venue 场馆信息管理
     * @return 结果
     */
    public int insertVenue(Venue venue);

    /**
     * 修改场馆信息管理
     * 
     * @param venue 场馆信息管理
     * @return 结果
     */
    public int updateVenue(Venue venue);

    /**
     * 批量删除场馆信息管理
     * 
     * @param venueIds 需要删除的场馆信息管理主键集合
     * @return 结果
     */
    public int deleteVenueByVenueIds(Long[] venueIds);

    /**
     * 删除场馆信息管理信息
     * 
     * @param venueId 场馆信息管理主键
     * @return 结果
     */
    public int deleteVenueByVenueId(Long venueId);
}
