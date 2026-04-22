# 课表解析服务（Python）对接文档

## 目标

前端上传课表文件到后端后，后端将文件上传至 OSS，并把 OSS 文件地址提交给 Python 解析服务。Python 返回解析结果（结构化课程列表）给 Java，Java 负责写入数据库 `student_schedule` 表。

## 总体流程

1. 前端调用 Java 接口上传课表文件（图片/PDF）。
2. Java 将文件上传到 OSS，拿到 `ossUrl`。
3. Java 调用 Python 解析接口，提交 `ossUrl`（以及 `studentId` 等元信息）。
4. Python 下载 OSS 文件并解析，返回结构化课程列表。
5. Java 按 `studentId` 先清空原课表，再批量写入新课表数据到数据库。

## 课表文件为 PDF 时怎么处理

1. Java 侧无需特殊处理：`file` 仍按原流程上传到 OSS，`contentType` 通常为 `application/pdf`，`fileName` 后缀为 `.pdf`。
2. Python 侧必须支持 `application/pdf` / `.pdf`：
   - 若 PDF 是“可选中文本 PDF”（有真实文字层）：优先走文本抽取（更稳定）。
   - 若 PDF 是“扫描件/图片型 PDF”（无文字层）：需要按页渲染成图片后做 OCR，再按版式/表格规则解析。
   - 若 PDF 多页：建议解析全部页面并合并为一个 `items`，必要时去重后返回。
3. 无论来源是图片还是 PDF，Python 返回 `items` 的字段与时间规则完全一致（仍按 `courseName/location/startTime/endTime` 输出）。

## 双方必须遵守的规则（重要）

### 时间与时区（避免“时间偏移”）

1. Python 返回的时间字段必须满足以下任一方案（推荐第 1 种）：
   - 方案 1（推荐）：返回本地时间（GMT+8）的字符串 `yyyy-MM-dd HH:mm:ss`，例如 `2026-04-08 08:00:00`
   - 方案 2：返回带时区偏移的 ISO 时间，例如 `2026-04-08T08:00:00+08:00` 或 `2026-04-08T00:00:00Z`
2. 禁止：将 UTC 时间“直接格式化成”不带时区的 `yyyy-MM-dd HH:mm:ss`（这会导致后端按本地时区解释，从而出现偏移）。
3. `startTime`/`endTime` 必须是“具体日期 + 具体时间”的时间点，不允许仅返回“第几节课/周几”这类抽象信息。

### 数据完整性与幂等

1. 以 `studentId` 作为维度整体替换课表：Java 侧会先删除该学生旧数据再写入新数据；因此同一个文件重复导入不会累积脏数据。
2. Python 返回的 `items` 应尽量去重（同一课程名 + 起止时间重复的记录只保留一条），并确保 `endTime > startTime`。
3. Python 若解析不确定，宁可返回失败（`code != 0`）并说明原因，不要返回不完整/猜测性数据。

### 接口与协议稳定性

1. 请求/响应字段名大小写固定（`courseName/location/startTime/endTime`），字段缺失会导致 Java 侧判定为无效项或失败。
2. 响应必须是 JSON 对象，且必须包含 `code/msg`；`code == 0` 表示成功，其他均视为失败。

## Java 侧接口（前端调用）

### 上传并解析导入

- Method: `POST`
- Path: `/upload/schedule/analyze`
- Form-Data:
  - `file`: 课表文件（MultipartFile）
  - `studentId`（可选）：学号；若前端已登录，可不传，后端会从 token 中获取

返回示例（成功）：
```json
{
  "code": 200,
  "msg": "导入成功",
  "data": {
    "ossUrl": "https://xxx/xxx.png",
    "studentId": "02240225",
    "inserted": 6,
    "invalidItems": []
  }
}
```

## Python 侧接口（Java 调用）

### 课表解析提交

- Method: `POST`
- Path: `/api/v1/file-analysis/submit`
- Content-Type: `application/json`

请求体（Java 会按此结构发送）：
```json
{
  "analysisType": "timetable",
  "ossUrl": "https://xxx/xxx.png",
  "studentId": "02240225",
  "fileName": "课表.png",
  "contentType": "image/png",
  "size": 123456
}
```

说明：
- `analysisType` 固定为 `timetable`（用于区分后续可能的其它分析任务）。
- `ossUrl` 为 OSS 公开可访问的地址；Python 需要自行下载该文件。

### Python 返回（成功）

Java 侧会读取 `data.items` 并落库。

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "studentId": "02240225",
    "items": [
      {
        "courseName": "高等数学",
        "location": "思齐楼 101",
        "startTime": "2026-04-08 08:00:00",
        "endTime": "2026-04-08 09:40:00"
      }
    ]
  }
}
```

字段要求：
- `courseName`：必填，非空字符串
- `location`：可为空字符串或缺省
- `startTime` / `endTime`：必填，必须能被后端正确解析
  - 推荐：`yyyy-MM-dd HH:mm:ss`（本地时间 GMT+8）
  - 也支持：`2026-04-08T08:00:00+08:00`（ISO 带偏移）与 `2026-04-08T08:00:00`（ISO 不带偏移，按后端服务器本地时区解释）
  - 必须满足 `endTime > startTime`

### Python 返回（失败）

```json
{
  "code": 1001,
  "msg": "解析失败：无法识别课表",
  "data": null
}
```

约定：
- `code != 0` 视为失败
- `msg` 给出可读错误原因

## 数据库落库规则（Java 已实现）

Java 写入表：`student_schedule`

字段映射：
- `student_id` ← `studentId`
- `course_name` ← `courseName`
- `location` ← `location`
- `start_time` ← `startTime`
- `end_time` ← `endTime`

写入策略：
- 同一 `studentId` 会先删除旧记录，再插入新记录（整体替换）。

表结构参考：`d:\zcst\zcst-b\sql\zcst.sql` 中的 `student_schedule`。

## Python 需要完成的工作清单

1. 提供 HTTP 服务并实现接口：`POST /api/v1/file-analysis/submit`
2. 支持从 `ossUrl` 下载文件（建议支持 png/jpg/jpeg/pdf）
3. 解析出课程列表并输出为 `data.items`
4. 严格遵守“时间与时区”规则，输出可被解析的时间格式（推荐 `yyyy-MM-dd HH:mm:ss` 的 GMT+8 本地时间）
5. 失败时返回 `code != 0` 与可读 `msg`
6. 建议在服务端对同一请求做超时控制（Java 默认等待超时为 10s，可由后端配置调整）
7. 建议增加基础校验：`ossUrl` 可访问性、文件类型/大小、下载超时、解析超时
8. 解析结果建议先在 Python 侧做去重/排序，降低导入冲突与脏数据概率

## 联调建议

1. 先用固定的 `ossUrl` 返回一组 mock `items`，验证 Java 能成功落库并返回 inserted 数量。
2. 再逐步替换为真实解析逻辑（OCR/表格识别/PDF 解析）。
3. 若解析结果可能包含重复或冲突时间段，建议 Python 侧先做去重/排序（Java 会过滤掉不合法时间段，但不会自动合并冲突）。
