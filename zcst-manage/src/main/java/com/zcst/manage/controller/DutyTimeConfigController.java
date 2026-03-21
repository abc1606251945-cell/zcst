package com.zcst.manage.controller;

import com.zcst.common.annotation.RepeatSubmit;
import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.domain.entity.SysRole;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.manage.domain.DutyTimeConfig;
import com.zcst.manage.domain.Venue;
import com.zcst.manage.service.IDutyTimeConfigService;
import com.zcst.manage.service.IVenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 值班时间配置Controller
 * 
 * @author zcst
 * @date 2026-03-21
 */
@RestController
@RequestMapping("/manage/dutyTimeConfig")
public class DutyTimeConfigController extends BaseController
{
    @Autowired
    private IDutyTimeConfigService dutyTimeConfigService;

    @Autowired
    private IVenueService venueService;

    /**
     * 判断是否为超级管理员
     */
    private boolean isSuperAdmin(SysUser user) {
        // 超级管理员用户ID通常为1，或者拥有admin角色
        return user.getUserId() == 1L || SecurityUtils.isAdmin(user.getUserId());
    }

    /**
     * 从角色名称中获取场馆ID
     */
    private Long getVenueIdByRoleName(String roleName) {
        // 从角色名称中提取场馆名称
        String venueName = roleName.replace("管理员", "").trim();
        if (!venueName.isEmpty()) {
            // 从数据库中查询场馆信息
            List<Venue> venues = venueService.selectVenueList(new Venue());
            for (Venue venue : venues) {
                if (venue.getVenueName().contains(venueName) || venueName.contains(venue.getVenueName())) {
                    return venue.getVenueId();
                }
            }
        }
        return null;
    }

    /**
     * 查询值班时间配置列表
     */
    @PreAuthorize("@ss.hasPermi('manage:dutyTimeConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(DutyTimeConfig dutyTimeConfig)
    {
        // 获取当前用户的场馆ID
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        // 非超级管理员只能查看自己场馆的值班时间配置
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中动态获取场馆ID
                        Long venueId = getVenueIdByRoleName(roleName);
                        if (venueId != null) {
                            dutyTimeConfig.setVenueId(venueId.intValue());
                        }
                        break;
                    }
                }
            }
        }
        startPage();
        List<DutyTimeConfig> list = dutyTimeConfigService.selectDutyTimeConfigList(dutyTimeConfig);
        return getDataTable(list);
    }

    /**
     * 按场馆ID查询值班时间配置
     */
    @GetMapping("/byVenue/{venueId}")
    public AjaxResult getByVenueId(@PathVariable Integer venueId)
    {
        // 验证权限：非超级管理员只能查询自己场馆的值班时间配置
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Long userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中动态获取场馆ID
                        userVenueId = getVenueIdByRoleName(roleName);
                        break;
                    }
                }
            }
            if (userVenueId != null && !userVenueId.equals(Long.valueOf(venueId))) {
                return AjaxResult.error("无权限查询其他场馆的值班时间配置");
            }
        }
        List<DutyTimeConfig> list = dutyTimeConfigService.selectDutyTimeConfigByVenueId(venueId);
        return success(list);
    }

    /**
     * 获取值班时间配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:dutyTimeConfig:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable Integer configId)
    {
        DutyTimeConfig dutyTimeConfig = dutyTimeConfigService.selectDutyTimeConfigByConfigId(configId);
        // 验证权限：非超级管理员只能查看自己场馆的值班时间配置
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Long userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中动态获取场馆ID
                        userVenueId = getVenueIdByRoleName(roleName);
                        break;
                    }
                }
            }
            if (userVenueId != null && !userVenueId.equals(Long.valueOf(dutyTimeConfig.getVenueId()))) {
                return AjaxResult.error("无权限查看其他场馆的值班时间配置");
            }
        }
        return success(dutyTimeConfig);
    }

    /**
     * 新增值班时间配置
     */
    @PreAuthorize("@ss.hasPermi('manage:dutyTimeConfig:add')")
    @RepeatSubmit
    @PostMapping
    public AjaxResult add(@Validated @RequestBody DutyTimeConfig dutyTimeConfig)
    {
        // 验证权限：非超级管理员只能添加自己场馆的值班时间配置
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Long userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中动态获取场馆ID
                        userVenueId = getVenueIdByRoleName(roleName);
                        break;
                    }
                }
            }
            if (userVenueId != null && !userVenueId.equals(Long.valueOf(dutyTimeConfig.getVenueId()))) {
                return AjaxResult.error("无权限添加其他场馆的值班时间配置");
            }
        }
        return toAjax(dutyTimeConfigService.insertDutyTimeConfig(dutyTimeConfig));
    }

    /**
     * 修改值班时间配置
     */
    @PreAuthorize("@ss.hasPermi('manage:dutyTimeConfig:edit')")
    @RepeatSubmit
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody DutyTimeConfig dutyTimeConfig)
    {
        // 验证权限：非超级管理员只能修改自己场馆的值班时间配置
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        DutyTimeConfig oldConfig = dutyTimeConfigService.selectDutyTimeConfigByConfigId(dutyTimeConfig.getConfigId());
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Long userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中动态获取场馆ID
                        userVenueId = getVenueIdByRoleName(roleName);
                        break;
                    }
                }
            }
            if (userVenueId != null && !userVenueId.equals(Long.valueOf(oldConfig.getVenueId()))) {
                return AjaxResult.error("无权限修改其他场馆的值班时间配置");
            }
        }
        return toAjax(dutyTimeConfigService.updateDutyTimeConfig(dutyTimeConfig));
    }

    /**
     * 删除值班时间配置
     */
    @PreAuthorize("@ss.hasPermi('manage:dutyTimeConfig:remove')")
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Integer[] configIds)
    {
        // 验证权限：非超级管理员只能删除自己场馆的值班时间配置
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Long userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中动态获取场馆ID
                        userVenueId = getVenueIdByRoleName(roleName);
                        break;
                    }
                }
            }
            if (userVenueId != null) {
                for (Integer configId : configIds) {
                    DutyTimeConfig dutyTimeConfig = dutyTimeConfigService.selectDutyTimeConfigByConfigId(configId);
                    if (!userVenueId.equals(Long.valueOf(dutyTimeConfig.getVenueId()))) {
                        return AjaxResult.error("无权限删除其他场馆的值班时间配置");
                    }
                }
            }
        }
        return toAjax(dutyTimeConfigService.deleteDutyTimeConfigByConfigIds(configIds));
    }
}
