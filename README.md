# zcst-b 后端项目文档

## 项目简介

zcst-b 是一个基于 Spring Boot 的学生值班考勤管理系统后端项目，提供了完整的 RESTful API 接口，支持学生信息管理、场地管理、值班排班、考勤管理等功能。该项目采用模块化设计，具有良好的可扩展性和可维护性。

## 技术架构

### 后端技术栈

| 技术/框架             | 版本    | 用途           |
| :-------------------- | :------ | :------------- |
| Spring Boot           | 4.0.3   | 后端核心框架   |
| Spring Security       | 内置    | 安全框架       |
| MyBatis               | 4.0.1   | ORM 框架       |
| Druid                 | 1.2.28  | 数据库连接池   |
| JWT                   | 0.9.1   | 认证令牌       |
| MySQL                 | 8.0+    | 数据库         |
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
├── zcst-upload/           # 文件上传模块
│   └── src/main/java/com/zcst/upload/  # 文件上传功能实现
├── sql/                   # 数据库脚本
│   ├── zcst.sql          # 主数据库脚本
│   └── test_data_*.sql   # 测试数据脚本
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
- **学校学生管理**：各学校学生的专门管理（笃学、国防、弘毅、思齐、新源、知行等）
- **值班表管理**：场馆值班表的查看、添加、编辑和删除，支持自动排班功能
  - 自动排班算法：基于学生值班时长智能分配
  - 时段排班：按配置的时间段自动排班
  - 自选日期排班：支持指定日期范围排班
  - 冲突检测：自动检测学生课表和值班冲突
- **值班时间配置**：场馆值班时间的配置和管理
- **考勤管理**：学生考勤记录的管理和统计
  - 打卡/签退功能
  - 月度统计：值班时长、打卡次数、出勤次数等
  - 按用户角色过滤数据
  - 考勤报表导出

### 4. 注册功能

- **学生注册**：学生用户注册，需填写学号、姓名、场馆等信息
- **管理员注册**：管理员用户注册
  - 昵称自动同步为姓名（无需手动输入）
  - 岗位无需注册时填写（后续配置）

### 5. 文件上传管理

- **课表照片上传**：支持学生上传课表照片到阿里云 OSS，自动按日期分类存储
- **文件命名规则**：采用日期 + 序号的命名方式，确保文件名唯一且有规律
- **序号持久化**：通过查询 OSS 当天已有文件数量，自动递增序号
- **双模式支持**：支持登录模式（自动获取学号）和测试模式（手动输入学号）

### 6. 权限控制说明

#### 6.1 场馆管理员权限隔离

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

#### 6.2 权限控制优势

| 对比项     | 旧方式（角色名称匹配）         | 新方式（venue_id 字段）  |
| ---------- | ------------------------------ | ------------------------ |
| 性能       | 需要查询场馆表（~45ms）        | 直接从内存获取（~0ms）   |
| 代码复杂度 | 15 行代码，多层循环            | 6 行代码，Stream API     |
| 安全性     | 依赖角色名称约定，存在安全隐患 | 基于数据库字段，更加可靠 |
| 维护性     | 新增场馆需要配置角色名称       | 无需额外配置             |

### 7. 工具管理

- **代码生成**：前后端代码的自动生成

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

1. 学生提交注册信息（学号、姓名、场馆、密码等）
2. 后端验证注册信息
3. 创建用户账号
4. 保存学生信息
5. 返回注册结果

### 3. 管理员注册流程

1. 管理员提交注册信息（用户名、姓名、场馆、密码等）
2. 后端验证注册信息
3. 自动将姓名同步为昵称
4. 创建用户账号并分配管理员角色
5. 返回注册结果

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
| `id`          | `BIGINT`       | 学生 ID  |
| `student_id`  | `VARCHAR(20)`  | 学号     |
| `name`        | `VARCHAR(50)`  | 姓名     |
| `gender`      | `CHAR(1)`      | 性别     |
| `venue_id`    | `INT`          | 场馆 ID  |
| `phone`       | `VARCHAR(20)`  | 手机号   |
| `major_id`    | `BIGINT`       | 专业 ID  |
| `grade`       | `VARCHAR(20)`  | 年级     |
| `create_time` | `DATETIME`     | 创建时间 |

