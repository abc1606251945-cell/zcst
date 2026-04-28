# x-file-storage 使用说明和修改总结

## 📚 x-file-storage 正确使用方法

根据 x-file-storage 官方文档（https://github.com/dromara/x-file-storage），正确的使用方式如下：

### 1. 依赖配置

```xml
<!-- 只需要引入这个依赖 -->
<dependency>
    <groupId>org.dromara.x-file-storage</groupId>
    <artifactId>x-file-storage-spring</artifactId>
    <version>2.3.0</version>
</dependency>
```

**说明**：
- `x-file-storage-spring` 已经包含了所有需要的依赖
- 不需要手动引入 `x-file-storage-core`
- 阿里云 OSS SDK 会自动被引入

### 2. application.yml 配置

```yaml
dromara:
  x-file-storage:
    default-platform: aliyun-oss-1  # 默认使用的存储平台
    thumbnail-suffix: ".min.jpg"
    
    aliyun-oss:
      - platform: aliyun-oss-1
        enable-storage: true
        access-key: YOUR_ACCESS_KEY
        secret-key: YOUR_SECRET_KEY
        end-point: oss-cn-beijing.aliyuncs.com
        bucket-name: your-bucket-name
        domain: https://your-bucket.oss-cn-beijing.aliyuncs.com/
        base-path: your-base-path/
```

**重要配置说明**：
- `default-platform`: 必须设置，指定默认使用的存储平台
- `platform`: 存储平台标识，必须与 default-platform 一致
- `enable-storage`: 启用存储
- `domain`: 访问域名，必须以 `/` 结尾
- `base-path`: OSS 中的基础路径

### 3. 不需要手动创建 Bean

**错误的做法**：
```java
@Configuration
public class XFileStorageConfig {
    @Bean
    public FileStorageService fileStorageService(...) {
        // 手动创建 Bean
    }
}
```

**正确的做法**：
```java
@Configuration
public class XFileStorageConfig {
    // 空配置类即可，x-file-storage 会自动配置所有 Bean
}
```

**说明**：
- x-file-storage 使用 Spring Boot 的自动配置机制
- 会自动创建 `FileStorageService` Bean
- 会自动创建各种存储平台的 `FileStorage` Bean
- 手动创建反而会导致冲突

### 4. 上传文件的正确方式

**简单上传**：
```java
@Autowired
private FileStorageService fileStorageService;

public String uploadFile(MultipartFile file) {
    FileInfo fileInfo = fileStorageService.of(file)
            .setPath("upload/")
            .upload();
    return fileInfo.getUrl();
}
```

**完整示例（带对象 ID 和类型）**：
```java
public String uploadFile(MultipartFile file, String objectId, String objectType) {
    FileInfo fileInfo = fileStorageService.of(file)
            .setPath("upload/")
            .setObjectId(objectId)      // 关联对象 ID（可选）
            .setObjectType(objectType)  // 关联对象类型（可选）
            .upload();
    return fileInfo.getUrl();
}
```

**关键点**：
- 直接使用 `fileStorageService.of(file)`
- **不需要**指定 `.setPlatform()` - 会自动使用 `default-platform`
- **不需要**指定 `.setSize()` - 会自动获取
- 调用 `.upload()` 完成上传

---

## 🔧 我的代码修改总结

### 修改 1：XFileStorageConfig.java

**修改前**：
```java
@Configuration
public class XFileStorageConfig {
    @Bean
    public FileStorageService fileStorageService(...) {
        return FileStorageServiceBuilder.create(...)
                .addFileStorage(...)
                .build();
    }
}
```

**修改后**：
```java
@Configuration
public class XFileStorageConfig {
    // 空配置类，x-file-storage 会自动配置所有 Bean
}
```

**原因**：x-file-storage 会自动配置，不需要手动创建 Bean

---

### 修改 2：FileUploadServiceImpl.java

**修改前**：
```java
// 错误：手动指定 platform
FileInfo fileInfo = fileStorageService.of(file)
        .setPath(fullPath)
        .setPlatform("aliyun-oss-1")
        .upload();
```

**修改后**：
```java
// 正确：自动使用 default-platform
FileInfo fileInfo = fileStorageService.of(file)
        .setPath(fullPath)
        .upload();
```

**原因**：
- 会自动使用 `application.yml` 中配置的 `default-platform`
- 不需要手动指定 platform

