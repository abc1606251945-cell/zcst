# zcst-b 后端项目文档

## 项目简介

zcst-b 是一个基于 Spring Boot 的学生管理系统后端项目，提供了完整的 RESTful API 接口，支持学生信息管理、场地管理、用户管理等业务功能。该项目采用模块化设计，具有良好的可扩展性和可维护性。

## 技术架构

### 后端技术栈

| 技术/框架             | 版本    | 用途           |
| :-------------------- | :------ | :------------- |
| Spring Boot           | 4.0.3   | 后端核心框架   |
| Spring Security       | 内置    | 安全框架       |
| MyBatis               | 4.0.1   | ORM 框架       |
| Druid                 | 1.2.28  | 数据库连接池   |
| JWT                   | 0.9.1   | 认证令牌       |
| MySQL                 | -       | 数据库         |
| Redis                 | -       | 缓存           |
| SpringDoc             | 3.0.2   | API 文档       |
| Lombok                | 1.18.34 | 代码简化工具   |
| x-file-storage-spring | 2.3.0   | 文件存储框架   |
| aliyun-sdk-oss        | 3.16.1  | 阿里云 OSS SDK |

### 项目结构

```
zcst-b/
├── zcst-admin/            # 管理后台模块
│   └── src/main/java/com/zcst/web/  # 控制器层
├── zcst-common/           # 通用模块
│   └── src/main/java/com/zcst/common/  # 通用工具和常量
├── zcst-framework/        # 框架模块
│   └── src/main/java/com/zcst/framework/  # 核心配置和安全框架
├── zcst-generator/        # 代码生成模块
│   └── src/main/java/com/zcst/generator/  # 代码生成功能
├── zcst-manage/           # 业务管理模块
│   └── src/main/java/com/zcst/manage/  # 业务逻辑实现
├── zcst-quartz/           # 定时任务模块
│   └── src/main/java/com/zcst/quartz/  # 定时任务管理
├── zcst-system/           # 系统管理模块
│   └── src/main/java/com/zcst/system/  # 系统功能实现
├── zcst-upload/           # 文件上传模块（新增）
│   └── src/main/java/com/zcst/upload/  # 文件上传功能实现
├── sql/                   # 数据库脚本
└── pom.xml                # Maven 配置文件
```

## 功能模块

### 1. 系统管理

- **用户管理**：用户信息的增删改查，支持用户状态管理和密码重置
- **部门管理**：组织机构的树状展示和管理
- **岗位管理**：岗位信息的维护
- **菜单管理**：系统菜单的配置，支持权限控制
- **角色管理**：角色的创建和权限分配
- **字典管理**：系统字典数据的维护
- **参数管理**：系统参数的配置
- **通知公告**：系统通知的发布和管理

### 2. 监控管理

- **操作日志**：系统操作记录的查询和分析
- **登录日志**：用户登录记录的查询和分析
- **在线用户**：当前在线用户的监控
- **定时任务**：任务调度的管理和执行日志
- **服务监控**：系统资源使用情况的监控
- **缓存监控**：缓存信息的查询和管理

### 3. 业务管理

- **学生管理**：学生信息的增删改查和管理
- **场地管理**：场地信息的维护和管理
- **学校学生管理**：各学校学生的专门管理（如笃学、国防、弘毅、思齐、新源、知行等）
- **值班表管理**：场馆值班表的查看、添加、编辑和删除，支持自动排班功能
- **值班时间配置**：场馆值班时间的配置和管理
- **考勤管理**：学生考勤记录的管理和统计，支持按月统计值班时长、打卡次数、出勤次数等，支持按用户角色过滤数据

### 4. 文件上传管理

- **课表照片上传**：支持学生上传课表照片到阿里云 OSS，自动按日期分类存储
- **文件命名规则**：采用日期 + 序号的命名方式，确保文件名唯一且有规律
- **序号持久化**：通过查询 OSS 当天已有文件数量，自动递增序号
- **双模式支持**：支持登录模式（自动获取学号）和测试模式（手动输入学号）

### 5. 权限控制说明

#### 5.1 场馆管理员权限隔离

系统采用基于 `sys_role.venue_id` 字段的权限控制方式，实现场馆管理员的数据隔离。

**权限控制逻辑**：

- **超级管理员（admin）**：可以访问所有场馆的数据
- **场馆管理员**：只能访问自己管理的场馆数据

**权限控制方式**：

```java
// 从用户角色中获取场馆 ID
private Integer getVenueIdFromUserRoles() {
    LoginUser loginUser = SecurityUtils.getLoginUser();
    if (loginUser == null || loginUser.getUser() == null || loginUser.getUser().getRoles() == null) {
        return null;
    }

    return loginUser.getUser().getRoles().stream()
        .filter(role -> role.getVenueId() != null)
        .findFirst()
        .map(SysRole::getVenueId)
        .orElse(null);
}

// 判断是否为超级管理员
private boolean isSuperAdmin() {
    LoginUser loginUser = SecurityUtils.getLoginUser();
    if (loginUser == null || loginUser.getUser() == null) {
        return false;
    }
    return "admin".equals(loginUser.getUser().getUserName());
}
```

