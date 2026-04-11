package com.zcst.manage.service;

import com.zcst.manage.domain.StudentNotice;

import java.util.List;

public interface IStudentNoticeService
{
    List<StudentNotice> selectMyNotices(String studentId, String readFlag);

    int markRead(Long noticeId, String studentId);

    int markReadAll(String studentId);

    int countUnread(String studentId);

    int pushNotice(StudentNotice notice);
}
