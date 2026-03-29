# 文件上传功能使用说明

## 1. 阿里云 OSS 配置

### 1.1 创建 OSS Bucket

1. 登录阿里云控制台
2. 进入对象存储 OSS 服务
3. 创建 Bucket：
   - Bucket 名称：`zcsst-student-timetable`（或自定义）
   - 地域：选择离你最近的地域（如：华东 2-上海）
   - 读写权限：私有（推荐）
   - 其他配置保持默认

### 1.2 获取 AccessKey

1. 登录阿里云控制台
2. 进入访问控制 RAM
3. 创建用户或使用现有用户
4. 获取 AccessKey ID 和 AccessKey Secret

### 1.3 配置 application.yml

编辑 `zcst-admin/src/main/resources/application.yml` 文件：

```yaml
dromara:
  x-file-storage:
    default-platform: aliyun-oss-1
    thumbnail-suffix: ".min.jpg"
    
    aliyun-oss:
      - platform: aliyun-oss-1
        enable-storage: true
        access-key: YOUR_ACCESS_KEY          # 替换为你的 AccessKey ID
        secret-key: YOUR_SECRET_KEY          # 替换为你的 AccessKey Secret
        end-point: oss-cn-shanghai.aliyuncs.com  # 替换为你的 Endpoint
        bucket-name: zcsst-student-timetable
        domain: https://zcsst-student-timetable.oss-cn-shanghai.aliyuncs.com/
        base-path: timetable/
```

**配置说明：**
- `access-key`: 你的阿里云 AccessKey ID
- `secret-key`: 你的阿里云 AccessKey Secret
- `end-point`: OSS 的 Endpoint，根据你选择的地域确定
  - 华东 1（杭州）：oss-cn-hangzhou.aliyuncs.com
  - 华东 2（上海）：oss-cn-shanghai.aliyuncs.com
  - 华北 1（青岛）：oss-cn-qingdao.aliyuncs.com
  - 华北 2（北京）：oss-cn-beijing.aliyuncs.com
  - 华南 1（深圳）：oss-cn-shenzhen.aliyuncs.com
- `bucket-name`: 你的 Bucket 名称
- `domain`: 访问域名，格式为 `https://{bucket-name}.{endpoint}/`
- `base-path`: OSS 中的基础路径，所有文件都会存储在这个路径下

### 1.4 配置 CORS（跨域访问）

如果前端需要直接访问 OSS 上的文件，需要配置 CORS：

1. 进入 OSS 控制台
2. 选择你的 Bucket
3. 进入"数据安全" -> "跨域设置"
4. 创建跨域规则：
   - 来源：`*`（或指定域名）
   - 允许 Methods：GET, POST, PUT, DELETE
   - 允许 Headers：`*`
   - 暴露 Headers：ETag
   - 缓存时间：600

## 2. API 接口说明

### 2.1 上传课表照片

**接口地址：** `POST /upload/schedule`

**请求参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | MultipartFile | 是 | 图片文件（最大 10MB） |
| studentId | String | 是 | 学生 ID |

**请求示例：**
```bash
curl -X POST http://localhost:8081/upload/schedule \
  -F "file=@/path/to/timetable.jpg" \
  -F "studentId=S001"
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": "https://zcsst-student-timetable.oss-cn-shanghai.aliyuncs.com/timetable/schedule/S001/2026/03/29/202603290001.jpg"
}
```

**文件路径规则：**
```
{base-path}/schedule/{studentId}/{年}/{月}/{日}/{日期}{4 位序号}.{扩展名}

例如：
timetable/schedule/S001/2026/03/29/202603290001.jpg
timetable/schedule/S001/2026/03/29/202603290002.jpg
timetable/schedule/S001/2026/03/30/202603300001.jpg
```

**说明：**
- 文件按日期分类存储，格式为 `年/月/日`
- 同一天的文件按上传顺序编号，序号从 0001 开始
- 每天重启后序号会重置（如需持久化序号，建议使用 Redis）

