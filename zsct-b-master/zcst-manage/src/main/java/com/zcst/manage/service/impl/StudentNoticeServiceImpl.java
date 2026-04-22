package com.zcst.manage.service.impl;

import com.zcst.common.constant.HttpStatus;
import com.zcst.common.exception.ServiceException;
import com.zcst.manage.domain.StudentNotice;
import com.zcst.manage.mapper.StudentNoticeMapper;
import com.zcst.manage.service.IStudentNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
/**
 * 学生通知中心服务实现
 *
 * 说明：
 * - 读写均通过 student_notice 表完成
 * - pushNotice 用于业务侧写入通知（如提交/撤销申请）
 */
public class StudentNoticeServiceImpl implements IStudentNoticeService
{
    @Autowired
    private StudentNoticeMapper studentNoticeMapper;

    @Override
    /**
     * 查询当前用户通知列表
     *
     * @param studentId 当前登录学生学号
     * @param readFlag 可选：0/1；为空返回全部
     */
    public List<StudentNotice> selectMyNotices(String studentId, String readFlag)
    {
        List<StudentNotice> list = studentNoticeMapper.selectStudentNoticeList(studentId, readFlag);
        for (StudentNotice n : list) {
            if (n.getPublishTime() != null) {
                n.setPublishTimeTs(n.getPublishTime().getTime());
            }
            if (n.getReadTime() != null) {
                n.setReadTimeTs(n.getReadTime().getTime());
            }
            if (n.getCreatedAt() != null) {
                n.setCreatedAtTs(n.getCreatedAt().getTime());
            }
            if (n.getUpdatedAt() != null) {
                n.setUpdatedAtTs(n.getUpdatedAt().getTime());
            }
        }
        return list;
    }

    @Override
    /**
     * 标记单条为已读（只允许操作本人通知）
     */
    public int markRead(Long noticeId, String studentId)
    {
        Date now = new Date();
        int rows = studentNoticeMapper.markRead(noticeId, studentId, now);
        if (rows > 0)
        {
            return rows;
        }
        StudentNotice owned = studentNoticeMapper.selectStudentNoticeByIdAndStudentId(noticeId, studentId);
        if (owned != null)
        {
            return 1;
        }
        throw new ServiceException("无权限操作该通知", HttpStatus.FORBIDDEN);
    }

    @Override
    /**
     * 全部已读（当前用户）
     */
    public int markReadAll(String studentId)
    {
        Date now = new Date();
        return studentNoticeMapper.markReadAll(studentId, now);
    }

    @Override
    /**
     * 未读数量
     */
    public int countUnread(String studentId)
    {
        return studentNoticeMapper.countUnread(studentId);
    }

    @Override
    /**
     * 推送通知（写入 student_notice）
     *
     * 约定：
     * - publishTime 为空则使用当前时间
     * - readFlag 为空默认 0（未读）
     */
    public int pushNotice(StudentNotice notice)
    {
        Date now = new Date();
        if (notice.getPublishTime() == null) {
            notice.setPublishTime(now);
        }
        if (notice.getReadFlag() == null || notice.getReadFlag().isEmpty()) {
            notice.setReadFlag("0");
        }
        notice.setCreatedAt(now);
        notice.setUpdatedAt(now);
        return studentNoticeMapper.insertStudentNotice(notice);
    }
}
