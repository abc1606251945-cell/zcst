-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: zcst
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_login_log`
--

DROP TABLE IF EXISTS `admin_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_login_log` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_login_log`
--

LOCK TABLES `admin_login_log` WRITE;
/*!40000 ALTER TABLE `admin_login_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance_record`
--

DROP TABLE IF EXISTS `attendance_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `duty_id` int NOT NULL COMMENT '值班表ID',
  `check_in_time` datetime NOT NULL COMMENT '打卡时间',
  `check_out_time` datetime DEFAULT NULL COMMENT '签退时间',
  `actual_duty_hours` decimal(5,2) DEFAULT NULL COMMENT '实际值班时长（小时）',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1迟到 2早退 3缺勤）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_duty_id` (`duty_id`),
  KEY `idx_check_in_time` (`check_in_time`),
  CONSTRAINT `fk_attendance_duty` FOREIGN KEY (`duty_id`) REFERENCES `duty_schedule` (`duty_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考勤记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_record`
--

LOCK TABLES `attendance_record` WRITE;
/*!40000 ALTER TABLE `attendance_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance_rule`
--

DROP TABLE IF EXISTS `attendance_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_rule` (
  `rule_id` int NOT NULL AUTO_INCREMENT COMMENT '规则 ID',
  `venue_id` int NOT NULL COMMENT '场馆 ID',
  `venue_name` varchar(100) DEFAULT NULL COMMENT '场馆名称',
  `late_threshold_minutes` int DEFAULT '15' COMMENT '迟到阈值（分钟）',
  `early_leave_threshold_minutes` int DEFAULT '15' COMMENT '早退阈值（分钟）',
  `min_checkin_before_minutes` int DEFAULT '30' COMMENT '最早提前打卡时间（分钟）',
  `max_checkin_after_minutes` int DEFAULT '60' COMMENT '最晚延后打卡时间（分钟）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0 正常 1 停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`rule_id`),
  UNIQUE KEY `venue_id` (`venue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考勤规则配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_rule`
--

LOCK TABLES `attendance_rule` WRITE;
/*!40000 ALTER TABLE `attendance_rule` DISABLE KEYS */;
INSERT INTO `attendance_rule` VALUES (1,6,'国防教育体验馆',15,15,30,60,'0',NULL,'2026-03-27 22:02:05',NULL),(2,2,'弘毅馆',15,15,30,60,'0',NULL,'2026-03-27 22:02:05',NULL),(3,3,'心缘馆',15,15,30,60,'0',NULL,'2026-03-27 22:02:05',NULL),(4,1,'思齐馆',15,15,30,60,'0',NULL,'2026-03-27 22:02:05',NULL),(5,5,'知行馆',15,15,30,60,'0',NULL,'2026-03-27 22:02:05',NULL),(6,4,'笃学馆',15,15,30,60,'0',NULL,'2026-03-27 22:02:05',NULL);
/*!40000 ALTER TABLE `attendance_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance_statistics`
--

DROP TABLE IF EXISTS `attendance_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_statistics` (
  `stat_id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `venue_id` int NOT NULL COMMENT '场馆ID',
  `year_month` varchar(7) NOT NULL COMMENT '年月（格式：2026-04）',
  `total_duty_hours` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '值班总时长',
  `check_in_count` int NOT NULL DEFAULT '0' COMMENT '打卡次数',
  `attendance_count` int NOT NULL DEFAULT '0' COMMENT '出勤次数',
  `absence_count` int NOT NULL DEFAULT '0' COMMENT '缺勤次数',
  `late_count` int NOT NULL DEFAULT '0' COMMENT '迟到次数',
  `early_leave_count` int NOT NULL DEFAULT '0' COMMENT '早退次数',
  `leave_count` int DEFAULT '0' COMMENT '请假次数',
  `exchange_count` int DEFAULT '0' COMMENT '调班次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`stat_id`),
  UNIQUE KEY `uk_student_year_month` (`student_id`,`year_month`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_venue_id` (`venue_id`),
  KEY `idx_year_month` (`year_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考勤统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_statistics`
--

LOCK TABLES `attendance_statistics` WRITE;
/*!40000 ALTER TABLE `attendance_statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance_statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `college`
--

DROP TABLE IF EXISTS `college`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `college` (
  `college_id` int NOT NULL AUTO_INCREMENT COMMENT '学院ID',
  `college_name` varchar(100) NOT NULL COMMENT '学院名称',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`college_id`),
  UNIQUE KEY `uk_college_name` (`college_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学院表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `college`
--

LOCK TABLES `college` WRITE;
/*!40000 ALTER TABLE `college` DISABLE KEYS */;
INSERT INTO `college` VALUES (1,'计算机学院','2026-03-18 15:49:37','2026-03-18 15:49:37'),(2,'电子信息工程学院','2026-03-18 15:49:37','2026-03-18 15:49:37'),(3,'经济管理学院','2026-03-18 15:49:37','2026-03-18 15:49:37');
/*!40000 ALTER TABLE `college` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `duty_schedule`
--

DROP TABLE IF EXISTS `duty_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `duty_schedule` (
  `duty_id` int NOT NULL AUTO_INCREMENT COMMENT '值班表ID',
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `venue_id` int NOT NULL COMMENT '场馆ID',
  `start_time` datetime NOT NULL COMMENT '值班开始时间（格式：2026-03-14 08:00:00）',
  `end_time` datetime NOT NULL COMMENT '值班结束时间（格式：2026-03-14 12:00:00）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注（非必填）',
  `attendance_status` char(1) DEFAULT NULL COMMENT '考勤状态（0 正常 1 迟到 2 早退 3 缺勤）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`duty_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_venue_id` (`venue_id`),
  KEY `idx_duty_time` (`start_time`,`end_time`)
) ENGINE=InnoDB AUTO_INCREMENT=2563 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='值班表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `duty_schedule`
--

LOCK TABLES `duty_schedule` WRITE;
/*!40000 ALTER TABLE `duty_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `duty_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `duty_time_config`
--

DROP TABLE IF EXISTS `duty_time_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `duty_time_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `venue_id` int NOT NULL COMMENT '场馆ID',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `is_enable` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`config_id`),
  KEY `idx_venue_id` (`venue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='值班时间配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `duty_time_config`
--

LOCK TABLES `duty_time_config` WRITE;
/*!40000 ALTER TABLE `duty_time_config` DISABLE KEYS */;
INSERT INTO `duty_time_config` VALUES (8,1,'08:30:00','10:30:00',0,'2026-03-22 13:31:10','2026-03-27 11:16:11');
/*!40000 ALTER TABLE `duty_time_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
INSERT INTO `gen_table` VALUES (1,'college','学院表',NULL,NULL,'College','crud','element-plus','com.zcst.system','system','college','学院','ruoyi','0','/',NULL,'admin','2026-03-18 16:55:04','',NULL,NULL),(2,'major','专业表',NULL,NULL,'Major','crud','element-plus','com.zcst.system','system','major','专业','ruoyi','0','/',NULL,'admin','2026-03-18 16:55:04','',NULL,NULL),(3,'student','学生表','','','Student','crud','element-plus','com.zcst.manage','manage','hongyiStudent','弘毅馆学生管理','zcst','0','/','{\"parentMenuId\":2056}','admin','2026-03-18 16:55:04','','2026-03-19 19:24:41',NULL),(4,'venue','场馆表',NULL,NULL,'Venue','crud','element-plus','com.zcst.manage','manage','venue','场馆信息管理','ji','0','/','{\"parentMenuId\":2069}','admin','2026-03-18 21:30:01','','2026-03-19 20:25:40',NULL);
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
INSERT INTO `gen_table_column` VALUES (1,1,'college_id','学院ID','int','Long','collegeId','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2026-03-18 16:55:04','',NULL),(2,1,'college_name','学院名称','varchar(100)','String','collegeName','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2026-03-18 16:55:04','',NULL),(3,1,'created_at','创建时间','datetime','Date','createdAt','0','0','1','1','1','1','1','EQ','datetime','',3,'admin','2026-03-18 16:55:04','',NULL),(4,1,'updated_at','更新时间','datetime','Date','updatedAt','0','0','1','1','1','1','1','EQ','datetime','',4,'admin','2026-03-18 16:55:04','',NULL),(5,2,'major_id','专业ID','int','Long','majorId','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2026-03-18 16:55:04','',NULL),(6,2,'major_name','专业名称','varchar(100)','String','majorName','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2026-03-18 16:55:04','',NULL),(7,2,'college_id','所属学院ID','int','Long','collegeId','0','0','1','1','1','1','1','EQ','input','',3,'admin','2026-03-18 16:55:04','',NULL),(8,2,'created_at','创建时间','datetime','Date','createdAt','0','0','1','1','1','1','1','EQ','datetime','',4,'admin','2026-03-18 16:55:04','',NULL),(9,2,'updated_at','更新时间','datetime','Date','updatedAt','0','0','1','1','1','1','1','EQ','datetime','',5,'admin','2026-03-18 16:55:04','',NULL),(10,3,'student_id','学号','varchar(20)','String','studentId','1','0','1','1',NULL,NULL,'1','EQ','input','',1,'admin','2026-03-18 16:55:04','','2026-03-19 19:24:41'),(11,3,'name','姓名','varchar(50)','String','name','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2026-03-18 16:55:04','','2026-03-19 19:24:41'),(12,3,'gender','性别（男/女）','char(1)','String','gender','0','0','1','1','1','1','1','EQ','input','',3,'admin','2026-03-18 16:55:04','','2026-03-19 19:24:41'),(13,3,'major_id','专业ID','int','Long','majorId','0','0','1','1','1','1','1','EQ','input','',4,'admin','2026-03-18 16:55:04','','2026-03-19 19:24:41'),(14,3,'grade','年级','varchar(20)','String','grade','0','0','1','1','1','1','1','EQ','input','',5,'admin','2026-03-18 16:55:04','','2026-03-19 19:24:41'),(15,3,'phone','手机号码','varchar(11)','String','phone','0','0','1','1','1','1','1','EQ','input','',6,'admin','2026-03-18 16:55:04','','2026-03-19 19:24:41'),(16,3,'password','密码','varchar(50)','String','password','0','0','1','1','1','1','1','EQ','input','',8,'admin','2026-03-18 16:55:04','','2026-03-19 19:24:41'),(17,3,'created_at','创建时间','datetime','Date','createdAt','0','0','0','0','0','0','1','EQ','datetime','',9,'admin','2026-03-18 16:55:04','','2026-03-19 19:24:41'),(18,3,'updated_at','更新时间','datetime','Date','updatedAt','0','0','0','0','0','0','1','EQ','datetime','',10,'admin','2026-03-18 16:55:04','','2026-03-19 19:24:41'),(19,3,'venue_id','场馆','int','Long','venueId','0','0','1','1','1','1','1','EQ','select','zcst_venue_name',7,'','2026-03-18 21:10:38','','2026-03-19 19:24:41'),(20,4,'venue_id','场馆ID','int','Long','venueId','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2026-03-18 21:30:01','','2026-03-19 20:25:40'),(21,4,'venue_name','场馆名称','varchar(100)','String','venueName','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2026-03-18 21:30:01','','2026-03-19 20:25:40'),(22,4,'created_at','创建时间','datetime','Date','createdAt','0','0','1','1','1','1','1','EQ','datetime','',3,'admin','2026-03-18 21:30:01','','2026-03-19 20:25:40'),(23,4,'updated_at','更新时间','datetime','Date','updatedAt','0','0','1','1','1','1','1','EQ','datetime','',4,'admin','2026-03-18 21:30:01','','2026-03-19 20:25:40');
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leave_application`
--

DROP TABLE IF EXISTS `leave_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leave_application` (
  `leave_id` int NOT NULL AUTO_INCREMENT COMMENT '请假 ID',
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `student_name` varchar(50) DEFAULT NULL COMMENT '学生姓名',
  `venue_id` int DEFAULT NULL COMMENT '场馆 ID',
  `duty_id` int DEFAULT NULL COMMENT '值班 ID（可选）',
  `leave_type` char(1) DEFAULT NULL COMMENT '请假类型（0 病假 1 事假 2 其他）',
  `start_time` datetime DEFAULT NULL COMMENT '请假开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '请假结束时间',
  `reason` varchar(500) DEFAULT NULL COMMENT '请假原因',
  `proof_image` varchar(500) DEFAULT NULL COMMENT '证明材料图片 URL',
  `status` char(1) DEFAULT '0' COMMENT '审批状态（0 待审批 1 已通过 2 已拒绝）',
  `approver_id` varchar(20) DEFAULT NULL COMMENT '审批人 ID',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `created_at` datetime DEFAULT NULL COMMENT '申请时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`leave_id`),
  KEY `student_id` (`student_id`),
  KEY `venue_id` (`venue_id`),
  KEY `status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='请假申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leave_application`
--

LOCK TABLES `leave_application` WRITE;
/*!40000 ALTER TABLE `leave_application` DISABLE KEYS */;
/*!40000 ALTER TABLE `leave_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `major`
--

DROP TABLE IF EXISTS `major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `major` (
  `major_id` int NOT NULL AUTO_INCREMENT COMMENT '专业ID',
  `major_name` varchar(100) NOT NULL COMMENT '专业名称',
  `college_id` int NOT NULL COMMENT '所属学院ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`major_id`),
  UNIQUE KEY `uk_college_major` (`college_id`,`major_name`),
  KEY `idx_college_id` (`college_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `major`
--

LOCK TABLES `major` WRITE;
/*!40000 ALTER TABLE `major` DISABLE KEYS */;
INSERT INTO `major` VALUES (1,'软件工程',1,'2026-03-18 15:49:37','2026-03-18 15:49:37'),(2,'计算机科学与技术',1,'2026-03-18 15:49:37','2026-03-18 15:49:37'),(3,'电子信息工程',2,'2026-03-18 15:49:37','2026-03-18 15:49:37'),(4,'通信工程',2,'2026-03-18 15:49:37','2026-03-18 15:49:37'),(5,'会计学',3,'2026-03-18 15:49:37','2026-03-18 15:49:37'),(6,'金融学',3,'2026-03-18 15:49:37','2026-03-18 15:49:37');
/*!40000 ALTER TABLE `major` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `makeup_application`
--

DROP TABLE IF EXISTS `makeup_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `makeup_application` (
  `makeup_id` int NOT NULL AUTO_INCREMENT COMMENT '补卡 ID',
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `student_name` varchar(50) DEFAULT NULL COMMENT '学生姓名',
  `venue_id` int DEFAULT NULL COMMENT '场馆 ID',
  `duty_id` int NOT NULL COMMENT '值班 ID',
  `miss_type` char(1) DEFAULT NULL COMMENT '缺卡类型（0 上班未打卡 1 下班未打卡 2 都未打卡）',
  `actual_start_time` datetime DEFAULT NULL COMMENT '实际上班时间',
  `actual_end_time` datetime DEFAULT NULL COMMENT '实际下班时间',
  `reason` varchar(500) DEFAULT NULL COMMENT '补卡原因',
  `proof_image` varchar(500) DEFAULT NULL COMMENT '证明材料图片 URL',
  `status` char(1) DEFAULT '0' COMMENT '审批状态（0 待审批 1 已通过 2 已拒绝）',
  `approver_id` varchar(20) DEFAULT NULL COMMENT '审批人 ID',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `created_at` datetime DEFAULT NULL COMMENT '申请时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`makeup_id`),
  KEY `student_id` (`student_id`),
  KEY `duty_id` (`duty_id`),
  KEY `status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='补卡申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `makeup_application`
--

LOCK TABLES `makeup_application` WRITE;
/*!40000 ALTER TABLE `makeup_application` DISABLE KEYS */;
/*!40000 ALTER TABLE `makeup_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission_assign_log`
--

DROP TABLE IF EXISTS `permission_assign_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission_assign_log` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限分配日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission_assign_log`
--

LOCK TABLES `permission_assign_log` WRITE;
/*!40000 ALTER TABLE `permission_assign_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `permission_assign_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_cron_triggers`
--

LOCK TABLES `qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_cron_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简单触发器的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simple_triggers`
--

LOCK TABLES `qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='同步机制的行锁表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simprop_triggers`
--

LOCK TABLES `qrtz_simprop_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shift_exchange`
--

DROP TABLE IF EXISTS `shift_exchange`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shift_exchange` (
  `exchange_id` int NOT NULL AUTO_INCREMENT COMMENT '调班 ID',
  `student_id_a` varchar(20) NOT NULL COMMENT '申请人学号',
  `student_name_a` varchar(50) DEFAULT NULL COMMENT '申请人姓名',
  `student_id_b` varchar(20) NOT NULL COMMENT '替换人学号',
  `student_name_b` varchar(50) DEFAULT NULL COMMENT '替换人姓名',
  `venue_id` int DEFAULT NULL COMMENT '场馆 ID',
  `duty_id` int NOT NULL COMMENT '原值班 ID',
  `exchange_reason` varchar(500) DEFAULT NULL COMMENT '调班原因',
  `status` char(1) DEFAULT '0' COMMENT '审批状态（0 待审批 1 已通过 2 已拒绝 3 已确认）',
  `approver_id` varchar(20) DEFAULT NULL COMMENT '审批人 ID',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `student_b_confirm` char(1) DEFAULT '0' COMMENT '替换人确认（0 未确认 1 已确认）',
  `student_b_confirm_time` datetime DEFAULT NULL COMMENT '替换人确认时间',
  `created_at` datetime DEFAULT NULL COMMENT '申请时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`exchange_id`),
  KEY `student_id_a` (`student_id_a`),
  KEY `student_id_b` (`student_id_b`),
  KEY `duty_id` (`duty_id`),
  KEY `status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调班申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shift_exchange`
--

LOCK TABLES `shift_exchange` WRITE;
/*!40000 ALTER TABLE `shift_exchange` DISABLE KEYS */;
/*!40000 ALTER TABLE `shift_exchange` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `user_id` bigint DEFAULT NULL COMMENT '关联 sys_user 表 ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `gender` char(1) NOT NULL COMMENT '性别（男/女）',
  `major_id` int NOT NULL COMMENT '专业ID',
  `grade` varchar(20) NOT NULL COMMENT '年级',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
  `phone` varchar(11) NOT NULL COMMENT '手机号码',
  `venue_id` int DEFAULT NULL COMMENT '场馆 ID',
  `password` varchar(255) NOT NULL DEFAULT '123456' COMMENT '密码',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_grade` (`grade`),
  KEY `idx_major_id` (`major_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_venue_id` (`venue_id`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES ('20260001',101,'陈铭','男',1,'2026级',NULL,'13800138001',1,'123456','2026-03-18 15:49:37','2026-03-19 16:20:58'),('20260002',102,'林晓月','女',1,'2026级',NULL,'13800138002',2,'123456','2026-03-18 15:49:37','2026-03-19 16:20:58'),('20260003',103,'张浩然','男',2,'2026级',NULL,'13800138003',1,'123456','2026-03-18 15:49:37','2026-03-19 16:20:58'),('20260004',104,'苏晴','女',2,'2026级',NULL,'13800138004',2,'123456','2026-03-18 15:49:37','2026-03-19 16:20:58'),('20260005',105,'王宇','男',3,'2025级',NULL,'13800138005',1,'123456','2026-03-18 15:49:37','2026-03-19 16:20:58'),('20260006',106,'李诗琪','女',3,'2025级',NULL,'13800138006',2,'123456','2026-03-18 15:49:37','2026-03-19 16:20:58'),('20260007',107,'刘阳','男',4,'2025级',NULL,'13800138007',1,'123456','2026-03-18 15:49:37','2026-03-19 16:20:58'),('20260008',108,'赵雨萱','女',5,'2024级',NULL,'13800138008',2,'123456','2026-03-18 15:49:37','2026-03-19 16:20:58'),('20260009',109,'孙浩博','男',6,'2024级',NULL,'13800138009',1,'123456','2026-03-18 15:49:37','2026-03-19 16:20:58'),('20260010',110,'周梓涵','女',4,'2024级',NULL,'13800138010',2,'123456','2026-03-18 15:49:37','2026-03-19 16:20:58'),('20260011',118,'吴思远','男',1,'2026级',NULL,'13800138011',3,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260012',119,'郑雨桐','女',2,'2026级',NULL,'13800138012',3,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260013',120,'马浩然','男',3,'2025级',NULL,'13800138013',3,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260014',121,'刘梦琪','女',4,'2025级',NULL,'13800138014',3,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260015',122,'周博文','男',5,'2024级',NULL,'13800138015',3,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260016',123,'陈明宇','男',2,'2026级',NULL,'13800138016',4,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260017',124,'林小雨','女',3,'2026级',NULL,'13800138017',4,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260018',125,'王浩轩','男',4,'2025级',NULL,'13800138018',4,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260019',126,'张诗涵','女',5,'2025级',NULL,'13800138019',4,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260020',127,'李博文','男',6,'2024级',NULL,'13800138020',4,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260021',128,'赵浩然','男',3,'2026级',NULL,'13800138021',5,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260022',129,'陈雨萱','女',4,'2026级',NULL,'13800138022',5,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260023',130,'刘博文','男',5,'2025级',NULL,'13800138023',5,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260024',131,'王梦琪','女',6,'2025级',NULL,'13800138024',5,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260025',132,'周浩然','男',1,'2024级',NULL,'13800138025',5,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260026',133,'吴博文','男',4,'2026级',NULL,'13800138026',6,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260027',134,'郑梦琪','女',5,'2026级',NULL,'13800138027',6,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260028',135,'马浩然','男',6,'2025级',NULL,'13800138028',6,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260029',136,'刘小雨','女',1,'2025级',NULL,'13800138029',6,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260030',137,'周雨桐','男',2,'2024级',NULL,'13800138030',6,'123456','2026-03-20 15:42:01','2026-03-20 15:42:01'),('20260101',140,'张明','男',1,'2026级',NULL,'13800138101',1,'123456','2026-03-20 16:59:44','2026-03-20 16:59:44'),('20260102',141,'李华','女',1,'2026级',NULL,'13800138102',1,'123456','2026-03-20 16:59:44','2026-03-20 16:59:44'),('20260103',142,'王强','男',2,'2026级',NULL,'13800138103',1,'123456','2026-03-20 16:59:44','2026-03-20 16:59:44'),('20260104',143,'赵敏','女',2,'2026级',NULL,'13800138104',1,'123456','2026-03-20 16:59:44','2026-03-20 16:59:44'),('20260105',144,'刘伟','男',3,'2025级',NULL,'13800138105',1,'123456','2026-03-20 16:59:44','2026-03-20 16:59:44'),('20260106',145,'陈丽','女',3,'2025级',NULL,'13800138106',1,'123456','2026-03-20 16:59:44','2026-03-20 16:59:44'),('20260107',146,'赵刚','男',4,'2025级',NULL,'13800138107',1,'123456','2026-03-20 16:59:44','2026-03-20 16:59:44'),('20260108',147,'孙燕','女',4,'2024级',NULL,'13800138108',1,'123456','2026-03-20 16:59:44','2026-03-20 16:59:44'),('20260109',148,'周杰','男',5,'2024级',NULL,'13800138109',1,'123456','2026-03-20 16:59:44','2026-03-20 16:59:44'),('20260110',149,'吴敏','女',5,'2024级',NULL,'13800138110',1,'123456','2026-03-20 16:59:44','2026-03-20 16:59:44'),('20260321',150,'20260321','男',1,'2026级',NULL,'13800138000',1,'$2a$10$PMl8rx3k5/RkVpDIbkHY9OWc5a1IOLH2.nidkEJG4dvNgDQhe7zsS','2026-03-21 10:03:54','2026-03-21 10:03:54');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS tri_student_insert */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_student_insert` BEFORE INSERT ON `student` FOR EACH ROW BEGIN
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
/*!50032 DROP TRIGGER IF EXISTS tri_student_update */;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tri_student_update` BEFORE UPDATE ON `student` FOR EACH ROW BEGIN
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `student_info_draft`
--

