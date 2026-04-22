import request from '@/utils/request'

// 查询学生管理列表
export function listStudent(query) {
  return request({
    url: '/manage/student/list',
    method: 'get',
    params: query
  })
}

// 查询学生管理详细
export function getStudent(studentId) {
  return request({
    url: '/manage/student/' + studentId,
    method: 'get'
  })
}

// 新增学生管理
export function addStudent(data) {
  return request({
    url: '/manage/student',
    method: 'post',
    data: data
  })
}

// 修改学生管理
export function updateStudent(data) {
  return request({
    url: '/manage/student',
    method: 'put',
    data: data
  })
}

// 删除学生管理
export function delStudent(studentIds) {
  const ids = Array.isArray(studentIds) ? studentIds.join(",") : studentIds
  return request({
    url: '/manage/student/' + ids,
    method: 'delete'
  })
}

// 获取当前学生信息
export function getStudentInfo() {
  return request({
    url: '/manage/student/info',
    method: 'get'
  })
}

// 更新学生信息
export function updateStudentInfo(data) {
  return request({
    url: '/manage/student/info',
    method: 'put',
    data: data,
    timeout: 20000,
    headers: {
      repeatSubmit: false
    }
  })
}
