package com.zcst.manage.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import com.zcst.common.core.page.PageDomain;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.common.core.domain.model.LoginUser;
import com.zcst.common.core.domain.entity.SysRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 场馆信息管理 Controller
 * 
 * @author ji
 * @date 2026-03-19
 */
@RestController
@RequestMapping("/manage/venue")
public class VenueController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(VenueController.class);
    
    @Autowired
    private IVenueService venueService;
    
    /**
     * 从用户角色中获取场馆 ID
     * 使用 sys_role 表的 venue_id 字段
     */
    private Integer getVenueIdFromUserRoles() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || loginUser.getUser().getRoles() == null) {
            return null;
        }
        
        return loginUser.getUser().getRoles().stream()
            .filter(role -> role.getVenueId() != null)
            .findFirst()
            .map(SysRole::getVenueId)
            .orElse(null);
    }
    
    /**
     * 判断当前用户是否为超级管理员
     */
    private boolean isSuperAdmin() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return false;
        }
        return "admin".equals(loginUser.getUser().getUserName());
    }

    /**
     * 查询场馆信息管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:venue:list')")
    @GetMapping("/list")
    public TableDataInfo list(Venue venue)
    {
        // 非超级管理员只能查看自己场馆的信息
        if (!isSuperAdmin()) {
            Integer venueId = getVenueIdFromUserRoles();
            if (venueId != null) {
                venue.setVenueId(venueId.longValue());
                log.info("场馆管理员查询场馆列表，venueId: {}", venueId);
            }
        }
        PageDomain pageDomain = getPageDomain();
        return getDataTable(venueService.selectVenueListWithPage(venue, pageDomain.getPageNum(), pageDomain.getPageSize()));
    }

    @GetMapping("/simpleList")
    public AjaxResult simpleList()
    {
        List<Venue> list = venueService.selectVenueList(new Venue());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Venue v : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("venueId", v.getVenueId());
            item.put("venueName", v.getVenueName());
            result.add(item);
        }
        return success(result);
    }

    /**
     * 导出场馆信息管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:venue:export')")
    @Log(title = "场馆信息管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Venue venue)
    {
        // 非超级管理员只能导出自己场馆的信息
        if (!isSuperAdmin()) {
            Integer venueId = getVenueIdFromUserRoles();
            if (venueId != null) {
                venue.setVenueId(venueId.longValue());
                log.info("场馆管理员导出场馆列表，venueId: {}", venueId);
            }
        }
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
        // 非超级管理员只能查看自己场馆的详细信息
        if (!isSuperAdmin()) {
            Integer currentVenueId = getVenueIdFromUserRoles();
            if (currentVenueId != null && !currentVenueId.equals(venueId.intValue())) {
                return AjaxResult.error("无权限查看其他场馆信息");
            }
            log.info("场馆管理员查看场馆详情，venueId: {}", venueId);
        }
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
        // 非超级管理员添加场馆时自动设置场馆 ID
        if (!isSuperAdmin()) {
            Integer venueId = getVenueIdFromUserRoles();
            if (venueId != null) {
                venue.setVenueId(venueId.longValue());
                log.info("场馆管理员添加场馆，venueId: {}", venueId);
            }
        }
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
        // 非超级管理员只能修改自己场馆的信息
        if (!isSuperAdmin()) {
            Integer currentVenueId = getVenueIdFromUserRoles();
            if (currentVenueId != null) {
                venue.setVenueId(currentVenueId.longValue());
                log.info("场馆管理员修改场馆，venueId: {}", currentVenueId);
            }
        }
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
        // 非超级管理员只能删除自己场馆的信息
        if (!isSuperAdmin()) {
            Integer currentVenueId = getVenueIdFromUserRoles();
            if (currentVenueId != null) {
                // 验证要删除的场馆是否属于本场馆管理员
                for (Long venueId : venueIds) {
                    if (!currentVenueId.equals(venueId.intValue())) {
                        return AjaxResult.error("无权限删除其他场馆");
                    }
                }
                log.info("场馆管理员删除场馆，venueId: {}", currentVenueId);
            }
        }
        return toAjax(venueService.deleteVenueByVenueIds(venueIds));
    }
}
