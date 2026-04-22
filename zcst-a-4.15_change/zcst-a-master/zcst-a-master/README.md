# zcst-a 前端项目文档

## 项目简介

zcst-a 是一个基于 Vue 3 + Element Plus 的学生值班考勤管理系统前端项目，与后端 zcst-b 配合使用，提供了完整的前端界面，支持学生管理、场地管理、值班排班、考勤管理等功能。

## 技术架构

### 前端技术栈

| 技术/框架    | 版本    | 用途           |
| :----------- | :------ | :------------- |
| Vue          | 3.x     | 核心框架       |
| Vite         | 5.x     | 构建工具       |
| Element Plus | 2.x     | UI 组件库      |
| Vue Router   | 4.x     | 路由管理       |
| Vuex         | 4.x     | 状态管理       |
| Axios        | 1.x     | HTTP 请求库    |
| Sass         | 1.x     | CSS 预处理器   |
| ECharts      | 5.x     | 图表库         |
| SVG Icon     | -       | 图标库         |

### 项目结构

```
zcst-a/
├── public/                  # 静态资源
│   └── favicon.ico
├── src/
│   ├── api/                # API 接口
│   │   ├── login.js        # 登录接口
│   │   ├── menu.js         # 菜单接口
│   │   ├── upload.js       # 上传接口
│   │   ├── manage/         # 业务管理接口
│   │   │   ├── student.js
│   │   │   ├── venue.js
│   │   │   ├── dutySchedule.js
│   │   │   ├── dutyTimeConfig.js
│   │   │   ├── attendance.js
│   │   │   └── *.js
│   │   ├── system/         # 系统管理接口
│   │   ├── monitor/        # 监控管理接口
│   │   └── tool/           # 工具接口
│   ├── assets/             # 静态资源
│   │   ├── images/         # 图片资源
│   │   ├── icons/          # 图标资源
│   │   └── styles/         # 样式文件
│   ├── components/         # 公共组件
│   │   ├── Breadcrumb/     # 面包屑
│   │   ├── Crontab/        #  Cron 表达式
│   │   ├── DictTag/        # 字典标签
│   │   ├── Editor/         # 富文本编辑器
│   │   ├── FileUpload/     # 文件上传
│   │   ├── Hamburger/      # 折叠按钮
│   │   ├── IconSelect/     # 图标选择
│   │   ├── Pagination/     # 分页
│   │   ├── RightToolbar/   # 右侧工具栏
│   │   ├── Screenfull/     # 全屏
│   │   ├── SizeSelect/     # 尺寸选择
│   │   ├── StudentDutyWeekGrid/  # 学生值班周表格
│   │   ├── SvgIcon/        # SVG 图标
│   │   └── TopNav/         # 顶部导航
│   ├── layout/             # 布局组件
│   │   ├── components/     # 布局子组件
│   │   │   ├── Navbar/     # 顶部导航栏
│   │   │   ├── Sidebar/    # 侧边栏
│   │   │   ├── TagsView/   # 标签页
│   │   │   ├── Settings/   # 设置面板
│   │   │   └── ...
│   │   └── index.vue       # 布局主组件
│   ├── plugins/            # 插件
│   │   ├── auth.js         # 权限认证
│   │   ├── cache.js        # 缓存
│   │   ├── download.js     # 下载
│   │   ├── modal.js        # 模态框
│   │   ├── tab.js          # 标签页
│   │   └── index.js        # 插件入口
│   ├── router/             # 路由配置
│   │   └── index.js
│   ├── store/              # Vuex Store
│   │   ├── modules/        # 模块
│   │   │   ├── app.js      # 应用配置
│   │   │   ├── user.js     # 用户信息
│   │   │   ├── dict.js     # 字典数据
│   │   │   └── ...
│   │   └── index.js
│   ├── utils/              # 工具函数
│   │   ├── auth.js         # 认证工具
│   │   ├── dict.js         # 字典工具
│   │   ├── dutyPeriod.js   # 值班时段工具
│   │   ├── request.js      # 请求封装
│   │   ├── validate.js     # 验证工具
│   │   └── ...
│   ├── views/              # 页面组件
│   │   ├── login.vue       # 登录页
│   │   ├── register.vue    # 注册页
│   │   ├── adminHome.vue   # 管理员首页
│   │   ├── home.vue        # 首页
│   │   ├── index.vue       # 系统首页
│   │   ├── manage/         # 业务管理页面
│   │   │   ├── student/    # 学生管理
│   │   │   ├── venue/      # 场地管理
│   │   │   ├── dutySchedule/   # 值班表管理
│   │   │   ├── dutyTimeConfig/ # 值班时间配置
│   │   │   └── *Student/   # 各学校学生管理
│   │   ├── student/        # 学生端页面
│   │   │   ├── duty/       # 值班管理
│   │   │   ├── attendance/ # 考勤管理
│   │   │   ├── schedule/   # 课表管理
│   │   │   ├── profile/    # 个人信息
│   │   │   └── notify/     # 通知管理
│   │   ├── system/         # 系统管理页面
│   │   ├── monitor/        # 监控管理页面
│   │   ├── tool/           # 工具页面
│   │   └── error/          # 错误页面
│   ├── App.vue             # 根组件
│   ├── main.js             # 入口文件
│   ├── permission.js       # 权限控制
│   └── settings.js         # 配置项
├── bin/                    # 脚本文件
│   ├── run-web.bat         # 启动脚本
│   ├── build.bat           # 打包脚本
│   └── package.bat         # 打包脚本
├── .env.development        # 开发环境变量
├── .env.production         # 生产环境变量
├── .env.test              # 测试环境变量
├── vite.config.js          # Vite 配置文件
├── package.json            # 项目依赖配置
└── README.md               # 项目文档
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
- **学校学生管理**：各学校学生的专门管理
  - 笃学学生管理
  - 国防学生管理
  - 弘毅学生管理
  - 思齐学生管理
  - 新源学生管理
  - 知行学生管理
- **值班表管理**：场馆值班表的查看、添加、编辑和删除
  - 按场馆查看值班表（笃学、国防、弘毅、思齐、新源、知行）
  - 场馆值班网格视图
  - 自动排班功能
- **值班时间配置**：场馆值班时间的配置和管理

### 4. 学生端功能

- **值班管理**：学生查看自己的值班安排
  - 我的值班
  - 空闲时段查看
- **考勤管理**：学生考勤打卡功能
  - 打卡/签退
  - 考勤记录查看
  - 考勤统计
- **课表管理**：学生课表查看和管理
- **个人信息**：学生个人信息完善和修改
  - 基本信息
  - 课表照片上传
- **通知管理**：学生接收系统通知

### 5. 注册功能

- **学生注册**：学生用户注册页面
- **管理员注册**：管理员用户注册页面
- **角色选择**：注册时选择用户角色

### 6. 文件上传

- **课表照片上传**：支持学生上传课表照片
- **图片预览**：支持上传后的图片预览

### 7. 权限控制

- **路由权限**：根据用户角色动态加载路由
- **按钮权限**：基于权限控制按钮显示/隐藏
- **数据权限**：场馆管理员只能查看自己场馆的数据

## 接口规范

### API 接口格式

所有 API 接口都遵循 RESTful 设计规范，使用 JSON 格式进行数据交换。

### 接口地址

开发环境：`http://localhost:8080`

