import request from '@/utils/request'

// 查询笃学馆学生管理列表
export function listStudent(query) {
  return request({
    url: '/manage/duxueStudent/list',
    method: 'get',
    params: query
  })
}

// 查询笃学馆学生管理详细信息
export function getStudent(studentId) {
  return request({
    url: '/manage/duxueStudent/' + studentId,
    method: 'get'
  })
}

// 新增笃学馆学生管理
export function addStudent(data) {
  return request({
    url: '/manage/duxueStudent',
    method: 'post',
    data: data
  })
}

// 修改笃学馆学生管理
export function updateStudent(data) {
  return request({
    url: '/manage/duxueStudent',
    method: 'put',
    data: data
  })
}

// 删除笃学馆学生管理
export function delStudent(studentIds) {
  return request({
    url: '/manage/duxueStudent/' + studentIds,
    method: 'delete'
  })
}

// 导出笃学馆学生管理
export function exportStudent(query) {
  return request({
    url: '/manage/duxueStudent/export',
    method: 'post',
    params: query
  })
}