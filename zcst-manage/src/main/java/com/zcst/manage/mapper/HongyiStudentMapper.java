package com.zcst.manage.mapper;

import java.util.List;
import com.zcst.manage.domain.Student;

/**
 * 弘毅馆学生管理 Mapper 接口
 *
 * @author zcst
 * @date 2026-03-19
 */
public interface HongyiStudentMapper
{
    /**
     * 查询弘毅馆学生管理
     *
     * @param studentId 弘毅馆学生管理主键
     * @return 弘毅馆学生管理
     */
    public Student selectStudentByStudentId(String studentId);

    /**
     * 查询弘毅馆学生管理列表
     *
     * @param student 弘毅馆学生管理
     * @return 弘毅馆学生管理集合
     */
    public List<Student> selectStudentList(Student student);

    /**
     * 新增弘毅馆学生管理
     *
     * @param student 弘毅馆学生管理
     * @return 结果
     */
    public int insertStudent(Student student);

    /**
     * 修改弘毅馆学生管理
     *
     * @param student 弘毅馆学生管理
     * @return 结果
     */
    public int updateStudent(Student student);

    /**
     * 删除弘毅馆学生管理
     *
     * @param studentId 弘毅馆学生管理主键
     * @return 结果
     */
    public int deleteStudentByStudentId(String studentId);

    /**
     * 批量删除弘毅馆学生管理
     *
     * @param studentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStudentByStudentIds(String[] studentIds);
}
