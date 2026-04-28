import request from '@/utils/request'

// 查询国防教育体验馆学生管理列表
export function listStudent(query) {
  return request({
    url: '/manage/guofangStudent/list',
    method: 'get',
    params: query
  })
}

// 查询国防教育体验馆学生管理详细信息
export function getStudent(studentId) {
  return request({
    url: '/manage/guofangStudent/' + studentId,
    method: 'get'
  })
}

// 新增国防教育体验馆学生管理
export function addStudent(data) {
  return request({
    url: '/manage/guofangStudent',
    method: 'post',
    data: data
  })
}

// 修改国防教育体验馆学生管理
export function updateStudent(data) {
  return request({
    url: '/manage/guofangStudent',
    method: 'put',
    data: data
  })
}

// 删除国防教育体验馆学生管理
export function delStudent(studentIds) {
  return request({
    url: '/manage/guofangStudent/' + studentIds,
    method: 'delete'
  })
}

// 导出国防教育体验馆学生管理
export function exportStudent(query) {
  return request({
    url: '/manage/guofangStudent/export',
    method: 'post',
    params: query
  })
}