DROP TABLE IF EXISTS `student_info_draft`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_info_draft` (
  `draft_id` bigint NOT NULL AUTO_INCREMENT COMMENT '草稿ID',
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `form_data` json NOT NULL COMMENT '表单数据',
  `current_step` int NOT NULL COMMENT '当前步骤',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`draft_id`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生信息暂存表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_info_draft`
--

LOCK TABLES `student_info_draft` WRITE;
/*!40000 ALTER TABLE `student_info_draft` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_info_draft` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_login_log`
--

DROP TABLE IF EXISTS `student_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_login_log` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_login_log`
--

LOCK TABLES `student_login_log` WRITE;
/*!40000 ALTER TABLE `student_login_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_schedule`
--

DROP TABLE IF EXISTS `student_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_schedule` (
  `schedule_id` int NOT NULL AUTO_INCREMENT COMMENT '课表ID',
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `course_name` varchar(100) NOT NULL COMMENT '??????',
  `location` varchar(100) DEFAULT NULL COMMENT '??????',
  `start_time` datetime NOT NULL COMMENT '课程开始时间（格式：2026-03-14 08:00:00）',
  `end_time` datetime NOT NULL COMMENT '课程结束时间（格式：2026-03-14 09:45:00）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`schedule_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_time_range` (`start_time`,`end_time`),
  KEY `idx_location` (`location`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生课表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_schedule`
--

LOCK TABLES `student_schedule` WRITE;
/*!40000 ALTER TABLE `student_schedule` DISABLE KEYS */;
INSERT INTO `student_schedule` VALUES (1,'20260001','',NULL,'2026-03-18 08:00:00','2026-03-18 09:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(2,'20260001','',NULL,'2026-03-18 14:00:00','2026-03-18 15:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(3,'20260001','',NULL,'2026-03-18 18:00:00','2026-03-18 19:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(4,'20260002','',NULL,'2026-03-18 10:00:00','2026-03-18 11:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(5,'20260002','',NULL,'2026-03-18 16:00:00','2026-03-18 17:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(6,'20260002','',NULL,'2026-03-18 19:50:00','2026-03-18 21:30:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(7,'20260003','',NULL,'2026-03-18 08:00:00','2026-03-18 09:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(8,'20260003','',NULL,'2026-03-18 16:00:00','2026-03-18 17:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(9,'20260004','',NULL,'2026-03-18 10:00:00','2026-03-18 11:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(10,'20260004','',NULL,'2026-03-18 18:00:00','2026-03-18 19:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(11,'20260005','',NULL,'2026-03-18 12:00:00','2026-03-18 13:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(12,'20260005','',NULL,'2026-03-18 14:00:00','2026-03-18 15:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(13,'20260005','',NULL,'2026-03-18 19:50:00','2026-03-18 21:30:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(14,'20260006','',NULL,'2026-03-18 08:00:00','2026-03-18 09:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(15,'20260006','',NULL,'2026-03-18 14:00:00','2026-03-18 15:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(16,'20260006','',NULL,'2026-03-18 19:50:00','2026-03-18 21:30:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(17,'20260007','',NULL,'2026-03-18 10:00:00','2026-03-18 11:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(18,'20260007','',NULL,'2026-03-18 16:00:00','2026-03-18 17:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(19,'20260007','',NULL,'2026-03-18 18:00:00','2026-03-18 19:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(20,'20260008','',NULL,'2026-03-18 12:00:00','2026-03-18 13:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(21,'20260008','',NULL,'2026-03-18 14:00:00','2026-03-18 15:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(22,'20260009','',NULL,'2026-03-18 08:00:00','2026-03-18 09:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(23,'20260009','',NULL,'2026-03-18 16:00:00','2026-03-18 17:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(24,'20260009','',NULL,'2026-03-18 18:00:00','2026-03-18 19:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(25,'20260010','',NULL,'2026-03-18 10:00:00','2026-03-18 11:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(26,'20260010','',NULL,'2026-03-18 12:00:00','2026-03-18 13:40:00','2026-03-18 15:49:37','2026-03-18 15:49:37'),(27,'20260010','',NULL,'2026-03-18 19:50:00','2026-03-18 21:30:00','2026-03-18 15:49:37','2026-03-18 15:49:37');
/*!40000 ALTER TABLE `student_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2026-03-18 16:35:46','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2026-03-18 16:35:46','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2026-03-18 16:35:46','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','false','Y','admin','2026-03-18 16:35:46','admin','2026-03-22 14:14:44','是否开启验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','true','Y','admin','2026-03-18 16:35:46','admin','2026-03-19 15:33:34','是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2026-03-18 16:35:46','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),(7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2026-03-18 16:35:46','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),(8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2026-03-18 16:35:46','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=201 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,0,'0','珠海科技学院',0,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-18 16:35:44','admin','2026-03-19 19:14:59'),(101,100,'0,100','思齐馆',0,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-18 16:35:44','admin','2026-03-19 19:15:23'),(102,100,'0,100','长沙分公司',2,'若依','15888888888','ry@qq.com','0','2','admin','2026-03-18 16:35:44','',NULL),(103,101,'0,100,101','值班',1,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-18 16:35:44','admin','2026-03-19 19:15:57'),(104,101,'0,100,101','市场部门',2,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-18 16:35:44','',NULL),(105,101,'0,100,101','测试部门',3,'若依','15888888888','ry@qq.com','0','0','admin','2026-03-18 16:35:44','',NULL),(106,101,'0,100,101','财务部门',4,'若依','15888888888','ry@qq.com','0','2','admin','2026-03-18 16:35:44','',NULL),(107,101,'0,100,101','运维部门',5,'若依','15888888888','ry@qq.com','0','2','admin','2026-03-18 16:35:44','',NULL),(108,102,'0,100,102','市场部门',1,'若依','15888888888','ry@qq.com','0','2','admin','2026-03-18 16:35:44','',NULL),(109,102,'0,100,102','财务部门',2,'若依','15888888888','ry@qq.com','0','2','admin','2026-03-18 16:35:44','',NULL),(200,100,'0,100','弘毅馆',1,NULL,NULL,NULL,'0','0','admin','2026-03-19 19:15:10','',NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','0','admin','2026-03-18 16:35:46','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','admin','2026-03-18 16:35:46','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','admin','2026-03-18 16:35:46','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2026-03-18 16:35:46','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2026-03-18 16:35:46','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2026-03-18 16:35:46','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2026-03-18 16:35:46','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2026-03-18 16:35:46','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2026-03-18 16:35:46','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2026-03-18 16:35:46','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2026-03-18 16:35:46','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2026-03-18 16:35:46','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2026-03-18 16:35:46','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2026-03-18 16:35:46','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2026-03-18 16:35:46','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2026-03-18 16:35:46','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2026-03-18 16:35:46','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2026-03-18 16:35:46','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2026-03-18 16:35:46','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2026-03-18 16:35:46','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2026-03-18 16:35:46','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2026-03-18 16:35:46','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2026-03-18 16:35:46','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2026-03-18 16:35:46','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2026-03-18 16:35:46','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2026-03-18 16:35:46','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2026-03-18 16:35:46','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2026-03-18 16:35:46','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2026-03-18 16:35:46','',NULL,'停用状态'),(100,1,'思齐馆','1','zcst_venue_name',NULL,'default','N','0','admin','2026-03-18 21:29:04','',NULL,NULL),(101,2,'弘毅馆','2','zcst_venue_name',NULL,'default','N','0','admin','2026-03-18 21:29:13','',NULL,NULL),(102,3,'心缘馆','3','zcst_venue_name',NULL,'default','N','0','admin','2026-03-20 13:14:13','',NULL,NULL),(103,4,'笃学馆','4','zcst_venue_name',NULL,'default','N','0','admin','2026-03-20 13:14:24','',NULL,NULL),(104,5,'知行馆','5','zcst_venue_name',NULL,'default','N','0','admin','2026-03-20 13:14:36','',NULL,NULL),(105,6,'国防教育体验馆','6','zcst_venue_name',NULL,'default','N','0','admin','2026-03-20 13:14:52','',NULL,NULL);
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2026-03-18 16:35:46','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2026-03-18 16:35:46','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2026-03-18 16:35:46','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2026-03-18 16:35:46','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2026-03-18 16:35:46','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2026-03-18 16:35:46','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2026-03-18 16:35:46','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2026-03-18 16:35:46','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2026-03-18 16:35:46','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2026-03-18 16:35:46','',NULL,'登录状态列表'),(100,'场馆名称','zcst_venue_name','0','admin','2026-03-18 21:28:23','',NULL,NULL);
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

LOCK TABLES `sys_job` WRITE;
/*!40000 ALTER TABLE `sys_job` DISABLE KEYS */;
INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2026-03-18 16:35:47','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2026-03-18 16:35:47','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2026-03-18 16:35:47','',NULL,'');
/*!40000 ALTER TABLE `sys_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--

LOCK TABLES `sys_job_log` WRITE;
/*!40000 ALTER TABLE `sys_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=259 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;
INSERT INTO `sys_logininfor` VALUES (100,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-18 17:15:24'),(101,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-18 17:15:25'),(102,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-18 18:56:56'),(103,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-18 19:55:15'),(104,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-18 19:55:57'),(105,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-18 19:56:00'),(106,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-18 20:47:54'),(107,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-18 22:14:46'),(108,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:25:59'),(109,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:33:38'),(110,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:33:42'),(111,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:36:25'),(112,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:36:47'),(113,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:36:53'),(114,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','注册成功','2026-03-19 15:37:05'),(115,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:37:08'),(116,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:37:42'),(117,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:37:44'),(118,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:39:08'),(119,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:39:14'),(120,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:39:41'),(121,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:39:43'),(122,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:40:18'),(123,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:40:28'),(124,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:41:31'),(125,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:41:33'),(126,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:41:57'),(127,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:42:03'),(128,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:42:15'),(129,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:42:17'),(130,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:43:35'),(131,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:43:41'),(132,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 15:43:52'),(133,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 15:43:58'),(134,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 17:30:32'),(135,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 17:30:35'),(136,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 17:31:12'),(137,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 17:31:14'),(138,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 17:32:25'),(139,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 17:32:31'),(140,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 17:40:13'),(141,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 17:40:14'),(142,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 17:46:03'),(143,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 19:18:41'),(144,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 19:18:44'),(145,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 19:18:52'),(146,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 19:18:55'),(147,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 20:06:25'),(148,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 20:19:51'),(149,'wangwu','127.0.0.1','内网IP','Edge 146','Windows >=10','1','用户不存在/密码错误','2026-03-19 20:19:54'),(150,'wangwu','127.0.0.1','内网IP','Edge 146','Windows >=10','1','用户不存在/密码错误','2026-03-19 20:19:57'),(151,'王五','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 20:20:03'),(152,'王五','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-19 20:20:13'),(153,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-19 20:20:15'),(154,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 12:34:12'),(155,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 13:09:25'),(156,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 15:46:38'),(157,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 15:48:41'),(158,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 15:48:48'),(159,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 15:49:08'),(160,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 15:49:10'),(161,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 15:52:37'),(162,'gazi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','注册成功','2026-03-20 15:53:16'),(163,'gazi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 15:53:25'),(164,'gazi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 15:53:30'),(165,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 15:53:31'),(166,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 16:03:18'),(167,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 16:07:43'),(168,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 16:10:39'),(169,'20260001','127.0.0.1','内网IP','Edge 146','Windows >=10','1','用户不存在/密码错误','2026-03-20 16:10:47'),(170,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 16:10:53'),(171,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 17:15:54'),(172,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 17:15:59'),(173,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 17:16:05'),(174,'20260001','127.0.0.1','内网IP','Edge 146','Windows >=10','1','用户不存在/密码错误','2026-03-20 17:16:13'),(175,'20260001','127.0.0.1','内网IP','Edge 146','Windows >=10','1','用户不存在/密码错误','2026-03-20 17:16:25'),(176,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 17:17:25'),(177,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 17:21:37'),(178,'20260001','127.0.0.1','内网IP','Edge 146','Windows >=10','1','用户不存在/密码错误','2026-03-20 17:21:43'),(179,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 17:34:17'),(180,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 17:42:59'),(181,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 17:43:06'),(182,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 17:43:14'),(183,'gazi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 17:43:26'),(184,'gazi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 17:43:33'),(185,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 17:43:34'),(186,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 22:43:17'),(187,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-20 22:46:33'),(188,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 22:46:41'),(189,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-20 23:36:57'),(190,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','1','验证码错误','2026-03-21 09:39:41'),(191,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 09:39:42'),(192,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 09:40:33'),(193,'20260321','127.0.0.1','内网IP','Edge 146','Windows >=10','0','注册成功','2026-03-21 10:03:57'),(194,'20260321','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 10:04:39'),(195,'20260321','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 10:05:01'),(196,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','注册成功','2026-03-21 10:05:47'),(197,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 10:05:57'),(198,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 10:06:02'),(199,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 10:06:05'),(200,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 10:06:43'),(201,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 10:06:52'),(202,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 10:06:58'),(203,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 19:55:14'),(204,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 20:17:09'),(205,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 20:17:10'),(206,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 20:37:48'),(207,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 20:37:56'),(208,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 20:39:43'),(209,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 20:39:46'),(210,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 21:30:13'),(211,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','1','验证码错误','2026-03-21 21:30:13'),(212,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 21:30:15'),(213,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 23:03:03'),(214,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 23:03:09'),(215,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-21 23:03:16'),(216,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-21 23:03:20'),(217,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 10:19:11'),(218,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 11:21:34'),(219,'admin','127.0.0.1','内网IP','Trae 1.107.1','Windows 10.0','0','登录成功','2026-03-22 11:22:32'),(220,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 11:32:52'),(221,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 11:32:55'),(222,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 13:07:06'),(223,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 13:30:47'),(224,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 13:30:47'),(225,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 13:45:04'),(226,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 13:45:12'),(227,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 13:45:24'),(228,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 13:45:30'),(229,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 14:03:40'),(230,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 14:03:54'),(231,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 14:08:51'),(232,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 14:09:01'),(233,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 14:11:11'),(234,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 14:11:20'),(235,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 14:11:35'),(236,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 14:11:44'),(237,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 14:12:10'),(238,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 14:12:19'),(239,'liuneng','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 14:12:30'),(240,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 14:13:01'),(241,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 14:14:06'),(242,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 14:14:14'),(243,'lisi','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-22 14:14:21'),(244,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-22 14:14:25'),(245,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-27 07:34:39'),(246,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-27 11:15:15'),(247,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-27 11:49:25'),(248,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-27 11:49:25'),(249,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-27 12:24:25'),(250,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-27 12:24:25'),(251,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-27 12:26:28'),(252,'admin','127.0.0.1','内网IP','TraeCN 1.107.1','Windows 10.0','0','登录成功','2026-03-27 12:27:52'),(253,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-27 12:44:10'),(254,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-27 12:44:10'),(255,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-27 12:52:47'),(256,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-27 12:52:47'),(257,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','退出成功','2026-03-27 12:58:05'),(258,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10','0','登录成功','2026-03-27 12:58:06');
/*!40000 ALTER TABLE `sys_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2146 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,11,'system',NULL,'','',1,0,'M','0','0','','system','admin','2026-03-18 16:35:45','admin','2026-03-19 15:28:00','系统管理目录'),(2,'系统监控',0,7,'monitor',NULL,'','',1,0,'M','0','0','','monitor','admin','2026-03-18 16:35:45','admin','2026-03-22 12:45:13','系统监控目录'),(3,'系统工具',0,13,'tool',NULL,'','',1,0,'M','0','0','','tool','admin','2026-03-18 16:35:45','admin','2026-03-19 15:27:56','系统工具目录'),(4,'若依官网',0,14,'http://ruoyi.vip',NULL,'','',0,0,'M','0','0','','guide','admin','2026-03-18 16:35:45','admin','2026-03-19 15:27:53','若依官网地址'),(100,'用户管理',1,1,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2026-03-18 16:35:45','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2026-03-18 16:35:45','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','0','0','system:menu:list','tree-table','admin','2026-03-18 16:35:45','',NULL,'菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2026-03-18 16:35:45','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','','',1,0,'C','0','0','system:post:list','post','admin','2026-03-18 16:35:45','',NULL,'岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','0','0','system:dict:list','dict','admin','2026-03-18 16:35:45','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','0','0','system:config:list','edit','admin','2026-03-18 16:35:45','',NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','0','0','system:notice:list','message','admin','2026-03-18 16:35:45','',NULL,'通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2026-03-18 16:35:45','',NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','admin','2026-03-18 16:35:45','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','monitor/job/index','','',1,0,'C','0','0','monitor:job:list','job','admin','2026-03-18 16:35:45','',NULL,'定时任务菜单'),(111,'数据监控',2,3,'druid','monitor/druid/index','','',1,0,'C','0','0','monitor:druid:list','druid','admin','2026-03-18 16:35:45','',NULL,'数据监控菜单'),(112,'服务监控',2,4,'server','monitor/server/index','','',1,0,'C','0','0','monitor:server:list','server','admin','2026-03-18 16:35:45','',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'cache','monitor/cache/index','','',1,0,'C','0','0','monitor:cache:list','redis','admin','2026-03-18 16:35:45','',NULL,'缓存监控菜单'),(114,'缓存列表',2,6,'cacheList','monitor/cache/list','','',1,0,'C','0','0','monitor:cache:list','redis-list','admin','2026-03-18 16:35:45','',NULL,'缓存列表菜单'),(115,'表单构建',3,1,'build','tool/build/index','','',1,0,'C','0','0','tool:build:list','build','admin','2026-03-18 16:35:45','',NULL,'表单构建菜单'),(116,'代码生成',3,2,'gen','tool/gen/index','','',1,0,'C','0','0','tool:gen:list','code','admin','2026-03-18 16:35:45','',NULL,'代码生成菜单'),(117,'系统接口',3,3,'swagger','tool/swagger/index','','',1,0,'C','0','0','tool:swagger:list','swagger','admin','2026-03-18 16:35:45','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','admin','2026-03-18 16:35:45','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','admin','2026-03-18 16:35:45','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2026-03-18 16:35:45','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2026-03-18 16:35:45','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2026-03-18 16:35:45','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2026-03-18 16:35:45','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2026-03-18 16:35:45','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2026-03-18 16:35:45','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2026-03-18 16:35:45','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2026-03-18 16:35:45','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2026-03-18 16:35:45','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2026-03-18 16:35:45','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2026-03-18 16:35:45','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2026-03-18 16:35:45','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','admin','2026-03-18 16:35:45','',NULL,''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','admin','2026-03-18 16:35:45','',NULL,''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','admin','2026-03-18 16:35:45','',NULL,''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','admin','2026-03-18 16:35:45','',NULL,''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','admin','2026-03-18 16:35:45','',NULL,''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','admin','2026-03-18 16:35:45','',NULL,''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','admin','2026-03-18 16:35:45','',NULL,''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','admin','2026-03-18 16:35:45','',NULL,''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','admin','2026-03-18 16:35:45','',NULL,''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','admin','2026-03-18 16:35:45','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','admin','2026-03-18 16:35:45','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','admin','2026-03-18 16:35:45','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','admin','2026-03-18 16:35:45','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','admin','2026-03-18 16:35:45','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','admin','2026-03-18 16:35:45','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','admin','2026-03-18 16:35:45','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','0','monitor:job:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','0','monitor:job:add','#','admin','2026-03-18 16:35:45','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','0','monitor:job:edit','#','admin','2026-03-18 16:35:45','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','0','monitor:job:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','0','monitor:job:changeStatus','#','admin','2026-03-18 16:35:45','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','0','monitor:job:export','#','admin','2026-03-18 16:35:45','',NULL,''),(1055,'生成查询',116,1,'#','','','',1,0,'F','0','0','tool:gen:query','#','admin','2026-03-18 16:35:45','',NULL,''),(1056,'生成修改',116,2,'#','','','',1,0,'F','0','0','tool:gen:edit','#','admin','2026-03-18 16:35:45','',NULL,''),(1057,'生成删除',116,3,'#','','','',1,0,'F','0','0','tool:gen:remove','#','admin','2026-03-18 16:35:45','',NULL,''),(1058,'导入代码',116,4,'#','','','',1,0,'F','0','0','tool:gen:import','#','admin','2026-03-18 16:35:45','',NULL,''),(1059,'预览代码',116,5,'#','','','',1,0,'F','0','0','tool:gen:preview','#','admin','2026-03-18 16:35:45','',NULL,''),(1060,'生成代码',116,6,'#','','','',1,0,'F','0','0','tool:gen:code','#','admin','2026-03-18 16:35:45','',NULL,''),(2008,'学生管理',0,0,'manage',NULL,NULL,'',1,0,'M','0','0',NULL,'#','admin','2026-03-18 18:58:53','',NULL,''),(2009,'全部学生管理',2008,0,'allStudent','manage/student/index',NULL,'',1,0,'C','0','0','','user','admin','2026-03-18 18:59:46','admin','2026-03-18 19:37:23',''),(2012,'学生管理',2009,1,'student','manage/student/index',NULL,'',1,0,'C','0','0','manage:student:list','#','admin','2026-03-18 21:12:22','',NULL,'学生管理菜单'),(2013,'学生管理查询',2012,1,'#','',NULL,'',1,0,'F','0','0','manage:student:query','#','admin','2026-03-18 21:12:22','',NULL,''),(2014,'学生管理新增',2012,2,'#','',NULL,'',1,0,'F','0','0','manage:student:add','#','admin','2026-03-18 21:12:22','',NULL,''),(2015,'学生管理修改',2012,3,'#','',NULL,'',1,0,'F','0','0','manage:student:edit','#','admin','2026-03-18 21:12:22','',NULL,''),(2016,'学生管理删除',2012,4,'#','',NULL,'',1,0,'F','0','0','manage:student:remove','#','admin','2026-03-18 21:12:22','',NULL,''),(2017,'学生管理导出',2012,5,'#','',NULL,'',1,0,'F','0','0','manage:student:export','#','admin','2026-03-18 21:12:22','',NULL,''),(2030,'场馆管理',0,1,'venue',NULL,NULL,'',1,0,'M','0','0','','#','admin','2026-03-19 15:27:34','admin','2026-03-19 20:21:49',''),(2044,'思齐馆学生管理',2008,1,'siqiStudent','manage/siqiStudent/index',NULL,'',1,0,'M','0','0','manage:siqiStudent:list','user','admin','2026-03-19 18:01:19','admin','2026-03-19 18:40:49',''),(2051,'思齐馆学生新增',2044,1,'',NULL,NULL,'',1,0,'F','0','0','manage:siqiStudent:add','#','admin','2026-03-19 18:19:24','admin','2026-03-19 18:19:49',''),(2052,'思齐馆学生导出',2044,2,'',NULL,NULL,'',1,0,'F','0','0','manage:siqiStudent:export','#','admin','2026-03-19 18:20:26','',NULL,''),(2053,'思齐馆学生刷新',2044,3,'',NULL,NULL,'',1,0,'F','0','0','manage:siqiStudent:query','#','admin','2026-03-19 18:21:21','',NULL,''),(2054,'思齐馆学生修改',2044,4,'',NULL,NULL,'',1,0,'F','0','0','manage:siqiStudent:edit','#','admin','2026-03-19 18:21:50','',NULL,''),(2055,'思齐馆学生删除',2044,5,'',NULL,NULL,'',1,0,'F','0','0','manage:siqiStudent:remove','#','admin','2026-03-19 18:22:15','',NULL,''),(2056,'弘毅馆学生管理',2008,2,'hongyiStudent','manage/hongyiStudent/index',NULL,'',1,0,'M','0','0','','user','admin','2026-03-19 18:41:46','admin','2026-03-19 19:24:17',''),(2057,'弘毅馆学生管理',2010,1,'hongyiStudent','manage/hongyiStudent/index',NULL,'',1,0,'C','0','0','manage:hongyiStudent:list','user','admin','2026-03-19 19:25:24','',NULL,'弘毅馆学生管理菜单'),(2058,'弘毅馆学生管理查询',2057,1,'#','',NULL,'',1,0,'F','0','0','manage:hongyiStudent:query','#','admin','2026-03-19 19:25:24','',NULL,''),(2059,'弘毅馆学生管理新增',2057,2,'#','',NULL,'',1,0,'F','0','0','manage:hongyiStudent:add','#','admin','2026-03-19 19:25:24','',NULL,''),(2060,'弘毅馆学生管理修改',2057,3,'#','',NULL,'',1,0,'F','0','0','manage:hongyiStudent:edit','#','admin','2026-03-19 19:25:24','',NULL,''),(2061,'弘毅馆学生管理删除',2057,4,'#','',NULL,'',1,0,'F','0','0','manage:hongyiStudent:remove','#','admin','2026-03-19 19:25:24','',NULL,''),(2062,'弘毅馆学生管理导出',2057,5,'#','',NULL,'',1,0,'F','0','0','manage:hongyiStudent:export','#','admin','2026-03-19 19:25:24','',NULL,''),(2063,'弘毅馆学生列表',2056,0,'',NULL,NULL,'',1,0,'F','0','0','manage:hongyiStudent:list','#','admin','2026-03-19 20:15:05','',NULL,''),(2064,'导出',2056,1,'',NULL,NULL,'',1,0,'F','0','0','manage:hongyiStudent:export','#','admin','2026-03-19 20:16:32','',NULL,''),(2065,'信息',2056,2,'',NULL,NULL,'',1,0,'F','0','0','manage:hongyiStudent:query','#','admin','2026-03-19 20:17:07','',NULL,''),(2066,'添加',2056,3,'',NULL,NULL,'',1,0,'F','0','0','manage:hongyiStudent:add','#','admin','2026-03-19 20:17:50','',NULL,''),(2067,'修改',2056,4,'',NULL,NULL,'',1,0,'F','0','0','manage:hongyiStudent:edit','#','admin','2026-03-19 20:18:32','',NULL,''),(2068,'删除',2056,5,'',NULL,NULL,'',1,0,'F','0','0','manage:hongyiStudent:remove','#','admin','2026-03-19 20:18:54','',NULL,''),(2069,'场馆信息管理',2030,0,'venuelist','manage/venue/index',NULL,'',1,0,'C','0','0',NULL,'#','admin','2026-03-19 20:24:56','',NULL,''),(2070,'弘毅馆学生管理',2010,1,'hongyiStudent','manage/hongyiStudent/index',NULL,'',1,0,'C','0','0','manage:hongyiStudent:list','#','admin','2026-03-19 20:26:30','',NULL,'弘毅馆学生管理菜单'),(2071,'弘毅馆学生管理查询',2070,1,'#','',NULL,'',1,0,'F','0','0','manage:hongyiStudent:query','#','admin','2026-03-19 20:26:31','',NULL,''),(2072,'弘毅馆学生管理新增',2070,2,'#','',NULL,'',1,0,'F','0','0','manage:hongyiStudent:add','#','admin','2026-03-19 20:26:31','',NULL,''),(2073,'弘毅馆学生管理修改',2070,3,'#','',NULL,'',1,0,'F','0','0','manage:hongyiStudent:edit','#','admin','2026-03-19 20:26:31','',NULL,''),(2074,'弘毅馆学生管理删除',2070,4,'#','',NULL,'',1,0,'F','0','0','manage:hongyiStudent:remove','#','admin','2026-03-19 20:26:31','',NULL,''),(2075,'弘毅馆学生管理导出',2070,5,'#','',NULL,'',1,0,'F','0','0','manage:hongyiStudent:export','#','admin','2026-03-19 20:26:31','',NULL,''),(2076,'心缘馆学生管理',2008,3,'xinyuanStudent','manage/xinyuanStudent/index',NULL,'',1,0,'C','0','0','manage:xinyuanStudent:list','user','admin','2026-03-20 12:19:20','',NULL,''),(2077,'心缘馆学生管理查询',2076,1,'',NULL,NULL,'',1,0,'F','0','0','manage:xinyuanStudent:query','#','admin','2026-03-20 12:19:20','',NULL,''),(2078,'心缘馆学生管理新增',2076,2,'',NULL,NULL,'',1,0,'F','0','0','manage:xinyuanStudent:add','#','admin','2026-03-20 12:19:20','',NULL,''),(2079,'心缘馆学生管理修改',2076,3,'',NULL,NULL,'',1,0,'F','0','0','manage:xinyuanStudent:edit','#','admin','2026-03-20 12:19:20','',NULL,''),(2080,'心缘馆学生管理删除',2076,4,'',NULL,NULL,'',1,0,'F','0','0','manage:xinyuanStudent:remove','#','admin','2026-03-20 12:19:20','',NULL,''),(2081,'心缘馆学生管理导出',2076,5,'',NULL,NULL,'',1,0,'F','0','0','manage:xinyuanStudent:export','#','admin','2026-03-20 12:19:20','',NULL,''),(2082,'笃学馆学生管理',2008,4,'duxueStudent','manage/duxueStudent/index',NULL,'',1,0,'C','0','0','manage:duxueStudent:list','user','admin','2026-03-20 12:19:20','',NULL,''),(2083,'笃学馆学生管理查询',2082,1,'',NULL,NULL,'',1,0,'F','0','0','manage:duxueStudent:query','#','admin','2026-03-20 12:19:20','',NULL,''),(2084,'笃学馆学生管理新增',2082,2,'',NULL,NULL,'',1,0,'F','0','0','manage:duxueStudent:add','#','admin','2026-03-20 12:19:20','',NULL,''),(2085,'笃学馆学生管理修改',2082,3,'',NULL,NULL,'',1,0,'F','0','0','manage:duxueStudent:edit','#','admin','2026-03-20 12:19:20','',NULL,''),(2086,'笃学馆学生管理删除',2082,4,'',NULL,NULL,'',1,0,'F','0','0','manage:duxueStudent:remove','#','admin','2026-03-20 12:19:20','',NULL,''),(2087,'笃学馆学生管理导出',2082,5,'',NULL,NULL,'',1,0,'F','0','0','manage:duxueStudent:export','#','admin','2026-03-20 12:19:20','',NULL,''),(2088,'知行馆学生管理',2008,5,'zhixingStudent','manage/zhixingStudent/index',NULL,'',1,0,'C','0','0','manage:zhixingStudent:list','user','admin','2026-03-20 12:19:20','',NULL,''),(2089,'知行馆学生管理查询',2088,1,'',NULL,NULL,'',1,0,'F','0','0','manage:zhixingStudent:query','#','admin','2026-03-20 12:19:20','',NULL,''),(2090,'知行馆学生管理新增',2088,2,'',NULL,NULL,'',1,0,'F','0','0','manage:zhixingStudent:add','#','admin','2026-03-20 12:19:20','',NULL,''),(2091,'知行馆学生管理修改',2088,3,'',NULL,NULL,'',1,0,'F','0','0','manage:zhixingStudent:edit','#','admin','2026-03-20 12:19:20','',NULL,''),(2092,'知行馆学生管理删除',2088,4,'',NULL,NULL,'',1,0,'F','0','0','manage:zhixingStudent:remove','#','admin','2026-03-20 12:19:20','',NULL,''),(2093,'知行馆学生管理导出',2088,5,'',NULL,NULL,'',1,0,'F','0','0','manage:zhixingStudent:export','#','admin','2026-03-20 12:19:20','',NULL,''),(2094,'国防教育体验馆学生管理',2008,6,'guofangStudent','manage/guofangStudent/index',NULL,'',1,0,'C','0','0','manage:guofangStudent:list','user','admin','2026-03-20 12:19:20','',NULL,''),(2095,'国防教育体验馆学生管理查询',2094,1,'',NULL,NULL,'',1,0,'F','0','0','manage:guofangStudent:query','#','admin','2026-03-20 12:19:20','',NULL,''),(2096,'国防教育体验馆学生管理新增',2094,2,'',NULL,NULL,'',1,0,'F','0','0','manage:guofangStudent:add','#','admin','2026-03-20 12:19:20','',NULL,''),(2097,'国防教育体验馆学生管理修改',2094,3,'',NULL,NULL,'',1,0,'F','0','0','manage:guofangStudent:edit','#','admin','2026-03-20 12:19:20','',NULL,''),(2098,'国防教育体验馆学生管理删除',2094,4,'',NULL,NULL,'',1,0,'F','0','0','manage:guofangStudent:remove','#','admin','2026-03-20 12:19:20','',NULL,''),(2099,'国防教育体验馆学生管理导出',2094,5,'',NULL,NULL,'',1,0,'F','0','0','manage:guofangStudent:export','#','admin','2026-03-20 12:19:20','',NULL,''),(2118,'值班表管理',2120,0,'dutySchedule','manage/dutySchedule/index',NULL,'',1,0,'C','0','0','manage:dutySchedule:list','table','admin','2026-03-21 20:28:04','admin','2026-03-22 12:44:24','值班表管理'),(2119,'值班时间配置',2120,1,'dutyTimeConfig','manage/dutyTimeConfig/index',NULL,'',1,0,'C','0','0','manage:dutyTimeConfig:list','time','admin','2026-03-21 20:28:04','admin','2026-03-22 12:44:32','值班时间配置'),(2120,'场馆值班管理',0,2,'venueDuty',NULL,NULL,'',1,0,'M','0','0','','peoples','admin','2026-03-22 10:55:45','admin','2026-03-22 12:45:07','各场馆值班管理目录'),(2121,'思齐馆值班',2120,2,'siqiDuty','manage/dutySchedule/venue/Siqi',NULL,'',1,0,'C','0','0','manage:dutySchedule:siqi','skill','admin','2026-03-22 10:55:45','admin','2026-03-22 12:44:40','思齐馆值班管理'),(2122,'弘毅馆值班',2120,3,'hongyiDuty','manage/dutySchedule/venue/Hongyi',NULL,'',1,0,'C','0','0','manage:dutySchedule:hongyi','skill','admin','2026-03-22 10:55:45','admin','2026-03-22 12:44:44','弘毅馆值班管理'),(2123,'心缘馆值班',2120,4,'xinyuanDuty','manage/dutySchedule/venue/Xinyuan',NULL,'',1,0,'C','0','0','manage:dutySchedule:xinyuan','skill','admin','2026-03-22 10:55:45','admin','2026-03-22 12:44:47','心缘馆值班管理'),(2124,'笃学馆值班',2120,5,'duxueDuty','manage/dutySchedule/venue/Duxue',NULL,'',1,0,'C','0','0','manage:dutySchedule:duxue','skill','admin','2026-03-22 10:55:45','admin','2026-03-22 12:44:51','笃学馆值班管理'),(2125,'知行馆值班',2120,6,'zhixingDuty','manage/dutySchedule/venue/Zhixing',NULL,'',1,0,'C','0','0','manage:dutySchedule:zhixing','skill','admin','2026-03-22 10:55:45','admin','2026-03-22 12:44:55','知行馆值班管理'),(2126,'国防教育体验馆值班',2120,7,'guofangDuty','manage/dutySchedule/venue/Guofang',NULL,'',1,0,'C','0','0','manage:dutySchedule:guofang','skill','admin','2026-03-22 10:55:45','admin','2026-03-22 12:44:59','国防教育体验馆值班管理'),(2127,'值班表添加',2118,2,'','',NULL,'',1,0,'F','0','0','manage:dutySchedule:add','#','admin','2026-03-22 14:01:14','',NULL,''),(2128,'值班表编辑',2118,3,'','',NULL,'',1,0,'F','0','0','manage:dutySchedule:edit','#','admin','2026-03-22 14:01:14','',NULL,''),(2129,'值班表删除',2118,4,'','',NULL,'',1,0,'F','0','0','manage:dutySchedule:remove','#','admin','2026-03-22 14:01:14','',NULL,''),(2130,'值班表查询',2118,5,'','',NULL,'',1,0,'F','0','0','manage:dutySchedule:query','#','admin','2026-03-22 14:01:14','',NULL,''),(2131,'值班时间配置查询',2119,2,'','',NULL,'',1,0,'F','0','0','manage:dutyTimeConfig:query','#','admin','2026-03-22 14:07:24','',NULL,''),(2132,'值班时间配置添加',2119,3,'','',NULL,'',1,0,'F','0','0','manage:dutyTimeConfig:add','#','admin','2026-03-22 14:07:24','',NULL,''),(2133,'值班时间配置编辑',2119,4,'','',NULL,'',1,0,'F','0','0','manage:dutyTimeConfig:edit','#','admin','2026-03-22 14:07:24','',NULL,''),(2134,'值班时间配置删除',2119,5,'','',NULL,'',1,0,'F','0','0','manage:dutyTimeConfig:remove','#','admin','2026-03-22 14:07:24','',NULL,''),(2135,'场馆管理列表',2120,2,'','',NULL,'',1,0,'F','0','0','manage:venue:list','#','admin','2026-03-22 14:10:20','',NULL,''),(2136,'场馆管理查询',2120,3,'','',NULL,'',1,0,'F','0','0','manage:venue:query','#','admin','2026-03-22 14:10:20','',NULL,''),(2137,'考勤管理',0,8,'attendance',NULL,NULL,'',1,0,'M','0','0','','table','admin','2026-03-26 11:37:18','',NULL,''),(2139,'考勤统计',2137,1,'/attendance/index','manage/attendance/index',NULL,'',1,0,'C','0','0','manage:attendance:list','table','admin','2026-03-26 11:40:22','',NULL,''),(2142,'考勤记录',2137,2,'record','manage/attendance/record',NULL,'',1,0,'C','0','0','manage:attendance:record','table','admin','2026-03-26 11:41:27','',NULL,''),(2143,'Admin Home',0,1,'adminHome','manage/AdministratorHomePage/index',NULL,'',1,0,'C','1','0','manage:adminHome:list','home','admin','2026-03-26 12:20:27','',NULL,''),(2144,'Duty Officer Home',0,2,'dutyHome','manage/DutyOfficerHomepage/index',NULL,'',1,0,'C','1','0','manage:dutyHome:list','home','admin','2026-03-26 12:20:27','',NULL,'');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 若依新版本发布啦','2',_binary '新版本内容','0','admin','2026-03-18 16:35:47','',NULL,'管理员'),(2,'维护通知：2018-07-01 若依系统凌晨维护','1',_binary '维护内容','0','admin','2026-03-18 16:35:47','',NULL,'管理员');
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=284 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (100,'部门管理',3,'com.zcst.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','值班','/system/dept/109','127.0.0.1','内网IP','109 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:36:21',26),(101,'部门管理',3,'com.zcst.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','值班','/system/dept/108','127.0.0.1','内网IP','108 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:36:23',15),(102,'部门管理',3,'com.zcst.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','值班','/system/dept/102','127.0.0.1','内网IP','102 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:36:24',8),(103,'部门管理',3,'com.zcst.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','值班','/system/dept/107','127.0.0.1','内网IP','107 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:36:26',9),(104,'部门管理',3,'com.zcst.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','值班','/system/dept/106','127.0.0.1','内网IP','106 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:36:27',13),(105,'部门管理',3,'com.zcst.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','值班','/system/dept/105','127.0.0.1','内网IP','105 ','{\"msg\":\"部门存在用户,不允许删除\",\"code\":601}',0,NULL,'2026-03-18 16:36:29',5),(106,'角色管理',1,'com.zcst.web.controller.system.SysRoleController.add()','POST',1,'admin','值班','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createBy\":\"admin\",\"deptCheckStrictly\":true,\"deptIds\":[],\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi\",\"roleName\":\"思齐馆管理员\",\"roleSort\":0,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:42:58',39),(107,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','值班','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:43:14',28),(108,'角色管理',1,'com.zcst.web.controller.system.SysRoleController.add()','POST',1,'admin','值班','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createBy\":\"admin\",\"deptCheckStrictly\":true,\"deptIds\":[],\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"roleId\":101,\"roleKey\":\"hongyi\",\"roleName\":\"弘毅馆管理员\",\"roleSort\":4,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:43:44',8),(109,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"学生管理\",\"menuType\":\"M\",\"orderNum\":0,\"params\":{},\"parentId\":0,\"path\":\"student\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:44:54',31),(110,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/allStudent/index\",\"createBy\":\"admin\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"allStudent\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:46:00',21),(111,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/allStudent/index\",\"createTime\":\"2026-03-18 16:46:00\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"allStudent\",\"perms\":\"all\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:53:02',15),(112,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/allStudent/index\",\"createTime\":\"2026-03-18 16:46:00\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"allStudent\",\"perms\":\"all\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:54:17',22),(113,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/allStudent/index\",\"createTime\":\"2026-03-18 16:46:00\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"allStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:54:43',14),(114,'代码生成',6,'com.zcst.generator.controller.GenController.importTableSave()','POST',1,'admin','值班','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":\"student,college,major\",\"tplWebType\":\"element-plus\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:55:04',91),(115,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','值班','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"student\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 16:58:55',52),(116,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','值班','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"student\"}',NULL,0,NULL,'2026-03-18 16:59:00',341),(117,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/allStudent/index\",\"createTime\":\"2026-03-18 16:46:00\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"student\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'全部学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-18 17:05:01',29),(118,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/allStudent/index\",\"createTime\":\"2026-03-18 16:46:00\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"student\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'全部学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-18 17:05:04',14),(119,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/student/index\",\"createTime\":\"2026-03-18 16:46:00\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"allStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 17:14:44',26),(120,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-18 16:44:54\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2000,\"menuName\":\"学生管理\",\"menuType\":\"M\",\"orderNum\":0,\"params\":{},\"parentId\":0,\"path\":\"manage\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 17:36:02',48),(121,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/student/index\",\"createTime\":\"2026-03-18 16:46:00\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"student\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'全部学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-18 17:36:09',10),(122,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/student/index\",\"createTime\":\"2026-03-18 16:46:00\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"student\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'全部学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-18 17:36:42',14),(123,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2000','127.0.0.1','内网IP','2000 ','{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}',0,NULL,'2026-03-18 18:57:42',14),(124,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2001','127.0.0.1','内网IP','2001 ','{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}',0,NULL,'2026-03-18 18:57:46',0),(125,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2002','127.0.0.1','内网IP','2002 ','{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}',0,NULL,'2026-03-18 18:57:50',3),(126,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2007','127.0.0.1','内网IP','2007 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 18:58:03',17),(127,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2006','127.0.0.1','内网IP','2006 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 18:58:05',11),(128,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2005','127.0.0.1','内网IP','2005 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 18:58:06',13),(129,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2004','127.0.0.1','内网IP','2004 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 18:58:08',11),(130,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2003','127.0.0.1','内网IP','2003 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 18:58:10',9),(131,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2002','127.0.0.1','内网IP','2002 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 18:58:12',18),(132,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2001','127.0.0.1','内网IP','2001 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 18:58:14',11),(133,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2000','127.0.0.1','内网IP','2000 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 18:58:16',13),(134,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"学生管理\",\"menuType\":\"M\",\"orderNum\":0,\"params\":{},\"parentId\":0,\"path\":\"manage\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 18:58:53',33),(135,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/student/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2008,\"path\":\"student\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 18:59:47',16),(136,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"student\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 16:58:55\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 16:58:55\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 16:58:55\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":t','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 19:00:31',71),(137,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"student\"}',NULL,0,NULL,'2026-03-18 19:01:21',430),(138,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/student/index\",\"createTime\":\"2026-03-18 18:59:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2009,\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2008,\"path\":\"allStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 19:37:07',45),(139,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/student/index\",\"createTime\":\"2026-03-18 18:59:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2009,\"menuName\":\"全部学生管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2008,\"path\":\"allStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 19:37:24',17),(140,'参数管理',2,'com.zcst.web.controller.system.SysConfigController.edit()','PUT',1,'admin','研发部门','/system/config','127.0.0.1','内网IP','{\"configId\":4,\"configKey\":\"sys.account.captchaEnabled\",\"configName\":\"账号自助-验证码开关\",\"configType\":\"Y\",\"configValue\":\"false\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:35:46\",\"params\":{},\"remark\":\"是否开启验证码功能（true开启，false关闭）\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 19:55:53',39),(141,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/siqi/student/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"思齐馆学生管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2009,\"path\":\"siqiStudent\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 20:49:57',41),(142,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyi/student/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2009,\"path\":\"hongyi\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 20:51:05',18),(143,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/siqi/student/index\",\"createTime\":\"2026-03-18 20:49:57\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2010,\"menuName\":\"思齐馆学生管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2008,\"path\":\"siqiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 20:52:41',22),(144,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyi/student/index\",\"createTime\":\"2026-03-18 20:51:05\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2011,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyi\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 20:52:53',20),(145,'代码生成',2,'com.zcst.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/student','127.0.0.1','内网IP','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 21:10:38',86),(146,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"student\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:10:38\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:10:38\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:10:38\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":t','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 21:10:49',66),(147,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"student\"}',NULL,0,NULL,'2026-03-18 21:10:58',202),(148,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"student\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:10:49\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:10:49\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:10:49\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":t','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 21:22:15',84),(149,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"student\"}',NULL,0,NULL,'2026-03-18 21:22:20',207),(150,'字典类型',1,'com.zcst.web.controller.system.SysDictTypeController.add()','POST',1,'admin','研发部门','/system/dict/type','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"dictName\":\"场馆名称\",\"dictType\":\"zcst_venue_name\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 21:28:23',199),(151,'字典数据',1,'com.zcst.web.controller.system.SysDictDataController.add()','POST',1,'admin','研发部门','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"思齐馆\",\"dictSort\":1,\"dictType\":\"zcst_venue_name\",\"dictValue\":\"1\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 21:29:04',17),(152,'字典数据',1,'com.zcst.web.controller.system.SysDictDataController.add()','POST',1,'admin','研发部门','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"弘毅馆\",\"dictSort\":2,\"dictType\":\"zcst_venue_name\",\"dictValue\":\"2\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 21:29:13',16),(153,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"student\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:22:15\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:22:15\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:22:15\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":t',NULL,1,'关联子表的表名不能为空','2026-03-18 21:29:51',26),(154,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"student\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"0\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:22:15\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:22:15\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:22:15\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":t','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 21:29:54',70),(155,'代码生成',6,'com.zcst.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":\"venue\",\"tplWebType\":\"element-plus\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 21:30:01',53),(156,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"student\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:29:54\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:29:54\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:29:54\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":tr','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-18 21:32:17',36),(157,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"student\"}',NULL,0,NULL,'2026-03-18 21:32:25',243),(158,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"场馆管理\",\"menuType\":\"M\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"venue\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:27:34',48),(159,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-18 16:35:45\",\"icon\":\"system\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1,\"menuName\":\"系统管理\",\"menuType\":\"M\",\"orderNum\":10,\"params\":{},\"parentId\":0,\"path\":\"system\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:27:45',17),(160,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-18 16:35:45\",\"icon\":\"guide\",\"isCache\":\"0\",\"isFrame\":\"0\",\"menuId\":4,\"menuName\":\"若依官网\",\"menuType\":\"M\",\"orderNum\":14,\"params\":{},\"parentId\":0,\"path\":\"http://ruoyi.vip\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:27:53',16),(161,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-18 16:35:45\",\"icon\":\"tool\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3,\"menuName\":\"系统工具\",\"menuType\":\"M\",\"orderNum\":13,\"params\":{},\"parentId\":0,\"path\":\"tool\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:27:56',17),(162,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-18 16:35:45\",\"icon\":\"system\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1,\"menuName\":\"系统管理\",\"menuType\":\"M\",\"orderNum\":11,\"params\":{},\"parentId\":0,\"path\":\"system\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:28:00',16),(163,'参数管理',2,'com.zcst.web.controller.system.SysConfigController.edit()','PUT',1,'admin','研发部门','/system/config','127.0.0.1','内网IP','{\"configId\":5,\"configKey\":\"sys.account.registerUser\",\"configName\":\"账号自助-是否开启用户注册功能\",\"configType\":\"Y\",\"configValue\":\"true\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:35:46\",\"params\":{},\"remark\":\"是否开启注册用户功能（true开启，false关闭）\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:33:34',24),(164,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','研发部门','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-19 15:37:05\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"lisi\",\"params\":{},\"phonenumber\":\"\",\"postIds\":[],\"pwdUpdateDate\":\"2026-03-19 15:37:05\",\"roleIds\":[100],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":100,\"userName\":\"lisi\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:38:24',33),(165,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2010],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:38:41',20),(166,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:43:44\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2011],\"params\":{},\"roleId\":101,\"roleKey\":\"hongyi\",\"roleName\":\"弘毅馆管理员\",\"roleSort\":4,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:38:47',22),(167,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2010],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi:*:*\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:41:54',16),(168,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2009,2012,2013,2014,2015,2016,2017,2018,2019,2020,2021,2022,2023,2024,2025,2026,2027,2028,2029,2010],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi:*:*\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:43:31',16),(169,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyi/student/index\",\"createTime\":\"2026-03-18 20:51:05\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2011,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyi\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:45:20',20),(170,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2010],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi:*:*\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:47:05',23),(171,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"student\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:32:17\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:32:17\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-18 21:32:17\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":tr','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:48:56',60),(172,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"hongyiStudent\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 15:48:56\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 15:48:56\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 15:48:56\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"inse','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 15:54:26',105),(173,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"student\"}',NULL,0,NULL,'2026-03-19 15:54:31',301),(174,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2010],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 16:33:14',245),(175,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-19 15:27:34\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2030,\"menuName\":\"场馆管理\",\"menuType\":\"M\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"venue\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 16:33:33',28),(176,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"siqiStudent\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 15:54:26\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 15:54:26\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 15:54:26\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:07:16',75),(177,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"student\"}',NULL,0,NULL,'2026-03-19 17:07:24',332),(178,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyi/student/index\",\"createTime\":\"2026-03-18 20:51:05\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2011,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-19 17:15:30',7),(179,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2036','127.0.0.1','内网IP','2036 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:15:49',17),(180,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2034','127.0.0.1','内网IP','2034 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:15:52',10),(181,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2033','127.0.0.1','内网IP','2033 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:15:54',10),(182,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2032','127.0.0.1','内网IP','2032 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:15:56',10),(183,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2035','127.0.0.1','内网IP','2035 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:15:58',6),(184,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','研发部门','/system/menu/2031','127.0.0.1','内网IP','2031 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:16:00',9),(185,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyi/student/index\",\"createTime\":\"2026-03-18 20:51:05\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2011,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:16:13',10),(186,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"siqiStudent\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 17:07:16\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 17:07:16\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 17:07:16\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:16:54',30),(187,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"student\"}',NULL,0,NULL,'2026-03-19 17:16:59',47),(188,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2010,2037,2038,2039,2040,2041,2042],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:30:11',21),(189,'角色管理',3,'com.zcst.web.controller.system.SysRoleController.remove()','DELETE',1,'admin','研发部门','/system/role/100','127.0.0.1','内网IP','[100] ',NULL,1,'思齐馆管理员已分配,不能删除','2026-03-19 17:31:33',9),(190,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"思齐馆学生管理\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2008,\"path\":\"manage/siqi/student/index\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 17:42:35',18),(191,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"思齐馆学生管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2008,\"path\":\"siqiStudent\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:01:19',221),(192,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','研发部门','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"siqiStudent\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 17:16:54\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 17:16:54\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 17:16:54\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:02:05',91),(193,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"student\"}',NULL,0,NULL,'2026-03-19 18:02:10',244),(194,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/siqi.student/index\",\"createTime\":\"2026-03-19 18:01:19\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2044,\"menuName\":\"思齐馆学生管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2008,\"path\":\"siqiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:11:28',49),(195,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/siqi/student/index\",\"createTime\":\"2026-03-19 18:01:19\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2044,\"menuName\":\"思齐馆学生管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2008,\"path\":\"siqiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:12:21',15),(196,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/siqiStudent/index\",\"createTime\":\"2026-03-19 18:01:19\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2044,\"menuName\":\"思齐馆学生管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2008,\"path\":\"siqiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:14:07',24),(197,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/siqiStudent/index\",\"createTime\":\"2026-03-19 18:01:19\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2044,\"menuName\":\"思齐馆学生管理add\",\"menuType\":\"F\",\"orderNum\":1,\"params\":{},\"parentId\":2008,\"path\":\"siqiStudent\",\"perms\":\"manage:siqiStudent:add\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:18:37',39),(198,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/siqiStudent/index\",\"createTime\":\"2026-03-19 18:01:19\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2044,\"menuName\":\"思齐馆学生管理\",\"menuType\":\"F\",\"orderNum\":1,\"params\":{},\"parentId\":2008,\"path\":\"siqiStudent\",\"perms\":\"manage:siqiStudent:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:19:02',26),(199,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"add\",\"menuType\":\"F\",\"orderNum\":1,\"params\":{},\"parentId\":2044,\"perms\":\"manage:siqiStudent:add\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:19:24',19),(200,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-19 18:19:24\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2051,\"menuName\":\"思齐馆学生新增\",\"menuType\":\"F\",\"orderNum\":1,\"params\":{},\"parentId\":2044,\"path\":\"\",\"perms\":\"manage:siqiStudent:add\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:19:49',17),(201,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"思齐馆学生导出\",\"menuType\":\"F\",\"orderNum\":2,\"params\":{},\"parentId\":2044,\"perms\":\"manage:siqiStudent:export\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:20:26',23),(202,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"思齐馆学生刷新\",\"menuType\":\"F\",\"orderNum\":3,\"params\":{},\"parentId\":2044,\"perms\":\"manage:siqiStudent:query\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:21:21',25),(203,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"思齐馆学生修改\",\"menuType\":\"F\",\"orderNum\":4,\"params\":{},\"parentId\":2044,\"perms\":\"manage:siqiStudent:edit\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:21:50',14),(204,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"思齐馆学生删除\",\"menuType\":\"F\",\"orderNum\":5,\"params\":{},\"parentId\":2044,\"perms\":\"manage:siqiStudent:remove\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:22:15',16),(205,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','研发部门','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2044,2051,2052,2053,2054,2055],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:22:30',41),(206,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/siqiStudent/index\",\"createTime\":\"2026-03-19 18:01:19\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2044,\"menuName\":\"思齐馆学生管理\",\"menuType\":\"M\",\"orderNum\":1,\"params\":{},\"parentId\":2008,\"path\":\"siqiStudent\",\"perms\":\"manage:siqiStudent:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:40:49',51),(207,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyi\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 18:41:46',36),(208,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','研发部门','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-19 18:42:00',16),(209,'部门管理',2,'com.zcst.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0\",\"children\":[],\"deptId\":100,\"deptName\":\"珠海科技学院\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":0,\"params\":{},\"parentId\":0,\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:14:59',47),(210,'部门管理',1,'com.zcst.web.controller.system.SysDeptController.add()','POST',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100\",\"children\":[],\"createBy\":\"admin\",\"deptName\":\"弘毅馆\",\"orderNum\":1,\"params\":{},\"parentId\":100,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:15:10',22),(211,'部门管理',2,'com.zcst.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100\",\"children\":[],\"deptId\":101,\"deptName\":\"思齐馆\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":1,\"params\":{},\"parentId\":100,\"parentName\":\"珠海科技学院\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:15:18',36),(212,'部门管理',2,'com.zcst.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100\",\"children\":[],\"deptId\":101,\"deptName\":\"思齐馆\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":0,\"params\":{},\"parentId\":100,\"parentName\":\"珠海科技学院\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:15:23',27),(213,'部门管理',2,'com.zcst.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":103,\"deptName\":\"值班员\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":1,\"params\":{},\"parentId\":101,\"parentName\":\"思齐馆\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:15:38',26),(214,'部门管理',2,'com.zcst.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":103,\"deptName\":\"值班\",\"email\":\"ry@qq.com\",\"leader\":\"若依\",\"orderNum\":1,\"params\":{},\"parentId\":101,\"parentName\":\"思齐馆\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:15:57',19),(215,'岗位管理',3,'com.zcst.web.controller.system.SysPostController.remove()','DELETE',1,'admin','研发部门','/system/post/3','127.0.0.1','内网IP','[3] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:16:04',16),(216,'岗位管理',3,'com.zcst.web.controller.system.SysPostController.remove()','DELETE',1,'admin','研发部门','/system/post/2','127.0.0.1','内网IP','[2] ',NULL,1,'项目经理已分配,不能删除','2026-03-19 19:16:06',7),(217,'岗位管理',1,'com.zcst.web.controller.system.SysPostController.add()','POST',1,'admin','研发部门','/system/post','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"flag\":false,\"params\":{},\"postCode\":\"sdi\",\"postId\":5,\"postName\":\"值班员\",\"postSort\":5,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:16:26',29),(218,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','研发部门','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"陈铭\",\"params\":{},\"phonenumber\":\"13800138001\",\"postIds\":[5],\"roleIds\":[],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":101,\"userName\":\"20260001\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:16:35',52),(219,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','值班','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"hongyiStudent\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 18:02:05\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 18:02:05\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 18:02:05\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"inse','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:21:41',69),(220,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:24:17',19),(221,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','值班','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"hongyiStudent\",\"className\":\"Student\",\"columns\":[{\"capJavaField\":\"StudentId\",\"columnComment\":\"学号\",\"columnId\":10,\"columnName\":\"student_id\",\"columnType\":\"varchar(20)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"studentId\",\"javaType\":\"String\",\"list\":false,\"params\":{},\"pk\":true,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":1,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 19:21:41\",\"usableColumn\":false},{\"capJavaField\":\"Name\",\"columnComment\":\"姓名\",\"columnId\":11,\"columnName\":\"name\",\"columnType\":\"varchar(50)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"name\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 19:21:41\",\"usableColumn\":false},{\"capJavaField\":\"Gender\",\"columnComment\":\"性别（男/女）\",\"columnId\":12,\"columnName\":\"gender\",\"columnType\":\"char(1)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"gender\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":3,\"updateBy\":\"\",\"updateTime\":\"2026-03-19 19:21:41\",\"usableColumn\":false},{\"capJavaField\":\"MajorId\",\"columnComment\":\"专业ID\",\"columnId\":13,\"columnName\":\"major_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:55:04\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"inse','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 19:24:41',33),(222,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','值班','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"student\"}',NULL,0,NULL,'2026-03-19 19:24:52',208),(223,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyiStudent/index\",\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-19 20:08:20',31),(224,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyiStudent/index\",\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-19 20:08:22',15),(225,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyiStudent/index\",\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-19 20:08:24',6),(226,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyiStudent/index\",\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-19 20:08:35',11),(227,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyiStudent/index\",\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-19 20:09:11',9),(228,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-19 20:09:22',7),(229,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-19 20:09:29',7),(230,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"弘毅馆学生列表\",\"menuType\":\"F\",\"orderNum\":0,\"params\":{},\"parentId\":2056,\"perms\":\"manage:hongyiStudent:list\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:15:05',267),(231,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyiStudent/index\",\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"F\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"manage:hongyiStudent:list\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-19 20:15:50',10),(232,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"导出\",\"menuType\":\"F\",\"orderNum\":1,\"params\":{},\"parentId\":2056,\"perms\":\"manage:hongyiStudent:export\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:16:32',17),(233,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"信息\",\"menuType\":\"F\",\"orderNum\":2,\"params\":{},\"parentId\":2056,\"perms\":\"manage:hongyiStudent:query\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:17:07',16),(234,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"添加\",\"menuType\":\"F\",\"orderNum\":3,\"params\":{},\"parentId\":2056,\"perms\":\"manage:hongyiStudent:add\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:17:51',22),(235,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"修改\",\"menuType\":\"F\",\"orderNum\":4,\"params\":{},\"parentId\":2056,\"perms\":\"manage:hongyiStudent:edit\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:18:32',20),(236,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"删除\",\"menuType\":\"F\",\"orderNum\":5,\"params\":{},\"parentId\":2056,\"perms\":\"manage:hongyiStudent:remove\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:18:55',24),(237,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','值班','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:43:44\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2056,2063,2064,2065,2066,2067,2068],\"params\":{},\"roleId\":101,\"roleKey\":\"hongyi\",\"roleName\":\"弘毅馆管理员\",\"roleSort\":4,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:19:08',36),(238,'用户管理',1,'com.zcst.web.controller.system.SysUserController.add()','POST',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"createBy\":\"admin\",\"nickName\":\"wangwu\",\"params\":{},\"postIds\":[],\"roleIds\":[101],\"status\":\"0\",\"userId\":117,\"userName\":\"王五\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:19:44',115),(239,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-19 15:27:34\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2030,\"menuName\":\"场馆信息管理\",\"menuType\":\"M\",\"orderNum\":1,\"params\":{},\"parentId\":2030,\"path\":\"venue\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'场馆信息管理\'失败，上级菜单不能选择自己\",\"code\":500}',0,NULL,'2026-03-19 20:20:52',5),(240,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"venue/index\",\"createTime\":\"2026-03-19 15:27:34\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2030,\"menuName\":\"场馆信息管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2030,\"path\":\"venue\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'场馆信息管理\'失败，上级菜单不能选择自己\",\"code\":500}',0,NULL,'2026-03-19 20:21:37',3),(241,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-19 15:27:34\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2030,\"menuName\":\"场馆管理\",\"menuType\":\"M\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"venue\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:21:49',17),(242,'菜单管理',1,'com.zcst.web.controller.system.SysMenuController.add()','POST',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/venue/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"场馆信息管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2030,\"path\":\"venuelist\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:24:56',23),(243,'代码生成',2,'com.zcst.generator.controller.GenController.editSave()','PUT',1,'admin','值班','/tool/gen','127.0.0.1','内网IP','{\"businessName\":\"venue\",\"className\":\"Venue\",\"columns\":[{\"capJavaField\":\"VenueId\",\"columnComment\":\"场馆ID\",\"columnId\":20,\"columnName\":\"venue_id\",\"columnType\":\"int\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 21:30:01\",\"dictType\":\"\",\"edit\":false,\"htmlType\":\"input\",\"increment\":true,\"insert\":true,\"isIncrement\":\"1\",\"isInsert\":\"1\",\"isPk\":\"1\",\"isRequired\":\"0\",\"javaField\":\"venueId\",\"javaType\":\"Long\",\"list\":false,\"params\":{},\"pk\":true,\"query\":false,\"queryType\":\"EQ\",\"required\":false,\"sort\":1,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"VenueName\",\"columnComment\":\"场馆名称\",\"columnId\":21,\"columnName\":\"venue_name\",\"columnType\":\"varchar(100)\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 21:30:01\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"input\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"venueName\",\"javaType\":\"String\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"LIKE\",\"required\":true,\"sort\":2,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"CreatedAt\",\"columnComment\":\"创建时间\",\"columnId\":22,\"columnName\":\"created_at\",\"columnType\":\"datetime\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 21:30:01\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"datetime\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequired\":\"1\",\"javaField\":\"createdAt\",\"javaType\":\"Date\",\"list\":true,\"params\":{},\"pk\":false,\"query\":true,\"queryType\":\"EQ\",\"required\":true,\"sort\":3,\"superColumn\":false,\"tableId\":4,\"updateBy\":\"\",\"usableColumn\":false},{\"capJavaField\":\"UpdatedAt\",\"columnComment\":\"更新时间\",\"columnId\":23,\"columnName\":\"updated_at\",\"columnType\":\"datetime\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 21:30:01\",\"dictType\":\"\",\"edit\":true,\"htmlType\":\"datetime\",\"increment\":false,\"insert\":true,\"isEdit\":\"1\",\"isIncrement\":\"0\",\"isInsert\":\"1\",\"isList\":\"1\",\"isPk\":\"0\",\"isQuery\":\"1\",\"isRequir','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-19 20:25:40',53),(244,'代码生成',8,'com.zcst.generator.controller.GenController.batchGenCode()','GET',1,'admin','值班','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":\"venue\"}',NULL,0,NULL,'2026-03-19 20:26:06',47),(245,'字典数据',1,'com.zcst.web.controller.system.SysDictDataController.add()','POST',1,'admin','值班','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"心缘馆\",\"dictSort\":3,\"dictType\":\"zcst_venue_name\",\"dictValue\":\"3\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 13:14:13',21),(246,'字典数据',1,'com.zcst.web.controller.system.SysDictDataController.add()','POST',1,'admin','值班','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"笃学馆\",\"dictSort\":4,\"dictType\":\"zcst_venue_name\",\"dictValue\":\"4\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 13:14:24',14),(247,'字典数据',1,'com.zcst.web.controller.system.SysDictDataController.add()','POST',1,'admin','值班','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"知行馆\",\"dictSort\":5,\"dictType\":\"zcst_venue_name\",\"dictValue\":\"5\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 13:14:36',21),(248,'字典数据',1,'com.zcst.web.controller.system.SysDictDataController.add()','POST',1,'admin','值班','/system/dict/data','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"default\":false,\"dictLabel\":\"国防教育体验馆\",\"dictSort\":6,\"dictType\":\"zcst_venue_name\",\"dictValue\":\"6\",\"listClass\":\"default\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 13:14:52',21),(249,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyiStudent/index\",\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-20 15:47:55',27),(250,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/hongyiStudent/index\",\"createTime\":\"2026-03-19 18:41:46\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2056,\"menuName\":\"弘毅馆学生管理\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":2008,\"path\":\"hongyiStudent\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'弘毅馆学生管理\'失败，路由名称或地址已存在\",\"code\":500}',0,NULL,'2026-03-20 15:48:00',8),(251,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"陈铭\",\"params\":{},\"phonenumber\":\"13800138001\",\"postIds\":[5],\"roleIds\":[2],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":101,\"userName\":\"20260001\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:41:29',291),(252,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"林晓月\",\"params\":{},\"phonenumber\":\"13800138002\",\"postIds\":[],\"roleIds\":[2],\"roles\":[],\"sex\":\"1\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":102,\"userName\":\"20260002\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:41:34',32),(253,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"张浩然\",\"params\":{},\"phonenumber\":\"13800138003\",\"postIds\":[],\"roleIds\":[2],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":103,\"userName\":\"20260003\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:41:39',25),(254,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"王宇\",\"params\":{},\"phonenumber\":\"13800138005\",\"postIds\":[],\"roleIds\":[2],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":105,\"userName\":\"20260005\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:41:47',30),(255,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"苏晴\",\"params\":{},\"phonenumber\":\"13800138004\",\"postIds\":[],\"roleIds\":[2],\"roles\":[],\"sex\":\"1\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":104,\"userName\":\"20260004\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:41:50',27),(256,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"李诗琪\",\"params\":{},\"phonenumber\":\"13800138006\",\"postIds\":[],\"roleIds\":[2],\"roles\":[],\"sex\":\"1\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":106,\"userName\":\"20260006\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:41:54',29),(257,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"刘阳\",\"params\":{},\"phonenumber\":\"13800138007\",\"postIds\":[],\"roleIds\":[2],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":107,\"userName\":\"20260007\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:41:58',22),(258,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"孙浩博\",\"params\":{},\"phonenumber\":\"13800138009\",\"postIds\":[],\"roleIds\":[2],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":109,\"userName\":\"20260009\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:42:01',25),(259,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"赵雨萱\",\"params\":{},\"phonenumber\":\"13800138008\",\"postIds\":[],\"roleIds\":[2],\"roles\":[],\"sex\":\"1\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":108,\"userName\":\"20260008\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:42:04',27),(260,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-18 15:49:37\",\"delFlag\":\"0\",\"email\":\"\",\"loginIp\":\"\",\"nickName\":\"周梓涵\",\"params\":{},\"phonenumber\":\"13800138010\",\"postIds\":[],\"roleIds\":[2],\"roles\":[],\"sex\":\"1\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":110,\"userName\":\"20260010\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:42:09',19),(261,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-20 15:53:16\",\"delFlag\":\"0\",\"dept\":{\"children\":[],\"deptId\":401,\"params\":{}},\"deptId\":401,\"email\":\"\",\"loginDate\":\"2026-03-20 15:53:25\",\"loginIp\":\"127.0.0.1\",\"nickName\":\"嘎子\",\"params\":{},\"phonenumber\":\"\",\"postIds\":[],\"pwdUpdateDate\":\"2026-03-20 15:53:16\",\"roleIds\":[103],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":138,\"userName\":\"gazi\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 17:42:18',30),(262,'参数管理',2,'com.zcst.web.controller.system.SysConfigController.edit()','PUT',1,'admin','值班','/system/config','127.0.0.1','内网IP','{\"configId\":4,\"configKey\":\"sys.account.captchaEnabled\",\"configName\":\"账号自助-验证码开关\",\"configType\":\"Y\",\"configValue\":\"true\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:35:46\",\"params\":{},\"remark\":\"是否开启验证码功能（true开启，false关闭）\",\"updateBy\":\"admin\",\"updateTime\":\"2026-03-18 19:55:53\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-20 22:43:38',37),(263,'用户管理',2,'com.zcst.web.controller.system.SysUserController.edit()','PUT',1,'admin','值班','/system/user','127.0.0.1','内网IP','{\"admin\":false,\"avatar\":\"\",\"createBy\":\"\",\"createTime\":\"2026-03-21 10:05:47\",\"delFlag\":\"0\",\"dept\":{\"ancestors\":\"0,100,101\",\"children\":[],\"deptId\":103,\"deptName\":\"值班\",\"leader\":\"若依\",\"orderNum\":1,\"params\":{},\"parentId\":101,\"status\":\"0\"},\"deptId\":103,\"email\":\"\",\"loginDate\":\"2026-03-21 10:05:58\",\"loginIp\":\"127.0.0.1\",\"nickName\":\"刘能\",\"params\":{},\"phonenumber\":\"\",\"postIds\":[],\"pwdUpdateDate\":\"2026-03-21 10:05:47\",\"roleIds\":[104],\"roles\":[],\"sex\":\"0\",\"status\":\"0\",\"updateBy\":\"admin\",\"userId\":151,\"userName\":\"liuneng\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-21 10:06:31',30),(264,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/dutySchedule/index\",\"createTime\":\"2026-03-21 20:02:51\",\"icon\":\"table\",\"isCache\":\"0\",\"isFrame\":\"0\",\"menuId\":2100,\"menuName\":\"值班表管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"dutySchedule\",\"perms\":\"manage:dutySchedule:list\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"0\"} ','{\"msg\":\"修改菜单\'值班表管理\'失败，地址必须以http(s)://开头\",\"code\":500}',0,NULL,'2026-03-21 20:18:45',29),(265,'场馆值班表',1,'com.zcst.manage.controller.DutyScheduleController.autoScheduleByConfig()','POST',1,'admin','值班','/manage/dutySchedule/autoScheduleByConfig','127.0.0.1','内网IP','{\"venueId\":2,\"startDate\":\"2026-03-21T12:39:58.594Z\",\"weeks\":2} ',NULL,1,'class java.lang.String cannot be cast to class java.util.Date (java.lang.String and java.util.Date are in module java.base of loader \'bootstrap\')','2026-03-21 20:40:18',4),(266,'场馆值班表',2,'com.zcst.manage.controller.DutyScheduleController.edit()','PUT',1,'admin','值班','/manage/dutySchedule','127.0.0.1','内网IP','{\"dutyId\":29,\"endTime\":\"2026-03-18 11:40:00\",\"params\":{},\"startTime\":\"2026-03-18 10:00:00\",\"studentId\":\"20260102\",\"venueId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:18:11',33),(267,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/dutySchedule/index\",\"createTime\":\"2026-03-21 20:28:04\",\"icon\":\"table\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2118,\"menuName\":\"值班表管理\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2120,\"path\":\"dutySchedule\",\"perms\":\"manage:dutySchedule:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:44:24',38),(268,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/dutyTimeConfig/index\",\"createTime\":\"2026-03-21 20:28:04\",\"icon\":\"time\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2119,\"menuName\":\"值班时间配置\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2120,\"path\":\"dutyTimeConfig\",\"perms\":\"manage:dutyTimeConfig:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:44:32',14),(269,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/dutySchedule/venue/Siqi\",\"createTime\":\"2026-03-22 10:55:45\",\"icon\":\"skill\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2121,\"menuName\":\"思齐馆值班\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2120,\"path\":\"siqiDuty\",\"perms\":\"manage:dutySchedule:siqi\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:44:40',18),(270,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/dutySchedule/venue/Hongyi\",\"createTime\":\"2026-03-22 10:55:45\",\"icon\":\"skill\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2122,\"menuName\":\"弘毅馆值班\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2120,\"path\":\"hongyiDuty\",\"perms\":\"manage:dutySchedule:hongyi\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:44:44',19),(271,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/dutySchedule/venue/Xinyuan\",\"createTime\":\"2026-03-22 10:55:45\",\"icon\":\"skill\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2123,\"menuName\":\"心缘馆值班\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2120,\"path\":\"xinyuanDuty\",\"perms\":\"manage:dutySchedule:xinyuan\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:44:47',14),(272,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/dutySchedule/venue/Duxue\",\"createTime\":\"2026-03-22 10:55:45\",\"icon\":\"skill\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2124,\"menuName\":\"笃学馆值班\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2120,\"path\":\"duxueDuty\",\"perms\":\"manage:dutySchedule:duxue\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:44:51',19),(273,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/dutySchedule/venue/Zhixing\",\"createTime\":\"2026-03-22 10:55:45\",\"icon\":\"skill\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2125,\"menuName\":\"知行馆值班\",\"menuType\":\"C\",\"orderNum\":6,\"params\":{},\"parentId\":2120,\"path\":\"zhixingDuty\",\"perms\":\"manage:dutySchedule:zhixing\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:44:55',17),(274,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"manage/dutySchedule/venue/Guofang\",\"createTime\":\"2026-03-22 10:55:45\",\"icon\":\"skill\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2126,\"menuName\":\"国防教育体验馆值班\",\"menuType\":\"C\",\"orderNum\":7,\"params\":{},\"parentId\":2120,\"path\":\"guofangDuty\",\"perms\":\"manage:dutySchedule:guofang\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:44:59',15),(275,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-22 10:55:45\",\"icon\":\"peoples\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2120,\"menuName\":\"场馆值班管理\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":0,\"path\":\"venueDuty\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:45:07',14),(276,'菜单管理',2,'com.zcst.web.controller.system.SysMenuController.edit()','PUT',1,'admin','值班','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-03-18 16:35:45\",\"icon\":\"monitor\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2,\"menuName\":\"系统监控\",\"menuType\":\"M\",\"orderNum\":7,\"params\":{},\"parentId\":0,\"path\":\"monitor\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 12:45:13',13),(277,'菜单管理',3,'com.zcst.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','值班','/system/menu/2070','127.0.0.1','内网IP','2070 ','{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}',0,NULL,'2026-03-22 12:45:55',3),(278,'场馆值班表',1,'com.zcst.manage.controller.DutyScheduleController.autoScheduleByConfig()','POST',1,'admin','值班','/manage/dutySchedule/autoScheduleByConfig','127.0.0.1','内网IP','{\"endDate\":\"2026-04-01 23:59:59\",\"startDate\":\"2026-03-16 00:00:00\",\"venueId\":1} ','{\"msg\":\"自动排班成功\",\"code\":200}',0,NULL,'2026-03-22 13:32:31',2574),(279,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','值班','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2120,2044,2051,2052,2053,2054,2055,2119,2121],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 13:45:48',59),(280,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','值班','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2120,2044,2051,2052,2053,2054,2055,2118,2127,2128,2129,2130,2119,2131,2132,2133,2134,2121,2135,2136],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 14:13:49',25),(281,'角色管理',2,'com.zcst.web.controller.system.SysRoleController.edit()','PUT',1,'admin','值班','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2026-03-18 16:42:58\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2008,2120,2044,2051,2052,2053,2054,2055,2118,2127,2128,2129,2130,2119,2131,2132,2133,2134,2121,2135,2136],\"params\":{},\"roleId\":100,\"roleKey\":\"siqi\",\"roleName\":\"思齐馆管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 14:14:03',21),(282,'参数管理',2,'com.zcst.web.controller.system.SysConfigController.edit()','PUT',1,'admin','值班','/system/config','127.0.0.1','内网IP','{\"configId\":4,\"configKey\":\"sys.account.captchaEnabled\",\"configName\":\"账号自助-验证码开关\",\"configType\":\"Y\",\"configValue\":\"false\",\"createBy\":\"admin\",\"createTime\":\"2026-03-18 16:35:46\",\"params\":{},\"remark\":\"是否开启验证码功能（true开启，false关闭）\",\"updateBy\":\"admin\",\"updateTime\":\"2026-03-20 22:43:38\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-22 14:14:44',16),(283,'角色管理',3,'com.zcst.web.controller.system.SysRoleController.remove()','DELETE',1,'admin','值班','/system/role/106','127.0.0.1','内网IP','[106] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-27 12:07:47',80);
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,'0','admin','2026-03-18 16:35:44','',NULL,''),(2,'se','项目经理',2,'0','admin','2026-03-18 16:35:44','',NULL,''),(4,'user','普通员工',4,'0','admin','2026-03-18 16:35:44','',NULL,''),(5,'sdi','值班员',5,'0','admin','2026-03-19 19:16:26','',NULL,NULL);
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `venue_id` int DEFAULT NULL COMMENT '关联场馆 ID（场馆管理员使用）',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`),
  KEY `idx_venue_id` (`venue_id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',NULL,1,'1',1,1,'0','0','admin','2026-03-18 16:35:45','',NULL,'超级管理员'),(2,'普通角色','common',NULL,2,'2',1,1,'0','0','admin','2026-03-18 16:35:45','',NULL,'普通角色'),(100,'思齐馆管理员','siqi',1,3,'1',1,1,'0','0','admin','2026-03-18 16:42:58','admin','2026-03-22 14:14:03',NULL),(101,'弘毅馆管理员','hongyi',2,4,'1',1,1,'0','0','admin','2026-03-18 16:43:44','admin','2026-03-19 20:19:08',NULL),(102,'心缘馆管理员','xinyuan',3,5,'1',1,1,'0','0','admin','2026-03-20 12:19:20','',NULL,NULL),(103,'笃学馆管理员','duxue',4,6,'1',1,1,'0','0','admin','2026-03-20 12:19:20','',NULL,NULL),(104,'知行馆管理员','zhixing',5,7,'1',1,1,'0','0','admin','2026-03-20 12:19:20','',NULL,NULL),(105,'国防教育体验馆管理员','guofang',6,8,'1',1,1,'0','0','admin','2026-03-20 12:19:20','',NULL,NULL),(106,'??????','total_admin',NULL,2,'1',1,1,'0','2','admin','2026-03-26 12:58:02','',NULL,'??????????????????????????');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (1,2143),(2,1),(2,2),(2,3),(2,4),(2,100),(2,101),(2,102),(2,103),(2,104),(2,105),(2,106),(2,107),(2,108),(2,109),(2,110),(2,111),(2,112),(2,113),(2,114),(2,115),(2,116),(2,117),(2,500),(2,501),(2,1000),(2,1001),(2,1002),(2,1003),(2,1004),(2,1005),(2,1006),(2,1007),(2,1008),(2,1009),(2,1010),(2,1011),(2,1012),(2,1013),(2,1014),(2,1015),(2,1016),(2,1017),(2,1018),(2,1019),(2,1020),(2,1021),(2,1022),(2,1023),(2,1024),(2,1025),(2,1026),(2,1027),(2,1028),(2,1029),(2,1030),(2,1031),(2,1032),(2,1033),(2,1034),(2,1035),(2,1036),(2,1037),(2,1038),(2,1039),(2,1040),(2,1041),(2,1042),(2,1043),(2,1044),(2,1045),(2,1046),(2,1047),(2,1048),(2,1049),(2,1050),(2,1051),(2,1052),(2,1053),(2,1054),(2,1055),(2,1056),(2,1057),(2,1058),(2,1059),(2,1060),(2,2137),(2,2138),(2,2139),(2,2140),(2,2141),(2,2142),(2,2144),(100,2008),(100,2044),(100,2051),(100,2052),(100,2053),(100,2054),(100,2055),(100,2118),(100,2119),(100,2120),(100,2121),(100,2127),(100,2128),(100,2129),(100,2130),(100,2131),(100,2132),(100,2133),(100,2134),(100,2135),(100,2136),(100,2137),(100,2139),(100,2142),(101,2008),(101,2056),(101,2063),(101,2064),(101,2065),(101,2066),(101,2067),(101,2068),(101,2118),(101,2119),(101,2120),(101,2122),(101,2127),(101,2128),(101,2129),(101,2130),(101,2131),(101,2132),(101,2133),(101,2134),(101,2135),(101,2136),(101,2137),(101,2139),(101,2142),(102,2008),(102,2076),(102,2077),(102,2078),(102,2079),(102,2080),(102,2081),(102,2118),(102,2119),(102,2120),(102,2123),(102,2127),(102,2128),(102,2129),(102,2130),(102,2131),(102,2132),(102,2133),(102,2134),(102,2135),(102,2136),(102,2137),(102,2139),(102,2142),(103,2008),(103,2082),(103,2083),(103,2084),(103,2085),(103,2086),(103,2087),(103,2118),(103,2119),(103,2120),(103,2124),(103,2127),(103,2128),(103,2129),(103,2130),(103,2131),(103,2132),(103,2133),(103,2134),(103,2135),(103,2136),(103,2137),(103,2139),(103,2142),(104,2008),(104,2088),(104,2089),(104,2090),(104,2091),(104,2092),(104,2093),(104,2118),(104,2119),(104,2120),(104,2125),(104,2127),(104,2128),(104,2129),(104,2130),(104,2131),(104,2132),(104,2133),(104,2134),(104,2135),(104,2136),(104,2137),(104,2139),(104,2142),(105,2008),(105,2094),(105,2095),(105,2096),(105,2097),(105,2098),(105,2099),(105,2118),(105,2119),(105,2120),(105,2126),(105,2127),(105,2128),(105,2129),(105,2130),(105,2131),(105,2132),(105,2133),(105,2134),(105,2135),(105,2136),(105,2137),(105,2139),(105,2142);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户 01管理员 02学生）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `venue_id` bigint DEFAULT NULL COMMENT '场馆ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`),
  KEY `idx_user_type` (`user_type`),
  KEY `idx_venue_id` (`venue_id`),
  KEY `idx_login_date` (`login_date`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,103,'admin','若依','00','ry@163.com','15888888888','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-03-27 12:58:07','2026-03-18 16:35:44',NULL,'admin','2026-03-18 16:35:44','',NULL,'管理员'),(2,105,'ry','若依','00','ry@qq.com','15666666666','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-03-18 16:35:44','2026-03-18 16:35:44',NULL,'admin','2026-03-18 16:35:44','',NULL,'测试员'),(100,NULL,'lisi','lisi','00','','','0','','$2a$10$x9XUfSiqT0CMupGeYeSrweMn06OJSIfBH4xZlN44hGlAy8OCaft2.','0','0','127.0.0.1','2026-03-22 14:14:14','2026-03-19 15:37:05',NULL,'','2026-03-19 15:37:05','admin','2026-03-19 15:38:24',NULL),(101,NULL,'20260001','陈铭','00','','13800138001','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-18 15:49:37','admin','2026-03-20 17:41:29',NULL),(102,NULL,'20260002','林晓月','00','','13800138002','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-18 15:49:37','admin','2026-03-20 17:41:34',NULL),(103,NULL,'20260003','张浩然','00','','13800138003','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-18 15:49:37','admin','2026-03-20 17:41:39',NULL),(104,NULL,'20260004','苏晴','00','','13800138004','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-18 15:49:37','admin','2026-03-20 17:41:50',NULL),(105,NULL,'20260005','王宇','00','','13800138005','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-18 15:49:37','admin','2026-03-20 17:41:47',NULL),(106,NULL,'20260006','李诗琪','00','','13800138006','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-18 15:49:37','admin','2026-03-20 17:41:54',NULL),(107,NULL,'20260007','刘阳','00','','13800138007','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-18 15:49:37','admin','2026-03-20 17:41:58',NULL),(108,NULL,'20260008','赵雨萱','00','','13800138008','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-18 15:49:37','admin','2026-03-20 17:42:04',NULL),(109,NULL,'20260009','孙浩博','00','','13800138009','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-18 15:49:37','admin','2026-03-20 17:42:01',NULL),(110,NULL,'20260010','周梓涵','00','','13800138010','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-18 15:49:37','admin','2026-03-20 17:42:09',NULL),(116,NULL,'2024001','张三','00','','13800138000','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-19 16:29:53','',NULL,NULL),(117,NULL,'王五','wangwu','00','','','0','','$2a$10$12VPcWfyol7ewZWFkS0nZOyU8OzBLpapYQGeL6Pg2LMJHgICcBwfG','0','0','127.0.0.1','2026-03-19 20:20:04',NULL,NULL,'admin','2026-03-19 20:19:44','',NULL,NULL),(118,NULL,'20260011','吴思远','00','','13800138011','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(119,NULL,'20260012','郑雨桐','00','','13800138012','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(120,NULL,'20260013','马浩然','00','','13800138013','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(121,NULL,'20260014','刘梦琪','00','','13800138014','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(122,NULL,'20260015','周博文','00','','13800138015','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(123,NULL,'20260016','陈明宇','00','','13800138016','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(124,NULL,'20260017','林小雨','00','','13800138017','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(125,NULL,'20260018','王浩轩','00','','13800138018','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(126,NULL,'20260019','张诗涵','00','','13800138019','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(127,NULL,'20260020','李博文','00','','13800138020','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(128,NULL,'20260021','赵浩然','00','','13800138021','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(129,NULL,'20260022','陈雨萱','00','','13800138022','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(130,NULL,'20260023','刘博文','00','','13800138023','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(131,NULL,'20260024','王梦琪','00','','13800138024','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(132,NULL,'20260025','周浩然','00','','13800138025','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(133,NULL,'20260026','吴博文','00','','13800138026','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(134,NULL,'20260027','郑梦琪','00','','13800138027','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(135,NULL,'20260028','马浩然','00','','13800138028','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(136,NULL,'20260029','刘小雨','00','','13800138029','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(137,NULL,'20260030','周雨桐','00','','13800138030','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 15:42:01','',NULL,NULL),(138,401,'gazi','嘎子','00','','','0','','$2a$10$utvChsJELZpK.E/JVvZZF.hqAGEsMwCqKop661TbAmRC8iOYB8WHS','0','0','127.0.0.1','2026-03-20 17:43:27','2026-03-20 15:53:16',NULL,'','2026-03-20 15:53:16','admin','2026-03-20 17:42:18',NULL),(140,NULL,'20260101','张明','00','','13800138101','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 16:59:44','',NULL,NULL),(141,NULL,'20260102','李华','00','','13800138102','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 16:59:44','',NULL,NULL),(142,NULL,'20260103','王强','00','','13800138103','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 16:59:44','',NULL,NULL),(143,NULL,'20260104','赵敏','00','','13800138104','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 16:59:44','',NULL,NULL),(144,NULL,'20260105','刘伟','00','','13800138105','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 16:59:44','',NULL,NULL),(145,NULL,'20260106','陈丽','00','','13800138106','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 16:59:44','',NULL,NULL),(146,NULL,'20260107','赵刚','00','','13800138107','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 16:59:44','',NULL,NULL),(147,NULL,'20260108','孙燕','00','','13800138108','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 16:59:44','',NULL,NULL),(148,NULL,'20260109','周杰','00','','13800138109','0','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 16:59:44','',NULL,NULL),(149,NULL,'20260110','吴敏','00','','13800138110','1','','123456','0','0','',NULL,NULL,NULL,'','2026-03-20 16:59:44','',NULL,NULL),(150,NULL,'20260321','20260321','00','','13800138000','0','','$2a$10$PMl8rx3k5/RkVpDIbkHY9OWc5a1IOLH2.nidkEJG4dvNgDQhe7zsS','0','0','127.0.0.1','2026-03-21 10:04:39',NULL,NULL,'','2026-03-21 10:03:54','',NULL,NULL),(151,103,'liuneng','刘能','00','','','0','','$2a$10$GaslM9dP/WfAAJ4czNZ95OHb6129BJAzaDVM8a0nucK52iBO34JaW','0','0','127.0.0.1','2026-03-22 14:12:20','2026-03-21 10:05:47',NULL,'','2026-03-21 10:05:47','admin','2026-03-21 10:06:31',NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1),(2,2),(101,5);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2,2),(100,100),(101,2),(102,2),(103,2),(104,2),(105,2),(106,2),(107,2),(108,2),(109,2),(110,2),(116,2),(117,101),(118,2),(119,2),(120,2),(121,2),(122,2),(123,2),(124,2),(125,2),(126,2),(127,2),(128,2),(129,2),(130,2),(131,2),(132,2),(133,2),(134,2),(135,2),(136,2),(137,2),(138,103),(140,2),(141,2),(142,2),(143,2),(144,2),(145,2),(146,2),(147,2),(148,2),(149,2),(150,2),(151,104);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `venue`
--

DROP TABLE IF EXISTS `venue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venue` (
  `venue_id` int NOT NULL AUTO_INCREMENT COMMENT '场馆ID',
  `venue_name` varchar(100) NOT NULL COMMENT '场馆名称',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`venue_id`),
  UNIQUE KEY `uk_venue_name` (`venue_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='场馆表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `venue`
--

LOCK TABLES `venue` WRITE;
/*!40000 ALTER TABLE `venue` DISABLE KEYS */;
INSERT INTO `venue` VALUES (1,'思齐馆','2026-03-18 15:49:37','2026-03-18 15:49:37'),(2,'弘毅馆','2026-03-18 15:49:37','2026-03-18 15:49:37'),(3,'心缘馆','2026-03-18 15:49:37','2026-03-18 15:49:37'),(4,'笃学馆','2026-03-20 11:15:21','2026-03-20 11:15:24'),(5,'知行馆','2026-03-20 11:15:55','2026-03-20 11:15:59'),(6,'国防教育体验馆','2026-03-20 11:16:12','2026-03-20 11:16:13');
/*!40000 ALTER TABLE `venue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'zcst'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-28 22:49:00
