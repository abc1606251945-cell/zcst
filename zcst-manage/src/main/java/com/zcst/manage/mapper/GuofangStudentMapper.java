package com.zcst.manage.mapper;

import java.util.List;
import com.zcst.manage.domain.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 国防教育体验馆学生管理Mapper接口
 *
 * @author zcst
 * @date 2026-03-20
 */
@Mapper
public interface GuofangStudentMapper
{
    /**
     * 查询国防教育体验馆学生管理
     *
     * @param studentId 国防教育体验馆学生管理主键
     * @return 国防教育体验馆学生管理
     */
    public Student selectStudentByStudentId(String studentId);

    /**
     * 查询国防教育体验馆学生管理列表
     *
     * @param student 国防教育体验馆学生管理
     * @return 国防教育体验馆学生管理集合
     */
    public List<Student> selectStudentList(Student student);

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
     * 删除国防教育体验馆学生管理
     *
     * @param studentId 国防教育体验馆学生管理主键
     * @return 结果
     */
    public int deleteStudentByStudentId(String studentId);

    /**
     * 批量删除国防教育体验馆学生管理
     *
     * @param studentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStudentByStudentIds(String[] studentIds);
}