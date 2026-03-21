package com.zcst.manage.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import com.zcst.manage.mapper.ZhixingStudentMapper;
import com.zcst.manage.service.IZhixingStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.StudentVo;
import com.zcst.system.mapper.SysPostMapper;
import com.zcst.system.domain.SysPost;

/**
 * 知行馆学生管理 Service 业务层处理
 *
 * @author zcst
 * @date 2026-03-20
 */
@Service
public class ZhixingStudentServiceImpl implements IZhixingStudentService
{
    @Autowired
    private ZhixingStudentMapper studentMapper;
    
    @Autowired
    private SysPostMapper sysPostMapper;

    /**
     * 查询知行馆学生管理
     *
     * @param studentId 知行馆学生管理主键
     * @return 知行馆学生管理 VO
     */
    @Override
    public StudentVo selectStudentByStudentId(String studentId)
    {
        Student student = studentMapper.selectStudentByStudentId(studentId);
        return convertToStudentVo(student);
    }

    /**
     * 查询知行馆学生管理列表
     *
     * @param student 知行馆学生管理
     * @return 知行馆学生管理 VO
     */
    @Override
    public List<StudentVo> selectStudentList(Student student)
    {
        // 设置知行馆的场馆ID为5
        student.setVenueId(5L);
        List<Student> students = studentMapper.selectStudentList(student);
        List<StudentVo> studentVos = new ArrayList<>();
        for (Student s : students) {
            studentVos.add(convertToStudentVo(s));
        }
        return studentVos;
    }

    /**
     * 查询知行馆学生管理列表（带分页信息）
     *
     * @param student 知行馆学生管理
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 包含分页信息的知行馆学生管理列表
     */
    @Override
    public PageInfo<StudentVo> selectStudentListWithPage(Student student, int pageNum, int pageSize)
    {
        // 设置知行馆的场馆ID为5
        student.setVenueId(5L);
        PageHelper.startPage(pageNum, pageSize);
        List<Student> students = studentMapper.selectStudentList(student);
        List<StudentVo> studentVos = new ArrayList<>();
        for (Student s : students) {
            studentVos.add(convertToStudentVo(s));
        }
        return new PageInfo<>(studentVos);
    }

    /**
     * 新增知行馆学生管理
     *
     * @param student 知行馆学生管理
     * @return 结果
     */
    @Override
    public int insertStudent(Student student)
    {
        return studentMapper.insertStudent(student);
    }

    /**
     * 修改知行馆学生管理
     *
     * @param student 知行馆学生管理
     * @return 结果
     */
    @Override
    public int updateStudent(Student student)
    {
        return studentMapper.updateStudent(student);
    }

    /**
     * 批量删除知行馆学生管理
     *
     * @param studentIds 需要删除的知行馆学生管理主键
     * @return 结果
     */
    @Override
    public int deleteStudentByStudentIds(String[] studentIds)
    {
        return studentMapper.deleteStudentByStudentIds(studentIds);
    }

    /**
     * 删除知行馆学生管理信息
     *
     * @param studentId 知行馆学生管理主键
     * @return 结果
     */
    @Override
    public int deleteStudentByStudentId(String studentId)
    {
        return studentMapper.deleteStudentByStudentId(studentId);
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