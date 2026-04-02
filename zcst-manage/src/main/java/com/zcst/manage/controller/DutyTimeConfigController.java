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
import com.zcst.manage.service.IDutyScheduleService;
import com.zcst.manage.service.IDutyTimeConfigService;
import com.zcst.manage.service.IVenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 值班时间配置 Controller
 * 
 * @author zcst
 * @date 2026-03-21
 */
@RestController
@RequestMapping("/manage/dutyTimeConfig")
public class DutyTimeConfigController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(DutyTimeConfigController.class);
    @Autowired
    private IDutyTimeConfigService dutyTimeConfigService;

    @Autowired
    private IVenueService venueService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 判断是否为超级管理员
     */
    private boolean isSuperAdmin(SysUser user) {
        // 超级管理员用户 ID 通常为 1，或者拥有 admin 角色
        return user.getUserId() == 1L || SecurityUtils.isAdmin(user.getUserId());
    }

    /**
     * 从用户角色中获取场馆 ID
     * 使用 sys_role 表的 venue_id 字段
     */
    private Integer getVenueIdFromUserRoles(SysUser user) {
        if (user == null || user.getRoles() == null) {
            return null;
        }
        
        return user.getRoles().stream()
            .filter(role -> role.getVenueId() != null)
            .findFirst()
            .map(role -> role.getVenueId())
            .orElse(null);
    }

    /**
     * 查询值班时间配置列表
     */
    @PreAuthorize("@ss.hasPermi('manage:dutyTimeConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(DutyTimeConfig dutyTimeConfig)
    {
        // 获取当前用户
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        
        // 非超级管理员只能查看自己场馆的值班时间配置
        if (!isSuperAdmin(currentUser)) {
            Integer venueId = getVenueIdFromUserRoles(currentUser);
            if (venueId != null) {
                dutyTimeConfig.setVenueId(venueId);
                log.info("场馆管理员查询值班时间配置，venueId: {}", venueId);
            }
        }
        startPage();
        List<DutyTimeConfig> list = dutyTimeConfigService.selectDutyTimeConfigList(dutyTimeConfig);
        return getDataTable(list);
    }

    /**
     * 按场馆 ID 查询值班时间配置
     */
    @GetMapping("/byVenue/{venueId}")
    public AjaxResult getByVenueId(@PathVariable Integer venueId)
    {
        // 验证权限：非超级管理员只能查询自己场馆的值班时间配置
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            Integer userVenueId = getVenueIdFromUserRoles(currentUser);
            if (userVenueId != null && !userVenueId.equals(venueId)) {
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
            Integer userVenueId = getVenueIdFromUserRoles(currentUser);
            if (userVenueId != null && !userVenueId.equals(dutyTimeConfig.getVenueId())) {
                return AjaxResult.error("无权限查看其他场馆的值班时间配置");
            }
        }
        return success(dutyTimeConfig);
    }

    @Autowired
    private IDutyScheduleService dutyScheduleService;

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
            Integer userVenueId = getVenueIdFromUserRoles(currentUser);
            if (userVenueId != null && !userVenueId.equals(dutyTimeConfig.getVenueId())) {
                return AjaxResult.error("无权限添加其他场馆的值班时间配置");
            }
        }
        int result = dutyTimeConfigService.insertDutyTimeConfig(dutyTimeConfig);
        if (result > 0) {
            Integer venueId = dutyTimeConfig.getVenueId();
            threadPoolTaskExecutor.execute(() -> {
                try {
                    dutyScheduleService.autoScheduleByConfig(venueId, new java.util.Date(), 19);
                } catch (Exception e) {
                    log.error("新增值班时段后自动重排失败，venueId={}", venueId, e);
                }
            });
        }
        return toAjax(result);
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
        if (oldConfig == null) {
            return AjaxResult.error("值班时间配置不存在");
        }
        if (!isSuperAdmin(currentUser)) {
            Integer userVenueId = getVenueIdFromUserRoles(currentUser);
            if (userVenueId != null && !userVenueId.equals(oldConfig.getVenueId())) {
                return AjaxResult.error("无权限修改其他场馆的值班时间配置");
            }
        }

        if (dutyTimeConfig.getVenueId() == null) {
            dutyTimeConfig.setVenueId(oldConfig.getVenueId());
        }
        if (dutyTimeConfig.getIsEnable() == null) {
            dutyTimeConfig.setIsEnable(oldConfig.getIsEnable());
        }
        if (dutyTimeConfig.getStartTime() == null || dutyTimeConfig.getStartTime().trim().isEmpty()) {
            dutyTimeConfig.setStartTime(oldConfig.getStartTime());
        }
        if (dutyTimeConfig.getEndTime() == null || dutyTimeConfig.getEndTime().trim().isEmpty()) {
            dutyTimeConfig.setEndTime(oldConfig.getEndTime());
        }

        int result = dutyTimeConfigService.updateDutyTimeConfig(dutyTimeConfig);
        if (result > 0) {
            Integer venueId = dutyTimeConfig.getVenueId();
            threadPoolTaskExecutor.execute(() -> {
                try {
                    dutyScheduleService.autoScheduleByConfig(venueId, new java.util.Date(), 19);
                } catch (Exception e) {
                    log.error("修改值班时段后自动重排失败，venueId={}", venueId, e);
                }
            });
        }
        return toAjax(result);
    }

    /**
     * 删除值班时间配置
     */
    @PreAuthorize("@ss.hasPermi('manage:dutyTimeConfig:remove')")
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Integer[] configIds)
    {
        Set<Integer> venueIds = new HashSet<>();
        // 验证权限：非超级管理员只能删除自己场馆的值班时间配置
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            Integer userVenueId = getVenueIdFromUserRoles(currentUser);
            if (userVenueId != null) {
                for (Integer configId : configIds) {
                    DutyTimeConfig dutyTimeConfig = dutyTimeConfigService.selectDutyTimeConfigByConfigId(configId);
                    if (dutyTimeConfig != null) {
                        venueIds.add(dutyTimeConfig.getVenueId());
                    }
                    if (!userVenueId.equals(dutyTimeConfig.getVenueId())) {
                        return AjaxResult.error("无权限删除其他场馆的值班时间配置");
                    }
                }
            }
        } else {
            for (Integer configId : configIds) {
                DutyTimeConfig dutyTimeConfig = dutyTimeConfigService.selectDutyTimeConfigByConfigId(configId);
                if (dutyTimeConfig != null) {
                    venueIds.add(dutyTimeConfig.getVenueId());
                }
            }
        }
        int result = dutyTimeConfigService.deleteDutyTimeConfigByConfigIds(configIds);
        if (result > 0) {
            Date startDate = new Date();
            for (Integer venueId : venueIds) {
                threadPoolTaskExecutor.execute(() -> {
                    try {
                        dutyScheduleService.autoScheduleByConfig(venueId, startDate, 19);
                    } catch (Exception e) {
                        log.error("删除值班时段后自动重排失败，venueId={}", venueId, e);
                    }
                });
            }
        }
        return toAjax(result);
    }
}
