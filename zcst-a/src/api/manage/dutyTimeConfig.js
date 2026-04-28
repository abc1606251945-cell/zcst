import request from '@/utils/request'

// 查询值班时间配置列表
export function listDutyTimeConfig(query) {
  return request({
    url: '/manage/dutyTimeConfig/list',
    method: 'get',
    params: query
  })
}

// 按场馆ID查询值班时间配置
export function getDutyTimeConfigByVenue(venueId) {
  return request({
    url: '/manage/dutyTimeConfig/byVenue/' + venueId,
    method: 'get'
  })
}

// 查询值班时间配置详细信息
export function getDutyTimeConfig(configId) {
  return request({
    url: '/manage/dutyTimeConfig/' + configId,
    method: 'get'
  })
}

// 新增值班时间配置
export function addDutyTimeConfig(data) {
  return request({
    url: '/manage/dutyTimeConfig',
    method: 'post',
    data: data
  })
}

// 修改值班时间配置
export function updateDutyTimeConfig(data) {
  return request({
    url: '/manage/dutyTimeConfig',
    method: 'put',
    data: data
  })
}

// 删除值班时间配置
export function delDutyTimeConfig(configIds) {
  const ids = Array.isArray(configIds) ? configIds.join(",") : configIds
  return request({
    url: '/manage/dutyTimeConfig/' + ids,
    method: 'delete'
  })
}
