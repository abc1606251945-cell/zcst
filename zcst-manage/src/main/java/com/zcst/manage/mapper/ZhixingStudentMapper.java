package com.zcst.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.zcst.manage.domain.Student;

/**
 * 知行馆学生管理Mapper接口
 *
 * @author zcst
 * @date 2026-03-20
 */
@Mapper
public interface ZhixingStudentMapper
{
    /**
     * 查询知行馆学生管理
     *
     * @param studentId 知行馆学生管理主键
     * @return 知行馆学生管理
     */
    public Student selectStudentByStudentId(String studentId);

    /**
     * 查询知行馆学生管理列表
     *
     * @param student 知行馆学生管理
     * @return 知行馆学生管理集合
     */
    public List<Student> selectStudentList(Student student);

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
     * 删除知行馆学生管理
     *
     * @param studentId 知行馆学生管理主键
     * @return 结果
     */
    public int deleteStudentByStudentId(String studentId);

    /**
     * 批量删除知行馆学生管理
     *
     * @param studentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStudentByStudentIds(String[] studentIds);
}