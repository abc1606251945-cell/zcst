import request from '@/utils/request'

// 查询弘毅馆学生管理列表
export function listHongyiStudent(query) {
  return request({
    url: '/manage/hongyiStudent/list',
    method: 'get',
    params: query
  })
}

// 查询弘毅馆学生管理详细
export function getHongyiStudent(studentId) {
  return request({
    url: '/manage/hongyiStudent/' + studentId,
    method: 'get'
  })
}

// 新增弘毅馆学生管理
export function addHongyiStudent(data) {
  return request({
    url: '/manage/hongyiStudent',
    method: 'post',
    data: data
  })
}

// 修改弘毅馆学生管理
export function updateHongyiStudent(data) {
  return request({
    url: '/manage/hongyiStudent',
    method: 'put',
    data: data
  })
}

// 删除弘毅馆学生管理
export function delHongyiStudent(studentIds) {
  const ids = Array.isArray(studentIds) ? studentIds.join(",") : studentIds
  return request({
    url: '/manage/hongyiStudent/' + ids,
    method: 'delete'
  })
}
