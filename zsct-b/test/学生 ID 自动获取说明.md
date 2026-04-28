# 文件上传功能 - 自动获取学号说明

## 📋 修改内容

### 后端修改

#### 1. FileUploadController.java

**修改前：**
```java
@PostMapping("/schedule")
public AjaxResult uploadSchedule(
        @RequestParam("file") MultipartFile file,
        @RequestParam("studentId") String studentId) {
    // 需要前端传递 studentId
    // ...
}
```

**修改后：**
```java
@PostMapping("/schedule")
public AjaxResult uploadSchedule(@RequestParam("file") MultipartFile file) {
    // 自动从登录信息中获取学号
    String studentId = SecurityUtils.getUsername();
    // ...
}
```

**说明：**
- 移除了 `studentId` 参数
- 使用 `SecurityUtils.getUsername()` 自动获取当前登录用户的学号
- 系统会将学生的学号存储在 `userName` 字段中

---

### 前端修改

#### 测试页面 (upload-test.html)

**修改前：**
```html
<input type="text" id="studentId" value="S001" placeholder="请输入学生 ID">
```

**修改后：**
```html
<div class="info-box">
    <p>💡 提示：学号会自动从登录信息中获取，无需手动输入。</p>
</div>
```

**JavaScript 修改：**
```javascript
// 修改前
formData.append('studentId', studentId);

// 修改后
// 不需要传递 studentId，后端会自动从登录信息获取
credentials: 'include' // 携带 cookie（包含 token）
```

---

## 🔍 工作原理

### 1. 登录流程

```
用户登录（使用学号）
  ↓
后端验证学号和密码
  ↓
创建 LoginUser 对象
  ↓
将学号存储在 userName 字段
  ↓
生成 JWT token
  ↓
返回给前端
```

### 2. 上传流程

```
前端发起上传请求（携带 token）
  ↓
后端从 token 中解析用户信息
  ↓
调用 SecurityUtils.getUsername()
  ↓
获取学号（从 userName 字段）
  ↓
使用学号构建文件路径
  ↓
上传到 OSS
```

---

## 📁 文件路径示例

```
timetable/schedule/{学号}/{年}/{月}/{日}/{yyyyMMdd}{序号}.jpg

例如：
timetable/schedule/20260321/2026/03/29/202603290001.jpg
timetable/schedule/20260101/2026/03/29/202603290001.jpg
```

---

## ✅ 优势

1. **简化前端**：前端不需要传递学号参数
2. **安全性更高**：学号从 token 中获取，防止伪造
3. **代码更简洁**：减少了参数验证逻辑
4. **用户体验更好**：无需手动输入学号

---

## 🚀 测试方法

### 方法一：浏览器测试（推荐）

1. 打开 `test/upload-test.html`
2. 选择图片文件
3. 点击"上传"按钮
4. 查看结果

**注意**：需要确保已登录系统（token 有效）

### 方法二：Postman 测试

1. 创建 POST 请求：`http://localhost:8081/upload/schedule`
2. Headers 中添加：`Authorization: Bearer YOUR_TOKEN`
3. Body 中选择 `form-data`
4. 添加文件字段：`file`（选择图片）
5. **不需要添加 studentId 字段**
6. 发送请求

### 方法三：命令行测试

修改 `test/test-upload.bat` 中的 TOKEN：
```batch
set TOKEN=your_jwt_token_here
```

然后运行脚本即可。

---

## ⚠️ 注意事项

### 1. Token 认证

上传接口需要登录认证，请求时必须携带有效的 token：

**方式一：Cookie**
```javascript
fetch('/upload/schedule', {
    method: 'POST',
    credentials: 'include' // 携带 cookie
});
```

**方式二：Header**
```javascript
fetch('/upload/schedule', {
    method: 'POST',
    headers: {
        'Authorization': 'Bearer YOUR_TOKEN'
    }
});
```

### 2. 学号获取

- 学号从 `SecurityUtils.getUsername()` 获取
- 对应数据库中的 `userName` 字段
- 对于学生用户，`userName` 存储的是学号

### 3. 错误处理

如果未登录或 token 过期，会返回 401 错误：
```json
{
    "code": 401,
    "msg": "登录用户未授权"
}
```

---

## 📝 数据库说明

### student 表结构

```sql
CREATE TABLE `student` (
  `student_id` varchar(20) NOT NULL COMMENT '学号',
  `user_id` bigint DEFAULT NULL COMMENT '关联 sys_user 表 ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  -- 其他字段...
  PRIMARY KEY (`student_id`)
);
```

### 登录逻辑

1. 学生使用学号登录
2. 后端将学号存储在 `sys_user.userName` 字段
3. 学生姓名存储在 `sys_user.nickName` 字段
4. 所以 `SecurityUtils.getUsername()` 返回的是学号

---

## 🎯 完成！

现在上传功能已经改为自动获取学号，前端无需传递该参数。

测试步骤：
1. ✅ 确保项目已启动
2. ✅ 确保已登录（获取到 token）
3. ✅ 打开测试页面或使用 Postman
4. ✅ 上传图片
5. ✅ 查看 OSS 确认上传成功

祝你使用愉快！🎉
