-- 学生端（小程序）接口补齐所需补丁
-- 变更点：
-- 1) leave_application / shift_exchange：增加撤销字段
-- 2) student_notice：新增学生通知中心表（按人已读/未读）

ALTER TABLE `leave_application`
    ADD COLUMN `cancel_time` datetime DEFAULT NULL COMMENT '撤销时间' AFTER `approve_remark`,
    ADD COLUMN `cancel_reason` varchar(500) DEFAULT NULL COMMENT '撤销原因' AFTER `cancel_time`;

ALTER TABLE `shift_exchange`
    ADD COLUMN `cancel_time` datetime DEFAULT NULL COMMENT '撤销时间' AFTER `student_b_confirm_time`,
    ADD COLUMN `cancel_reason` varchar(500) DEFAULT NULL COMMENT '撤销原因' AFTER `cancel_time`;

CREATE TABLE IF NOT EXISTS `student_notice` (
    `notice_id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `student_id` varchar(20) NOT NULL COMMENT '接收人学号',
    `title` varchar(100) NOT NULL COMMENT '标题',
    `content` text COMMENT '内容',
    `type` varchar(32) DEFAULT NULL COMMENT '类型',
    `biz_type` varchar(32) DEFAULT NULL COMMENT '业务类型',
    `biz_id` varchar(64) DEFAULT NULL COMMENT '业务ID',
    `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
    `read_flag` char(1) NOT NULL DEFAULT '0' COMMENT '是否已读（0未读 1已读）',
    `read_time` datetime DEFAULT NULL COMMENT '已读时间',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`notice_id`),
    KEY `idx_student_publish` (`student_id`, `publish_time`),
    KEY `idx_student_read_publish` (`student_id`, `read_flag`, `publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生通知中心';
