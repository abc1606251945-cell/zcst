import request from '@/utils/request'

// 查询场馆信息管理列表
export function listVenue(query) {
  return request({
    url: '/manage/venue/list',
    method: 'get',
    params: query
  })
}

// 查询场馆信息管理详细
export function getVenue(venueId) {
  return request({
    url: '/manage/venue/' + venueId,
    method: 'get'
  })
}

// 新增场馆信息管理
export function addVenue(data) {
  return request({
    url: '/manage/venue',
    method: 'post',
    data: data
  })
}

// 修改场馆信息管理
export function updateVenue(data) {
  return request({
    url: '/manage/venue',
    method: 'put',
    data: data
  })
}

// 删除场馆信息管理
export function delVenue(venueIds) {
  const ids = Array.isArray(venueIds) ? venueIds.join(",") : venueIds
  return request({
    url: '/manage/venue/' + ids,
    method: 'delete'
  })
}

export function listVenueSimple() {
  return request({
    url: '/manage/venue/simpleList',
    method: 'get'
  })
}
