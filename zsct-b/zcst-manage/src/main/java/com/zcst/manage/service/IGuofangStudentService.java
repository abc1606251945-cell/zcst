package com.zcst.manage.service;

import java.util.List;
import com.github.pagehelper.PageInfo;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.StudentVo;

/**
 * 国防教育体验馆学生管理Service接口
 *
 * @author zcst
 * @date 2026-03-20
 */
public interface IGuofangStudentService
{
    /**
     * 查询国防教育体验馆学生管理
     *
     * @param studentId 国防教育体验馆学生管理主键
     * @return 国防教育体验馆学生管理
     */
    public StudentVo selectStudentByStudentId(String studentId);

    /**
     * 查询国防教育体验馆学生管理列表
     *
     * @param student 国防教育体验馆学生管理
     * @return 国防教育体验馆学生管理集合
     */
    public List<StudentVo> selectStudentList(Student student);

    /**
     * 查询国防教育体验馆学生管理列表（带分页信息）
     *
     * @param student 国防教育体验馆学生管理
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 包含分页信息的国防教育体验馆学生管理列表
     */
    public PageInfo<StudentVo> selectStudentListWithPage(Student student, int pageNum, int pageSize);

    /**
     * 新增国防教育体验馆学生管理
     *
     * @param student 国防教育体验馆学生管理
     * @return 结果
     */
    public int insertStudent(Student student);

    /**
     * 修改国防教育体验馆学生管理
     *
     * @param student 国防教育体验馆学生管理
     * @return 结果
     */
    public int updateStudent(Student student);

    /**
     * 批量删除国防教育体验馆学生管理
     *
     * @param studentIds 需要删除的国防教育体验馆学生管理主键集合
     * @return 结果
     */
    public int deleteStudentByStudentIds(String[] studentIds);

    /**
     * 删除国防教育体验馆学生管理信息
     *
     * @param studentId 国防教育体验馆学生管理主键
     * @return 结果
     */
    public int deleteStudentByStudentId(String studentId);
}