生产环境：根据实际部署配置

### 请求封装

使用 Axios 进行 HTTP 请求封装，统一处理请求响应、错误处理、Token 认证等。

```javascript
// 请求示例
request({
  url: '/manage/student/list',
  method: 'get',
  params: {
    pageNum: 1,
    pageSize: 10
  }
})
```

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

## 开发指南

### 环境要求

- Node.js >= 16.x
- npm >= 8.x 或 pnpm >= 7.x

### 安装依赖

```bash
# 使用 npm
npm install

# 或使用 pnpm
pnpm install
```

### 启动项目

```bash
# 开发模式
npm run dev

# 或使用 pnpm
pnpm dev
```

访问：http://localhost:8080

### 构建打包

```bash
# 生产环境打包
npm run build

# 或使用 pnpm
pnpm build
```

### 代码检查

```bash
# ESLint 检查
npm run lint
```

## 核心业务流程

### 1. 用户登录流程

1. 用户在登录页面输入用户名和密码
2. 前端调用登录接口获取 token
3. 存储 token 到 localStorage
4. 获取用户信息和菜单权限
5. 跳转到首页

### 2. 学生注册流程

1. 用户访问注册页面
2. 选择学生角色
3. 填写注册信息（学号、姓名、场馆、密码等）
4. 提交注册表单
5. 调用后端注册接口
6. 注册成功跳转到登录页

