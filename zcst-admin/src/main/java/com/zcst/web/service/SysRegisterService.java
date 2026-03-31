package com.zcst.web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.zcst.common.constant.CacheConstants;
import com.zcst.common.constant.Constants;
import com.zcst.common.constant.UserConstants;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.core.domain.model.RegisterBody;
import com.zcst.common.core.redis.RedisCache;
import com.zcst.common.exception.user.CaptchaException;
import com.zcst.common.exception.user.CaptchaExpireException;
import com.zcst.common.utils.DateUtils;
import com.zcst.common.utils.MessageUtils;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.common.utils.StringUtils;
import com.zcst.framework.manager.AsyncManager;
import com.zcst.framework.manager.factory.AsyncFactory;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.StudentVo;
import com.zcst.manage.service.IStudentService;
import com.zcst.system.service.ISysConfigService;
import com.zcst.system.service.ISysUserService;

/**
 * 注册校验方法
 * 
 * @author ruoyi
 */
@Component
@RequiredArgsConstructor
public class SysRegisterService
{
    private final ISysUserService userService;

    private final ISysConfigService configService;

    private final RedisCache redisCache;

    private final IStudentService studentService;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "";
        String type = registerBody.getType();

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            String username = "";
            if ("student".equals(type)) {
                username = registerBody.getStudentId();
            } else {
                username = registerBody.getUsername();
            }
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if ("student".equals(type)) {
            msg = registerStudent(registerBody);
        } else {
            msg = registerAdmin(registerBody);
        }

        return msg;
    }

    /**
     * 学生注册
     */
    private String registerStudent(RegisterBody registerBody) {
        String msg = "";
        String studentId = registerBody.getStudentId();
        String password = registerBody.getPassword();
        String name = registerBody.getName();

        if (StringUtils.isEmpty(studentId)) {
            msg = "学号不能为空";
        } else if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (StringUtils.isEmpty(name)) {
            msg = "姓名不能为空";
        } else if (studentId.length() < 6 || studentId.length() > 20) {
            msg = "学号长度必须在 6 到 20 个字符之间";
        } else if (password.length() < 6) {
            msg = "密码长度必须至少 6 位";
        } else {
            // 检查学号是否已存在
            StudentVo studentVo = studentService.selectStudentByStudentId(studentId);
            if (studentVo != null) {
                msg = "保存学生'" + studentId + "'失败，学号已存在";
            } else {
                // 创建学生对象
                Student newStudent = new Student();
                newStudent.setStudentId(studentId);
                newStudent.setName(name); // 使用用户输入的姓名
                newStudent.setGender("男"); // 临时默认值，后续在信息完善页面修改
                newStudent.setPhone("13800138000"); // 临时默认值，后续在信息完善页面修改
                newStudent.setVenueId(1L); // 临时默认值，后续在信息完善页面修改
                newStudent.setMajorId(1L); // 临时默认值，后续在信息完善页面修改
                newStudent.setGrade("2026级"); // 临时默认值，后续在信息完善页面修改
                newStudent.setPassword(SecurityUtils.encryptPassword(password));

                // 保存学生信息（会触发触发器创建sys_user并分配角色）
                int regFlag = studentService.insertStudent(newStudent);
                if (regFlag <= 0) {
                    msg = "注册失败,请联系系统管理人员";
                } else {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(studentId, Constants.REGISTER, MessageUtils.message("user.register.success")));
                }
            }
        }
        return msg;
    }

    /**
     * 管理人员注册
     */
    private String registerAdmin(RegisterBody registerBody) {
        String msg = "";
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String name = registerBody.getName();
        String gender = registerBody.getGender();
        Long venueId = registerBody.getVenueId();

        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (StringUtils.isEmpty(name)) {
            msg = "姓名不能为空";
        } else if (StringUtils.isEmpty(gender)) {
            msg = "性别不能为空";
        } else if (venueId == null) {
            msg = "所在场馆不能为空";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (password.length() < 6) {
            msg = "密码长度必须至少6位";
        } else if (!userService.checkUserNameUnique(sysUser)) {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        } else {
            sysUser.setNickName(name);
            sysUser.setSex(gender);
            sysUser.setVenueId(venueId);
            sysUser.setPwdUpdateDate(DateUtils.getNowDate());
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            // 设置为管理人员角色（假设角色 ID 为 3）
            sysUser.setRoleIds(new Long[]{3L});
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}