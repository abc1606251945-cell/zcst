package com.zcst.manage.domain;

import com.zcst.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 考勤记录表实体类
 * 对应数据库表：attendance_record
 * 
 * 用于记录学生每次值班的考勤情况，包括：
 * - 打卡时间（check_in_time）
 * - 签退时间（check_out_time）
 * - 实际值班时长（actual_duty_hours）
 * - 考勤状态（status：0-正常，1-迟到，2-早退，3-缺勤）
 * 
 * 业务流程：
 * 1. 学生到达场馆后调用打卡接口，记录 checkInTime
 * 2. 学生值班结束后调用签退接口，记录 checkOutTime 并计算 actualDutyHours
 * 3. 系统根据打卡时间和值班开始时间判断是否迟到
 * 4. 系统根据签退时间和值班结束时间判断是否早退
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class AttendanceRecord extends BaseEntity {
    /**
     * 序列化版本 UID
     * 用于对象序列化时的版本控制
     */
    private static final long serialVersionUID = 1L;

    /**
     * 记录 ID（主键自增）
     * 对应数据库字段：record_id
     */
    private Long recordId;

    /**
     * 学号（学生唯一标识）
     * 对应数据库字段：student_id
     * 外键关联：sys_user 表
     */
    private String studentId;

    /**
     * 值班表 ID
     * 对应数据库字段：duty_id
     * 外键关联：duty_schedule 表
     * 用于关联具体的值班安排
     */
    private Integer dutyId;

    /**
     * 场馆 ID（通过 duty_schedule 表关联）
     * 用于按场馆过滤考勤记录
     */
    private Integer venueId;

    /**
     * 打卡时间（到达场馆时间）
     * 对应数据库字段：check_in_time
     * 由学生在值班开始时主动打卡记录
     */
    private Date checkInTime;

    /**
     * 签退时间（离开场馆时间）
     * 对应数据库字段：check_out_time
     * 由学生在值班结束时主动签退记录
     * 系统根据此时间和打卡时间计算实际值班时长
     */
    private Date checkOutTime;

    /**
     * 实际值班时长（单位：小时）
     * 对应数据库字段：actual_duty_hours
     * 精度：保留 2 位小数
     * 计算公式：签退时间 - 打卡时间
     * 用于统计学生的实际工作时长
     */
    private Double actualDutyHours;

    /**
     * 考勤状态
     * 对应数据库字段：status
     * 取值范围：
     * - "0": 正常（按时打卡和签退）
     * - "1": 迟到（晚于规定时间打卡）
     * - "2": 早退（早于规定时间签退）
     * - "3": 缺勤（未打卡）
     */
    private String status;

    /**
     * 备注说明
     * 对应数据库字段：remark
     * 用于记录特殊情况说明（如补卡原因等）
     */
    private String remark;

    /**
     * 创建时间
     * 对应数据库字段：created_at
     * 记录创建时的时间戳
     */
    private Date createdAt;

    /**
     * 更新时间
     * 对应数据库字段：updated_at
     * 记录最后一次修改的时间戳
     */
    private Date updatedAt;
}
