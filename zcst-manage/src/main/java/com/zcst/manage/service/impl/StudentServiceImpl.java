package com.zcst.manage.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zcst.manage.mapper.StudentMapper;
import com.zcst.manage.domain.Student;
import com.zcst.manage.service.IStudentService;

/**
 * 学生管理Service业务层处理
 * 
 * @author zcst
 * @date 2026-03-18
 */
@Service
public class StudentServiceImpl implements IStudentService 
{
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 查询学生管理
     * 
     * @param studentId 学生管理主键
     * @return 学生管理
     */
    @Override
    public Student selectStudentByStudentId(String studentId)
    {
        return studentMapper.selectStudentByStudentId(studentId);
    }

    /**
     * 查询学生管理列表
     * 
     * @param student 学生管理
     * @return 学生管理
     */
    @Override
    public List<Student> selectStudentList(Student student)
    {
        return studentMapper.selectStudentList(student);
    }

    /**
     * 新增学生管理
     * 
     * @param student 学生管理
     * @return 结果
     */
    @Override
    public int insertStudent(Student student)
    {
        return studentMapper.insertStudent(student);
    }

    /**
     * 修改学生管理
     * 
     * @param student 学生管理
     * @return 结果
     */
    @Override
    public int updateStudent(Student student)
    {
        return studentMapper.updateStudent(student);
    }

    /**
     * 批量删除学生管理
     * 
     * @param studentIds 需要删除的学生管理主键
     * @return 结果
     */
    @Override
    public int deleteStudentByStudentIds(String[] studentIds)
    {
        return studentMapper.deleteStudentByStudentIds(studentIds);
    }

    /**
     * 删除学生管理信息
     * 
     * @param studentId 学生管理主键
     * @return 结果
     */
    @Override
    public int deleteStudentByStudentId(String studentId)
    {
        return studentMapper.deleteStudentByStudentId(studentId);
    }
}
