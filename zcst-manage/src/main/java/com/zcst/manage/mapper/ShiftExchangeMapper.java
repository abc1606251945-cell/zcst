package com.zcst.manage.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 调班申请 Mapper 接口
 * 对应数据库表：shift_exchange
 * 
 * @author zcst
 * @version 1.2.0
 * @date 2026-03
 */
public interface ShiftExchangeMapper {
    /**
     * 根据学生 ID 和月份统计调班次数
     * 
     * @param studentId 学生 ID
     * @param yearMonth 年月（格式：yyyy-MM，如 2026-03）
     * @return 调班次数
     */
    int countByStudentIdAndMonth(@Param("studentId") String studentId, @Param("yearMonth") String yearMonth);
}
