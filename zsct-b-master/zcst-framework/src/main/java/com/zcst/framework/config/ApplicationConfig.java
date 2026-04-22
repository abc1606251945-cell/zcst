package com.zcst.framework.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 程序注解配置
 *
 * @author ruoyi
 */
@Configuration
// 表示通过 aop 框架暴露该代理对象，AopContext 能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的 Mapper 类的包的路径
@MapperScan("com.zcst.**.mapper")
// 扫描所有子模块的组件（包括 manage、system、quartz 等）
@ComponentScan(basePackages = "com.zcst")
public class ApplicationConfig
{
}
