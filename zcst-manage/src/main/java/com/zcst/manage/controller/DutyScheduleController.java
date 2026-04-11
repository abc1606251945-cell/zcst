package com.zcst.manage.controller;

import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.domain.Vo.AvailableStudentVo;
import com.zcst.manage.domain.Vo.DutyScheduleVo;
import com.zcst.manage.domain.Venue;
import com.zcst.manage.domain.dto.AvailableStudentsDTO;
import com.zcst.manage.domain.dto.AutoScheduleDTO;
import com.zcst.manage.domain.dto.AutoScheduleByConfigDTO;
import com.zcst.manage.domain.Student;
import com.zcst.manage.mapper.StudentMapper;
import com.zcst.manage.service.IDutyScheduleService;
import com.zcst.manage.service.IVenueService;

import com.zcst.common.core.domain.entity.SysRole;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.annotation.Log;
import com.zcst.common.constant.HttpStatus;
import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.exception.ServiceException;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    @Autowired
    private IDutyScheduleService dutyScheduleService;

    @Autowired
    private IVenueService venueService;

    @Autowired
    private StudentMapper studentMapper;

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
                return Date.from(ldt.atZone(ZONE_ID).toInstant());
            } catch (Exception ignored) {
            }
            String normalized = v.replace("T", " ").replace("Z", "");
            normalized = normalized.replaceAll("\\.\\d{3}$", "");
            return DateUtils.parseDate(normalized);
        }
        try {
            LocalDateTime ldt = LocalDateTime.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return Date.from(ldt.atZone(ZONE_ID).toInstant());
        } catch (Exception ignored) {
        }
        try {
            LocalDate ld = LocalDate.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return Date.from(ld.atStartOfDay(ZONE_ID).toInstant());
        } catch (Exception ignored) {
        }
        return DateUtils.parseDate(v);
    }

    private LocalDate toLocalDate(Date date)
    {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZONE_ID).toLocalDate();
    }

    private Date startOfDay(LocalDate date)
    {
        return Date.from(date.atStartOfDay(ZONE_ID).toInstant());
    }

    private Date endOfDay(LocalDate date)
    {
        return Date.from(date.atTime(23, 59, 59).atZone(ZONE_ID).toInstant());
    }

    private Date[] resolveRange(String from, String to, Integer days)
    {
        int d = (days == null || days <= 0) ? 7 : days;
        Date fromDate = parseClientDate(from);
        Date toDate = parseClientDate(to);

        LocalDate start;
        LocalDate end;

        if (fromDate == null && toDate == null) {
            start = LocalDate.now(ZONE_ID);
            end = start.plusDays(d - 1L);
        } else if (fromDate != null && toDate == null) {
            start = toLocalDate(fromDate);
            end = start.plusDays(d - 1L);
        } else if (fromDate == null) {
            end = toLocalDate(toDate);
            start = end.minusDays(d - 1L);
        } else {
            start = toLocalDate(fromDate);
            end = toLocalDate(toDate);
        }

        if (start.isAfter(end)) {
            LocalDate tmp = start;
            start = end;
            end = tmp;
        }

        return new Date[] { startOfDay(start), endOfDay(end) };
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
        if ("student".equalsIgnoreCase(currentUser.getAccountType()))
        {
            throw new ServiceException("无权限查看该数据", HttpStatus.FORBIDDEN);
        }
        
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
        if (venueId == null) {
            return AjaxResult.error("场馆不能为空");
        }
        if (startDate == null) {
            return AjaxResult.error("开始日期不能为空或格式不正确");
        }
        if (endDate == null && (weeks == null || weeks < 1)) {
            return AjaxResult.error("排班周数不能为空");
        }

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
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:studentWeek')")
    @GetMapping("/currentDuty")
    public AjaxResult getCurrentDuty(@RequestParam(value = "studentId", required = false) String studentId)
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null) {
            return error("未登录或用户信息不存在");
        }
        String currentStudentId = currentUser.getUserName();
        if (currentStudentId == null || currentStudentId.isEmpty()) {
            return error("无法获取用户学号");
        }
        if (studentId != null && !studentId.isEmpty() && !studentId.equals(currentStudentId))
        {
            log.warn("学生越权尝试查询他人 currentDuty, userId={}, username={}, requestStudentId={}",
                currentUser.getUserId(), currentStudentId, studentId);
        }
        studentId = currentStudentId;
        
        // 查询学生当前可签到的值班信息
        List<DutySchedule> currentDutyList = dutyScheduleService.selectCurrentAvailableDuty(studentId, null);
        
        if (currentDutyList.isEmpty()) {
            log.info("学生 {} 当前没有可签到的值班", studentId);
            return success(new ArrayList<>());
        }
        
        log.info("学生 {} 当前可签到的值班数量：{}", studentId, currentDutyList.size());
        return success(currentDutyList);
    }

    @GetMapping("/student/week")
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:studentWeek')")
    /**
     * 学生端：查询某一周的值班列表（按场馆可选过滤）
     *
     * 取数规则：
     * - studentId 一律取当前登录用户（userName），不信任前端传参
     * - 时间范围：weekStart 所在周的周一 00:00:00 ~ 周日 23:59:59（服务器时区 Asia/Shanghai）
     *
     * canCheckIn 计算规则：
     * - 当前时间在 startTime/endTime 之间（含边界）
     * - 且 duty_schedule.attendance_status 为空（尚未产生考勤状态）
     *
     * @param weekStart 周一日期，格式 yyyy-MM-dd；为空默认本周周一
     * @param venueId 场馆ID；<=0 或为空表示全部场馆
     */
    public AjaxResult getStudentWeekDuties(
        @RequestParam(value = "weekStart", required = false) String weekStart,
        @RequestParam(value = "venueId", required = false) Integer venueId
    )
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null) {
            return error("未登录或用户信息不存在");
        }
        String studentId = currentUser.getUserName();
        if (studentId == null || studentId.isEmpty()) {
            return error("无法获取用户学号");
        }
        Long boundVenueIdLong = currentUser.getVenueId();
        if (boundVenueIdLong == null)
        {
            Student me = studentMapper.selectStudentByStudentId(studentId);
            boundVenueIdLong = me == null ? null : me.getVenueId();
        }
        Integer boundVenueId = boundVenueIdLong == null ? null : boundVenueIdLong.intValue();
        if (boundVenueId == null || boundVenueId <= 0)
        {
            return success(new ArrayList<>());
        }

        LocalDate monday;
        if (weekStart == null || weekStart.isEmpty()) {
            monday = LocalDate.now(ZONE_ID).with(DayOfWeek.MONDAY);
        } else {
            try {
                monday = LocalDate.parse(weekStart, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                return error("参数错误：weekStart 格式应为 yyyy-MM-dd");
            }
        }

        Date startTime = startOfDay(monday);
        Date endTime = endOfDay(monday.plusDays(6));

        Integer resolvedVenueId;
        if (venueId != null && venueId > 0 && !venueId.equals(boundVenueId))
        {
            log.warn("学生越权尝试查询其他场馆 weekDuty, userId={}, username={}, boundVenueId={}, requestVenueId={}",
                currentUser.getUserId(), studentId, boundVenueId, venueId);
            return success(new ArrayList<>());
        }
        resolvedVenueId = boundVenueId;
        List<DutyScheduleVo> list = new ArrayList<>(dutyScheduleService.selectStudentWeekDuty(studentId, resolvedVenueId, startTime, endTime));

        Date now = new Date();
        for (DutyScheduleVo vo : list) {
            if (vo.getStartTime() != null) {
                vo.setStartTimeTs(vo.getStartTime().getTime());
            }
            if (vo.getEndTime() != null) {
                vo.setEndTimeTs(vo.getEndTime().getTime());
            }
            boolean inRange = vo.getStartTime() != null && vo.getEndTime() != null
                && !now.before(vo.getStartTime())
                && !now.after(vo.getEndTime());
            boolean hasCheckIn = vo.getRecordId() != null && vo.getCheckInTime() != null;
            boolean hasCheckOut = vo.getCheckOutTime() != null;
            vo.setCanCheckIn(inRange && !hasCheckIn);
            vo.setCanCheckOut(hasCheckIn && !hasCheckOut);
        }
        list.sort(Comparator.comparing(DutyScheduleVo::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return success(list);
    }

    @GetMapping("/my")
    @PreAuthorize("@ss.hasPermi('manage:dutySchedule:studentWeek')")
    public AjaxResult getMyDuties(
        @RequestParam(value = "from", required = false) String from,
        @RequestParam(value = "to", required = false) String to,
        @RequestParam(value = "days", required = false) Integer days
    )
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null) {
            return error("未登录或用户信息不存在");
        }
        String studentId = currentUser.getUserName();
        if (studentId == null || studentId.isEmpty()) {
            return error("无法获取用户学号");
        }

        Date[] range = resolveRange(from, to, days);
        DutySchedule query = new DutySchedule();
        query.setStudentId(studentId);
        query.setStartTime(range[0]);
        query.setEndTime(range[1]);

        List<DutySchedule> list = new ArrayList<>(dutyScheduleService.selectDutyScheduleList(query));
        list.sort(Comparator.comparing(DutySchedule::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return success(list);
    }

    @GetMapping("/myVenue")
    public AjaxResult getMyVenueDuties(
        @RequestParam(value = "venueId", required = false) Integer venueId,
        @RequestParam(value = "from", required = false) String from,
        @RequestParam(value = "to", required = false) String to,
        @RequestParam(value = "days", required = false) Integer days
    )
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null) {
            return error("未登录或用户信息不存在");
        }

        Integer resolvedVenueId = venueId;
        if (!isSuperAdmin(currentUser)) {
            resolvedVenueId = getVenueIdFromUserRoles(currentUser);
        }
        if (resolvedVenueId == null) {
            return success(new ArrayList<>());
        }

        Date[] range = resolveRange(from, to, days);
        DutySchedule query = new DutySchedule();
        query.setVenueId(resolvedVenueId);
        query.setStartTime(range[0]);
        query.setEndTime(range[1]);

        List<DutySchedule> list = new ArrayList<>(dutyScheduleService.selectDutyScheduleList(query));
        list.sort(Comparator.comparing(DutySchedule::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return success(list);
    }
}
