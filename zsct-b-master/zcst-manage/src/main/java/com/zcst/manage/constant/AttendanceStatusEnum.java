package com.zcst.manage.constant;

/**
 * 考勤状态枚举类
 * 定义考勤记录和值班表的统一状态码
 * 
 * @author zcst
 * @date 2026-03
 */
public class AttendanceStatusEnum {
    
    /**
     * 考勤状态：正常
     * 表示学生按时打卡并签退
     */
    public static final String NORMAL = "0";
    
    /**
     * 考勤状态：迟到
     * 表示学生打卡时间晚于值班开始时间
     */
    public static final String LATE = "1";
    
    /**
     * 考勤状态：早退
     * 表示学生签退时间早于值班结束时间
     */
    public static final String EARLY_LEAVE = "2";
    
    /**
     * 考勤状态：缺勤
     * 表示学生未进行打卡
     */
    public static final String ABSENCE = "3";
    
    /**
     * 值班状态：未打卡
     * 用于 duty_schedule 表的 attendance_status 字段
     */
    public static final String NOT_CHECKED = "0";
    
    /**
     * 值班状态：已打卡
     * 用于 duty_schedule 表的 attendance_status 字段
     */
    public static final String CHECKED = "1";
    
    /**
     * 根据状态码获取状态描述（考勤记录）
     * 用于 attendance_record 和 attendance_statistics 表
     * 
     * @param status 状态码
     * @return 状态描述
     */
    public static String getAttendanceStatusName(String status) {
        switch (status) {
            case NORMAL:
                return "正常";
            case LATE:
                return "迟到";
            case EARLY_LEAVE:
                return "早退";
            case ABSENCE:
                return "缺勤";
            default:
                return "未知状态";
        }
    }
    
    /**
     * 根据状态码获取状态描述（值班表）
     * 用于 duty_schedule 表
     * 
     * @param status 状态码
     * @return 状态描述
     */
    public static String getDutyStatusName(String status) {
        switch (status) {
            case NOT_CHECKED:
                return "未打卡";
            case CHECKED:
                return "已打卡";
            default:
                return "未知状态";
        }
    }
}
