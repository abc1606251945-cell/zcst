package com.zcst.web.controller.system;

import java.util.Date;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.zcst.common.constant.Constants;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.core.domain.entity.SysMenu;
import com.zcst.common.core.domain.entity.SysUser;
import com.zcst.common.core.domain.model.LoginBody;
import com.zcst.common.core.domain.model.LoginUser;
import com.zcst.common.core.text.Convert;
import com.zcst.common.utils.DateUtils;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.common.utils.StringUtils;
import com.zcst.framework.web.service.SysLoginService;
import com.zcst.framework.web.service.SysPermissionService;
import com.zcst.framework.web.service.TokenService;
import com.zcst.system.service.ISysConfigService;
import com.zcst.system.service.ISysMenuService;
import com.zcst.system.service.ISysUserService;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@RestController
@RequiredArgsConstructor
public class SysLoginController
{
    private final SysLoginService loginService;

    private final ISysMenuService menuService;

    private final SysPermissionService permissionService;

    private final TokenService tokenService;

    private final ISysConfigService configService;

    private final ISysUserService userService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        boolean shouldRefreshToken = false;
        if (StringUtils.isEmpty(user.getAccountType()))
        {
            SysUser dbUser = userService.selectUserByUserName(user.getUserName());
            if (dbUser == null)
            {
                user.setAccountType("student");
            }
            else if (user.isAdmin())
            {
                user.setAccountType("admin");
            }
            else
            {
                user.setAccountType("manager");
            }
            shouldRefreshToken = true;
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions))
        {
            loginUser.setPermissions(permissions);
            shouldRefreshToken = true;
        }
        if (shouldRefreshToken)
        {
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
    
    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate)
    {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate)
    {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0)
        {
            if (StringUtils.isNull(pwdUpdateDate))
            {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
