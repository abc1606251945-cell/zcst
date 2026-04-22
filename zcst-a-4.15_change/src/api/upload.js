import request from "@/utils/request"

export function uploadSchedule(file, studentId) {
  const formData = new FormData()
  formData.append("file", file)
  if (studentId) {
    formData.append("studentId", studentId)
  }
  return request({
    url: "/upload/schedule/analyze",
    method: "post",
    data: formData,
    timeout: 30000,
    headers: {
      "Content-Type": "multipart/form-data",
      repeatSubmit: false,
    },
  })
}
