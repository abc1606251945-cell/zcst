import request from '@/utils/request'

// 查询场馆学生列表
export function listVenueStudent(query) {
  return request({
    url: '/manage/student/list',
    method: 'get',
    params: query
  })
}

// 查询学生详细
export function getVenueStudent(studentId) {
  return request({
    url: '/manage/student/' + studentId,
    method: 'get'
  })
}

// 新增学生
export function