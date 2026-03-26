package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.manage.domain.AttendanceRecord;
import com.zcst.manage.service.IAttendanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考勤记录Controller
 */
@RestController
@RequestMapping("/manage/attendance/record")
public class AttendanceRecordController extends BaseController {
    @Autowired
    private IAttendanceRecordService attendanceRecordService;

    /**
     * 查询考勤记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(AttendanceRecord attendanceRecord) {
        // 获取当前登录用户
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        String username = currentUser.getUserName();
        
        // 根据用户角色设置过滤条件
        // 值班员（common角色）只显示自己的考勤信息
        if (currentUser.getRoles().stream().anyMatch(role -> "common".equals(role.getRoleKey()))) {
            attendanceRecord.setStudentId(username);
        }
        // 场馆管理员只显示对应场馆的考勤信息
        // 这里需要根据实际的角色配置来判断，暂时假设角色key包含场馆名称
        else if (currentUser.getRoles().stream().anyMatch(role -> role.getRoleKey().contains("venue"))) {
            // 从角色key中提取场馆ID，这里需要根据实际情况调整
            // 暂时跳过，后续在Service层处理
        }
        
        startPage();
        List<AttendanceRecord> list = attendanceRecordService.selectAttendanceRecordList(attendanceRecord);
        return getDataTable(list);
    }

    /**
     * 获取考勤记录详细信息
     */
    @GetMapping("/info/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId) {
        return success(attendanceRecordService.selectAttendanceRecordById(recordId));
    }

    /**
     * 新增考勤记录
     */
    @PostMapping
    public AjaxResult add(@RequestBody AttendanceRecord attendanceRecord) {
        return toAjax(attendanceRecordService.insertAttendanceRecord(attendanceRecord));
    }

    /**
     * 修改考勤记录
     */
    @PutMapping
    public AjaxResult edit(@RequestBody AttendanceRecord attendanceRecord) {
        return toAjax(attendanceRecordService.updateAttendanceRecord(attendanceRecord));
    }

    /**
     * 删除考勤记录
     */
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds) {
        return toAjax(attendanceRecordService.deleteAttendanceRecordByIds(recordIds));
    }

    /**
     * 打卡
     */
    @PostMapping("/checkIn")
    public AjaxResult checkIn(@RequestParam String studentId, @RequestParam Integer dutyId) {
        int result = attendanceRecordService.checkIn(studentId, dutyId);
        if (result > 0) {
            return success("打卡成功");
        } else {
            return error("打卡失败，可能已经打卡或值班信息不存在");
        }
    }

    /**
     * 根据学生ID和月份查询考勤记录
     */
    @GetMapping("/student/month")
    public AjaxResult getStudentAttendanceByMonth(@RequestParam String studentId, @RequestParam String yearMonth) {
        List<AttendanceRecord> records = attendanceRecordService.selectAttendanceRecordByStudentIdAndMonth(studentId, yearMonth);
        return success(records);
    }
}