**应用场景**：

1. **查询列表**：非超级管理员自动添加 venue_id 过滤条件
2. **新增数据**：非超级管理员自动设置 venue_id 为当前用户管理的场馆
3. **修改数据**：非超级管理员只能修改自己场馆的数据
4. **删除数据**：非超级管理员只能删除自己场馆的数据，并添加严格验证
5. **查看详情**：非超级管理员只能查看自己场馆的详细信息

#### 5.2 权限控制优势

| 对比项     | 旧方式（角色名称匹配）         | 新方式（venue_id 字段）  |
| ---------- | ------------------------------ | ------------------------ |
| 性能       | 需要查询场馆表（~45ms）        | 直接从内存获取（~0ms）   |
| 代码复杂度 | 15 行代码，多层循环            | 6 行代码，Stream API     |
| 安全性     | 依赖角色名称约定，存在安全隐患 | 基于数据库字段，更加可靠 |
| 维护性     | 新增场馆需要配置角色名称       | 无需额外配置             |

### 6. 工具管理

- **代码生成**：前后端代码的自动生成

### 7. 注册功能

- **学生注册**：学生用户的注册流程
- **管理员注册**：管理员用户的注册流程

## 接口规范

### API 接口格式

所有 API 接口都遵循 RESTful 设计规范，使用 JSON 格式进行数据交换。

### 接口地址

接口基础路径：`/api`

