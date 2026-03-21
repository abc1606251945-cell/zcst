package com.zcst.framework.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.core.domain.model.LoginUser;
import com.zcst.common.enums.UserStatus;
import com.zcst.common.exception.ServiceException;
import com.zcst.common.utils.MessageUtils;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.common.utils.StringUtils;
import com.zcst.common.utils.spring.SpringUtils;
import com.zcst.system.service.ISysUserService;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private SysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        // 首先尝试从管理员表查询
        SysUser user = userService.selectUserByUserName(username);
        boolean isStudent = false;
        
        // 如果管理员表中不存在，尝试从学生表查询
        if (StringUtils.isNull(user))
        {
            try {
                // 使用SpringUtils动态获取StudentMapper实例，避免循环依赖
                Object studentMapper = SpringUtils.getBean("studentMapper");
                if (studentMapper != null) {
                    // 反射调用selectStudentByStudentId方法
                    Object student = studentMapper.getClass().getMethod("selectStudentByStudentId", String.class).invoke(studentMapper, username);
                    if (student != null) {
                        // 反射获取学生信息并转换为SysUser对象
                        user = convertStudentToSysUser(student);
                        isStudent = true;
                    }
                }
            } catch (Exception e) {
                log.error("查询学生信息失败: {}", e.getMessage());
            }
            
            if (StringUtils.isNull(user))
            {
                log.info("登录用户：{} 不存在.", username);
                throw new ServiceException(MessageUtils.message("user.not.exists"));
            }
        }
        else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException(MessageUtils.message("user.password.delete"));
        }
        else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException(MessageUtils.message("user.blocked"));
        }

        // 对于学生，先检查密码是否需要加密
        if (isStudent && !user.getPassword().startsWith("$2a$"))
        {
            // 学生密码未加密，先加密后再验证
            user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        }

        passwordService.validate(user);

        return createLoginUser(user);
    }

    /**
     * 将学生信息转换为SysUser对象
     */
    private SysUser convertStudentToSysUser(Object student)
    {
        SysUser user = new SysUser();
        try {
            // 反射获取学生信息
            String studentId = (String) student.getClass().getMethod("getStudentId").invoke(student);
            String name = (String) student.getClass().getMethod("getName").invoke(student);
            String password = (String) student.getClass().getMethod("getPassword").invoke(student);
            String gender = (String) student.getClass().getMethod("getGender").invoke(student);
            String phone = (String) student.getClass().getMethod("getPhone").invoke(student);
            Long venueId = (Long) student.getClass().getMethod("getVenueId").invoke(student);
            
            // 使用学生ID的哈希值作为userId，确保唯一性
            long userId = Math.abs(studentId.hashCode());
            
            user.setUserId(userId);
            user.setUserName(studentId);
            user.setNickName(name);
            user.setPassword(password);
            user.setSex(gender);
            user.setPhonenumber(phone);
            user.setVenueId(venueId);
            user.setStatus("0"); // 正常状态
            user.setDelFlag("0"); // 未删除
            user.setDeptId(1L); // 默认部门
        } catch (Exception e) {
            log.error("转换学生信息失败: {}", e.getMessage());
        }
        return user;
    }

    public UserDetails createLoginUser(SysUser user)
    {
        return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
    }
}
