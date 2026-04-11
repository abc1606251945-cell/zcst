package com.zcst.manage.controller;

import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.manage.domain.Vo.DutyApplicationVo;
import com.zcst.manage.domain.dto.SubmitDutyApplicationDTO;
import com.zcst.manage.service.IDutyApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manage/duty/application")
@PreAuthorize("@ss.hasPermi('manage:dutyApplication:student')")
/**
 * 学生端：值班申请接口
 *
 * 统一入口覆盖两类申请：
 * - leave：请假（leave_application）
 * - swap：调班（实际语义为替班，shift_exchange）
 *
 * 安全策略：
 * - studentId 一律取当前登录用户（userName），不信任前端传参
 * - dutyId 必须属于当前登录学生
 */
public class DutyApplicationController extends BaseController
{
    @Autowired
    private IDutyApplicationService dutyApplicationService;

    @PostMapping
    /**
     * 提交申请
     *
     * @param dto 申请参数（applicationType/dutyId/reason/relatedStudentId 等）
     */
    public AjaxResult submit(@RequestBody SubmitDutyApplicationDTO dto)
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null) {
            return error("未登录或用户信息不存在");
        }
        String studentId = currentUser.getUserName();
        if (studentId == null || studentId.isEmpty()) {
            return error("无法获取用户学号");
        }
        DutyApplicationVo data = dutyApplicationService.submit(studentId, dto);
        return success(data);
    }

    @GetMapping("/my")
    /**
     * 我的申请列表（leave + swap 混合返回）
     *
     * @param status 可选：pending/approved/rejected/cancelled；不传则返回全部
     */
    public AjaxResult my(@RequestParam(value = "status", required = false) String status)
    {
        SysUser currentUser = SecurityUtils.getLoginUser().getUser();
        if (currentUser == null) {
            return error("未登录或用户信息不存在");
        }
        String studentId = currentUser.getUserName();
        if (studentId == null || studentId.isEmpty()) {
            return error("无法获取用户学号");
        }
        List<DutyApplicationVo> list = dutyApplicationService.selectMyApplications(studentId, status);
        return success(list);
    }

    @GetMapping("/{id}")
    /**
     * 申请详情
     *
     * @param id 申请ID（根据 applicationType 选择 leave_id 或 exchange_id）
     * @param applicationType leave | swap
     */
    public AjaxResult detail(
        @PathVariable("id") Integer id,
        @RequestParam("applicationType") String applicationType
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
        DutyApplicationVo data = dutyApplicationService.selectMyApplicationDetail(studentId, applicationType, id);
        if (data == null) {
            return error("记录不存在");
        }
        return success(data);
    }

    @PutMapping("/cancel/{id}")
    /**
     * 撤销申请
     *
     * 规则：
     * - leave：仅允许 status=0（待审批）撤销，撤销后 status=3（cancelled）
     * - swap：仅允许 status in (0,3) 撤销，撤销后 status=4（cancelled）
     *
     * @param id 申请ID
     * @param applicationType leave | swap
     * @param reason 可选撤销原因
     */
    public AjaxResult cancel(
        @PathVariable("id") Integer id,
        @RequestParam("applicationType") String applicationType,
        @RequestParam(value = "reason", required = false) String reason
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
        dutyApplicationService.cancelMyApplication(studentId, applicationType, id, reason);
        return success();
    }
}