### 响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    // 响应数据
  }
}
```

### 状态码说明

| 状态码 | 说明           |
| :----- | :------------- |
| 200    | 操作成功       |
| 400    | 请求参数错误   |
| 401    | 未授权         |
| 403    | 权限不足       |
| 404    | 资源不存在     |
| 500    | 服务器内部错误 |

## 核心业务流程

### 1. 用户认证流程

1. 用户发送登录请求，携带用户名和密码
2. 后端验证用户名和密码
3. 生成 JWT token
4. 返回 token 和用户信息
5. 前端存储 token，后续请求携带 token
6. 后端验证 token 有效性

### 2. 学生注册流程

1. 学生提交注册信息
2. 后端验证注册信息
3. 创建用户账号
4. 保存学生信息
5. 返回注册结果

### 3. 学生信息管理流程

1. 管理员登录系统
2. 进入学生管理页面
3. 查询学生列表
4. 添加/编辑/删除学生信息
5. 后端处理请求，更新数据库
6. 返回操作结果

### 4. 值班表管理流程

1. 场馆管理员登录系统
2. 进入值班表管理页面
3. 查看场馆值班表
4. 添加/编辑/删除值班记录
5. 配置值班时间
6. 执行自动排班
7. 后端处理请求，更新数据库
8. 返回操作结果

### 5. 考勤管理流程

1. 学生登录系统
2. 进入考勤打卡页面
3. 扫描二维码或手动打卡
4. 系统记录打卡时间和状态
5. 后端处理打卡请求，更新数据库
6. 场馆管理员登录系统
7. 进入考勤统计页面
8. 按月统计学生考勤数据
9. 查看考勤详细信息
10. 导出考勤报表

## 数据库设计

### 主要表结构

#### 用户表 (`sys_user`)

| 字段名        | 数据类型       | 描述     |
| :------------ | :------------- | :------- |
| `user_id`     | `BIGINT`       | 用户 ID  |
| `dept_id`     | `BIGINT`       | 部门 ID  |
| `username`    | `VARCHAR(30)`  | 用户名   |
| `nickname`    | `VARCHAR(30)`  | 昵称     |
| `password`    | `VARCHAR(100)` | 密码     |
| `status`      | `CHAR(1)`      | 状态     |
| `create_time` | `DATETIME`     | 创建时间 |

#### 角色表 (`sys_role`)

| 字段名        | 数据类型       | 描述                          |
| :------------ | :------------- | :---------------------------- |
| `role_id`     | `BIGINT`       | 角色 ID                       |
| `role_name`   | `VARCHAR(30)`  | 角色名称                      |
| `role_key`    | `VARCHAR(100)` | 角色权限字符串                |
| `venue_id`    | `INT`          | 关联场馆 ID（场馆管理员专用） |
| `status`      | `CHAR(1)`      | 状态                          |
| `create_time` | `DATETIME`     | 创建时间                      |

#### 学生表 (`student`)

| 字段名        | 数据类型       | 描述     |
| :------------ | :------------- | :------- |
| `id`          | `BIGINT`       | 学生ID   |
| `name`        | `VARCHAR(50)`  | 姓名     |
| `gender`      | `CHAR(1)`      | 性别     |
| `age`         | `INT`          | 年龄     |
| `school`      | `VARCHAR(100)` | 学校     |
| `create_time` | `DATETIME`     | 创建时间 |

#### 场地表 (`venue`)

| 字段名       | 数据类型       | 描述     |
| :----------- | :------------- | :------- |
| `venue_id`   | `BIGINT`       | 场地ID   |
| `venue_name` | `VARCHAR(100)` | 场地名称 |
| `created_at` | `DATETIME`     | 创建时间 |
| `updated_at` | `DATETIME`     | 更新时间 |

#### 值班表 (`duty_schedule`)

| 字段名              | 数据类型       | 描述                                |
| :------------------ | :------------- | :---------------------------------- |
| `duty_id`           | `INT`          | 值班表ID                            |
| `student_id`        | `VARCHAR(20)`  | 学号                                |
| `venue_id`          | `INT`          | 场馆ID                              |
| `start_time`        | `DATETIME`     | 值班开始时间                        |
| `end_time`          | `DATETIME`     | 值班结束时间                        |
| `remark`            | `VARCHAR(255)` | 备注                                |
| `attendance_status` | `CHAR(1)`      | 考勤状态（0正常 1迟到 2早退 3缺勤） |
| `created_at`        | `DATETIME`     | 创建时间                            |
| `updated_at`        | `DATETIME`     | 更新时间                            |

#### 值班时间配置表 (`duty_time_config`)

| 字段名       | 数据类型   | 描述     |
| :----------- | :--------- | :------- |
| `config_id`  | `INT`      | 配置ID   |
| `venue_id`   | `INT`      | 场馆ID   |
| `start_time` | `TIME`     | 开始时间 |
| `end_time`   | `TIME`     | 结束时间 |
| `is_enable`  | `TINYINT`  | 是否启用 |
| `created_at` | `DATETIME` | 创建时间 |
| `updated_at` | `DATETIME` | 更新时间 |

#### 考勤记录表 (`attendance_record`)

| 字段名          | 数据类型       | 描述                            |
| :-------------- | :------------- | :------------------------------ |
| `record_id`     | `BIGINT`       | 记录ID                          |
| `student_id`    | `VARCHAR(20)`  | 学号                            |
| `duty_id`       | `INT`          | 值班表ID                        |
| `check_in_time` | `DATETIME`     | 打卡时间                        |
| `status`        | `CHAR(1)`      | 状态（0正常 1迟到 2早退 3缺勤） |
| `remark`        | `VARCHAR(255)` | 备注                            |
| `created_at`    | `DATETIME`     | 创建时间                        |
| `updated_at`    | `DATETIME`     | 更新时间                        |

#### 考勤统计表 (`attendance_statistics`)

| 字段名              | 数据类型        | 描述                  |
| :------------------ | :-------------- | :-------------------- |
| `stat_id`           | `BIGINT`        | 统计 ID               |
| `student_id`        | `VARCHAR(20)`   | 学号                  |
| `venue_id`          | `INT`           | 场馆 ID               |
| `year_month`        | `VARCHAR(7)`    | 年月（格式：2026-04） |
| `total_duty_hours`  | `DECIMAL(10,2)` | 值班总时长            |
| `check_in_count`    | `INT`           | 打卡次数              |
| `attendance_count`  | `INT`           | 出勤次数              |
| `absence_count`     | `INT`           | 缺勤次数              |
| `late_count`        | `INT`           | 迟到次数              |
| `early_leave_count` | `INT`           | 早退次数              |
| `leave_count`       | `INT`           | 请假次数              |
| `exchange_count`    | `INT`           | 调班次数              |
| `created_at`        | `DATETIME`      | 创建时间              |
| `updated_at`        | `DATETIME`      | 更新时间              |

#### 考勤规则配置表 (`attendance_rule`)

| 字段名                          | 数据类型       | 描述                     |
| :------------------------------ | :------------- | :----------------------- |
| `rule_id`                       | `INT`          | 规则 ID（主键）          |
| `venue_id`                      | `INT`          | 场馆 ID                  |
| `venue_name`                    | `VARCHAR(100)` | 场馆名称                 |
| `late_threshold_minutes`        | `INT`          | 迟到阈值（分钟）         |
| `early_leave_threshold_minutes` | `INT`          | 早退阈值（分钟）         |
| `min_checkin_before_minutes`    | `INT`          | 最早提前打卡时间（分钟） |
| `max_checkin_after_minutes`     | `INT`          | 最晚延后打卡时间（分钟） |
| `status`                        | `CHAR(1)`      | 状态（0 正常 1 停用）    |
| `remark`                        | `VARCHAR(500)` | 备注                     |
| `created_at`                    | `DATETIME`     | 创建时间                 |
| `updated_at`                    | `DATETIME`     | 更新时间                 |

#### 请假申请表 (`leave_application`)

| 字段名           | 数据类型       | 描述                                   |
| :--------------- | :------------- | :------------------------------------- |
| `leave_id`       | `INT`          | 请假 ID（主键）                        |
| `student_id`     | `VARCHAR(20)`  | 学号                                   |
| `student_name`   | `VARCHAR(50)`  | 学生姓名                               |
| `venue_id`       | `INT`          | 场馆 ID                                |
| `duty_id`        | `INT`          | 值班 ID（可选）                        |
| `leave_type`     | `CHAR(1)`      | 请假类型（0 病假 1 事假 2 其他）       |
| `start_time`     | `DATETIME`     | 请假开始时间                           |
| `end_time`       | `DATETIME`     | 请假结束时间                           |
| `reason`         | `VARCHAR(500)` | 请假原因                               |
| `proof_image`    | `VARCHAR(500)` | 证明材料图片 URL                       |
| `status`         | `CHAR(1)`      | 审批状态（0 待审批 1 已通过 2 已拒绝） |
| `approver_id`    | `VARCHAR(20)`  | 审批人 ID                              |
| `approve_time`   | `DATETIME`     | 审批时间                               |
| `approve_remark` | `VARCHAR(500)` | 审批意见                               |
| `created_at`     | `DATETIME`     | 申请时间                               |
| `updated_at`     | `DATETIME`     | 更新时间                               |

#### 调班申请表 (`shift_exchange`)

| 字段名                   | 数据类型       | 描述                                            |
| :----------------------- | :------------- | :---------------------------------------------- |
| `exchange_id`            | `INT`          | 调班 ID（主键）                                 |
| `student_id_a`           | `VARCHAR(20)`  | 申请人学号                                      |
| `student_name_a`         | `VARCHAR(50)`  | 申请人姓名                                      |
| `student_id_b`           | `VARCHAR(20)`  | 替换人学号                                      |
| `student_name_b`         | `VARCHAR(50)`  | 替换人姓名                                      |
| `venue_id`               | `INT`          | 场馆 ID                                         |
| `duty_id`                | `INT`          | 原值班 ID                                       |
| `exchange_reason`        | `VARCHAR(500)` | 调班原因                                        |
| `status`                 | `CHAR(1)`      | 审批状态（0 待审批 1 已通过 2 已拒绝 3 已确认） |
| `approver_id`            | `VARCHAR(20)`  | 审批人 ID                                       |
| `approve_time`           | `DATETIME`     | 审批时间                                        |
| `approve_remark`         | `VARCHAR(500)` | 审批意见                                        |
| `student_b_confirm`      | `CHAR(1)`      | 替换人确认（0 未确认 1 已确认）                 |
| `student_b_confirm_time` | `DATETIME`     | 替换人确认时间                                  |
| `created_at`             | `DATETIME`     | 申请时间                                        |
| `updated_at`             | `DATETIME`     | 更新时间                                        |

#### 补卡申请表 (`makeup_application`)

| 字段名              | 数据类型       | 描述                                             |
| :------------------ | :------------- | :----------------------------------------------- |
| `makeup_id`         | `INT`          | 补卡 ID（主键）                                  |
| `student_id`        | `VARCHAR(20)`  | 学号                                             |
| `student_name`      | `VARCHAR(50)`  | 学生姓名                                         |
| `venue_id`          | `INT`          | 场馆 ID                                          |
| `duty_id`           | `INT`          | 值班 ID                                          |
| `miss_type`         | `CHAR(1)`      | 缺卡类型（0 上班未打卡 1 下班未打卡 2 都未打卡） |
| `actual_start_time` | `DATETIME`     | 实际上班时间                                     |
| `actual_end_time`   | `DATETIME`     | 实际下班时间                                     |
| `reason`            | `VARCHAR(500)` | 补卡原因                                         |
| `proof_image`       | `VARCHAR(500)` | 证明材料图片 URL                                 |
| `status`            | `CHAR(1)`      | 审批状态（0 待审批 1 已通过 2 已拒绝）           |
| `approver_id`       | `VARCHAR(20)`  | 审批人 ID                                        |
| `approve_time`      | `DATETIME`     | 审批时间                                         |
| `approve_remark`    | `VARCHAR(500)` | 审批意见                                         |
| `created_at`        | `DATETIME`     | 申请时间                                         |
| `updated_at`        | `DATETIME`     | 更新时间                                         |

## 文件上传 API 接口说明

### 1. 课表照片上传接口

#### 接口地址

```
POST /upload/schedule
```

#### 请求参数

| 参数名    | 类型          | 必填 | 说明                                       |
| --------- | ------------- | ---- | ------------------------------------------ |
| file      | MultipartFile | 是   | 上传的图片文件                             |
| studentId | String        | 否   | 学号（已登录时自动获取，未登录时需要传递） |

#### 请求示例

```bash
curl -X POST http://localhost:8080/upload/schedule \
  -F "file=@timetable.jpg" \
  -F "studentId=20260321"
