package com.zcst.manage.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import com.zcst.manage.mapper.VenueMapper;
import com.zcst.manage.domain.Venue;
import com.zcst.manage.service.IVenueService;

/**
 * 场馆信息管理Service业务层处理
 * 
 * @author ji
 * @date 2026-03-19
 */
@Service
public class VenueServiceImpl implements IVenueService 
{
    @Autowired
    private VenueMapper venueMapper;

    /**
     * 查询场馆信息管理
     * 
     * @param venueId 场馆信息管理主键
     * @return 场馆信息管理
     */
    @Override
    public Venue selectVenueByVenueId(Long venueId)
    {
        return venueMapper.selectVenueByVenueId(venueId);
    }

    /**
     * 查询场馆信息管理列表
     * 
     * @param venue 场馆信息管理
     * @return 场馆信息管理
     */
    @Override
    public List<Venue> selectVenueList(Venue venue)
    {
        return venueMapper.selectVenueList(venue);
    }

    /**
     * 查询场馆信息管理列表（带分页信息）
     * 
     * @param venue 场馆信息管理
     * @return 包含分页信息的场馆信息管理列表
     */
    @Override
    public PageInfo<Venue> selectVenueListWithPage(Venue venue)
    {
        PageHelper.startPage(1, 10);
        List<Venue> list = venueMapper.selectVenueList(venue);
        return new PageInfo<>(list);
    }

    /**
     * 新增场馆信息管理
     * 
     * @param venue 场馆信息管理
     * @return 结果
     */
    @Override
    public int insertVenue(Venue venue)
    {
        return venueMapper.insertVenue(venue);
    }

    /**
     * 修改场馆信息管理
     * 
     * @param venue 场馆信息管理
     * @return 结果
     */
    @Override
    public int updateVenue(Venue venue)
    {
        return venueMapper.updateVenue(venue);
    }

    /**
     * 批量删除场馆信息管理
     * 
     * @param venueIds 需要删除的场馆信息管理主键
     * @return 结果
     */
    @Override
    public int deleteVenueByVenueIds(Long[] venueIds)
    {
        return venueMapper.deleteVenueByVenueIds(venueIds);
    }

    /**
     * 删除场馆信息管理信息
     * 
     * @param venueId 场馆信息管理主键
     * @return 结果
     */
    @Override
    public int deleteVenueByVenueId(Long venueId)
    {
        return venueMapper.deleteVenueByVenueId(venueId);
    }
}
