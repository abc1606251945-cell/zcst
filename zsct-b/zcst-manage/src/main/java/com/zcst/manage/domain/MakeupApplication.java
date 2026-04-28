package com.zcst.manage.domain;

import com.zcst.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 补卡申请表实体类
 * 对应数据库表：makeup_application
 * 
 * 用于学生申请补卡（忘记打卡时），支持以下功能：
 * - 提交补卡申请（填写缺卡类型、实际时间、原因）
 * - 上传证明材料（现场照片等）
 * - 管理员审批（通过/拒绝）
 * - 审批通过后自动创建考勤记录
 * 
 * 缺卡类型：
 * - "0": 上班未打卡（忘记打卡，但实际到岗了）
 * - "1": 下班未打卡（忘记签退，但实际值班结束了）
 * - "2": 都未打卡（完全忘记打卡和签退）
 * 
 * 补卡流程：
 * 1. 学生提交补卡申请，status="0"（待审批）
 * 2. 填写实际上班/下班时间和补卡原因
 * 3. 上传证明材料（如现场照片、监控截图等）
 * 4. 管理员审批，status="1"（通过）或"2"（拒绝）
 * 5. 审批通过后，系统自动创建或更新考勤记录
 * 
 * 业务规则：
 * - 补卡申请必须在值班结束后 24 小时内提交
 * - 必须填写补卡原因和实际时间
 * - 建议上传证明材料提高通过率
 * - 每月补卡次数限制（如不超过 3 次）
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class MakeupApplication extends BaseEntity {
    /**
     * 序列化版本 UID
     * 用于对象序列化时的版本控制
     */
    private static final long serialVersionUID = 1L;

    /**
     * 补卡 ID（主键自增）
     * 对应数据库字段：makeup_id
     */
    private Integer makeupId;

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
     * 补卡所属的场馆
     */
    private Integer venueId;

    /**
     * 值班 ID
     * 对应数据库字段：duty_id
     * 外键关联：duty_schedule 表
     * 需要补卡的值班记录 ID
     */
    private Integer dutyId;

    /**
     * 缺卡类型
     * 对应数据库字段：miss_type
     * 取值范围：
     * - "0": 上班未打卡（忘记打卡，但实际到岗了）
     * - "1": 下班未打卡（忘记签退，但实际值班结束了）
     * - "2": 都未打卡（完全忘记打卡和签退）
     */
    private String missType;

    /**
     * 实际上班时间
     * 对应数据库字段：actual_start_time
     * 格式：yyyy-MM-dd HH:mm:ss
     * 学生实际到达场馆的时间
     * 用于补录打卡时间
     */
    private Date actualStartTime;

    /**
     * 实际下班时间
     * 对应数据库字段：actual_end_time
     * 格式：yyyy-MM-dd HH:mm:ss
     * 学生实际离开场馆的时间
     * 用于补录签退时间
     */
    private Date actualEndTime;

    /**
     * 补卡原因
     * 对应数据库字段：reason
     * 必填字段，详细说明忘记打卡的原因
     * 如：打卡系统故障、手机没电、紧急情况等
     */
    private String reason;

    /**
     * 证明材料图片 URL
     * 对应数据库字段：proof_image
     * 可选字段，建议上传相关证明
     * 如：现场照片、监控截图、证人证言等
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