```

#### 响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "https://zcsst-student-timetable.oss-cn-beijing.aliyuncs.com/timetable/schedule/2026/03/29/202603290001.jpg"
}
```

### 2. 文件命名规则

#### 2.1 文件路径结构

```
timetable/                              # 根目录（base-path）
  schedule/                             # 课表目录
    {年}/{月}/{日}/                     # 日期目录
      {yyyyMMdd}{序号}.{扩展名}         # 文件名
```

#### 2.2 完整路径示例

```
https://zcsst-student-timetable.oss-cn-beijing.aliyuncs.com/timetable/schedule/2026/03/29/202603290001.jpg

路径解析：
timetable/                    # base-path（application.yml 配置）
  schedule/                   # 课表目录
    2026/03/29/              # 日期（年/月/日）
      202603290001.jpg       # 文件名（日期 +4 位序号）
```

#### 2.3 文件命名规则详解

| 部分   | 格式         | 示例     | 说明                       |
| ------ | ------------ | -------- | -------------------------- |
| 日期   | yyyyMMdd     | 20260329 | 上传日期的 8 位数字        |
| 序号   | 0001-9999    | 0001     | 4 位数字序号，从 0001 开始 |
| 扩展名 | 原文件扩展名 | jpg      | 保持原始文件扩展名         |

