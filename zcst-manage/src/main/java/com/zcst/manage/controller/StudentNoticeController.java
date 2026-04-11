package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.manage.domain.StudentNotice;
import com.zcst.manage.service.IStudentNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manage/student/notice")
@PreAuthorize("@ss.hasPermi('manage:studentNotice:student')")
/**
 * 学生端：通知中心
 *
 * 数据来源：student_notice（按人存储 readFlag/readTime）
 * 安全策略：studentId 一律取当前登录用户（userName）
 */
public class StudentNoticeController extends BaseController
{
    @Autowired
    private IStudentNoticeService studentNoticeService;

    @GetMapping("/list")
    /**
     * 通知列表
     *
     * @param readFlag 可选：0 未读 / 1 已读；不传返回全部
     */
    public AjaxResult list(@RequestParam(value = "readFlag", required = false) String readFlag)
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null) {
            return error("未登录或用户信息不存在");
        }
        String studentId = currentUser.getUserName();
        if (studentId == null || studentId.isEmpty()) {
            return error("无法获取用户学号");
        }
        List<StudentNotice> list = studentNoticeService.selectMyNotices(studentId, readFlag);
        return success(list);
    }

    @PutMapping("/read/{noticeId}")
    /**
     * 将单条通知标记为已读
     *
     * 只允许操作自己的通知（按 student_id 约束）。
     */
    public AjaxResult read(@PathVariable("noticeId") Long noticeId)
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null) {
            return error("未登录或用户信息不存在");
        }
        String studentId = currentUser.getUserName();
        if (studentId == null || studentId.isEmpty()) {
            return error("无法获取用户学号");
        }
        int rows = studentNoticeService.markRead(noticeId, studentId);
        return toAjax(rows);
    }

    @PutMapping("/readAll")
    /**
     * 将当前用户所有未读通知标记为已读
     */
    public AjaxResult readAll()
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null) {
            return error("未登录或用户信息不存在");
        }
        String studentId = currentUser.getUserName();
        if (studentId == null || studentId.isEmpty()) {
            return error("无法获取用户学号");
        }
        int rows = studentNoticeService.markReadAll(studentId);
        return toAjax(rows);
    }

    @GetMapping("/unread/count")
    /**
     * 未读数量
     */
    public AjaxResult unreadCount()
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null) {
            return error("未登录或用户信息不存在");
        }
        String studentId = currentUser.getUserName();
        if (studentId == null || studentId.isEmpty()) {
            return error("无法获取用户学号");
        }
        return success(studentNoticeService.countUnread(studentId));
    }
}
