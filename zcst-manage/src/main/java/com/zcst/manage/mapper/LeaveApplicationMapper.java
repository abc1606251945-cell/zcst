package com.zcst.manage.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 请假申请 Mapper 接口
 * 对应数据库表：leave_application
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
public interface LeaveApplicationMapper {
    /**
     * 根据学生 ID 和月份统计请假次数
     * 
     * @param studentId 学生 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 请假次数
     */
    int countByStudentIdAndMonth(@Param("studentId") String studentId, @Param("yearMonth") String yearMonth);
}