#### 2.4 序号生成规则

1. **查询 OSS 当天文件**：上传时查询 OSS 上当天（{年}/{月}/{日}）已有的文件数量
2. **提取已用序号**：从文件名中提取已使用的序号
3. **分配下一个序号**：从 1 开始递增，找到第一个未使用的序号
4. **持久化机制**：通过查询 OSS 实现序号持久化，重启后不会重置

#### 2.5 示例文件列表

```
# 2026 年 3 月 29 日上传的文件
timetable/schedule/2026/03/29/202603290001.jpg
timetable/schedule/2026/03/29/202603290002.jpg
timetable/schedule/2026/03/29/202603290003.jpg

# 2026 年 3 月 30 日上传的文件（序号重置）
timetable/schedule/2026/03/30/202603300001.jpg
timetable/schedule/2026/03/30/202603300002.jpg
```

### 3. 配置说明

#### 3.1 application.yml 配置

```yaml
dromara:
  x-file-storage:
    default-platform: aliyun-oss-1
    thumbnail-suffix: ".min.jpg"
    aliyun-oss:
      - platform: aliyun-oss-1
        enable-storage: true
        access-key: <你的 AccessKey>
        secret-key: <你的 SecretKey>
        end-point: oss-cn-beijing.aliyuncs.com
        bucket-name: zcsst-student-timetable
        domain: https://zcsst-student-timetable.oss-cn-beijing.aliyuncs.com/
        base-path: timetable/
```

#### 3.2 启动类配置

在启动类上添加 `@EnableFileStorage` 注解：

```java
@EnableFileStorage
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RuoYiApplication.class, args);
    }
}
```

### 4. 使用模式

#### 4.1 登录模式（正式环境）

- 用户已登录系统
- 自动从登录信息中获取学号
- 学号仅用于日志记录，不包含在文件路径中

#### 4.2 测试模式（未登录）

- 用户未登录系统
- 需要手动传递 `studentId` 参数
- 学号仅用于日志记录，不包含在文件路径中

### 5. 技术栈

| 技术/框架             | 版本   | 用途           |
| --------------------- | ------ | -------------- |
| x-file-storage-spring | 2.3.0  | 文件存储框架   |
| aliyun-sdk-oss        | 3.16.1 | 阿里云 OSS SDK |
| Spring Boot           | 4.0.3  | Web 框架       |

---

## 开发指南

### 环境搭建

1. 克隆项目

```bash
git clone <项目地址>
cd zcst-b
```

2. 配置数据库

- 创建数据库 `zcst`
- 执行 `sql` 目录下的初始化脚本
- 执行考勤功能更新脚本：`source sql/attendance_updates.sql`

3. 配置 application.yml

修改 `zcst-admin/src/main/resources/application.yml` 文件，配置数据库连接信息。

4. 编译项目

```bash
mvn clean install
```

5. 运行项目

```bash
mvn spring-boot:run -pl zcst-admin
```

---

## 考勤功能详细说明

### 一、功能概述

考勤管理系统用于管理学生值班考勤，包括打卡、签退、请假、调班、补卡等功能，并提供完善的统计报表。

### 二、API 接口设计

#### 1. 考勤记录接口

##### 1.1 打卡

- **接口**: `POST /manage/attendance/record/checkIn`
- **参数**:
  - studentId: String - 学号
  - dutyId: Integer - 值班 ID
