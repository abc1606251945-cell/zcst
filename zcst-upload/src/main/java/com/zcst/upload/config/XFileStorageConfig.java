package com.zcst.upload.config;

import org.springframework.context.annotation.Configuration;

/**
 * x-file-storage 配置类
 * 注意：x-file-storage 会自动配置所有 Bean，不需要手动创建
 * 所有配置在 application.yml 中完成
 * 启动类上需要添加 @EnableFileStorage 注解
 */
@Configuration
public class XFileStorageConfig {
    // 此类仅作为说明，实际配置在 application.yml 中
    // Bean 的创建由 @EnableFileStorage 自动完成
}
