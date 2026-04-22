import request from "@/utils/request"

export function listMyWeekSchedule(params) {
  return request({
    url: "/manage/schedule/my/week",
    method: "get",
    params,
  })
}

