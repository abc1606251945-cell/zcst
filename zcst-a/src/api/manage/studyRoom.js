import axios from "axios"
import { getToken } from "@/utils/auth"

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 10000,
})

service.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 查询自习场地占用情况
export function listStudyRoomOccupancy(params) {
  return service({
    url: "/manage/studyRoom/occupancy",
    method: "get",
    params,
  }).then((response) => response.data)
}
