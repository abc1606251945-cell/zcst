package com.zcst.manage.controller;

import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.domain.Vo.AvailableStudentVo;
import com.zcst.manage.domain.Venue;
import com.zcst.manage.domain.dto.AutoScheduleDTO;
import com.zcst.manage.service.IDutyScheduleService;
import com.zcst.manage.service.IVenueService;
import com.zcst.common.core.domain.entity.SysRole;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.annotation.Log;
import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.enums.BusinessType;
import com.zcst.common.utils.SecurityUtils;
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

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 场馆值班表管理 Controller
 *
 * @author zcst
 * @date 2026-03-21
 */
@RestController
@RequestMapping("/manage/dutySchedule")
public class DutyScheduleController extends BaseController
{
    @Autowired
    private IDutyScheduleService dutyScheduleService;

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
     * 根据角色名称获取场馆ID
     */
    private Integer getVenueIdByRoleName(String roleName) {
        // 获取所有场馆
        List<Venue> venues = venueService.selectVenueList(new Venue());
        for (Venue venue : venues) {
            if (roleName.contains(venue.getVenueName())) {
                return venue.getVenueId().intValue();
            }
        }
        return null;
    }

    /**
     * 查询值班表列表
     */
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:list')")
    @GetMapping("/list")
    public TableDataInfo list(DutySchedule dutySchedule)
    {
        // 获取当前用户的场馆ID
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        // 非超级管理员只能查看自己场馆的值班信息
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中提取场馆ID
                        Integer venueId = getVenueIdByRoleName(roleName);
                        if (venueId != null) {
                            dutySchedule.setVenueId(venueId);
                        }
                        break;
                    }
                }
            }
        }
        startPage();
        List<DutySchedule> list = dutyScheduleService.selectDutyScheduleList(dutySchedule);
        return getDataTable(list);
    }

    /**
     * 获取值班表详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:query')")
    @GetMapping(value = "/{dutyId}")
    public AjaxResult getInfo(@PathVariable("dutyId") Integer dutyId)
    {
        DutySchedule dutySchedule = dutyScheduleService.selectDutyScheduleByDutyId(dutyId);
        // 验证权限：非超级管理员只能查看自己场馆的值班信息
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Integer userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中提取场馆ID
                        userVenueId = getVenueIdByRoleName(roleName);
                        break;
                    }
                }
            }
            if (userVenueId != null && !userVenueId.equals(dutySchedule.getVenueId())) {
                return AjaxResult.error("无权限查看其他场馆的值班信息");
            }
        }
        return success(dutySchedule);
    }

    /**
     * 新增值班表
     */
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:add')")
    @Log(title = "场馆值班表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DutySchedule dutySchedule)
    {
        // 验证权限：非超级管理员只能添加自己场馆的值班信息
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Integer userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中提取场馆ID
                        userVenueId = getVenueIdByRoleName(roleName);
                        break;
                    }
                }
            }
            if (userVenueId != null && !userVenueId.equals(dutySchedule.getVenueId())) {
                return AjaxResult.error("无权限添加其他场馆的值班信息");
            }
        }
        return toAjax(dutyScheduleService.insertDutySchedule(dutySchedule));
    }

    /**
     * 修改值班表
     */
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:edit')")
    @Log(title = "场馆值班表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DutySchedule dutySchedule)
    {
        // 验证权限：非超级管理员只能修改自己场馆的值班信息
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        DutySchedule oldDuty = dutyScheduleService.selectDutyScheduleByDutyId(dutySchedule.getDutyId());
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Integer userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中提取场馆ID
                        userVenueId = getVenueIdByRoleName(roleName);
                        break;
                    }
                }
            }
            if (userVenueId != null && !userVenueId.equals(oldDuty.getVenueId())) {
                return AjaxResult.error("无权限修改其他场馆的值班信息");
            }
        }
        return toAjax(dutyScheduleService.updateDutySchedule(dutySchedule));
    }

    /**
     * 删除值班表
     */
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:remove')")
    @Log(title = "场馆值班表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dutyIds}")
    public AjaxResult remove(@PathVariable Integer[] dutyIds)
    {
        // 验证权限：非超级管理员只能删除自己场馆的值班信息
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Integer userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中提取场馆ID
                        userVenueId = getVenueIdByRoleName(roleName);
                        break;
                    }
                }
            }
            if (userVenueId != null) {
                for (Integer dutyId : dutyIds) {
                    DutySchedule dutySchedule = dutyScheduleService.selectDutyScheduleByDutyId(dutyId);
                    if (!userVenueId.equals(dutySchedule.getVenueId())) {
                        return AjaxResult.error("无权限删除其他场馆的值班信息");
                    }
                }
            }
        }
        return toAjax(dutyScheduleService.deleteDutyScheduleByDutyIds(dutyIds));
    }

    /**
     * 获取可用学生列表
     */
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:list')")
    @PostMapping("/availableStudents")
    public AjaxResult getAvailableStudents(@RequestBody Map<String, Object> params)
    {
        Integer venueId = (Integer) params.get("venueId");
        Date startTime = (Date) params.get("startTime");
        Date endTime = (Date) params.get("endTime");

        // 验证权限：非超级管理员只能查询自己场馆的学生
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Integer userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中提取场馆ID
                        if (roleName.contains("思齐馆")) {
                            userVenueId = 1;
                        } else if (roleName.contains("弘毅馆")) {
                            userVenueId = 2;
                        } else if (roleName.contains("心缘馆")) {
                            userVenueId = 3;
                        } else if (roleName.contains("笃学馆")) {
                            userVenueId = 4;
                        } else if (roleName.contains("知行馆")) {
                            userVenueId = 5;
                        } else if (roleName.contains("国防教育体验馆")) {
                            userVenueId = 6;
                        }
                        break;
                    }
                }
            }
            if (userVenueId != null && !userVenueId.equals(venueId)) {
                return AjaxResult.error("无权限查询其他场馆的学生");
            }
        }

        List<AvailableStudentVo> students = dutyScheduleService.getAvailableStudents(venueId, startTime, endTime);
        return success(students);
    }

    /**
     * 自动排班
     */
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:add')")
    @Log(title = "场馆值班表", businessType = BusinessType.INSERT)
    @PostMapping("/autoSchedule")
    public AjaxResult autoSchedule(@RequestBody AutoScheduleDTO autoScheduleDTO)
    {
        Integer venueId = autoScheduleDTO.getVenueId();
        Date startDate = autoScheduleDTO.getStartDate();
        Date endDate = autoScheduleDTO.getEndDate();
        List<IDutyScheduleService.TimeSlot> timeSlots = autoScheduleDTO.getTimeSlots();

        // 验证权限：非超级管理员只能为自己场馆排班
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Integer userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中提取场馆ID
                        if (roleName.contains("思齐馆")) {
                            userVenueId = 1;
                        } else if (roleName.contains("弘毅馆")) {
                            userVenueId = 2;
                        } else if (roleName.contains("心缘馆")) {
                            userVenueId = 3;
                        } else if (roleName.contains("笃学馆")) {
                            userVenueId = 4;
                        } else if (roleName.contains("知行馆")) {
                            userVenueId = 5;
                        } else if (roleName.contains("国防教育体验馆")) {
                            userVenueId = 6;
                        }
                        break;
                    }
                }
            }
            if (userVenueId != null && !userVenueId.equals(venueId)) {
                return AjaxResult.error("无权限为其他场馆排班");
            }
        }

        boolean result = dutyScheduleService.autoSchedule(venueId, startDate, endDate, timeSlots);
        if (result) {
            return success("自动排班成功");
        } else {
            return error("自动排班失败，可能是该场馆没有学生");
        }
    }

    /**
     * 根据值班时间配置自动排班
     */
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:add')")
    @Log(title = "场馆值班表", businessType = BusinessType.INSERT)
    @PostMapping("/autoScheduleByConfig")
    public AjaxResult autoScheduleByConfig(@RequestBody Map<String, Object> params)
    {
        Integer venueId = (Integer) params.get("venueId");
        Date startDate = (Date) params.get("startDate");
        Integer weeks = (Integer) params.get("weeks");

        // 验证权限：非超级管理员只能为自己场馆排班
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Integer userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    String roleName = role.getRoleName();
                    if (roleName.contains("管理员")) {
                        // 从角色名称中提取场馆ID
                        if (roleName.contains("思齐馆")) {
                            userVenueId = 1;
                        } else if (roleName.contains("弘毅馆")) {
                            userVenueId = 2;
                        } else if (roleName.contains("心缘馆")) {
                            userVenueId = 3;
                        } else if (roleName.contains("笃学馆")) {
                            userVenueId = 4;
                        } else if (roleName.contains("知行馆")) {
                            userVenueId = 5;
                        } else if (roleName.contains("国防教育体验馆")) {
                            userVenueId = 6;
                        }
                        break;
                    }
                }
            }
            if (userVenueId != null && !userVenueId.equals(venueId)) {
                return AjaxResult.error("无权限为其他场馆排班");
            }
        }

        boolean result = dutyScheduleService.autoScheduleByConfig(venueId, startDate, weeks);
        if (result) {
            return success("自动排班成功");
        } else {
            return error("自动排班失败，可能是该场馆没有学生或值班时间配置");
        }
    }
}
