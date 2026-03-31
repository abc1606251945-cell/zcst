package com.zcst.manage.controller;

import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.domain.Vo.AvailableStudentVo;
import com.zcst.manage.domain.Venue;
import com.zcst.manage.domain.dto.AvailableStudentsDTO;
import com.zcst.manage.domain.dto.AutoScheduleDTO;
import com.zcst.manage.domain.dto.AutoScheduleByConfigDTO;
import com.zcst.manage.service.IDutyScheduleService;
import com.zcst.manage.service.IVenueService;

import com.zcst.common.core.domain.entity.SysRole;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.annotation.Log;
import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.enums.BusinessType;
import com.zcst.common.utils.DateUtils;
import com.zcst.common.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private static final Logger log = LoggerFactory.getLogger(DutyScheduleController.class);

    @Autowired
    private IDutyScheduleService dutyScheduleService;

    @Autowired
    private IVenueService venueService;

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
     * 根据角色名称获取场馆 ID（旧方法，保留以兼容旧代码）
     * 通过角色名称匹配场馆名称来获取场馆 ID
     * 
     * @param roleName 角色名称
     * @return 场馆 ID，如果匹配失败返回 null
     */
    private Integer getVenueIdByRoleName(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            return null;
        }
        
        // 从角色名称中提取场馆信息
        if (roleName.contains("siqi")) {
            return 1; // 思齐馆
        } else if (roleName.contains("hongyi")) {
            return 2; // 弘毅馆
        } else if (roleName.contains("xinyuan")) {
            return 3; // 心缘馆
        } else if (roleName.contains("duxue")) {
            return 4; // 笃学馆
        } else if (roleName.contains("zhixing")) {
            return 5; // 知行馆
        } else if (roleName.contains("guofang")) {
            return 6; // 国防教育体验馆
        }
        
        return null;
    }

    private Date parseClientDate(String value)
    {
        if (value == null) {
            return null;
        }
        String v = value.trim();
        if (v.isEmpty()) {
            return null;
        }
        if (v.matches("^\\d+$")) {
            try {
                return new Date(Long.parseLong(v));
            } catch (NumberFormatException ignored) {
            }
        }
        if (v.contains("T")) {
            try {
                return Date.from(Instant.parse(v));
            } catch (Exception ignored) {
            }
            try {
                OffsetDateTime odt = OffsetDateTime.parse(v, DateTimeFormatter.ISO_DATE_TIME);
                return Date.from(odt.toInstant());
            } catch (Exception ignored) {
            }
            try {
                LocalDateTime ldt = LocalDateTime.parse(v, DateTimeFormatter.ISO_DATE_TIME);
                return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
            } catch (Exception ignored) {
            }
            String normalized = v.replace("T", " ").replace("Z", "");
            normalized = normalized.replaceAll("\\.\\d{3}$", "");
            return DateUtils.parseDate(normalized);
        }
        return DateUtils.parseDate(v);
    }

    /**
     * 查询值班表列表
     */
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:list')")
    @GetMapping("/list")
    public TableDataInfo list(DutySchedule dutySchedule)
    {
        // 获取当前用户
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        
        // 非超级管理员只能查看自己场馆的值班信息
        if (!isSuperAdmin(currentUser)) {
            // 从用户角色中获取场馆 ID
            Integer venueId = getVenueIdFromUserRoles(currentUser);
            if (venueId != null) {
                dutySchedule.setVenueId(venueId);
                log.info("场馆管理员查询值班安排，venueId: {}", venueId);
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
            Integer userVenueId = getVenueIdFromUserRoles(currentUser);
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
            Integer userVenueId = getVenueIdFromUserRoles(currentUser);
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
            Integer userVenueId = getVenueIdFromUserRoles(currentUser);
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
            Integer userVenueId = getVenueIdFromUserRoles(currentUser);
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
    public AjaxResult getAvailableStudents(@RequestBody AvailableStudentsDTO dto)
    {
        Integer venueId = dto.getVenueId();
        Date startTime = parseClientDate(dto.getStartTime());
        Date endTime = parseClientDate(dto.getEndTime());

        // 验证权限：非超级管理员只能查询自己场馆的学生
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            Integer userVenueId = getVenueIdFromUserRoles(currentUser);
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
                    userVenueId = getVenueIdByRoleName(role.getRoleName());
                    if (userVenueId != null) break;
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
    public AjaxResult autoScheduleByConfig(@RequestBody AutoScheduleByConfigDTO dto)
    {
        Integer venueId = dto.getVenueId();
        Date startDate = parseClientDate(dto.getStartDate());
        Date endDate = parseClientDate(dto.getEndDate());
        Integer weeks = dto.getWeeks();

        // 验证权限：非超级管理员只能为自己场馆排班
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (!isSuperAdmin(currentUser)) {
            // 尝试从用户角色中获取场馆信息
            Integer userVenueId = null;
            List<SysRole> roles = currentUser.getRoles();
            if (roles != null) {
                for (SysRole role : roles) {
                    userVenueId = getVenueIdByRoleName(role.getRoleName());
                    if (userVenueId != null) break;
                }
            }
            if (userVenueId != null && !userVenueId.equals(venueId)) {
                return AjaxResult.error("无权限为其他场馆排班");
            }
        }

        boolean result;
        if (endDate != null) {
            result = dutyScheduleService.autoScheduleByConfig(venueId, startDate, endDate);
        } else {
            result = dutyScheduleService.autoScheduleByConfig(venueId, startDate, weeks == null ? 1 : weeks);
        }
        if (result) {
            return success("自动排班成功");
        } else {
            return error("自动排班失败，可能是该场馆没有学生或值班时间配置");
        }
    }

    /**
     * 查询学生当前可签到的值班信息
     * 学生可以通过此接口获取当前可以签到的值班记录（dutyId）
     * 
     * @param studentId 学号，为空时自动使用当前登录用户的学号
     * @return 可签到的值班信息列表
     */
    @GetMapping("/currentDuty")
    public AjaxResult getCurrentDuty(@RequestParam(value = "studentId", required = false) String studentId)
    {
        // 如果未指定学号，使用当前登录用户的信息
        if (studentId == null || studentId.isEmpty()) {
            SysUser currentUser = SecurityUtils.getLoginUser().getUser();
            if (currentUser == null) {
                return error("未登录或用户信息不存在");
            }
            studentId = currentUser.getUserName();
            if (studentId == null || studentId.isEmpty()) {
                return error("无法获取用户学号");
            }
            log.info("未指定学号，使用当前登录用户：{}", studentId);
        }
        
        // 查询学生当前可签到的值班信息
        List<DutySchedule> currentDutyList = dutyScheduleService.selectCurrentAvailableDuty(studentId, null);
        
        if (currentDutyList.isEmpty()) {
            log.info("学生 {} 当前没有可签到的值班", studentId);
            return success(new ArrayList<>());
        }
        
        log.info("学生 {} 当前可签到的值班数量：{}", studentId, currentDutyList.size());
        return success(currentDutyList);
    }
}
