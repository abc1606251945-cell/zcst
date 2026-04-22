package com.zcst.manage.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.zcst.manage.mapper.SiqiStudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zcst.manage.mapper.StudentMapper;
import com.github.pagehelper.PageInfo;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.StudentVo;
import com.zcst.manage.service.IStudentService;
import com.github.pagehelper.PageHelper;
import com.zcst.system.mapper.SysPostMapper;
import com.zcst.system.mapper.SysRoleMapper;
import com.zcst.system.mapper.SysUserMapper;
import com.zcst.system.mapper.SysUserPostMapper;
import com.zcst.system.mapper.SysUserRoleMapper;
import com.zcst.system.domain.SysPost;
import com.zcst.common.core.domain.entity.SysRole;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.system.domain.SysUserPost;
import com.zcst.system.domain.SysUserRole;

/**
 * 学生管理Service业务层处理
 * 
 * @author zcst
 * @date 2026-03-18
 */
@Service
public class StudentServiceImpl implements IStudentService 
{
    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentMapper studentMapper ;
    
    @Autowired
    private SysRoleMapper sysRoleMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 查询学生管理
     * 
     * @param studentId 学生管理主键
     * @return 学生管理 VO
     */
    @Override
    public StudentVo selectStudentByStudentId(String studentId)
    {
        Student student = studentMapper.selectStudentByStudentId(studentId);
        return convertToStudentVo(student);
    }

    /**
     * 查询学生管理列表（带分页信息）
     * 
     * @param student 学生管理
     * @return 包含分页信息的学生管理 VO 列表
     */
    @Override
    public PageInfo<StudentVo> selectStudentListWithPage(Student student)
    {
        List<Student> students = studentMapper.selectStudentList(student);
        PageInfo<Student> studentsPage = new PageInfo<>(students);
        List<StudentVo> studentVos = students.stream().map(this::convertToStudentVo).collect(Collectors.toList());
        PageInfo<StudentVo> result = new PageInfo<>(studentVos);
        result.setTotal(studentsPage.getTotal());
        return result;
    }

    /**
     * 查询学生管理列表
     * 
     * @param student 学生管理
     * @return 学生管理 VO 列表
     */
    @Override
    public List<StudentVo> selectStudentList(Student student)
    {
        List<Student> students = studentMapper.selectStudentList(student);
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
     * 查询学生管理列表（带分页信息）
     * 
     * @param student 学生管理
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 包含分页信息的学生管理 VO 列表
     */
    @Override
    public PageInfo<StudentVo> selectStudentListWithPage(Student student, int pageNum, int pageSize)
    {
        PageHelper.startPage(pageNum, pageSize);
        List<Student> students = studentMapper.selectStudentList(student);
        PageInfo<Student> studentsPage = new PageInfo<>(students);
        List<StudentVo> studentVos = students.stream().map(this::convertToStudentVo).collect(Collectors.toList());
        PageInfo<StudentVo> result = new PageInfo<>(studentVos);
        result.setTotal(studentsPage.getTotal());
        return result;
    }



    /**
     * 新增学生管理
     * 
     * @param student 学生管理
     * @return 结果
     */
    @Override
    @Transactional
    public int insertStudent(Student student)
    {
        int result = studentMapper.insertStudent(student);
        return result;
    }

    /**
     * 修改学生管理
     * 
     * @param student 学生管理
     * @return 结果
     */
    @Override
    @Transactional
    public int updateStudent(Student student)
    {
        int result = studentMapper.updateStudent(student);
        return result;
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
    
    /**
     * 将 Student 转换为 StudentVo
     * 
     * @param student 学生对象
     * @return 学生 VO 对象
     */
    private StudentVo convertToStudentVo(Student student) {
        if (student == null) {
            return null;
        }
        StudentVo vo = new StudentVo();
        BeanUtils.copyProperties(student, vo);
        
        // 通过学号查询角色
        if (student.getStudentId() != null) {
            List<SysRole> roles = sysRoleMapper.selectRolesByUserName(student.getStudentId());
            vo.setRoleList(roles);
            vo.setRoleNames(roles.stream()
                    .map(SysRole::getRoleName)
                    .collect(Collectors.joining(", ")));
        }
        
        return vo;
    }
    
    /**
     * 通过学号获取用户ID
     * 
     * @param studentId 学号
     * @return 用户ID
     */
    private Long getUserIdByStudentId(String studentId) {
        SysUser user = sysUserMapper.selectUserByUserName(studentId);
        return user != null ? user.getUserId() : null;
    }
    
    /**
     * 自动绑定普通员工角色
     * 
     * @param student 学生对象
     */
    private void autoBindEmployeeRole(Student student) {
        try {
            // 获取普通员工角色
            SysRole role = sysRoleMapper.checkRoleNameUnique("普通员工");
            if (role != null) {
                Long userId = getUserIdByStudentId(student.getStudentId());
                if (userId != null) {
                    // 绑定普通员工角色
                    List<SysUserRole> userRoleList = new ArrayList<>();
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(role.getRoleId());
                    userRoleList.add(userRole);
                    sysUserRoleMapper.batchUserRole(userRoleList);
                }
            }
        } catch (Exception e) {
            log.error("自动绑定员工角色失败", e);
        }
    }
}
