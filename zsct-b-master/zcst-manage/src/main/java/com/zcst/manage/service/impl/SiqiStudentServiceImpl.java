package com.zcst.manage.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
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
        // 设置思齐馆的场馆ID为1
        student.setVenueId(1L);
        List<Student> students = siqiStudentMapper.selectStudentList(student);
        if (students instanceof com.github.pagehelper.Page) {
            com.github.pagehelper.Page<Student> page = (com.github.pagehelper.Page<Student>) students;
            com.github.pagehelper.Page<StudentVo> result = new com.github.pagehelper.Page<>(page.getPageNum(), page.getPageSize());
            result.setTotal(page.getTotal());
            for (Student s : students) {
                result.add(convertToStudentVo(s));
            }
            return result;
        }
        List<StudentVo> studentVos = new ArrayList<>();
        for (Student s : students) {
            studentVos.add(convertToStudentVo(s));
        }
        return studentVos;
    }

    /**
     * 查询思齐馆学生管理列表（带分页信息）
     *
     * @param student 思齐馆学生管理
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 包含分页信息的思齐馆学生管理列表
     */
    @Override
    public PageInfo<StudentVo> selectStudentListWithPage(Student student, int pageNum, int pageSize)
    {
        // 设置思齐馆的场馆ID为1
        student.setVenueId(1L);
        PageHelper.startPage(pageNum, pageSize);
        List<Student> students = siqiStudentMapper.selectStudentList(student);
        PageInfo<Student> studentsPage = new PageInfo<>(students);
        List<StudentVo> studentVos = students.stream().map(this::convertToStudentVo).collect(Collectors.toList());
        PageInfo<StudentVo> result = new PageInfo<>(studentVos);
        result.setTotal(studentsPage.getTotal());
        return result;
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
