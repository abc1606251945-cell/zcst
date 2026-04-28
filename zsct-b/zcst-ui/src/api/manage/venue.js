import request from '@/utils/request'

// 查询场馆列表
export function listVenue(query) {
  return request({
    url: '/manage/venue/list',
    method: 'get',
    params: query
  })
}

// 查询场馆详细信息
export function getVenue(venueId) {
  return request({
    url: `/manage/venue/${venueId}`,
    method: 'get'
  })
}

// 新增场馆
export function addVenue(data) {
  return request({
    url: '/manage/venue',
    method: 'post',
    data: data
  })
}

// 修改场馆
export function updateVenue(data) {
  return request({
    url: '/manage/venue',
    method: 'put',
    data: data
  })
}

// 删除场馆
export function delVenue(venueIds) {
  return request({
    url: `/manage/venue/${venueIds}`,
    method: 'delete'
  })
}
