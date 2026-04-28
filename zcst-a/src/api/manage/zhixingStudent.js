import request from '@/utils/request'

// 查询知行馆学生管理列表
export function listStudent(query) {
  return request({
    url: '/manage/zhixingStudent/list',
    method: 'get',
    params: query
  })
}

// 查询知行馆学生管理详细信息
export function getStudent(studentId) {
  return request({
    url: '/manage/zhixingStudent/' + studentId,
    method: 'get'
  })
}

// 新增知行馆学生管理
export function addStudent(data) {
  return request({
    url: '/manage/zhixingStudent',
    method: 'post',
    data: data
  })
}

// 修改知行馆学生管理
export function updateStudent(data) {
  return request({
    url: '/manage/zhixingStudent',
    method: 'put',
    data: data
  })
}

// 删除知行馆学生管理
export function delStudent(studentIds) {
  return request({
    url: '/manage/zhixingStudent/' + studentIds,
    method: 'delete'
  })
}

// 导出知行馆学生管理
export function exportStudent(query) {
  return request({
    url: '/manage/zhixingStudent/export',
    method: 'post',
    params: query
  })
}