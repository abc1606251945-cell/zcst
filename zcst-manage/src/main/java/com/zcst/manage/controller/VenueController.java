package com.zcst.manage.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zcst.common.annotation.Log;
import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.enums.BusinessType;
import com.zcst.manage.domain.Venue;
import com.zcst.manage.service.IVenueService;
import com.zcst.common.utils.poi.ExcelUtil;
import com.zcst.common.core.page.TableDataInfo;

/**
 * 场馆信息管理Controller
 * 
 * @author ji
 * @date 2026-03-19
 */
@RestController
@RequestMapping("/manage/venue")
public class VenueController extends BaseController
{
    @Autowired
    private IVenueService venueService;

    /**
     * 查询场馆信息管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:venue:list')")
    @GetMapping("/list")
    public TableDataInfo list(Venue venue)
    {
        return getDataTable(venueService.selectVenueListWithPage(venue));
    }

    /**
     * 导出场馆信息管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:venue:export')")
    @Log(title = "场馆信息管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Venue venue)
    {
        List<Venue> list = venueService.selectVenueList(venue);
        ExcelUtil<Venue> util = new ExcelUtil<Venue>(Venue.class);
        util.exportExcel(response, list, "场馆信息管理数据");
    }

    /**
     * 获取场馆信息管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:venue:query')")
    @GetMapping(value = "/{venueId}")
    public AjaxResult getInfo(@PathVariable("venueId") Long venueId)
    {
        return success(venueService.selectVenueByVenueId(venueId));
    }

    /**
     * 新增场馆信息管理
     */
    @PreAuthorize("@ss.hasPermi('manage:venue:add')")
    @Log(title = "场馆信息管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Venue venue)
    {
        return toAjax(venueService.insertVenue(venue));
    }

    /**
     * 修改场馆信息管理
     */
    @PreAuthorize("@ss.hasPermi('manage:venue:edit')")
    @Log(title = "场馆信息管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Venue venue)
    {
        return toAjax(venueService.updateVenue(venue));
    }

    /**
     * 删除场馆信息管理
     */
    @PreAuthorize("@ss.hasPermi('manage:venue:remove')")
    @Log(title = "场馆信息管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{venueIds}")
    public AjaxResult remove(@PathVariable Long[] venueIds)
    {
        return toAjax(venueService.deleteVenueByVenueIds(venueIds));
    }
}