### 2.2 通用文件上传

**接口地址：** `POST /upload/file`

**请求参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | MultipartFile | 是 | 文件 |

**请求示例：**
```bash
curl -X POST http://localhost:8081/upload/file \
  -F "file=@/path/to/file.txt"
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": "https://zcsst-student-timetable.oss-cn-shanghai.aliyuncs.com/timetable/uploads/2026/03/29/202603290001.txt"
}
```

## 3. 前端集成示例

### Vue3 示例

```vue
<template>
  <div>
    <input type="file" @change="handleFileChange" accept="image/*" />
    <button @click="uploadFile">上传课表</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const selectedFile = ref(null);
const studentId = 'S001'; // 从用户信息获取

const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0];
};

const uploadFile = async () => {
  if (!selectedFile.value) {
    alert('请选择文件');
    return;
  }

  const formData = new FormData();
  formData.append('file', selectedFile.value);
  formData.append('studentId', studentId);

  try {
    const response = await axios.post('/upload/schedule', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    if (response.data.code === 200) {
      alert('上传成功');
      console.log('文件 URL:', response.data.data);
    } else {
      alert('上传失败：' + response.data.msg);
    }
  } catch (error) {
    console.error('上传错误:', error);
    alert('上传失败');
  }
};
</script>
```

### React 示例

```jsx
import React, { useState } from 'react';
import axios from 'axios';

function UploadComponent() {
  const [file, setFile] = useState(null);
  const studentId = 'S001'; // 从用户信息获取

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const uploadFile = async () => {
    if (!file) {
      alert('请选择文件');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('studentId', studentId);

    try {
      const response = await axios.post('/upload/schedule', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.data.code === 200) {
        alert('上传成功');
        console.log('文件 URL:', response.data.data);
      } else {
        alert('上传失败：' + response.data.msg);
      }
    } catch (error) {
      console.error('上传错误:', error);
      alert('上传失败');
    }
  };

  return (
    <div>
      <input type="file" onChange={handleFileChange} accept="image/*" />
      <button onClick={uploadFile}>上传课表</button>
    </div>
  );
}

export default UploadComponent;
```

## 4. 注意事项

### 4.1 序号持久化

当前实现使用内存计数器，应用重启后序号会重置。如果需要严格的序号连续性，建议使用 Redis：

```java
// 使用 Redis 实现序号生成
@Autowired
private RedisTemplate<String, String> redisTemplate;

private int generateSequence(String datePath) {
    String key = "upload:sequence:" + datePath;
    return (int) redisTemplate.opsForValue().increment(key);
}
```

### 4.2 文件类型限制

当前只允许上传图片文件，如需支持其他类型，修改 Controller 中的验证逻辑：

```java
// 允许所有文件类型
// 删除或修改以下代码
if (contentType == null || !contentType.startsWith("image/")) {
    return AjaxResult.error("只能上传图片文件");
}
```

### 4.3 文件大小限制

当前限制为 10MB，如需修改，在 `application.yml` 中调整：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 100MB
```

同时在 Controller 中修改验证逻辑。

### 4.4 安全性

1. **文件类型验证**：建议同时验证 MIME 类型和文件扩展名
2. **病毒扫描**：生产环境建议对上传的文件进行病毒扫描
3. **访问控制**：确保只有授权用户可以上传文件
4. **文件清理**：定期清理无用的上传文件

## 5. 测试

### 5.1 使用 Postman 测试

1. 创建 POST 请求：`http://localhost:8081/upload/schedule`
2. 选择 Body -> form-data
3. 添加字段：
   - file: 选择图片文件（类型选择 File）
   - studentId: S001（类型选择 Text）
4. 发送请求

### 5.2 使用 cURL 测试

