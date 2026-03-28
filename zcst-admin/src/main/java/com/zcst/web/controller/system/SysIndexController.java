package com.zcst.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zcst.common.config.RuoYiConfig;
import com.zcst.common.utils.StringUtils;

/**
 * 首页
 *
 * @author ruoyi
 */
@RestController
public class SysIndexController
{
    /** 系统基础配置 */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    /**
     * 访问首页，提示语
     * 注意：这个路由只用于测试后端是否启动成功，实际访问应该通过前端地址
     */
    @RequestMapping("/backend-index")
    public String index()
    {
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }
}
