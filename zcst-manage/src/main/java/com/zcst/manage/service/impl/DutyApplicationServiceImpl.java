package com.zcst.manage.service.impl;

import com.zcst.common.constant.HttpStatus;
import com.zcst.common.exception.ServiceException;
import com.zcst.manage.domain.DutySchedule;
import com.zcst.manage.domain.LeaveApplication;
import com.zcst.manage.domain.ShiftExchange;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.StudentNotice;
import com.zcst.manage.domain.Vo.DutyApplicationVo;
import com.zcst.manage.domain.dto.SubmitDutyApplicationDTO;
import com.zcst.manage.mapper.DutyScheduleMapper;
import com.zcst.manage.mapper.LeaveApplicationMapper;
import com.zcst.manage.mapper.ShiftExchangeMapper;
import com.zcst.manage.mapper.StudentMapper;
import com.zcst.manage.service.IDutyApplicationService;
import com.zcst.manage.service.IStudentNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
/**
 * 学生端：值班申请业务实现
 *
 * 设计点：
 * - applicationType=leave：落表 leave_application
 * - applicationType=swap：落表 shift_exchange（语义为“替班”，非互换）
 * - 对外统一返回 DutyApplicationVo，并通过 SQL 做状态映射（pending/approved/rejected/cancelled）
 *
 * 通知：
 * - 提交/撤销会写入 student_notice，供小程序“通知中心”展示
 */
public class DutyApplicationServiceImpl implements IDutyApplicationService
{
    @Autowired
    private LeaveApplicationMapper leaveApplicationMapper;

    @Autowired
    private ShiftExchangeMapper shiftExchangeMapper;

    @Autowired
    private DutyScheduleMapper dutyScheduleMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private IStudentNoticeService studentNoticeService;

