package com.zcst.manage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.zcst.manage.mapper.SiqiStudentMapper;
import com.zcst.manage.service.ISiqiStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.zcst.manage.mapper.StudentMapper;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.StudentVo;
import com.zcst.manage.service.IStudentService;
import com.zcst.system.mapper.SysPostMapper;
import com.zcst.system.domain.SysPost;

/**
 * 思齐馆学生管理Service业务层处理
 *
 * @author zcst
 * @date 2026-03-19
 */
@Service
public class SiqiStudentServiceImpl implements ISiqiStudentService
{
    @Autowired
    private SiqiStudentMapper siqiStudentMapper;
    
    @Autowired
    private SysPostMapper sysPostMapper;

    /**
     * 查询思齐馆学生管理
     *
     * @param studentId 思齐馆学生管理主键
     * @return 思齐馆学生管理 VO
     */
    @Override
    public StudentVo selectStudentByStudentId(String studentId)
    {
        Student student = siqiStudentMapper.selectStudentByStudentId(studentId);
        return convertToStudentVo(student);
    }

    /**
     * 查询思齐馆学生管理列表
     *
     * @param student 思齐馆学生管理
     * @return 思齐馆学生管理 VO
     */
    @Override
    public List<StudentVo> selectStudentList(Student student)
    {
        List<Student> students = siqiStudentMapper.selectStudentList(student);
        return students.stream()
                .map(this::convertToStudentVo)
                .collect(Collectors.toList());
    }

    /**
     * 新增思齐馆学生管理
     *
     * @param student 思齐馆学生管理
     * @return 结果
     */
    @Override
    public int insertStudent(Student student)
    {
        return siqiStudentMapper.insertStudent(student);
    }

    /**
     * 修改思齐馆学生管理
     *
     * @param student 思齐馆学生管理
     * @return 结果
     */
    @Override
    public int updateStudent(Student student)
    {
        return siqiStudentMapper.updateStudent(student);
    }

    /**
     * 批量删除思齐馆学生管理
     *
     * @param studentIds 需要删除的思齐馆学生管理主键
     * @return 结果
     */
    @Override
    public int deleteStudentByStudentIds(String[] studentIds)
    {
        return siqiStudentMapper.deleteStudentByStudentIds(studentIds);
    }

    /**
     * 删除思齐馆学生管理信息
     *
     * @param studentId 思齐馆学生管理主键
     * @return 结果
     */
    @Override
    public int deleteStudentByStudentId(String studentId)
    {
        return siqiStudentMapper.deleteStudentByStudentId(studentId);
    }
    
    /**
     * 将 Student 转换为 StudentVo
     * 
     * @param student 学生对象
     * @return 学生 VO 对象
     */
    private StudentVo convertToStudentVo(Student student) {
        StudentVo vo = new StudentVo();
        BeanUtils.copyProperties(student, vo);
        
        // 通过学号查询岗位
        if (student.getStudentId() != null) {
            List<SysPost> posts = sysPostMapper.selectPostsByUserName(student.getStudentId());
            vo.setPostList(posts);
            vo.setPostNames(posts.stream()
                    .map(SysPost::getPostName)
                    .collect(Collectors.joining(", ")));
        }
        
        return vo;
    }
}
