# 值班安排 API 接口文档

## 新增接口：查询学生当前可签到的值班信息

### 接口说明

该接口用于查询学生当前正在进行的值班信息，返回学生可以签到的值班记录（包含 dutyId）。

前端可以通过此接口获取学生当前绑定场馆的值班信息，解决无法稳定识别可签到 dutyId 的问题。

---

## 1. 查询学生当前可签到的值班信息

### 请求说明

- **接口路径**：`GET /manage/dutySchedule/currentDuty`
- **请求方式**：GET
- **是否需要登录**：是
- **权限要求**：无特殊权限要求（学生、场馆管理员均可访问）

### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| studentId | String | 否 | 学号。不传则自动使用当前登录用户的学号 | `2023001` |

### 请求示例

#### 示例 1：查询当前登录学生的值班信息（推荐）

```javascript
// 前端调用示例
import request from '@/utils/request'

// 不传参数，自动使用当前登录用户
export function getCurrentDuty() {
  return request({
    url: '/manage/dutySchedule/currentDuty',
    method: 'get'
  })
}

// 使用示例
getCurrentDuty().then(response => {
  console.log('当前值班信息：', response.data)
})
```

#### 示例 2：查询指定学生的值班信息

```javascript
// 查询指定学生的当前值班
export function getCurrentDutyByStudent(studentId) {
  return request({
    url: '/manage/dutySchedule/currentDuty',
    method: 'get',
    params: { studentId }
  })
}

// 使用示例
getCurrentDutyByStudent('2023001').then(response => {
  console.log('指定学生的值班信息：', response.data)
})
```

### 响应说明

#### 成功响应

**响应格式**：JSON

**响应参数说明**：

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码（200 表示成功） |
| msg | String | 响应消息 |
| data | Array | 值班信息列表 |

**data 数组中的对象结构**：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| dutyId | Integer | 值班 ID（用于签到） |
| studentId | String | 学号 |
| studentName | String | 学生姓名 |
| gender | String | 性别 |
| venueId | Integer | 场馆 ID |
| startTime | String | 值班开始时间（格式：yyyy-MM-dd HH:mm:ss） |
| endTime | String | 值班结束时间（格式：yyyy-MM-dd HH:mm:ss） |
| remark | String | 备注 |
| createdAt | String | 创建时间 |
| updatedAt | String | 更新时间 |

#### 响应示例 1：有值班信息

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "dutyId": 123,
      "studentId": "2023001",
      "studentName": "康一",
      "gender": "男",
      "venueId": 1,
      "startTime": "2026-03-29 08:00:00",
      "endTime": "2026-03-29 10:00:00",
      "remark": "思齐馆值班",
      "createdAt": "2026-03-28 10:00:00",
      "updatedAt": "2026-03-28 10:00:00"
    }
  ]
}
```

#### 响应示例 2：没有值班信息

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": []
}
```

#### 错误响应

**未登录或用户信息不存在**：
```json
{
  "code": 500,
  "msg": "未登录或用户信息不存在",
  "data": null
}
```

**无法获取用户学号**：
```json
{
  "code": 500,
  "msg": "无法获取用户学号",
  "data": null
}
```

---

## 2. 前端使用场景示例

### 场景 1：学生签到页面

```vue
<template>
  <div class="check-in-page">
    <div v-if="currentDutyList.length > 0" class="duty-info">
      <h3>当前值班信息</h3>
      <div v-for="duty in currentDutyList" :key="duty.dutyId" class="duty-item">
        <p>值班时间：{{ formatTime(duty.startTime) }} - {{ formatTime(duty.endTime) }}</p>
        <p>场馆：{{ getVenueName(duty.venueId) }}</p>
        <el-button type="primary" @click="handleCheckIn(duty.dutyId)">签到</el-button>
      </div>
    </div>
    <div v-else class="no-duty">
      <p>当前没有可签到的值班</p>
    </div>
  </div>
</template>

<script>
import { getCurrentDuty } from '@/api/manage/dutySchedule'
import { checkIn } from '@/api/manage/attendanceRecord'

export default {
  name: 'CheckInPage',
  data() {
    return {
      currentDutyList: []
    }
  },
  created() {
    this.loadCurrentDuty()
  },
  methods: {
    // 加载当前值班信息
    loadCurrentDuty() {
      getCurrentDuty().then(response => {
        this.currentDutyList = response.data
        if (this.currentDutyList.length > 0) {
          this.$message.success(`找到 ${this.currentDutyList.length} 个可签到的值班`)
        }
      }).catch(error => {
        this.$message.error('加载值班信息失败')
      })
    },
    
    // 处理签到
    handleCheckIn(dutyId) {
      this.$confirm('确认签到？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 使用返回的 dutyId 进行签到
        checkIn({ dutyId }).then(() => {
          this.$message.success('签到成功')
          this.loadCurrentDuty() // 重新加载
        }).catch(() => {
          this.$message.error('签到失败')
        })
      })
    },
    
    formatTime(timeStr) {
      // 时间格式化，只保留时分
      return timeStr.split(' ')[1].substring(0, 5)
    },
    
    getVenueName(venueId) {
      const venueMap = {
        1: '思齐馆',
        2: '弘毅馆',
        3: '心缘馆',
        4: '笃学馆',
        5: '知行馆',
        6: '国防教育体验馆'
      }
      return venueMap[venueId] || '未知场馆'
    }
  }
}
</script>
```

