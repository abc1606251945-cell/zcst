package com.zcst.manage.service;

import java.util.List;
import com.github.pagehelper.PageInfo;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.StudentVo;

/**
 * 笃学馆学生管理Service接口
 *
 * @author zcst
 * @date 2026-03-20
 */
public interface IDuxueStudentService
{
    /**
     * 查询笃学馆学生管理
     *
     * @param studentId 笃学馆学生管理主键
     * @return 笃学馆学生管理
     */
    public StudentVo selectStudentByStudentId(String studentId);

    /**
     * 查询笃学馆学生管理列表
     *
     * @param student 笃学馆学生管理
     * @return 笃学馆学生管理集合
     */
    public List<StudentVo> selectStudentList(Student student);

    /**
     * 查询笃学馆学生管理列表（带分页信息）
     *
     * @param student 笃学馆学生管理
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 包含分页信息的笃学馆学生管理列表
     */
    public PageInfo<StudentVo> selectStudentListWithPage(Student student, int pageNum, int pageSize);

    /**
     * 新增笃学馆学生管理
     *
     * @param student 笃学馆学生管理
     * @return 结果
     */
    public int insertStudent(Student student);

    /**
     * 修改笃学馆学生管理
     *
     * @param student 笃学馆学生管理
     * @return 结果
     */
    public int updateStudent(Student student);

    /**
     * 批量删除笃学馆学生管理
     *
     * @param studentIds 需要删除的笃学馆学生管理主键集合
     * @return 结果
     */
    public int deleteStudentByStudentIds(String[] studentIds);

    /**
     * 删除笃学馆学生管理信息
     *
     * @param studentId 笃学馆学生管理主键
     * @return 结果
     */
    public int deleteStudentByStudentId(String studentId);
}