package com.zcst.manage.service;

import com.github.pagehelper.PageInfo;

import java.util.List;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.StudentVo;

/**
 * 学生管理Service接口
 * 
 * @author zcst
 * @date 2026-03-18
 */
public interface IStudentService 
{
    /**
     * 查询学生管理
     * 
     * @param studentId 学生管理主键
     * @return 学生管理 VO
     */
    public StudentVo selectStudentByStudentId(String studentId);

    /**
     * 查询学生管理列表
     * 
     * @param student 学生管理
     * @return 学生管理 VO
     */
    public List<StudentVo> selectStudentList(Student student);
    
    /**
     * 查询学生管理列表（带分页信息）
     * 
     * @param student 学生管理
     * @return 包含分页信息的学生管理 VO 列表
     */
    public PageInfo<StudentVo> selectStudentListWithPage(Student student);

    /**
     * 新增学生管理
     * 
     * @param student 学生管理
     * @return 结果
     */
    public int insertStudent(Student student);

    /**
     * 修改学生管理
     * 
     * @param student 学生管理
     * @return 结果
     */
    public int updateStudent(Student student);

    /**
     * 批量删除学生管理
     * 
     * @param studentIds 需要删除的学生管理主键集合
     * @return 结果
     */
    public int deleteStudentByStudentIds(String[] studentIds);

    /**
     * 删除学生管理信息
     * 
     * @param studentId 学生管理主键
     * @return 结果
     */
    public int deleteStudentByStudentId(String studentId);
}
