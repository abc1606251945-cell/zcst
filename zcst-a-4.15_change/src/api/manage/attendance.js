import request from '@/utils/request'

export function checkIn(studentId, dutyId) {
  return request({
    url: '/manage/attendance/record/checkIn',
    method: 'post',
    params: { studentId, dutyId }
  })
}

export function checkOut(recordId, dutyId) {
  return request({
    url: '/manage/attendance/record/checkOut',
    method: 'post',
    params: { recordId, dutyId }
  })
}

export function getStudentAttendanceByMonth(studentId, yearMonth) {
  return request({
    url: '/manage/attendance/record/student/month',
    method: 'get',
    params: { studentId, yearMonth }
  })
}

export function getStudentStatisticsByMonth(studentId, yearMonth) {
  return request({
    url: '/manage/attendance/statistics/student/month',
    method: 'get',
    params: { studentId, yearMonth }
  })
}
