package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.manage.domain.AttendanceRecord;
import com.zcst.manage.domain.Vo.AttendanceRecordVo;
import com.zcst.manage.service.IAttendanceRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        String username = currentUser.getUserName();
        
        // 根据用户角色设置过滤条件
        // 值班员（common 角色）只显示自己的考勤信息
        if (currentUser.getRoles().stream().anyMatch(role -> "common".equals(role.getRoleKey()))) {
            attendanceRecord.setStudentId(username);
        }
        // 场馆管理员只显示对应场馆的考勤信息
        else if (currentUser.getRoles().stream().anyMatch(role -> 
            role.getRoleKey().contains("siqi") || 
            role.getRoleKey().contains("hongyi") || 
            role.getRoleKey().contains("xinyuan") || 
            role.getRoleKey().contains("duxue") || 
            role.getRoleKey().contains("zhixing") || 
            role.getRoleKey().contains("guofang"))) {
            // 从角色的 venue_id 字段获取场馆 ID
            Integer venueId = currentUser.getRoles().stream()
                .filter(role -> role.getVenueId() != null)
                .findFirst()
                .map(role -> role.getVenueId())
                .orElse(null);
            if (venueId != null) {
                attendanceRecord.setVenueId(venueId);
                log.info("场馆管理员查询考勤记录，venueId: {}", venueId);
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
        return success(attendanceRecordService.selectAttendanceRecordById(recordId));
    }

    /**
     * 新增考勤记录
     * 
     * @param attendanceRecord 考勤记录信息
     * @return 操作结果
     */
    @PostMapping
    public AjaxResult add(@RequestBody AttendanceRecord attendanceRecord) {
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
        return toAjax(attendanceRecordService.deleteAttendanceRecordByIds(recordIds));
    }

    /**
     * 打卡接口
     * 学生到达场馆后进行打卡，系统自动判断是否迟到
     * 
     * @param studentId 学号
     * @param dutyId 值班 ID
     * @return 打卡结果
     */
    @PostMapping("/checkIn")
    public AjaxResult checkIn(@RequestParam String studentId, @RequestParam Integer dutyId) {
        // 参数校验
        if (studentId == null || studentId.isEmpty() || dutyId == null) {
            return error("参数错误：学号和值班 ID 不能为空");
        }
        
        int result = attendanceRecordService.checkIn(studentId, dutyId);
        if (result > 0) {
            return success("打卡成功");
        } else {
            return error("打卡失败，可能已经打卡或值班信息不存在");
        }
    }

    /**
     * 签退接口
     * 学生值班结束后进行签退，系统自动计算值班时长并判断是否早退
     * 
     * @param recordId 考勤记录 ID
     * @param dutyId 值班 ID
     * @return 签退结果
     */
    @PostMapping("/checkOut")
    public AjaxResult checkOut(@RequestParam Long recordId, @RequestParam Integer dutyId) {
        // 参数校验
        if (recordId == null || dutyId == null) {
            return error("参数错误：考勤记录 ID 和值班 ID 不能为空");
        }
        
        int result = attendanceRecordService.checkOut(recordId, dutyId);
        if (result > 0) {
            return success("签退成功");
        } else {
            return error("签退失败，考勤记录不存在");
        }
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
    public AjaxResult getStudentAttendanceByMonth(@RequestParam String studentId, @RequestParam String yearMonth) {
        List<AttendanceRecordVo> records = attendanceRecordService.selectAttendanceRecordVoByStudentIdAndMonth(studentId, yearMonth);
        return success(records);
    }
}