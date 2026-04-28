package com.zcst.manage.domain;

import com.zcst.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 调班申请表实体类
 * 对应数据库表：shift_exchange
 * 
 * 用于学生申请调班（与他人交换值班时间），支持以下功能：
 * - 提交调班申请（申请人 A 和替换人 B）
 * - 替换人确认（B 确认愿意替换）
 * - 管理员审批（通过/拒绝）
 * - 更新值班表（审批通过后自动更新）
 * 
 * 调班流程：
 * 1. 申请人 A 提交调班申请，status="0"（待审批）
 * 2. 替换人 B 确认，studentBConfirm="1"，status="3"（已确认）
 * 3. 管理员审批，status="1"（通过）或"2"（拒绝）
 * 4. 审批通过后，系统自动更新值班表中的学生 ID
 * 
 * 审批状态：
 * - "0": 待审批（A 提交申请，等待 B 确认）
 * - "1": 已通过（管理员批准）
 * - "2": 已拒绝（管理员或 B 拒绝）
 * - "3": 已确认（B 已确认，等待管理员审批）
 * 
 * 业务规则：
 * - 替换人 B 必须确认同意
 * - 调班申请必须在值班开始前提交
 * - 审批通过后自动更新值班表
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ShiftExchange extends BaseEntity {
    /**
     * 序列化版本 UID
     * 用于对象序列化时的版本控制
     */
    private static final long serialVersionUID = 1L;

    /**
     * 调班 ID（主键自增）
     * 对应数据库字段：exchange_id
     */
    private Integer exchangeId;

    /**
     * 申请人学号（A）
     * 对应数据库字段：student_id_a
     * 外键关联：sys_user 表
     * 发起调班申请的学生
     */
    private String studentIdA;

    /**
     * 申请人姓名（A）
     * 对应数据库字段：student_name_a
     * 冗余字段，便于快速显示
     */
    private String studentNameA;

    /**
     * 替换人学号（B）
     * 对应数据库字段：student_id_b
     * 外键关联：sys_user 表
     * 被请求替换值班的学生
     */
    private String studentIdB;

    /**
     * 替换人姓名（B）
     * 对应数据库字段：student_name_b
     * 冗余字段，便于快速显示
     */
    private String studentNameB;

    /**
     * 场馆 ID
     * 对应数据库字段：venue_id
     * 外键关联：venue 表
     * 调班所属的场馆
     */
    private Integer venueId;

    /**
     * 原值班 ID
     * 对应数据库字段：duty_id
     * 外键关联：duty_schedule 表
     * 需要调班的值班记录 ID
     */
    private Integer dutyId;

    /**
     * 调班原因
     * 对应数据库字段：exchange_reason
     * 必填字段，详细说明调班事由
     */
    private String exchangeReason;

    /**
     * 审批状态
     * 对应数据库字段：status
     * 取值范围：
     * - "0": 待审批（A 提交申请，等待 B 确认）
     * - "1": 已通过（管理员批准）
     * - "2": 已拒绝（管理员或 B 拒绝）
     * - "3": 已确认（B 已确认，等待管理员审批）
     */
    private String status;

    /**
     * 审批人 ID
     * 对应数据库字段：approver_id
     * 外键关联：sys_user 表（管理员）
     * 记录审批该申请的管理员 ID
     */
    private String approverId;

    /**
     * 审批时间
     * 对应数据库字段：approve_time
     * 格式：yyyy-MM-dd HH:mm:ss
     * 记录管理员审批的时间
     */
    private Date approveTime;

    /**
     * 审批意见
     * 对应数据库字段：approve_remark
     * 管理员填写的审批意见
     * 拒绝时必须填写拒绝原因
     */
    private String approveRemark;

    /**
     * 替换人确认状态（B 确认）
     * 对应数据库字段：student_b_confirm
     * 取值范围：
     * - "0": 未确认（默认状态）
     * - "1": 已确认（B 同意替换）
     * 替换人必须确认后才能提交管理员审批
     */
    private String studentBConfirm;

    /**
     * 替换人确认时间
     * 对应数据库字段：student_b_confirm_time
     * 格式：yyyy-MM-dd HH:mm:ss
     * 记录替换人 B 确认的时间
     */
    private Date studentBConfirmTime;

    private Date cancelTime;

    private String cancelReason;

    private Date createdAt;

    private Date updatedAt;
}
