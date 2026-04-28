# 首页排班与课表信息管理系统后端设计与实现方案

本文档根据要求，为前端 Vue 项目提供了配套的后端（基于 Spring Boot + MySQL + MyBatis Plus）排班与课表信息管理系统的完整架构设计与实现方案。

## 1. 数据存储方案（数据库设计）

为高效存储和查询空闲时间段，我们设计 `sys_student_free_schedule` 表，支持根据学生ID、学周、星期几以及具体时间段进行快速检索。

```sql
CREATE TABLE `sys_student_free_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` varchar(50) NOT NULL COMMENT '学号',
  `student_name` varchar(50) DEFAULT NULL COMMENT '学生姓名',
  `academic_week` int(11) NOT NULL COMMENT '学周（如：1-17周）',
  `day_of_week` int(11) NOT NULL COMMENT '星期几（1-7代表周一到周日）',
  `section_range` varchar(20) NOT NULL COMMENT '节次范围（如：1-2节, 3-4节）',
  `free_start_time` time DEFAULT NULL COMMENT '空闲开始时间',
  `free_end_time` time DEFAULT NULL COMMENT '空闲结束时间',
  `file_hash` varchar(128) DEFAULT NULL COMMENT '上传文件的Hash，用于防重复',
  `created_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_week_day_section` (`student_id`,`academic_week`,`day_of_week`,`section_range`),
  KEY `idx_student_time` (`student_id`,`academic_week`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生空闲时间段表';
```
*说明：引入了 `UNIQUE KEY` 确保同一学生在同一周、同一天的同一节次不会有重复的空闲记录（满足重复数据检测与数据完整性校验）。*

## 2. 接口开发（RESTful API）

提供支持高并发的文件上传接口。使用 Swagger/SpringDoc 生成完整的 API 文档。

### 接口定义

- **路径**: `POST /api/schedule/upload`
- **Content-Type**: `multipart/form-data`
- **参数**:
  - `file`: 课表文件（`.pdf`）
  - `studentId`: 学号
  - `academicWeek`: 学周（可选，如果不传则默认解析全学期）
- **返回结构**:
  ```json
  {
    "code": 200,
    "msg": "解析并存入成功",
    "data": {
      "parsedSlots": 35,
      "costTimeMs": 850
    }
  }
  ```

### Java Controller 代码示例

```java
@RestController
@RequestMapping("/api/schedule")
@Api(tags = "课表与排班管理")
public class ScheduleController {

    @Autowired
    private IScheduleService scheduleService;

    @PostMapping("/upload")
    @ApiOperation("上传并解析课表文件提取空闲时间")
    @Log(title = "上传课表", businessType = BusinessType.IMPORT) // 操作日志记录
    public AjaxResult uploadSchedule(@RequestParam("file") MultipartFile file, 
                                     @RequestParam("studentId") String studentId) {
        // 1. 文件格式与大小验证
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            return AjaxResult.error("仅支持 PDF 格式的文件");
        }
        
        long startTime = System.currentTimeMillis();
        try {
            // 2. 调用 Service 层进行解析与入库
            int slots = scheduleService.parseAndSaveFreeTime(file, studentId);
            long costTime = System.currentTimeMillis() - startTime;
            
            // 性能要求：解析时间不超过3秒 (3000ms)
            if (costTime > 3000) {
                log.warn("文件解析耗时过长: {} ms", costTime);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("parsedSlots", slots);
            result.put("costTimeMs", costTime);
            
            return AjaxResult.success("解析成功", result);
        } catch (DuplicateDataException e) {
            return AjaxResult.error("重复上传相同的课表数据");
        } catch (Exception e) {
            log.error("课表解析失败", e);
            return AjaxResult.error("解析失败: " + e.getMessage());
        }
    }
}
```

## 3. 数据处理逻辑（后端服务）

后端使用 `Apache PDFBox` 或 `IText` 等库解析 PDF 文件内容。由于 PDF 的排版复杂，通常需要根据具体学校的课表模板格式，通过文本坐标或正则表达式提取课程信息。
**算法逻辑：**
1. 定义标准全集时间段（如周一至周日，每天 1-2节, 3-4节, ..., 11-12节）。
2. 解析 PDF 课表文件，提取出**有课**的单元格集合。
3. 求差集：`空闲时间段 = 全集时间段 - 有课时间段`。
4. 将空闲时间段映射为标准 JSON 结构并构建实体对象 `SysStudentFreeSchedule`。

## 4. 集成要求（事务与并发控制）

为了满足 99.9% 的入库成功率和数据一致性，采用以下策略：

```java
@Service
public class ScheduleServiceImpl implements IScheduleService {

    @Autowired
    private SysStudentFreeScheduleMapper scheduleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务处理
    public int parseAndSaveFreeTime(MultipartFile file, String studentId) throws Exception {
        // 1. 计算文件哈希防重
        String fileHash = DigestUtils.md5Hex(file.getInputStream());
        if (isDuplicate(studentId, fileHash)) {
            throw new DuplicateDataException("文件已存在");
        }

        // 2. 解析 PDF 文件提取有课时间段并求差集
        List<SysStudentFreeSchedule> allSlots = this.extractFreeSlotsFromPdf(file, studentId);
        
        // 3. 删除该生原有排班冲突数据（保证幂等性）
        QueryWrapper<SysStudentFreeSchedule> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        scheduleMapper.delete(wrapper);

        // 4. 批量插入数据库 (MyBatis Plus saveBatch)
        // 采用分批插入（如每 500 条一批），避免单次大事务导致的死锁和性能问题
        this.saveBatch(allSlots, 500);

        return allSlots.size();
    }
    
    // 解析课表求差集的具体私有方法省略...
}
```

## 5. 性能与质量保障机制

1. **并发支持机制**：
   - 采用 `Apache PDFBox` 将文档按页拆分进行并行解析，控制内存使用。
   - 数据库层面通过设置合理的连接池大小（Druid），支持高并发写操作。
2. **处理速度 (<3秒)**：
   - 文件在内存中直接进行流式读取解析，不落盘。
   - 提取文本内容时采用多线程优化，结合预编译的正则表达式进行内容匹配。
   - 数据库操作使用 `saveBatch` 批量处理。
3. **数据完整性校验**：
   - 利用 MySQL 的 `UNIQUE KEY` 防止同一时间段出现重复插入。
   - 配合 `@Transactional` 注解，确保发生异常时整体回滚。
4. **日志记录**：
   - 依托 RuoYi 框架自带的 `@Log` 注解实现操作日志自动落表（`sys_oper_log`）。
   - 在业务层加入耗时告警日志（`log.warn`），当单文件处理超过预设阈值时便于追踪。