package com.zcst.manage.domain;

import com.zcst.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 请假申请表实体类
 * 对应数据库表：leave_application
 * 
 * 用于学生提交请假申请，支持以下功能：
 * - 提交请假申请（填写请假类型、时间、原因）
 * - 上传证明材料（病假条等）
 * - 管理员审批（通过/拒绝）
 * - 审批意见记录
 * 
 * 请假类型：
 * - "0": 病假（需提供医院证明）
 * - "1": 事假（个人事务）
 * - "2": 其他（特殊情况）
 * 
 * 审批流程：
 * 1. 学生提交申请，status 设为"0"（待审批）
 * 2. 管理员审批，status 更新为"1"（通过）或"2"（拒绝）
 * 3. 记录审批人、审批时间和审批意见
 * 
 * 业务规则：
 * - 请假时间不能早于当前时间
 * - 必须填写请假原因
 * - 病假建议上传证明材料
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class LeaveApplication extends BaseEntity {
    /**
     * 序列化版本 UID
     * 用于对象序列化时的版本控制
     */
    private static final long serialVersionUID = 1L;

    /**
     * 请假 ID（主键自增）
     * 对应数据库字段：leave_id
     */
    private Integer leaveId;

    /**
     * 学号（申请人）
     * 对应数据库字段：student_id
     * 外键关联：sys_user 表
     */
    private String studentId;

    /**
     * 学生姓名（申请人）
     * 对应数据库字段：student_name
     * 冗余字段，便于快速显示
     */
    private String studentName;

    /**
     * 场馆 ID
     * 对应数据库字段：venue_id
     * 外键关联：venue 表
     * 用于标识请假所属的场馆
     */
    private Integer venueId;

    /**
     * 值班 ID（可选）
     * 对应数据库字段：duty_id
     * 外键关联：duty_schedule 表
     * 如果请假针对特定值班，则填写此字段
     */
    private Integer dutyId;

    /**
     * 请假类型
     * 对应数据库字段：leave_type
     * 取值范围：
     * - "0": 病假（需提供医院证明）
     * - "1": 事假（个人事务）
     * - "2": 其他（特殊情况）
     */
    private String leaveType;

    /**
     * 请假开始时间
     * 对应数据库字段：start_time
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    private Date startTime;

    /**
     * 请假结束时间
     * 对应数据库字段：end_time
     * 格式：yyyy-MM-dd HH:mm:ss
     * 必须晚于开始时间
     */
    private Date endTime;

    /**
     * 请假原因
     * 对应数据库字段：reason
     * 必填字段，详细说明请假事由
     */
    private String reason;

    /**
     * 证明材料图片 URL
     * 对应数据库字段：proof_image
     * 可选字段，病假建议上传医院证明
     * 存储路径：文件服务器 URL 或相对路径
     */
    private String proofImage;

    /**
     * 审批状态
     * 对应数据库字段：status
     * 取值范围：
     * - "0": 待审批（刚提交申请）
     * - "1": 已通过（管理员批准）
     * - "2": 已拒绝（管理员拒绝）
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
}