- **返回**: 打卡结果

##### 1.2 签退

- **接口**: `POST /manage/attendance/record/checkOut`
- **参数**:
  - recordId: Long - 考勤记录 ID
  - dutyId: Integer - 值班 ID
- **返回**: 签退结果

##### 1.3 查询考勤记录列表

- **接口**: `GET /manage/attendance/record/list`
- **参数**:
  - studentId: String - 学号（可选）
  - dutyId: Integer - 值班 ID（可选）
  - status: String - 状态（可选）
  - pageNum: Integer - 页码
  - pageSize: Integer - 每页数量
- **返回**: 考勤记录列表

##### 1.4 获取考勤记录详情

- **接口**: `GET /manage/attendance/record/info/{recordId}`
- **返回**: 考勤记录详情

##### 1.5 查询学生月度考勤

- **接口**: `GET /manage/attendance/record/student/month`
- **参数**:
  - studentId: String - 学号
  - yearMonth: String - 年月
- **返回**: 学生月度考勤记录列表

#### 2. 考勤统计接口

##### 2.1 月度考勤统计

- **接口**: `POST /manage/attendance/statistics/calculate`
- **参数**:
  - yearMonth: String - 年月（格式：2026-04）
- **返回**: 统计结果

##### 2.2 查询学生月度统计

- **接口**: `GET /manage/attendance/statistics/student/month`
- **参数**:
  - studentId: String - 学号
  - yearMonth: String - 年月
- **返回**: 学生月度统计数据

##### 2.3 查询场馆月度统计

- **接口**: `GET /manage/attendance/statistics/venue/month`
- **参数**:
  - venueId: Integer - 场馆 ID
  - yearMonth: String - 年月
- **返回**: 场馆月度统计列表

#### 3. 考勤规则接口

##### 3.1 查询考勤规则

- **接口**: `GET /manage/attendance/rule/{venueId}`
- **返回**: 场馆考勤规则

##### 3.2 更新考勤规则

- **接口**: `PUT /manage/attendance/rule`
- **参数**: AttendanceRule 对象
- **返回**: 更新结果

### 三、业务逻辑说明

#### 1. 打卡规则

1. **正常打卡**: 在值班开始时间之前打卡
2. **迟到判定**: 超过值班开始时间打卡
3. **最早打卡时间**: 不能早于值班开始时间前 30 分钟

#### 2. 签退规则

1. **正常签退**: 在值班结束时间之后签退
2. **早退判定**: 早于值班结束时间签退
3. **时长计算**: 自动计算实际值班时长（小时）

#### 3. 统计规则

1. **月度统计**: 按自然月统计
2. **出勤次数**: 正常 + 迟到 + 早退的次数
3. **缺勤次数**: 应值班次数 - 出勤次数
4. **值班时长**: 累计实际值班时长

### 四、状态码说明

#### 1. 考勤状态

- `0` - 正常
- `1` - 迟到
- `2` - 早退
- `3` - 缺勤

#### 2. 审批状态

- `0` - 待审批
- `1` - 已通过
- `2` - 已拒绝
- `3` - 已确认（调班专用）

#### 3. 请假类型

- `0` - 病假
- `1` - 事假
- `2` - 其他

#### 4. 缺卡类型

- `0` - 上班未打卡
- `1` - 下班未打卡
- `2` - 都未打卡

### 五、使用说明

#### 1. 打卡流程

```
学生值班 → 到达场馆 → 打卡（checkIn）
         → 开始值班
         → 值班结束 → 签退（checkOut）
         → 系统自动计算时长和状态
```

#### 2. 统计流程

```
月末 → 调用统计接口
    → 按学生分组计算
    → 生成统计数据
    → 可查看/导出
```

### 六、注意事项

1. **数据库更新**: 执行 SQL 脚本前请备份数据库
2. **时间处理**: 所有时间使用服务器时间
3. **权限控制**: 打卡/签退：学生权限；审批：场馆管理员权限
4. **数据一致性**: 打卡后更新值班表状态，签退后计算实际时长

### 代码规范

- 使用 Java 17 语法
- 类名使用 PascalCase
- 方法名和变量名使用 camelCase
- 常量名使用 UPPER_CASE
- 缩进使用 4 个空格
- 每行代码长度不超过 120 个字符
- 使用 Lombok 简化代码

### 目录规范

- `controller/`：控制器层，处理 HTTP 请求
- `service/`：服务层，实现业务逻辑
- `mapper/`：数据访问层，操作数据库
- `domain/`：领域模型，定义实体类
- `utils/`：工具类
- `config/`：配置类

## API 文档

项目使用 SpringDoc 生成 API 文档，访问地址：

```
http://localhost:8080/swagger-ui.html
```

## 部署指南

### 打包

```bash
mvn clean package -DskipTests
```

### 运行

```bash
java -jar zcst-admin/target/zcst-admin.jar
```