    @Override
    @Transactional
    /**
     * 提交申请
     *
     * 校验要点：
     * - dutyId 必须存在且属于当前学生
     * - swap 必须提供 relatedStudentId（替班人）且不能是本人
     *
     * @param studentId 当前登录学生学号
     * @param dto 提交参数
     * @return 新建申请的详情（用于前端回显）
     */
    public DutyApplicationVo submit(String studentId, SubmitDutyApplicationDTO dto)
    {
        if (dto == null || dto.getApplicationType() == null || dto.getApplicationType().isEmpty()) {
            throw new ServiceException("参数错误：applicationType 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (dto.getDutyId() == null) {
            throw new ServiceException("参数错误：dutyId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (dto.getReason() == null || dto.getReason().isEmpty()) {
            throw new ServiceException("参数错误：reason 不能为空", HttpStatus.BAD_REQUEST);
        }

        DutySchedule duty = dutyScheduleMapper.selectDutyScheduleByDutyId(dto.getDutyId());
        if (duty == null) {
            throw new ServiceException("参数错误：值班不存在", HttpStatus.BAD_REQUEST);
        }
        if (duty.getStudentId() == null || !duty.getStudentId().equals(studentId)) {
            throw new ServiceException("无权限操作该值班", HttpStatus.FORBIDDEN);
        }

        Student me = studentMapper.selectStudentByStudentId(studentId);
        String myName = me == null ? null : me.getName();

        Date now = new Date();
        String type = dto.getApplicationType().trim();

        if ("leave".equalsIgnoreCase(type)) {
            LeaveApplication la = new LeaveApplication();
            la.setStudentId(studentId);
            la.setStudentName(myName);
            la.setVenueId(duty.getVenueId());
            la.setDutyId(dto.getDutyId());
            la.setLeaveType(dto.getLeaveType());
            la.setStartTime(duty.getStartTime());
            la.setEndTime(duty.getEndTime());
            la.setReason(dto.getReason());
            la.setProofImage(dto.getProofImage());
            la.setStatus("0");
            la.setCreatedAt(now);
            la.setUpdatedAt(now);
            leaveApplicationMapper.insertLeaveApplication(la);

            StudentNotice notice = new StudentNotice();
            notice.setStudentId(studentId);
            notice.setTitle("请假申请已提交");
            notice.setContent("你的请假申请已提交，等待审批。");
            notice.setType("approve");
            notice.setBizType("leave");
            notice.setBizId(String.valueOf(la.getLeaveId()));
            studentNoticeService.pushNotice(notice);

            return leaveApplicationMapper.selectLeaveApplicationDetail(la.getLeaveId(), studentId);
        }

        if ("swap".equalsIgnoreCase(type)) {
            if (dto.getRelatedStudentId() == null || dto.getRelatedStudentId().isEmpty()) {
                throw new ServiceException("参数错误：relatedStudentId 不能为空", HttpStatus.BAD_REQUEST);
            }
            if (studentId.equals(dto.getRelatedStudentId())) {
                throw new ServiceException("参数错误：relatedStudentId 不能是本人", HttpStatus.BAD_REQUEST);
            }
            Student b = studentMapper.selectStudentByStudentId(dto.getRelatedStudentId());
            if (b == null) {
                throw new ServiceException("参数错误：替班人不存在", HttpStatus.BAD_REQUEST);
            }

            ShiftExchange se = new ShiftExchange();
            se.setStudentIdA(studentId);
            se.setStudentNameA(myName);
            se.setStudentIdB(dto.getRelatedStudentId());
            se.setStudentNameB(b.getName());
            se.setVenueId(duty.getVenueId());
            se.setDutyId(dto.getDutyId());
            se.setExchangeReason(dto.getReason());
            se.setStatus("0");
            se.setStudentBConfirm("0");
            se.setCreatedAt(now);
            se.setUpdatedAt(now);
            shiftExchangeMapper.insertShiftExchange(se);

            StudentNotice notice = new StudentNotice();
            notice.setStudentId(studentId);
            notice.setTitle("调班申请已提交");
            notice.setContent("你的调班申请已提交，等待处理。");
            notice.setType("approve");
            notice.setBizType("swap");
            notice.setBizId(String.valueOf(se.getExchangeId()));
            studentNoticeService.pushNotice(notice);

            return shiftExchangeMapper.selectShiftExchangeDetail(se.getExchangeId(), studentId);
        }

        throw new ServiceException("参数错误：不支持的 applicationType", HttpStatus.BAD_REQUEST);
    }

    @Override
    /**
     * 查询我的申请列表（leave + swap 混合）
     *
     * @param studentId 当前登录学生学号
     * @param status 统一状态 pending/approved/rejected/cancelled（也兼容传原始状态码）
     */
    public List<DutyApplicationVo> selectMyApplications(String studentId, String status)
    {
        List<DutyApplicationVo> list = new ArrayList<>();
        list.addAll(leaveApplicationMapper.selectMyLeaveApplications(studentId, status));
        list.addAll(shiftExchangeMapper.selectMyShiftExchangeApplications(studentId, status));
        for (DutyApplicationVo vo : list) {
            if (vo.getStartTime() != null) {
                vo.setStartTimeTs(vo.getStartTime().getTime());
            }
            if (vo.getEndTime() != null) {
                vo.setEndTimeTs(vo.getEndTime().getTime());
            }
            if (vo.getApproveTime() != null) {
                vo.setApproveTimeTs(vo.getApproveTime().getTime());
            }
            if (vo.getCancelTime() != null) {
                vo.setCancelTimeTs(vo.getCancelTime().getTime());
            }
            if (vo.getCreatedAt() != null) {
                vo.setCreatedAtTs(vo.getCreatedAt().getTime());
            }
        }
        list.sort(Comparator.comparing(DutyApplicationVo::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return list;
    }

    @Override
    /**
     * 查询我的申请详情
     *
     * @param studentId 当前登录学生学号
     * @param applicationType leave | swap
     * @param id 申请ID
     */
    public DutyApplicationVo selectMyApplicationDetail(String studentId, String applicationType, Integer id)
    {
        if (applicationType == null || applicationType.isEmpty() || id == null) {
            throw new ServiceException("参数错误：applicationType 或 id 不能为空", HttpStatus.BAD_REQUEST);
        }
        DutyApplicationVo vo;
        if ("leave".equalsIgnoreCase(applicationType)) {
            vo = leaveApplicationMapper.selectLeaveApplicationDetail(id, studentId);
        } else if ("swap".equalsIgnoreCase(applicationType)) {
            vo = shiftExchangeMapper.selectShiftExchangeDetail(id, studentId);
        } else {
            throw new ServiceException("参数错误：不支持的 applicationType", HttpStatus.BAD_REQUEST);
        }
        if (vo == null) {
            return null;
        }
        if (vo.getStartTime() != null) {
            vo.setStartTimeTs(vo.getStartTime().getTime());
        }
        if (vo.getEndTime() != null) {
            vo.setEndTimeTs(vo.getEndTime().getTime());
        }
        if (vo.getApproveTime() != null) {
            vo.setApproveTimeTs(vo.getApproveTime().getTime());
        }
        if (vo.getCancelTime() != null) {
            vo.setCancelTimeTs(vo.getCancelTime().getTime());
        }
        if (vo.getCreatedAt() != null) {
            vo.setCreatedAtTs(vo.getCreatedAt().getTime());
        }
        return vo;
    }

    @Override
    @Transactional
    /**
     * 撤销我的申请
     *
     * 撤销规则由 SQL 约束：
     * - leave：status=0 才能撤销 -> status=3
     * - swap：status in (0,3) 才能撤销 -> status=4
     *
     * @param studentId 当前登录学生学号
     * @param applicationType leave | swap
     * @param id 申请ID
     * @param cancelReason 可选撤销原因
     * @return 影响行数（>0 表示撤销成功）
     */
    public int cancelMyApplication(String studentId, String applicationType, Integer id, String cancelReason)
    {
        Date now = new Date();
        int rows = 0;
        if ("leave".equalsIgnoreCase(applicationType)) {
            rows = leaveApplicationMapper.cancelLeaveApplication(id, studentId, now, cancelReason);
        } else if ("swap".equalsIgnoreCase(applicationType)) {
            rows = shiftExchangeMapper.cancelShiftExchange(id, studentId, now, cancelReason);
        } else {
            throw new ServiceException("参数错误：不支持的 applicationType", HttpStatus.BAD_REQUEST);
        }
        if (rows <= 0)
        {
            throw new ServiceException("申请已处理，不能撤销", HttpStatus.CONFLICT);
        }
        if (rows > 0) {
            StudentNotice notice = new StudentNotice();
            notice.setStudentId(studentId);
            notice.setTitle("申请已撤销");
            notice.setContent("你的申请已撤销。");
            notice.setType("approve");
            notice.setBizType(applicationType);
            notice.setBizId(String.valueOf(id));
            studentNoticeService.pushNotice(notice);
        }
        return rows;
    }
}
