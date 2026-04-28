package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.constant.HttpStatus;
import com.zcst.common.exception.ServiceException;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.manage.domain.AttendanceRecord;
import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.domain.Vo.AttendanceRecordVo;
import com.zcst.manage.mapper.DutyScheduleMapper;
import com.zcst.manage.service.IAttendanceRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考勤记录 Controller
 * 提供考勤记录的增删改查、打卡、签退等功能接口
 * 
 * @author zcst
 * @date 2026-03-27
 */
@RestController
@RequestMapping("/manage/attendance/record")
public class AttendanceRecordController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(AttendanceRecordController.class);
    
    /**
     * 考勤记录 Service
     */
    @Autowired
    private IAttendanceRecordService attendanceRecordService;

    @Autowired
    private DutyScheduleMapper dutyScheduleMapper;

    /**
     * 查询考勤记录列表
     * 支持按学生 ID、值班 ID、状态等条件查询
     * 根据用户角色自动过滤数据：
     * - 普通学生（common 角色）：只能查看自己的考勤记录
     * - 场馆管理员：查看对应场馆的考勤记录
     * 
     * @param attendanceRecord 考勤记录查询条件
     * @return 考勤记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(AttendanceRecord attendanceRecord) {
        // 获取当前登录用户信息
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null)
        {
            throw new ServiceException("未登录或用户信息不存在", HttpStatus.UNAUTHORIZED);
        }
        String username = currentUser.getUserName();
        boolean isStudent = "student".equalsIgnoreCase(currentUser.getAccountType());
        
        if (isStudent)
        {
            if (attendanceRecord.getStudentId() != null && !attendanceRecord.getStudentId().isEmpty()
                && !attendanceRecord.getStudentId().equals(username))
            {
                log.warn("学生越权尝试查询他人考勤列表, userId={}, username={}, requestStudentId={}",
                    currentUser.getUserId(), username, attendanceRecord.getStudentId());
            }
            attendanceRecord.setStudentId(username);
        }
        else if (!currentUser.isAdmin())
        {
            Integer venueId = currentUser.getRoles() == null ? null : currentUser.getRoles().stream()
                .filter(role -> role.getVenueId() != null)
                .findFirst()
                .map(role -> role.getVenueId())
                .orElse(null);
            if (venueId != null)
            {
                attendanceRecord.setVenueId(venueId);
            }
        }
        
        // 启动分页
        startPage();
        // 查询考勤记录 VO 列表（包含关联信息）
        List<AttendanceRecordVo> list = attendanceRecordService.selectAttendanceRecordVoList(attendanceRecord);
        return getDataTable(list);
    }

    /**
     * 获取考勤记录详细信息
     * 
     * @param recordId 考勤记录 ID
     * @return 考勤记录详情
     */
    @GetMapping("/info/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null)
        {
            throw new ServiceException("未登录或用户信息不存在", HttpStatus.UNAUTHORIZED);
        }
        AttendanceRecord record = attendanceRecordService.selectAttendanceRecordById(recordId);
        if (record == null)
        {
            return error("记录不存在");
        }
        boolean isStudent = "student".equalsIgnoreCase(currentUser.getAccountType());
        if (isStudent)
        {
            String username = currentUser.getUserName();
            if (record.getStudentId() == null || !record.getStudentId().equals(username))
            {
                throw new ServiceException("无权限查看该数据", HttpStatus.FORBIDDEN);
            }
        }
        else if (!currentUser.isAdmin())
        {
            Integer venueId = currentUser.getRoles() == null ? null : currentUser.getRoles().stream()
                .filter(role -> role.getVenueId() != null)
                .findFirst()
                .map(role -> role.getVenueId())
                .orElse(null);
            if (venueId != null && record.getDutyId() != null)
            {
                DutySchedule duty = dutyScheduleMapper.selectDutyScheduleByDutyId(record.getDutyId());
                if (duty != null && duty.getVenueId() != null && !venueId.equals(duty.getVenueId()))
                {
                    throw new ServiceException("无权限查看该数据", HttpStatus.FORBIDDEN);
                }
            }
        }
        return success(record);
    }

    /**
     * 新增考勤记录
     * 
     * @param attendanceRecord 考勤记录信息
     * @return 操作结果
     */
    @PostMapping
    public AjaxResult add(@RequestBody AttendanceRecord attendanceRecord) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null)
        {
            throw new ServiceException("未登录或用户信息不存在", HttpStatus.UNAUTHORIZED);
        }
        if ("student".equalsIgnoreCase(currentUser.getAccountType()))
        {
            throw new ServiceException("无权限操作该数据", HttpStatus.FORBIDDEN);
        }
        return toAjax(attendanceRecordService.insertAttendanceRecord(attendanceRecord));
    }

    /**
     * 修改考勤记录
     * 
     * @param attendanceRecord 考勤记录信息
     * @return 操作结果
     */
    @PutMapping
    public AjaxResult edit(@RequestBody AttendanceRecord attendanceRecord) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null)
        {
            throw new ServiceException("未登录或用户信息不存在", HttpStatus.UNAUTHORIZED);
        }
        if ("student".equalsIgnoreCase(currentUser.getAccountType()))
        {
            throw new ServiceException("无权限操作该数据", HttpStatus.FORBIDDEN);
        }
        return toAjax(attendanceRecordService.updateAttendanceRecord(attendanceRecord));
    }

    /**
     * 删除考勤记录
     * 支持批量删除
     * 
     * @param recordIds 考勤记录 ID 数组
     * @return 操作结果
     */
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null)
        {
            throw new ServiceException("未登录或用户信息不存在", HttpStatus.UNAUTHORIZED);
        }
        if ("student".equalsIgnoreCase(currentUser.getAccountType()))
        {
            throw new ServiceException("无权限操作该数据", HttpStatus.FORBIDDEN);
        }
        return toAjax(attendanceRecordService.deleteAttendanceRecordByIds(recordIds));
    }

    /**
     * 打卡接口
     * 学生到达场馆后进行打卡，系统自动判断是否迟到
     * 
     * 说明：
     * - studentId 参数仅为兼容，后端实际以当前登录用户为准（不信任前端传入）
     *
     * @param studentId 学号（兼容参数，实际忽略）
     * @param dutyId 值班 ID
     * @return 打卡结果
     */
    @PostMapping("/checkIn")
    @PreAuthorize("@ss.hasPermi('manage:attendance:student')")
    public AjaxResult checkIn(@RequestParam(value = "studentId", required = false) String studentId, @RequestParam Integer dutyId) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null)
        {
            throw new ServiceException("未登录或用户信息不存在", HttpStatus.UNAUTHORIZED);
        }
        String currentStudentId = currentUser.getUserName();
        if (!"student".equalsIgnoreCase(currentUser.getAccountType()))
        {
            throw new ServiceException("无权限操作该接口", HttpStatus.FORBIDDEN);
        }
        if (currentStudentId == null || currentStudentId.isEmpty()) {
            throw new ServiceException("无法获取用户学号", HttpStatus.BAD_REQUEST);
        }
        if (studentId != null && !studentId.isEmpty() && !studentId.equals(currentStudentId))
        {
            log.warn("学生越权尝试 checkIn, userId={}, username={}, requestStudentId={}",
                currentUser.getUserId(), currentStudentId, studentId);
        }
        if (dutyId == null) {
            throw new ServiceException("参数错误：值班 ID 不能为空", HttpStatus.BAD_REQUEST);
        }

        attendanceRecordService.checkIn(currentStudentId, dutyId);
        return success("打卡成功");
    }

    /**
     * 签退接口
     * 学生值班结束后进行签退，系统自动计算值班时长并判断是否早退
     * 
     * 说明：
     * - 学生端只需要传 dutyId：后端会用（当前登录学生 + dutyId）定位未签退记录并完成签退
     * - 管理端/非学生角色仍可传 recordId + dutyId 进行签退（兼容历史调用）
     *
     * @param recordId 考勤记录 ID（管理端兼容参数；学生端可不传）
     * @param dutyId 值班 ID
     * @return 签退结果
     */
    @PostMapping("/checkOut")
    @PreAuthorize("@ss.hasPermi('manage:attendance:student')")
    public AjaxResult checkOut(
        @RequestParam(value = "recordId", required = false) Long recordId,
        @RequestParam(value = "dutyId", required = false) Integer dutyId,
        @RequestParam(value = "studentId", required = false) String studentId
    ) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null)
        {
            throw new ServiceException("未登录或用户信息不存在", HttpStatus.UNAUTHORIZED);
        }
        String currentStudentId = currentUser.getUserName();
        if (!"student".equalsIgnoreCase(currentUser.getAccountType()))
        {
            throw new ServiceException("无权限操作该接口", HttpStatus.FORBIDDEN);
        }
        if (currentStudentId == null || currentStudentId.isEmpty()) {
            throw new ServiceException("无法获取用户学号", HttpStatus.BAD_REQUEST);
        }
        if (studentId != null && !studentId.isEmpty() && !studentId.equals(currentStudentId))
        {
            log.warn("学生越权尝试 checkOut, userId={}, username={}, requestStudentId={}",
                currentUser.getUserId(), currentStudentId, studentId);
        }

        if (recordId != null)
        {
            attendanceRecordService.checkOutByRecordId(currentStudentId, recordId);
            return success("签退成功");
        }
        if (dutyId == null)
        {
            throw new ServiceException("参数错误：recordId 或 dutyId 至少传一个", HttpStatus.BAD_REQUEST);
        }
        attendanceRecordService.checkOutByDuty(currentStudentId, dutyId);
        return success("签退成功");
    }

    /**
     * 根据学生 ID 和月份查询考勤记录
     * 用于查看学生某个月的所有考勤记录
     * 
     * @param studentId 学号
     * @param yearMonth 年月（格式：2026-04）
     * @return 考勤记录列表
     */
    @GetMapping("/student/month")
    @PreAuthorize("@ss.hasPermi('manage:attendance:student')")
    public AjaxResult getStudentAttendanceByMonth(@RequestParam(value = "studentId", required = false) String studentId, @RequestParam String yearMonth) {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null)
        {
            throw new ServiceException("未登录或用户信息不存在", HttpStatus.UNAUTHORIZED);
        }
        if (!"student".equalsIgnoreCase(currentUser.getAccountType()))
        {
            throw new ServiceException("无权限查看该数据", HttpStatus.FORBIDDEN);
        }
        String currentStudentId = currentUser.getUserName();
        if (currentStudentId == null || currentStudentId.isEmpty())
        {
            throw new ServiceException("无法获取用户学号", HttpStatus.BAD_REQUEST);
        }
        if (studentId != null && !studentId.isEmpty() && !studentId.equals(currentStudentId))
        {
            log.warn("学生越权尝试查询他人 monthAttendance, userId={}, username={}, requestStudentId={}",
                currentUser.getUserId(), currentStudentId, studentId);
        }
        List<AttendanceRecordVo> records = attendanceRecordService.selectAttendanceRecordVoByStudentIdAndMonth(currentStudentId, yearMonth);
        return success(records);
    }
}