### 3. 管理员注册流程

1. 用户访问注册页面
2. 选择管理员角色
3. 填写注册信息（用户名、姓名、场馆、密码等）
4. 提交注册表单（昵称自动同步为姓名）
5. 调用后端注册接口
6. 注册成功跳转到登录页

### 4. 值班表管理流程

1. 场馆管理员登录系统
2. 进入值班表管理页面
3. 选择场馆查看值班表
4. 添加/编辑/删除值班记录
5. 执行自动排班功能
6. 查看排班结果

### 5. 考勤打卡流程

1. 学生登录系统
2. 进入考勤管理页面
3. 查看当前值班信息
4. 点击打卡/签退按钮
5. 系统记录打卡时间和状态
6. 查看考勤统计

## 主要组件说明

### 1. StudentDutyWeekGrid

学生值班周表格组件，用于展示一周内的值班安排。

**使用示例**：

```vue
<template>
  <StudentDutyWeekGrid 
    :venue-id="venueId"
    :week-start="weekStart"
    @cell-click="handleCellClick"
  />
</template>
```

### 2. Pagination

分页组件，封装了 Element Plus 的分页功能。

**使用示例**：

```vue
<template>
  <Pagination
    v-model:total="total"
    v-model:page="pageNum"
    v-model:limit="pageSize"
    @pagination="getList"
  />
</template>
```

### 3. RightToolbar

右侧工具栏组件，提供搜索、新增、导出等功能按钮。

**使用示例**：

```vue
<template>
  <RightToolbar
    v-model:show="show"
    @queryTable="getList"
    @resetTable="resetQuery"
  >
    <template #button>
      <el-button type="primary" @click="handleAdd">新增</el-button>
    </template>
  </RightToolbar>
</template>
```

## 权限控制

### 1. 路由权限

在 `permission.js` 中实现路由守卫，根据用户角色动态加载可访问的路由。

### 2. 按钮权限

使用 `v-hasPermi` 指令控制按钮的显示/隐藏。

```vue
<template>
  <el-button 
    v-hasPermi="['manage:student:add']"
    @click="handleAdd"
  >
    新增
  </el-button>
</template>
```

### 3. 数据权限

场馆管理员只能查看自己场馆的数据，通过后端接口自动过滤。

## 环境变量配置

### .env.development

```
VUE_APP_BASE_API = 'http://localhost:8080'
```

### .env.production

```
VUE_APP_BASE_API = 'https://your-production-api.com'
```

## 常见问题

### 1. 如何修改 API 地址？

修改 `.env.development` 或 `.env.production` 文件中的 `VUE_APP_BASE_API` 配置。

### 2. 如何添加新的页面？

1. 在 `src/views` 目录下创建新的页面组件
2. 在路由配置中添加路由
3. 在菜单配置中添加菜单项

### 3. 如何调用后端接口？

1. 在 `src/api` 目录下创建对应的接口文件
2. 使用 `request` 工具封装 API 方法
3. 在页面组件中引入并调用

### 4. 如何处理跨域问题？

在 `vite.config.js` 中配置代理：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 更新日志

### 2026-04-02

- ✅ 优化管理员注册页面（昵称自动同步为姓名）
- ✅ 优化值班表管理界面
- ✅ 修复考勤打卡功能

### 2026-03-xx

- ✅ 添加考勤管理功能
- ✅ 添加课表照片上传功能
- ✅ 优化权限控制逻辑

## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

## 许可证

本项目采用 MIT 许可证。
