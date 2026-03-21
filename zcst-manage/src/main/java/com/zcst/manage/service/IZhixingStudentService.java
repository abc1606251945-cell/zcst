package com.zcst.manage.service;

import java.util.List;
import com.github.pagehelper.PageInfo;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.StudentVo;

/**
 * 知行馆学生管理Service接口
 *
 * @author zcst
 * @date 2026-03-20
 */
public interface IZhixingStudentService
{
    /**
     * 查询知行馆学生管理
     *
     * @param studentId 知行馆学生管理主键
     * @return 知行馆学生管理
     */
    public StudentVo selectStudentByStudentId(String studentId);

    /**
     * 查询知行馆学生管理列表
     *
     * @param student 知行馆学生管理
     * @return 知行馆学生管理集合
     */
    public List<StudentVo> selectStudentList(Student student);

    /**
     * 查询知行馆学生管理列表（带分页信息）
     *
     * @param student 知行馆学生管理
     * @return 包含分页信息的知行馆学生管理列表
     */
    public PageInfo<StudentVo> selectStudentListWithPage(Student student);

    /**
     * 新增知行馆学生管理
     *
     * @param student 知行馆学生管理
     * @return 结果
     */
    public int insertStudent(Student student);

    /**
     * 修改知行馆学生管理
     *
     * @param student 知行馆学生管理
     * @return 结果
     */
    public int updateStudent(Student student);

    /**
     * 批量删除知行馆学生管理
     *
     * @param studentIds 需要删除的知行馆学生管理主键集合
     * @return 结果
     */
    public int deleteStudentByStudentIds(String[] studentIds);

    /**
     * 删除知行馆学生管理信息
     *
     * @param studentId 知行馆学生管理主键
     * @return 结果
     */
    public int deleteStudentByStudentId(String studentId);
}