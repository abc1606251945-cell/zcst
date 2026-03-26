# zcst-b 后端项目文档

## 项目简介

zcst-b 是一个基于 Spring Boot 的学生管理系统后端项目，提供了完整的 RESTful API 接口，支持学生信息管理、场地管理、用户管理等业务功能。该项目采用模块化设计，具有良好的可扩展性和可维护性。

## 技术架构

### 后端技术栈

| 技术/框架 | 版本 | 用途 |
| :--- | :--- | :--- |
| Spring Boot | 4.0.3 | 后端核心框架 |
| Spring Security | 内置 | 安全框架 |
| MyBatis | 4.0.1 | ORM 框架 |
| Druid | 1.2.28 | 数据库连接池 |
| JWT | 0.9.1 | 认证令牌 |
| MySQL | - | 数据库 |
| Redis | - | 缓存 |
| SpringDoc | 3.0.2 | API 文档 |
| Lombok | 1.18.34 | 代码简化工具 |

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

### 4. 工具管理

- **代码生成**：前后端代码的自动生成

### 5. 注册功能

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

| 状态码 | 说明 |
| :--- | :--- |
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

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

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| `user_id` | `BIGINT` | 用户ID |
| `dept_id` | `BIGINT` | 部门ID |
| `username` | `VARCHAR(30)` | 用户名 |
| `nickname` | `VARCHAR(30)` | 昵称 |
| `password` | `VARCHAR(100)` | 密码 |
| `status` | `CHAR(1)` | 状态 |
| `create_time` | `DATETIME` | 创建时间 |

#### 学生表 (`student`)

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| `id` | `BIGINT` | 学生ID |
| `name` | `VARCHAR(50)` | 姓名 |
| `gender` | `CHAR(1)` | 性别 |
| `age` | `INT` | 年龄 |
| `school` | `VARCHAR(100)` | 学校 |
| `create_time` | `DATETIME` | 创建时间 |

#### 场地表 (`venue`)

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| `venue_id` | `BIGINT` | 场地ID |
| `venue_name` | `VARCHAR(100)` | 场地名称 |
| `created_at` | `DATETIME` | 创建时间 |
| `updated_at` | `DATETIME` | 更新时间 |

#### 值班表 (`duty_schedule`)

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| `duty_id` | `INT` | 值班表ID |
| `student_id` | `VARCHAR(20)` | 学号 |
| `venue_id` | `INT` | 场馆ID |
| `start_time` | `DATETIME` | 值班开始时间 |
| `end_time` | `DATETIME` | 值班结束时间 |
| `remark` | `VARCHAR(255)` | 备注 |
| `attendance_status` | `CHAR(1)` | 考勤状态（0正常 1迟到 2早退 3缺勤） |
| `created_at` | `DATETIME` | 创建时间 |
| `updated_at` | `DATETIME` | 更新时间 |

#### 值班时间配置表 (`duty_time_config`)

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| `config_id` | `INT` | 配置ID |
| `venue_id` | `INT` | 场馆ID |
| `start_time` | `TIME` | 开始时间 |
| `end_time` | `TIME` | 结束时间 |
| `is_enable` | `TINYINT` | 是否启用 |
| `created_at` | `DATETIME` | 创建时间 |
| `updated_at` | `DATETIME` | 更新时间 |

#### 考勤记录表 (`attendance_record`)

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| `record_id` | `BIGINT` | 记录ID |
| `student_id` | `VARCHAR(20)` | 学号 |
| `duty_id` | `INT` | 值班表ID |
| `check_in_time` | `DATETIME` | 打卡时间 |
| `status` | `CHAR(1)` | 状态（0正常 1迟到 2早退 3缺勤） |
| `remark` | `VARCHAR(255)` | 备注 |
| `created_at` | `DATETIME` | 创建时间 |
| `updated_at` | `DATETIME` | 更新时间 |

#### 考勤统计表 (`attendance_statistics`)

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| `stat_id` | `BIGINT` | 统计ID |
| `student_id` | `VARCHAR(20)` | 学号 |
| `venue_id` | `INT` | 场馆ID |
| `year_month` | `VARCHAR(7)` | 年月（格式：2026-04） |
| `total_duty_hours` | `DECIMAL(10,2)` | 值班总时长 |
| `check_in_count` | `INT` | 打卡次数 |
| `attendance_count` | `INT` | 出勤次数 |
| `absence_count` | `INT` | 缺勤次数 |
| `late_count` | `INT` | 迟到次数 |
| `early_leave_count` | `INT` | 早退次数 |
| `created_at` | `DATETIME` | 创建时间 |
| `updated_at` | `DATETIME` | 更新时间 |

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

| 环境变量 | 描述 | 默认值 |
| :--- | :--- | :--- |
| `SERVER_PORT` | 服务端口 | 8080 |
| `DATABASE_URL` | 数据库连接地址 | jdbc:mysql://localhost:3306/zcst |
| `DATABASE_USERNAME` | 数据库用户名 | root |
| `DATABASE_PASSWORD` | 数据库密码 | 123456 |
| `REDIS_HOST` | Redis 主机 | localhost |
| `REDIS_PORT` | Redis 端口 | 6379 |

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

- 问题：场馆管理员无法查看值班表或执行相关操作
- 原因：缺少必要的权限配置
- 解决方法：确保场馆管理员角色拥有以下权限：
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

## 联系方式

- 项目负责人：XXX
- 技术支持：XXX
- 邮箱：XXX
- 电话：XXX
