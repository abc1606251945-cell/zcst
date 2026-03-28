package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.manage.domain.AttendanceStatistics;
import com.zcst.manage.service.IAttendanceStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考勤统计 Controller
 * 提供考勤统计数据的 RESTful API 接口
 * 
 * 主要功能：
 * 1. 查询考勤统计列表（支持分页）
 * 2. 查询考勤统计详细信息
 * 3. 新增/修改/删除考勤统计
 * 4. 按月统计考勤数据（调用 Service 层计算）
 * 5. 按学生/场馆查询指定月份的考勤统计
 * 
 * 权限控制：
 * - 值班员（common 角色）：只能查看自己的考勤统计
 * - 场馆管理员：查看对应场馆的考勤统计
 * - 管理员：查看所有考勤统计
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
@RestController
@RequestMapping("/manage/attendance/statistics")
public class AttendanceStatisticsController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(AttendanceStatisticsController.class);
    /**
     * 考勤统计 Service
     * 用于处理考勤统计业务逻辑
     */
    @Autowired
    private IAttendanceStatisticsService attendanceStatisticsService;

    /**
     * 查询考勤统计列表
     * 支持分页查询，根据用户角色自动过滤数据
     * 
     * 权限控制逻辑：
     * 1. 值班员（common 角色）：自动设置 studentId 为当前用户名，只显示自己的考勤
     * 2. 场馆管理员：根据角色的 venue_id 字段过滤对应场馆的数据
     * 3. 管理员：不进行过滤，显示所有数据
     * 
     * @param attendanceStatistics 查询条件（学生 ID、场馆 ID、年月等）
     * @return 分页后的考勤统计列表
     */
    @GetMapping("/list")
    public TableDataInfo list(AttendanceStatistics attendanceStatistics) {
        // 获取当前登录用户
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        String username = currentUser.getUserName();
        
        // 根据用户角色设置过滤条件
        // 值班员（common 角色）只显示自己的考勤信息
        if (currentUser.getRoles().stream().anyMatch(role -> "common".equals(role.getRoleKey()))) {
            attendanceStatistics.setStudentId(username);
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
                attendanceStatistics.setVenueId(venueId);
                log.info("场馆管理员查询考勤统计，venueId: {}", venueId);
            }
        }
        
        startPage();
        List<AttendanceStatistics> list = attendanceStatisticsService.selectAttendanceStatisticsList(attendanceStatistics);
        return getDataTable(list);
    }

    /**
     * 获取考勤统计详细信息
     * 
     * @param statId 统计 ID
     * @return 考勤统计对象
     */
    @GetMapping("/info/{statId}")
    public AjaxResult getInfo(@PathVariable("statId") Long statId) {
        return success(attendanceStatisticsService.selectAttendanceStatisticsById(statId));
    }

    /**
     * 新增考勤统计记录
     * 
     * @param attendanceStatistics 考勤统计对象
     * @return 操作结果（成功/失败）
     */
    @PostMapping
    public AjaxResult add(@RequestBody AttendanceStatistics attendanceStatistics) {
        return toAjax(attendanceStatisticsService.insertAttendanceStatistics(attendanceStatistics));
    }

    /**
     * 修改考勤统计记录
     * 
     * @param attendanceStatistics 考勤统计对象
     * @return 操作结果（成功/失败）
     */
    @PutMapping
    public AjaxResult edit(@RequestBody AttendanceStatistics attendanceStatistics) {
        return toAjax(attendanceStatisticsService.updateAttendanceStatistics(attendanceStatistics));
    }

    /**
     * 删除考勤统计记录
     * 支持批量删除
     * 
     * @param statIds 统计 ID 数组
     * @return 操作结果（成功/失败）
     */
    @DeleteMapping("/{statIds}")
    public AjaxResult remove(@PathVariable Long[] statIds) {
        return toAjax(attendanceStatisticsService.deleteAttendanceStatisticsByIds(statIds));
    }

    /**
     * 按月统计考勤数据
     * 调用 Service 层计算指定月份的考勤统计数据
     * 
     * 统计内容包括：
     * - 打卡次数、出勤次数、缺勤次数
     * - 迟到次数、早退次数
     * - 值班总时长
     * 
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 操作结果（成功/失败）
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
     * 根据学生 ID 和年月查询考勤统计
     * 用于查询某个学生在指定月份的考勤统计详细信息
     * 
     * @param studentId 学号
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计对象
     */
    @GetMapping("/student/month")
    public AjaxResult getStudentStatisticsByMonth(@RequestParam String studentId, @RequestParam String yearMonth) {
        AttendanceStatistics statistics = attendanceStatisticsService.selectAttendanceStatisticsByStudentIdAndMonth(studentId, yearMonth);
        return success(statistics);
    }

    /**
     * 根据场馆 ID 和年月查询考勤统计列表
     * 用于查询某个场馆在指定月份所有学生的考勤统计数据
     * 
     * @param venueId 场馆 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 考勤统计列表
     */
    @GetMapping("/venue/month")
    public AjaxResult getVenueStatisticsByMonth(@RequestParam Integer venueId, @RequestParam String yearMonth) {
        List<AttendanceStatistics> statisticsList = attendanceStatisticsService.selectAttendanceStatisticsByVenueIdAndMonth(venueId, yearMonth);
        return success(statisticsList);
    }
}