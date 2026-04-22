import request from '@/utils/request'

// 查询心缘馆学生管理列表
export function listStudent(query) {
  return request({
    url: '/manage/xinyuanStudent/list',
    method: 'get',
    params: query
  })
}

// 查询心缘馆学生管理详细信息
export function getStudent(studentId) {
  return request({
    url: '/manage/xinyuanStudent/' + studentId,
    method: 'get'
  })
}

// 新增心缘馆学生管理
export function addStudent(data) {
  return request({
    url: '/manage/xinyuanStudent',
    method: 'post',
    data: data
  })
}

// 修改心缘馆学生管理
export function updateStudent(data) {
  return request({
    url: '/manage/xinyuanStudent',
    method: 'put',
    data: data
  })
}

// 删除心缘馆学生管理
export function delStudent(studentIds) {
  return request({
    url: '/manage/xinyuanStudent/' + studentIds,
    method: 'delete'
  })
}

// 导出心缘馆学生管理
export function exportStudent(query) {
  return request({
    url: '/manage/xinyuanStudent/export',
    method: 'post',
    params: query
  })
}