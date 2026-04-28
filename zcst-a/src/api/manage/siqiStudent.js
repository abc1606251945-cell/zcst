import request from '@/utils/request'

// 查询思齐馆学生管理列表
export function listSiqiStudent(query) {
  return request({
    url: '/manage/siqiStudent/list',
    method: 'get',
    params: query
  })
}

// 查询思齐馆学生管理详细
export function getSiqiStudent(studentId) {
  return request({
    url: '/manage/siqiStudent/' + studentId,
    method: 'get'
  })
}

// 新增思齐馆学生管理
export function addSiqiStudent(data) {
  return request({
    url: '/manage/siqiStudent',
    method: 'post',
    data: data
  })
}

// 修改思齐馆学生管理
export function updateSiqiStudent(data) {
  return request({
    url: '/manage/siqiStudent',
    method: 'put',
    data: data
  })
}

// 删除思齐馆学生管理
export function delSiqiStudent(studentIds) {
  const ids = Array.isArray(studentIds) ? studentIds.join(",") : studentIds
  return request({
    url: '/manage/siqiStudent/' + ids,
    method: 'delete'
  })
}
