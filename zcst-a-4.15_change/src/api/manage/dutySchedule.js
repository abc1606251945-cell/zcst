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
    url: '/manage/dutySchedule/' + dutyId,
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
  const ids = Array.isArray(dutyIds) ? dutyIds.join(",") : dutyIds
  return request({
    url: '/manage/dutySchedule/' + ids,
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

// 根据值班时间配置自动排班
export function autoScheduleByConfig(data) {
  return request({
    url: '/manage/dutySchedule/autoScheduleByConfig',
    method: 'post',
    data: data
  })
}

// 查询学生当前可签到的值班信息
export function getCurrentDuty(studentId) {
  return request({
    url: '/manage/dutySchedule/currentDuty',
    method: 'get',
    params: studentId ? { studentId } : undefined
  })
}

export function listMyDutySchedule(params) {
  return request({
    url: '/manage/dutySchedule/my',
    method: 'get',
    params
  })
}

export function listMyVenueDutySchedule(params) {
  return request({
    url: '/manage/dutySchedule/myVenue',
    method: 'get',
    params
  })
}