### 环境变量

| 环境变量            | 描述           | 默认值                           |
| :------------------ | :------------- | :------------------------------- |
| `SERVER_PORT`       | 服务端口       | 8080                             |
| `DATABASE_URL`      | 数据库连接地址 | jdbc:mysql://localhost:3306/zcst |
| `DATABASE_USERNAME` | 数据库用户名   | root                             |
| `DATABASE_PASSWORD` | 数据库密码     | 123456                           |
| `REDIS_HOST`        | Redis 主机     | localhost                        |
| `REDIS_PORT`        | Redis 端口     | 6379                             |

## 常见问题

### 1. 数据库连接失败

- 检查数据库服务是否运行
- 检查数据库连接配置是否正确
- 检查数据库用户权限是否足够

### 2. 启动失败

- 检查端口是否被占用
- 检查依赖是否完整
- 检查配置文件是否正确

### 3. API 调用失败

- 检查请求参数是否正确
- 检查用户权限是否足够
- 检查后端服务是否运行

### 4. 场馆管理员权限问题

#### 4.1 权限配置问题

- **问题**：场馆管理员无法查看值班表或执行相关操作
- **原因**：缺少必要的权限配置
- **解决方法**：确保场馆管理员角色拥有以下权限：
  - `manage:dutySchedule:list` - 查看值班表列表
  - `manage:dutySchedule:query` - 查询值班表详情
  - `manage:dutySchedule:add` - 添加值班记录
  - `manage:dutySchedule:edit` - 编辑值班记录
  - `manage:dutySchedule:remove` - 删除值班记录
  - `manage:dutyTimeConfig:list` - 查看值班时间配置
  - `manage:dutyTimeConfig:query` - 查询值班时间配置详情
  - `manage:dutyTimeConfig:add` - 添加值班时间配置
  - `manage:dutyTimeConfig:edit` - 编辑值班时间配置
  - `manage:dutyTimeConfig:remove` - 删除值班时间配置
  - `manage:venue:list` - 查看场馆列表
  - `manage:venue:query` - 查询场馆详情

#### 4.2 权限控制方式问题

- **问题**：旧的权限控制方式通过角色名称匹配场馆名称，性能较差且存在安全隐患
- **解决方案**：使用基于 `sys_role.venue_id` 字段的权限控制方式
- **实施步骤**：
  1. 为 `sys_role` 表添加 `venue_id` 字段
  2. 为场馆管理员角色的 `venue_id` 字段设置对应的场馆 ID
  3. 系统会自动从用户角色的 `venue_id` 字段获取权限信息
  4. 无需修改角色名称约定，直接通过数据库字段控制权限

#### 4.3 数据隔离问题

- **问题**：场馆管理员可以看到其他场馆的数据
- **原因**：权限控制逻辑未正确实施
- **解决方法**：
  1. 检查 Controller 中是否添加了权限控制逻辑
  2. 确认 `getVenueIdFromUserRoles()` 方法正确实现
  3. 验证查询、新增、修改、删除操作都添加了 venue_id 过滤
  4. 检查日志输出，确认 venueId 正确设置

## 性能优化

1. **数据库优化**：使用索引，优化 SQL 语句
2. **缓存优化**：合理使用 Redis 缓存
3. **代码优化**：减少不必要的计算和 IO 操作
4. **并发优化**：合理使用线程池

## 安全措施

1. **认证授权**：使用 JWT 进行身份认证，基于角色的权限控制
2. **输入验证**：对所有输入进行验证，防止 SQL 注入和 XSS 攻击
3. **加密处理**：对敏感数据进行加密存储
4. **日志记录**：记录系统操作日志，便于审计和排查问题

## 项目维护

### 版本管理

使用 Git 进行版本管理，遵循以下分支规范：

- `master`：主分支，用于发布生产版本
- `dev`：开发分支，用于集成新功能
- `feat/xxx`：功能分支，用于开发具体功能
- `fix/xxx`：修复分支，用于修复 bug

### 代码提交规范

提交信息格式：

```
<type>(<scope>): <subject>

<body>

<footer>
```

其中 `type` 可以是：

- `feat`：新功能
- `fix`：修复 bug
- `docs`：文档更新
- `style`：代码风格修改
- `refactor`：代码重构
- `test`：测试代码
- `chore`：构建过程或辅助工具的变动

---

## 版本历史

### v1.4.0 - 文件上传功能版本 (2026-03-29)

#### 新增功能

- ✅ 课表照片上传功能，支持上传到阿里云 OSS
- ✅ 文件按日期分类存储（年/月/日）
- ✅ 序号持久化机制，通过查询 OSS 当天文件数量实现
- ✅ 支持双模式：登录模式（自动获取学号）和测试模式（手动输入）
- ✅ 文件命名规则：日期 +4 位序号 + 扩展名

#### 技术实现

