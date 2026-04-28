# 文件上传功能 - 快速测试指南

## 📦 测试文件夹说明

这个文件夹包含以下测试文件：

- `upload-test.html` - 浏览器测试页面（推荐）
- `test-upload.bat` - Windows 命令行测试脚本

---

## 🚀 方法一：使用浏览器测试（推荐）

### 步骤：

1. **启动项目**
   ```bash
   # 确保项目已启动，访问地址：http://localhost:8081
   ```

2. **打开测试页面**
   - 双击 `upload-test.html` 文件
   - 或在浏览器中打开这个文件

3. **上传图片**
   - 点击"选择图片文件"选择一张图片
   - 点击"上传"按钮
   - **学号会自动从登录信息中获取，无需手动输入**

4. **查看结果**
   - 成功会显示文件 URL 和预览
   - 失败会显示错误信息

### 优点：
✅ 图形界面，操作简单  
✅ 实时预览图片  
✅ 显示详细错误信息  
✅ 可以直接访问上传的文件  

---

## 💻 方法二：使用命令行测试

### 步骤：

1. **编辑测试脚本**
   - 右键编辑 `test-upload.bat`
   - 修改 `IMAGE_PATH` 为你的图片路径
   - 保存文件

2. **运行脚本**
   - 双击 `test-upload.bat`
   - 或命令行运行：`test-upload.bat`

3. **查看结果**
   - 查看命令行输出
   - 成功会返回 JSON 格式的响应

### 示例：
```batch
REM 编辑这一行，修改为你的图片路径
set IMAGE_PATH=C:\Users\你的用户名\Pictures\test.jpg
```

### 优点：
✅ 快速测试  
✅ 适合自动化脚本  
✅ 可以看到详细的 HTTP 请求信息  

---

## 📝 方法三：使用 Postman 测试

### 步骤：

1. **打开 Postman**

2. **创建 POST 请求**
   - URL: `http://localhost:8081/upload/schedule`
   - Method: POST

3. **设置 Body**
   - 选择 `form-data`
   - 添加字段：
     - Key: `file` (Type: File) - 选择图片
     - **不需要传递 studentId，后端会自动获取**

4. **发送请求**

---

## ✅ 验证上传结果

### 成功的响应：
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": "https://zcsst-student-timetable.oss-cn-beijing.aliyuncs.com/timetable/schedule/S001/2026/03/29/202603290001.jpg"
}
```

### 检查 OSS：

1. 登录阿里云 OSS 控制台
2. 找到 Bucket: `zcsst-student-timetable`
3. 查看路径：`timetable/schedule/S001/2026/03/29/`
4. 应该能看到文件：`202603290001.jpg`

### 访问文件：
在浏览器中打开返回的 URL，应该能看到图片

---

## ❗ 常见问题

### 1. 401 Unauthorized（未授权）

**原因**：接口需要登录认证

**解决方法**：
- 先调用登录接口获取 token
- 或在请求头中添加：`Authorization: Bearer YOUR_TOKEN`

### 2. 连接失败

**原因**：服务未启动

**解决方法**：
- 确保项目已启动
- 检查端口是否为 8081

### 3. OSS 上传失败

**原因**：OSS 配置错误

**解决方法**：
- 检查 `application.yml` 中的 OSS 配置
- 确认 AccessKey、SecretKey、Endpoint 正确

---

## 🎯 测试完成后

测试完成后，可以删除整个 `test` 文件夹：

```bash
# Windows
rmdir /s /q test

# 或在文件资源管理器中右键删除
```

---

## 📚 更多信息

详细测试指南请查看项目根目录的：`UPLOAD_TEST_GUIDE.md`

祝你测试顺利！🎉