```bash
# 上传课表照片
curl -X POST http://localhost:8081/upload/schedule \
  -F "file=@/path/to/timetable.jpg" \
  -F "studentId=S001" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 通用文件上传
curl -X POST http://localhost:8081/upload/file \
  -F "file=@/path/to/file.txt" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 6. 常见问题

### Q1: 上传失败，提示 "文件上传失败"

**原因：** OSS 配置不正确

**解决方法：**
1. 检查 AccessKey 和 SecretKey 是否正确
2. 检查 Endpoint 是否正确
3. 检查 Bucket 是否存在
4. 检查网络连接

### Q2: 上传成功但无法访问文件

**原因：** CORS 配置问题或文件权限问题

**解决方法：**
1. 检查 OSS 的 CORS 配置
2. 检查 Bucket 的读写权限
3. 确认域名配置正确

### Q3: 序号不连续

**原因：** 应用重启后内存计数器重置

**解决方法：** 使用 Redis 持久化序号（见 4.1 节）

## 7. 实现原理

### 7.1 文件命名规则

上传的文件按照以下规则命名：

```
{base-path}/schedule/{studentId}/{年}/{月}/{日}/{yyyyMMdd}{4 位序号}.{扩展名}

例如：
timetable/schedule/S001/2026/03/29/202603290001.jpg
timetable/schedule/S001/2026/03/29/202603290002.jpg
timetable/schedule/S001/2026/03/30/202603300001.jpg  # 新的一天，序号重新从 1 开始
```

### 7.2 序号持久化实现

系统通过以下方式实现序号持久化：

1. **查询 OSS 文件列表**：每次上传前，使用阿里云 OSS 的 ListObjects API 查询当天已有文件
2. **提取序号**：从已有文件名中提取已使用的序号
3. **查找下一个可用序号**：从 1 开始，找到第一个未被使用的序号
4. **缓存优化**：使用内存缓存避免频繁查询 OSS，提高性能

**优点：**
- ✅ 序号严格连续，不会重复
- ✅ 应用重启后序号不会重置
- ✅ 支持并发上传（通过缓存优化）
- ✅ 不依赖额外的数据库或 Redis

**性能优化：**
- 使用内存缓存当天已使用的序号
- 避免每次上传都查询 OSS
- 第一次上传时查询，后续上传直接使用缓存

### 7.3 并发处理

系统使用以下策略处理并发上传：

1. **缓存机制**：内存缓存已使用的序号
2. **查找可用序号**：从 1 开始查找第一个未使用的序号
3. **原子性保证**：OSS 的文件名唯一性保证

**注意：** 在极高并发场景下（如每秒上百次上传），建议使用 Redis 实现分布式锁或序号生成器。

## 8. 常见问题

### Q1: 上传失败，提示 "文件上传失败"

**原因：** OSS 配置不正确

**解决方法：**
1. 检查 AccessKey 和 SecretKey 是否正确
2. 检查 Endpoint 是否正确
3. 检查 Bucket 是否存在
4. 检查网络连接

### Q2: 上传成功但无法访问文件

**原因：** CORS 配置问题或文件权限问题

**解决方法：**
1. 检查 OSS 的 CORS 配置
2. 检查 Bucket 的读写权限
3. 确认域名配置正确

### Q3: 第一次上传很慢

**原因：** 第一次上传需要查询 OSS 上所有当天文件，如果文件数量较多会比较慢

**解决方法：**
1. 这是正常现象，后续上传会使用缓存，速度会很快
2. 如果当天文件数量特别多（超过 1000 个），可以考虑按学生 ID 分别查询

### Q4: 序号跳号

**原因：** 如果有文件被删除，可能会出现序号跳号

**解决方法：**
1. 这是正常现象，系统会找到下一个未使用的序号
2. 如果不希望跳号，需要维护一个连续的序号表（建议使用 Redis）

现在你可以：
1. 配置阿里云 OSS 参数
2. 启动项目测试上传功能
3. 集成到前端应用
4. 根据需要调整文件命名规则

如有问题，请查看日志文件获取详细错误信息。