#### 场地表 (`venue`)

| 字段名       | 数据类型       | 描述     |
| :----------- | :------------- | :------- |
| `venue_id`   | `BIGINT`       | 场地 ID  |
| `venue_name` | `VARCHAR(100)` | 场地名称 |
| `created_at` | `DATETIME`     | 创建时间 |
| `updated_at` | `DATETIME`     | 更新时间 |

#### 值班表 (`duty_schedule`)

| 字段名              | 数据类型       | 描述                                |
| :------------------ | :------------- | :---------------------------------- |
| `duty_id`           | `INT`          | 值班表 ID                           |
| `student_id`        | `VARCHAR(20)`  | 学号                                |
| `venue_id`          | `INT`          | 场馆 ID                             |
| `start_time`        | `DATETIME`     | 值班开始时间                        |
| `end_time`          | `DATETIME`     | 值班结束时间                        |
| `remark`            | `VARCHAR(255)` | 备注                                |
| `attendance_status` | `CHAR(1)`      | 考勤状态（0 正常 1 迟到 2 早退 3 缺勤） |
| `created_at`        | `DATETIME`     | 创建时间                            |
| `updated_at`        | `DATETIME`     | 更新时间                            |

#### 值班时间配置表 (`duty_time_config`)

| 字段名       | 数据类型   | 描述     |
| :----------- | :--------- | :------- |
| `config_id`  | `INT`      | 配置 ID  |
| `venue_id`   | `INT`      | 场馆 ID  |
| `start_time` | `TIME`     | 开始时间 |
| `end_time`   | `TIME`     | 结束时间 |
| `is_enable`  | `TINYINT`  | 是否启用 |
| `created_at` | `DATETIME` | 创建时间 |
| `updated_at` | `DATETIME` | 更新时间 |

#### 考勤记录表 (`attendance_record`)

| 字段名          | 数据类型       | 描述                            |
| :-------------- | :------------- | :------------------------------ |
| `record_id`     | `BIGINT`       | 记录 ID                         |
| `student_id`    | `VARCHAR(20)`  | 学号                            |
| `duty_id`       | `INT`          | 值班表 ID                       |
| `check_in_time` | `DATETIME`     | 打卡时间                        |
| `status`        | `CHAR(1)`      | 状态（0 正常 1 迟到 2 早退 3 缺勤） |
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
        SpringApplication.run(RuoYiApplication.java);
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
- 执行 `sql/zcst.sql` 初始化脚本

3. 配置 application.yml

修改 `zcst-admin/src/main/resources/application.yml` 文件，配置数据库连接信息和 OSS 配置。

4. 编译项目

```bash
mvn clean install
```

5. 运行项目

```bash
mvn spring-boot:run -pl zcst-admin
```

---

## 更新日志

### 2026-04-02

- ✅ 修复排班算法中的时间单位转换错误（统一使用秒为单位）
- ✅ 优化学生课表冲突检测逻辑
- ✅ 优化 Calendar 时间设置边界处理
- ✅ 优化管理员注册流程（昵称自动同步为姓名，岗位无需填写）
- ✅ 改进跨天排班逻辑
- ✅ 添加并发控制注释说明

### 2026-03-xx

- ✅ 添加考勤管理功能
- ✅ 添加课表照片上传功能
- ✅ 优化权限控制逻辑

---

## 常见问题

### 1. 如何配置阿里云 OSS？

在 `application.yml` 中配置阿里云 OSS 相关参数，包括 access-key、secret-key、bucket-name 等。

### 2. 排班算法如何工作？

系统根据学生已有的值班时长，优先安排值班时长较短的学生，确保公平分配。同时自动检测课表冲突和值班冲突。

### 3. 如何实现权限隔离？

通过 `sys_role` 表的 `venue_id` 字段实现场馆管理员的数据隔离，超级管理员可以访问所有数据，场馆管理员只能访问自己管理的场馆数据。

---

## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

## 许可证

本项目采用 MIT 许可证。