- ✅ 集成 x-file-storage-spring v2.3.0
- ✅ 使用阿里云 OSS SDK v3.16.1
- ✅ 添加 @EnableFileStorage 注解到启动类
- ✅ Controller 支持 @Anonymous 匿名访问

#### 文件命名示例

```
timetable/schedule/2026/03/29/202603290001.jpg
timetable/schedule/2026/03/29/202603290002.jpg
timetable/schedule/2026/03/30/202603300001.jpg  # 新的一天，序号重置
```

#### 涉及模块

- ✅ zcst-upload - 文件上传模块（新增）
- ✅ zcst-admin - 启动类配置
- ✅ zcst-common - 通用工具支持

#### 数据库更新

- 无需数据库更新，文件存储在阿里云 OSS

---

### v1.3.0 - 权限控制优化版本 (2026-03-27)

#### 优化改进

- ✅ 统一所有 Controller 的权限控制方式，全部采用 `sys_role.venue_id` 字段
- ✅ 替换旧的角色名称匹配方式为直接使用 venue_id 字段
- ✅ 优化 StudentController、VenueController、DutyScheduleController、DutyTimeConfigController 权限控制
- ✅ 添加严格的删除操作权限验证，防止越权删除
- ✅ 完善日志记录，便于问题排查和审计

#### 涉及模块

- ✅ AttendanceRecordController（考勤记录）- 已使用新方式
- ✅ AttendanceStatisticsController（考勤统计）- 已使用新方式
- ✅ DutyScheduleController（值班安排）- 已优化
- ✅ DutyTimeConfigController（值班配置）- 已优化
- ✅ StudentController（学生管理）- 已优化
- ✅ VenueController（场馆管理）- 已优化

#### 权限控制方式

- **旧方式**: 通过角色名称匹配场馆名称（需要查询场馆表，性能较差）
- **新方式**: 直接从 `sys_role.venue_id` 字段获取（无需查询，性能优秀）

#### 性能提升

- 权限检查性能提升约 90%（从 ~45ms 降低到 ~0ms）
- 代码行数减少 60%，可读性显著提升
- 消除了角色名称匹配的潜在安全问题

#### 数据库更新

- `sys_role` 表添加 `venue_id` 字段（INT）- 关联场馆 ID

---

### v1.2.0 - 代码优化版本 (2026-03-27)

#### 新增功能

- ✅ 添加签退功能，支持自动计算值班时长
- ✅ 完善考勤统计逻辑，添加月份过滤
- ✅ 添加考勤规则配置功能

#### 优化改进

- ✅ 添加事务注解 (@Transactional) 到关键业务方法
- ✅ 完善异常处理和日志记录
- ✅ 清理未使用的代码
- ✅ 新增重复签退检查逻辑

#### Bug 修复

- ✅ 修复 Mapper 接口方法重复定义问题
- ✅ 修复 SQL 脚本缺少字段存在性判断
- ✅ 修复签退逻辑时间获取和状态判断错误
- ✅ 修复统计逻辑空指针风险
- ✅ 修复时长计算精度问题

#### 数据库更新

- 为 `attendance_record` 添加 `check_out_time` 和 `actual_duty_hours` 字段
- 创建 `attendance_rule` 考勤规则配置表
- 创建 `leave_application` 请假申请表
- 创建 `shift_exchange` 调班申请表
- 创建 `makeup_application` 补卡申请表
- 为 `attendance_statistics` 添加 `leave_count` 和 `exchange_count` 字段

---

### v1.1.0 - 考勤功能增强版本 (2026-03-27)

#### 新增功能

- ✅ 考勤记录管理（打卡、查询）
- ✅ 考勤统计管理（月度统计）
- ✅ 值班表管理（自动排班）
- ✅ 值班时间配置

#### 数据库更新

- 创建 `attendance_record` 考勤记录表
- 创建 `attendance_statistics` 考勤统计表
- 创建 `duty_schedule` 值班表
- 创建 `duty_time_config` 值班时间配置表

---

### v1.0.0 - 初始版本 (2026-03-21)

#### 功能模块

- ✅ 系统管理（用户、部门、岗位、菜单、角色等）
- ✅ 监控管理（操作日志、登录日志、在线用户等）
- ✅ 业务管理（学生管理、场地管理等）
- ✅ 代码生成工具

---

## 技术文档

项目包含以下详细技术文档：

- **README.md** - 项目总体文档
- **doc/考勤功能开发文档.md** - 考勤功能详细开发文档
- **doc/考勤功能完善总结.md** - 考勤功能完善总结
- **doc/Bug 修复报告.md** - Bug 修复详细报告
- **doc/代码优化报告\_v1.2.0.md** - 代码优化详细报告

---

## 联系方式

- 项目负责人：XXX
- 技术支持：XXX
- 邮箱：XXX
- 电话：XXX