### 场景 2：值班列表页面

```javascript
// api/manage/dutySchedule.js
import request from '@/utils/request'

/**
 * 查询学生当前可签到的值班信息
 * @param {string} studentId - 学号（可选，不传则使用当前登录用户）
 * @returns {Promise}
 */
export function getCurrentDuty(studentId) {
  return request({
    url: '/manage/dutySchedule/currentDuty',
    method: 'get',
    params: { studentId }
  })
}

/**
 * 判断学生当前是否有可签到的值班
 * @returns {Promise<boolean>}
 */
export function hasCurrentDuty() {
  return getCurrentDuty().then(response => {
    return response.data && response.data.length > 0
  })
}

/**
 * 获取当前值班的 dutyId（如果有多个值班，返回第一个）
 * @returns {Promise<number|null>}
 */
export function getCurrentDutyId() {
  return getCurrentDuty().then(response => {
    if (response.data && response.data.length > 0) {
      return response.data[0].dutyId
    }
    return null
  })
}
```

---

## 3. 业务逻辑说明

### 3.1 查询逻辑

1. **不传 studentId 参数**：
   - 系统自动从当前登录用户信息中获取学号
   - 如果用户未登录，返回错误：`"未登录或用户信息不存在"`
   - 如果用户信息中没有学号，返回错误：`"无法获取用户学号"`

2. **传入 studentId 参数**：
   - 直接查询指定学生的当前值班信息
   - 需要确保传入的学号在系统中存在

### 3.2 时间判断逻辑

接口会查询满足以下条件的值班记录：
```
start_time <= 当前时间 <= end_time
```

即：当前时间在值班开始时间和结束时间之间（包含边界）。

### 3.3 返回结果排序

返回的值班列表按 `start_time` 升序排列（最早的值班在前）。

---

## 4. 常见问题 FAQ

### Q1: 什么时候调用这个接口？

**A**: 在以下场景调用：
- 学生进入签到页面时
- 学生点击签到按钮前
- 需要判断学生当前是否有可签到的值班时

### Q2: 返回空数组代表什么？

**A**: 表示学生当前没有正在进行的值班，可能的原因：
- 当前时间不在任何值班时间段内
- 学生还没有被排班
- 学生的值班已经结束

### Q3: 如何获取 dutyId 用于签到？

**A**: 从接口返回的 `data` 数组中获取：
```javascript
getCurrentDuty().then(response => {
  if (response.data.length > 0) {
    const dutyId = response.data[0].dutyId
    // 使用 dutyId 进行签到
    checkIn({ dutyId })
  }
})
```

### Q4: 一个学生可能同时有多个值班吗？

**A**: 理论上可能（比如连续排班），但这种情况较少。如果有多个值班，接口会返回所有符合条件的值班记录，按开始时间排序。前端可以根据实际需求选择第一个或让用户选择。

### Q5: 接口返回的时间格式是什么？

**A**: 格式为：`"yyyy-MM-dd HH:mm:ss"`，例如：`"2026-03-29 08:00:00"`

---

## 5. 更新日志

| 日期 | 版本 | 说明 |
|------|------|------|
| 2026-03-29 | v1.0 | 初始版本，新增查询学生当前可签到值班接口 |

---

## 6. 联系支持

如有问题或需要技术支持，请联系后端开发团队。
