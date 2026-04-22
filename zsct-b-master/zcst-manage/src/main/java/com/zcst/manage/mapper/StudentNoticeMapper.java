package com.zcst.manage.mapper;

import com.zcst.manage.domain.StudentNotice;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 学生通知 Mapper
 *
 * 对应表：student_notice
 */
public interface StudentNoticeMapper
{
    /**
     * 新增通知
     */
    int insertStudentNotice(StudentNotice studentNotice);

    /**
     * 查询通知列表（按 studentId，readFlag 可选）
     */
    List<StudentNotice> selectStudentNoticeList(@Param("studentId") String studentId, @Param("readFlag") String readFlag);

    /**
     * 查询通知详情
     */
    StudentNotice selectStudentNoticeById(@Param("noticeId") Long noticeId);

    StudentNotice selectStudentNoticeByIdAndStudentId(@Param("noticeId") Long noticeId, @Param("studentId") String studentId);

    /**
     * 标记单条已读（带 studentId 约束）
     */
    int markRead(@Param("noticeId") Long noticeId, @Param("studentId") String studentId, @Param("readTime") Date readTime);

    /**
     * 全部已读（按 studentId 批量更新）
     */
    int markReadAll(@Param("studentId") String studentId, @Param("readTime") Date readTime);

    /**
     * 未读数量
     */
    int countUnread(@Param("studentId") String studentId);
}
