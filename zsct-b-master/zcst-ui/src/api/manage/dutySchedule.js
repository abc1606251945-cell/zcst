import request from '@/utils/request'

// 查询值班表列表
export function listDutySchedule(query) {
  return request({
    url: '/manage/dutySchedule/list',
    method: 'get',
    params: query
  })
}

// 查询值班表详细信息
export function getDutySchedule(dutyId) {
  return request({
    url: `/manage/dutySchedule/${dutyId}`,
    method: 'get'
  })
}

// 新增值班表
export function addDutySchedule(data) {
  return request({
    url: '/manage/dutySchedule',
    method: 'post',
    data: data
  })
}

// 修改值班表
export function updateDutySchedule(data) {
  return request({
    url: '/manage/dutySchedule',
    method: 'put',
    data: data
  })
}

// 删除值班表
export function delDutySchedule(dutyIds) {
  return request({
    url: `/manage/dutySchedule/${dutyIds}`,
    method: 'delete'
  })
}

// 获取可用学生列表
export function getAvailableStudents(data) {
  return request({
    url: '/manage/dutySchedule/availableStudents',
    method: 'post',
    data: data
  })
}

// 自动排班
export function autoSchedule(data) {
  return request({
    url: '/manage/dutySchedule/autoSchedule',
    method: 'post',
    data: data
  })
}

// 根据配置自动排班
export function autoScheduleByConfig(data) {
  return request({
    url: '/manage/dutySchedule/autoScheduleByConfig',
    method: 'post',
    data: data
  })
}
