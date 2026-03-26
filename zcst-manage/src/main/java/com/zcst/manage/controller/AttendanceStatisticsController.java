package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.manage.domain.AttendanceStatistics;
import com.zcst.manage.service.IAttendanceStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考勤统计Controller
 */
@RestController
@RequestMapping("/manage/attendance/statistics")
public class AttendanceStatisticsController extends BaseController {
    @Autowired
    private IAttendanceStatisticsService attendanceStatisticsService;

    /**
     * 查询考勤统计列表
     */
    @GetMapping("/list")
    public TableDataInfo list(AttendanceStatistics attendanceStatistics) {
        // 获取当前登录用户
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        String username = currentUser.getUserName();
        
        // 根据用户角色设置过滤条件
        // 值班员（common角色）只显示自己的考勤信息
        if (currentUser.getRoles().stream().anyMatch(role -> "common".equals(role.getRoleKey()))) {
            attendanceStatistics.setStudentId(username);
        }
        // 场馆管理员只显示对应场馆的考勤信息
        // 这里需要根据实际的角色配置来判断，暂时假设角色key包含场馆名称
        else if (currentUser.getRoles().stream().anyMatch(role -> role.getRoleKey().contains("venue"))) {
            // 从角色key中提取场馆ID，这里需要根据实际情况调整
            // 暂时跳过，后续在Service层处理
        }
        
        startPage();
        List<AttendanceStatistics> list = attendanceStatisticsService.selectAttendanceStatisticsList(attendanceStatistics);
        return getDataTable(list);
    }

    /**
     * 获取考勤统计详细信息
     */
    @GetMapping("/info/{statId}")
    public AjaxResult getInfo(@PathVariable("statId") Long statId) {
        return success(attendanceStatisticsService.selectAttendanceStatisticsById(statId));
    }

    /**
     * 新增考勤统计
     */
    @PostMapping
    public AjaxResult add(@RequestBody AttendanceStatistics attendanceStatistics) {
        return toAjax(attendanceStatisticsService.insertAttendanceStatistics(attendanceStatistics));
    }

    /**
     * 修改考勤统计
     */
    @PutMapping
    public AjaxResult edit(@RequestBody AttendanceStatistics attendanceStatistics) {
        return toAjax(attendanceStatisticsService.updateAttendanceStatistics(attendanceStatistics));
    }

    /**
     * 删除考勤统计
     */
    @DeleteMapping("/{statIds}")
    public AjaxResult remove(@PathVariable Long[] statIds) {
        return toAjax(attendanceStatisticsService.deleteAttendanceStatisticsByIds(statIds));
    }

    /**
     * 按月统计考勤数据
     */
    @PostMapping("/calculate")
    public AjaxResult calculateMonthlyAttendance(@RequestParam String yearMonth) {
        int result = attendanceStatisticsService.calculateMonthlyAttendance(yearMonth);
        if (result > 0) {
            return success("统计成功");
        } else {
            return error("统计失败");
        }
    }

    /**
     * 根据学生ID和年月查询考勤统计
     */
    @GetMapping("/student/month")
    public AjaxResult getStudentStatisticsByMonth(@RequestParam String studentId, @RequestParam String yearMonth) {
        AttendanceStatistics statistics = attendanceStatisticsService.selectAttendanceStatisticsByStudentIdAndMonth(studentId, yearMonth);
        return success(statistics);
    }

    /**
     * 根据场馆ID和年月查询考勤统计
     */
    @GetMapping("/venue/month")
    public AjaxResult getVenueStatisticsByMonth(@RequestParam Integer venueId, @RequestParam String yearMonth) {
        List<AttendanceStatistics> statisticsList = attendanceStatisticsService.selectAttendanceStatisticsByVenueIdAndMonth(venueId, yearMonth);
        return success(statisticsList);
    }
}