---

### 修改 3：pom.xml

**修改前**：
```xml
<dependencies>
    <dependency>
        <groupId>org.dromara.x-file-storage</groupId>
        <artifactId>x-file-storage-core</artifactId>
        <version>2.3.0</version>
    </dependency>
    <dependency>
        <groupId>org.dromara.x-file-storage</groupId>
        <artifactId>x-file-storage-spring</artifactId>
        <version>2.3.0</version>
    </dependency>
    <dependency>
        <groupId>com.aliyun.oss</groupId>
        <artifactId>aliyun-sdk-oss</artifactId>
        <version>3.16.1</version>
    </dependency>
</dependencies>
```

**修改后**：
```xml
<dependencies>
    <!-- 只需要这个依赖 -->
    <dependency>
        <groupId>org.dromara.x-file-storage</groupId>
        <artifactId>x-file-storage-spring</artifactId>
        <version>2.3.0</version>
    </dependency>
    
    <dependency>
        <groupId>com.zcst</groupId>
        <artifactId>zcst-common</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
```

**原因**：
- `x-file-storage-spring` 已经包含了所有需要的依赖
- 阿里云 OSS SDK 会自动被引入
- 不需要手动引入多个依赖

---

## ✅ 最终代码

### FileUploadServiceImpl.java（关键部分）

```java
@Override
public String uploadScheduleImage(MultipartFile file, String studentId) {
    try {
        log.info("开始上传课表照片 - 文件名：{}, 类型：{}, 大小：{}, 学号：{}", 
                 file.getOriginalFilename(), 
                 file.getContentType(),
                 file.getSize(),
                 studentId);
        
        String datePath = LocalDate.now().format(DATE_FORMATTER);
        String filename = generateSequentialFilename(datePath, file.getOriginalFilename());
        String fullPath = basePath + "schedule/" + studentId + "/" + datePath + "/" + filename;
        
        log.info("上传路径：{}", fullPath);
        
        // 直接上传，自动使用 default-platform
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath(fullPath)
                .upload();
        
        if (fileInfo == null) {
            throw new RuntimeException("文件上传失败");
        }
        
        log.info("课表照片上传成功：{}", fileInfo.getUrl());
        return fileInfo.getUrl();
        
    } catch (Exception e) {
        log.error("课表照片上传失败", e);
        throw new RuntimeException("课表照片上传失败：" + e.getMessage(), e);
    }
}
```

---

## 🚀 测试步骤

1. **确保 application.yml 配置正确**
   - `default-platform: aliyun-oss-1`
   - `platform: aliyun-oss-1`（必须与 default-platform 一致）
   - AccessKey、SecretKey、Endpoint 配置正确

2. **重新启动项目**

3. **测试上传**
   - 打开 `test/upload-test.html`
   - 输入学号
   - 选择图片上传

4. **查看结果**
   - 成功会返回 OSS 文件 URL
   - 可以在阿里云 OSS 控制台查看文件

---

## 📝 常见问题

### Q1: 报错 "不支持此文件"

**原因**：
- 手动创建了 `FileStorageService` Bean
- 或者指定了错误的 `platform`

**解决方法**：
- 删除手动创建 Bean 的代码
- 不要指定 `.setPlatform()`
- 确保 `default-platform` 配置正确

### Q2: 报错 "找不到 FileStorageService Bean"

**原因**：
- 依赖配置不正确
- 只引入了 `x-file-storage-core`，没有引入 `x-file-storage-spring`

**解决方法**：
- 确保引入了 `x-file-storage-spring` 依赖
- 不要手动创建 Bean

### Q3: 上传成功但无法访问文件

**原因**：
- `domain` 配置不正确

**解决方法**：
- 确保 `domain` 以 `/` 结尾
- 例如：`https://bucket.oss-cn-beijing.aliyuncs.com/`

---

## 🎯 总结

使用 x-file-storage 的正确方式：

1. ✅ 只引入 `x-file-storage-spring` 依赖
2. ✅ 在 `application.yml` 中配置 `default-platform`
3. ✅ 不要手动创建 `FileStorageService` Bean
4. ✅ 上传时不要指定 `platform`
5. ✅ 直接使用 `fileStorageService.of(file).setPath().upload()`

**核心原则**：让 x-file-storage 自动配置，不要手动干预！

祝你使用顺利！🎉
