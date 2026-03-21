-- 数据库结构优化脚本
-- 优化学生信息表与管理员信息表的存储结构

-- 1. 修改 student 表，添加班级字段
ALTER TABLE `student` ADD COLUMN `class_name` varchar(50) DEFAULT NULL COMMENT '班级' AFTER `grade`;

-- 2. 修改 student 表，优化索引
ALTER TABLE `student` ADD INDEX `idx_venue_id` (`venue_id`);
ALTER TABLE `student` ADD INDEX `idx_phone` (`phone`);

-- 3. 修改 sys_user 表，添加用户类型字段（明确区分管理员和学生）
ALTER TABLE `sys_user` MODIFY COLUMN `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户 01管理员 02学生）';

-- 4. 为 sys_user 表添加索引
ALTER TABLE `sys_user` ADD INDEX `idx_user_type` (`user_type`);
ALTER TABLE `sys_user` ADD INDEX `idx_venue_id` (`venue_id`);
ALTER TABLE `sys_user` ADD INDEX `idx_login_date` (`login_date`);

-- 5. 创建学生登录日志表
CREATE TABLE IF NOT EXISTS `student_login_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `login_ip` varchar(128) NOT NULL COMMENT '登录IP',
  `device_info` varchar(255) DEFAULT NULL COMMENT '设备信息',
  `login_status` char(1) NOT NULL COMMENT '登录状态（0成功 1失败）',
  `error_msg` varchar(255) DEFAULT NULL COMMENT '错误信息',
  PRIMARY KEY (`log_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_login_ip` (`login_ip`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生登录日志表';

-- 6. 创建管理员登录日志表（如果不存在）
CREATE TABLE IF NOT EXISTS `admin_login_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(30) NOT NULL COMMENT '用户名',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `login_ip` varchar(128) NOT NULL COMMENT '登录IP',
  `device_info` varchar(255) DEFAULT NULL COMMENT '设备信息',
  `login_status` char(1) NOT NULL COMMENT '登录状态（0成功 1失败）',
  `error_msg` varchar(255) DEFAULT NULL COMMENT '错误信息',
  PRIMARY KEY (`log_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_login_ip` (`login_ip`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员登录日志表';

-- 7. 创建权限分配日志表
CREATE TABLE IF NOT EXISTS `permission_assign_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(30) NOT NULL COMMENT '用户名',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `assign_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  `operator` varchar(64) NOT NULL COMMENT '操作人',
  `ip_address` varchar(128) NOT NULL COMMENT '操作IP',
  PRIMARY KEY (`log_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_assign_time` (`assign_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限分配日志表';

-- 8. 修改 student 表的触发器，确保用户类型正确设置
DELIMITER ;;  
DROP TRIGGER IF EXISTS `tri_student_insert`;;
CREATE TRIGGER `tri_student_insert` BEFORE INSERT ON `student` FOR EACH ROW BEGIN
    DECLARE v_user_id BIGINT;

    -- 插入 sys_user
    INSERT INTO `sys_user` (
        `dept_id`, `user_name`, `nick_name`, `user_type`,
        `phonenumber`, `sex`, `password`, `status`, `create_time`
    ) VALUES (
                 NULL,
                 NEW.student_id,           -- 学号作为账号
                 NEW.name,                 -- 姓名作为昵称
                 '02',                     -- 学生用户类型
                 NEW.phone,
                 CASE NEW.gender WHEN '男' THEN '0' WHEN '女' THEN '1' ELSE '0' END,
                 NEW.password,
                 '0',                      -- 状态正常
                 NOW()
             );

    -- 获取刚插入的 user_id
    SET v_user_id = LAST_INSERT_ID();

    -- 设置 student 表的 user_id
    SET NEW.user_id = v_user_id;

    -- 自动关联 role_id=2 的角色（普通员工）
    INSERT INTO `sys_user_role` (user_id, role_id)
    VALUES (v_user_id, 2);
    
    -- 记录权限分配日志
    INSERT INTO `permission_assign_log` (user_id, username, role_id, role_name, operator, ip_address)
    VALUES (v_user_id, NEW.student_id, 2, '普通员工', 'system', '127.0.0.1');
END;;
DELIMITER ;

-- 9. 修改 student 表的更新触发器
DELIMITER ;;  
DROP TRIGGER IF EXISTS `tri_student_update`;;
CREATE TRIGGER `tri_student_update` BEFORE UPDATE ON `student` FOR EACH ROW BEGIN
    -- 如果密码发生变化，同步到 sys_user
    IF NEW.password != OLD.password THEN
        UPDATE `sys_user`
        SET password = NEW.password,
            update_time = NOW()
        WHERE user_id = NEW.user_id;
    END IF;

    -- 同步其他基本信息
    UPDATE `sys_user`
    SET nick_name = NEW.name,
        phonenumber = NEW.phone,
        sex = CASE NEW.gender WHEN '男' THEN '0' WHEN '女' THEN '1' ELSE '0' END,
        update_time = NOW()
    WHERE user_id = NEW.user_id;
END;;
DELIMITER ;

-- 10. 创建学生信息暂存表，用于信息完善过程中的数据暂存
CREATE TABLE IF NOT EXISTS `student_info_draft` (
  `draft_id` bigint NOT NULL AUTO_INCREMENT COMMENT '草稿ID',
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `form_data` json NOT NULL COMMENT '表单数据',
  `current_step` int NOT NULL COMMENT '当前步骤',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`draft_id`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生信息暂存表';
