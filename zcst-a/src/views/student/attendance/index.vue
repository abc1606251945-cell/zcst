<template>
  <div class="app-container">
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div class="clearfix" style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: bold;">考勤管理</span>
          <el-tag type="success">{{ currentVenueName || "暂未绑定场馆" }}</el-tag>
        </div>
      </template>
      <div style="display: flex; flex-wrap: wrap; gap: 16px; align-items: center;">
        <el-input :model-value="currentVenueName || '暂未绑定场馆'" style="width: 240px" readonly />
        <el-button type="success" icon="Check" @click="handleCheckIn" :loading="checkInSubmitting" :disabled="isCheckedIn">签到</el-button>
        <el-button type="warning" icon="Close" @click="handleCheckOut" :disabled="!isCheckedIn || checkInSubmitting">签退</el-button>
      </div>
      <div style="margin-top: 16px;">
        <el-alert
          v-if="isCheckedIn"
          :title="`当前状态：已签到（场馆：${currentVenueName}，签到时间：${checkInTimeFormatted}）`"
          type="success"
          show-icon
          :closable="false"
        />
        <el-alert
          v-else
          :title="attendanceHint"
          type="info"
          show-icon
          :closable="false"
        />
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card style="margin-bottom: 20px;">
          <template #header>
            <div class="clearfix" style="display: flex; justify-content: space-between; align-items: center;">
              <span style="font-weight: bold;">个人考勤数据统计</span>
              <div style="display: flex; align-items: center; gap: 15px;">
                <span style="font-size: 14px; color: #606266;">统计时间段：</span>
                <el-date-picker
                  v-model="statDateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  @change="calculateStats"
                  clearable
                />
              </div>
            </div>
          </template>
          <div style="display: flex; justify-content: space-around; text-align: center; padding: 10px 0;">
            <div>
              <div style="font-size: 14px; color: #909399; margin-bottom: 10px;">累计签到次数</div>
              <div style="font-size: 24px; font-weight: bold; color: #409EFF;">{{ totalCheckInCount }} 次</div>
            </div>
            <div>
              <div style="font-size: 14px; color: #909399; margin-bottom: 10px;">累计值班总时长</div>
              <div style="font-size: 24px; font-weight: bold; color: #67C23A;">{{ totalDurationFormatted }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="clearfix" style="display: flex; justify-content: space-between; align-items: center;">
          <span>考勤记录</span>
          <div style="display: flex; align-items: center; gap: 15px;">
            <el-date-picker
              v-model="selectedDate"
              type="date"
              placeholder="选择日期查看考勤"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
                  @change="handleDateChange"
              clearable
            />
            <el-button type="primary" link @click="loadData">
              刷新记录
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="tableData" style="width: 100%" border stripe>
        <el-table-column prop="studentId" label="学号" width="150" align="center" />
        <el-table-column prop="checkInTime" label="签到时间" width="180" align="center" />
        <el-table-column prop="statusLabel" label="状态" width="100" align="center" />
        <el-table-column prop="dutyId" label="值班ID" width="100" align="center" />
        <el-table-column prop="remark" label="备注" min-width="180" align="center" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="StudentAttendance">
import { ref, onMounted, onActivated, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { listVenue } from "@/api/manage/venue"
import { getStudentInfo, updateStudentInfo } from "@/api/manage/student"
import { getCurrentDuty } from "@/api/manage/dutySchedule"
import { checkIn, checkOut, getStudentAttendanceByMonth, getStudentStatisticsByMonth } from "@/api/manage/attendance"
import useUserStore from "@/store/modules/user"

const tableData = ref([])
const allData = ref([])
const selectedDate = ref('')
const statDateRange = ref([])
const venueOptions = ref([])
const selectedVenueId = ref(null)
const lastBoundVenueId = ref(null)
const fallbackVenueName = ref('')
const studentInfo = ref(null)
const activeDuty = ref(null)
const lastCheckInTime = ref('')
const checkInSubmitting = ref(false)
const userStore = useUserStore()
const fallbackVenueOptions = [
  { venueId: 1, venueName: "康一(思齐馆)" },
  { venueId: 2, venueName: "梅一(弘毅馆)" },
  { venueId: 3, venueName: "桂一(心缘馆)" },
  { venueId: 4, venueName: "松三(笃学馆)" },
  { venueId: 5, venueName: "榕五(知行馆)" },
  { venueId: 6, venueName: "竹四(国防教育体验馆)" },
]

// 统计数据
const totalCheckInCount = ref(0)
const totalDurationFormatted = ref('0小时 0分钟')

const currentVenueName = computed(() => {
  const matchedVenue = venueOptions.value.find((item) => item.venueId === selectedVenueId.value)
  return matchedVenue?.venueName || fallbackVenueName.value || ''
})

const isCheckedIn = computed(() => {
  if (!activeDuty.value) {
    return false
  }
  const record = allData.value.find((item) => item.dutyId === activeDuty.value.dutyId)
  if (!record) {
    return false
  }
  return !record.checkOutTime
})

const checkInTimeFormatted = computed(() => {
  return lastCheckInTime.value || ''
})

const canListVenue = computed(() => hasPermi("manage:venue:list"))
const attendanceHint = computed(() => {
  if (!currentVenueName.value) {
    return '请先在个人中心绑定场馆后再进行签到'
  }
  if (activeDuty.value) {
    return `当前已绑定场馆：${currentVenueName.value}，可签到时段：${formatShortTime(activeDuty.value.startTime)}-${formatShortTime(activeDuty.value.endTime)}`
  }
  return `当前已绑定场馆：${currentVenueName.value}，当前没有可签到的值班`
})

function formatShortTime(value) {
  if (!value) {
    return '--'
  }
  return String(value).slice(11, 16)
}

function formatDateTime(value) {
  if (!value) {
    return '--'
  }
  return String(value).replace('T', ' ').slice(0, 19)
}

function formatStatus(status) {
  if (status === '0') return '正常'
  if (status === '1') return '迟到'
  if (status === '2') return '早退'
  if (status === '3') return '缺勤'
  return '未知'
}

function formatHours(hoursValue) {
  const totalMinutes = Math.round(Number(hoursValue || 0) * 60)
  const hours = Math.floor(totalMinutes / 60)
  const minutes = totalMinutes % 60
  return `${hours}小时 ${minutes}分钟`
}

function getDateString(date = new Date()) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function getMonthString(dateValue = new Date()) {
  const date = typeof dateValue === 'string' ? new Date(`${dateValue} 00:00:00`) : dateValue
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  return `${year}-${month}`
}

function getMonthsInRange(startDate, endDate) {
  const months = []
  const cursor = new Date(`${startDate} 00:00:00`)
  const last = new Date(`${endDate} 00:00:00`)
  cursor.setDate(1)
  last.setDate(1)
  while (cursor <= last) {
    months.push(getMonthString(cursor))
    cursor.setMonth(cursor.getMonth() + 1)
  }
  return months
}

function filterRecordsByDateRange(records, startDate, endDate) {
  const start = new Date(`${startDate} 00:00:00`).getTime()
  const end = new Date(`${endDate} 23:59:59`).getTime()
  return records.filter((record) => {
    const time = new Date(record.checkInTime).getTime()
    return time >= start && time <= end
  })
}

function mapAttendanceRecords(records) {
  return (records || [])
    .map((record) => ({
      recordId: record.recordId,
      studentId: record.studentId,
      dutyId: record.dutyId,
      checkInTime: formatDateTime(record.checkInTime),
      checkOutTime: formatDateTime(record.checkOutTime),
      status: record.status,
      statusLabel: formatStatus(record.status),
      remark: record.remark || '--',
    }))
    .sort((a, b) => new Date(b.checkInTime).getTime() - new Date(a.checkInTime).getTime())
}

async function calculateStats() {
  try {
    if (!studentInfo.value?.studentId) {
      totalCheckInCount.value = 0
      totalDurationFormatted.value = '0小时 0分钟'
      return
    }

    if (statDateRange.value && statDateRange.value.length === 2) {
      const months = getMonthsInRange(statDateRange.value[0], statDateRange.value[1])
      const responses = await Promise.all(
        months.map((month) => getStudentAttendanceByMonth(studentInfo.value.studentId, month))
      )
      const records = responses.flatMap((response) => response.data || [])
      const filteredRecords = filterRecordsByDateRange(records, statDateRange.value[0], statDateRange.value[1])
      totalCheckInCount.value = filteredRecords.length
      totalDurationFormatted.value = '后端仅支持按月统计'
      return
    }

    const month = getMonthString(new Date())
    const [recordRes, statRes] = await Promise.all([
      getStudentAttendanceByMonth(studentInfo.value.studentId, month),
      getStudentStatisticsByMonth(studentInfo.value.studentId, month),
    ])
    totalCheckInCount.value = (recordRes.data || []).length
    totalDurationFormatted.value = statRes.data ? formatHours(statRes.data.totalDutyHours) : '0小时 0分钟'
  } catch (error) {
    totalCheckInCount.value = 0
    totalDurationFormatted.value = '0小时 0分钟'
  }
}

const loadData = async () => {
  try {
    if (!studentInfo.value?.studentId) {
      allData.value = []
      tableData.value = []
      return
    }
    const month = getMonthString(selectedDate.value || new Date())
    const response = await getStudentAttendanceByMonth(studentInfo.value.studentId, month)
    allData.value = mapAttendanceRecords(response.data || [])
    filterByDate()
    if (activeDuty.value) {
      const currentRecord = allData.value.find((item) => item.dutyId === activeDuty.value.dutyId)
      lastCheckInTime.value = currentRecord?.checkInTime || ''
    }
  } catch (error) {
    allData.value = []
    tableData.value = []
    lastCheckInTime.value = ''
  }
}

const loadStudentVenue = async () => {
  try {
    let venueRows = fallbackVenueOptions
    if (canListVenue.value) {
      const venueRes = await listVenue()
      venueRows = venueRes.rows || fallbackVenueOptions
    }
    const studentRes = await getStudentInfo()
    venueOptions.value = venueRows
    if (studentRes.code === 200 && studentRes.data) {
      const profileVenueId = Number(userStore.venueId || "") || null
      const cachedVenueId = Number(localStorage.getItem("profile_selected_venue_id") || "") || null
      const studentVenueId = Number(studentRes.data.venueId || "") || null
      const finalVenueId = profileVenueId || cachedVenueId || studentVenueId
      studentInfo.value = studentRes.data
      selectedVenueId.value = finalVenueId
      lastBoundVenueId.value = finalVenueId
      fallbackVenueName.value = studentRes.data.venueName || ''
      const targetVenueId = profileVenueId || cachedVenueId
      if (targetVenueId && studentVenueId !== targetVenueId) {
        try {
          const syncRes = await updateStudentInfo({ venueId: targetVenueId })
          if (syncRes.code === 200) {
            studentInfo.value.venueId = targetVenueId
            userStore.venueId = targetVenueId
            localStorage.setItem("profile_selected_venue_id", String(targetVenueId))
          }
        } catch (error) {
        }
      }
    }
  } catch (error) {
    venueOptions.value = []
    selectedVenueId.value = null
    lastBoundVenueId.value = null
    studentInfo.value = null
  }
}

const refreshActiveDuty = async () => {
  if (!studentInfo.value?.studentId || !selectedVenueId.value) {
    activeDuty.value = null
    lastCheckInTime.value = ''
    return
  }
  try {
    const response = await getCurrentDuty(studentInfo.value.studentId)
    const duties = (response.data || []).filter((item) => Number(item.venueId) === Number(selectedVenueId.value))
    activeDuty.value = duties[0] || null
    if (!activeDuty.value) {
      lastCheckInTime.value = ''
      return
    }
    const currentRecord = allData.value.find((item) => item.dutyId === activeDuty.value.dutyId)
    lastCheckInTime.value = currentRecord?.checkInTime || ''
  } catch (error) {
    activeDuty.value = null
    lastCheckInTime.value = ''
  }
}

const handleCheckIn = () => {
  doCheckIn()
}

const doCheckIn = async () => {
  if (!studentInfo.value?.studentId) {
    ElMessage.warning('未获取到当前学生信息')
    return
  }
  if (!selectedVenueId.value || !currentVenueName.value) {
    ElMessage.warning('请先选择并绑定场馆后再签到')
    return
  }
  checkInSubmitting.value = true
  try {
    await refreshActiveDuty()
    if (!activeDuty.value) {
      ElMessage.warning('当前没有可签到的值班，请在值班时间内再尝试')
      return
    }
    if (isCheckedIn.value) {
      ElMessage.success('当前时段已完成签到')
      return
    }
    const response = await checkIn(studentInfo.value.studentId, activeDuty.value.dutyId)
    if (response.code !== 200) {
      ElMessage.error(response.msg || '签到失败')
      return
    }
    await loadData()
    await calculateStats()
    await refreshActiveDuty()
    ElMessage.success('已通过后端接口完成签到')
  } catch (error) {
    ElMessage.error('签到失败，请稍后重试')
  } finally {
    checkInSubmitting.value = false
  }
}

const handleCheckOut = () => {
  doCheckOut()
}

const doCheckOut = async () => {
  if (!activeDuty.value) {
    ElMessage.warning("当前没有可签退的值班")
    return
  }
  const record = allData.value.find((item) => item.dutyId === activeDuty.value.dutyId)
  if (!record?.recordId) {
    ElMessage.warning("未找到当前值班对应的考勤记录，请先签到")
    return
  }
  if (record.checkOutTime) {
    ElMessage.success("当前时段已完成签退")
    return
  }
  checkInSubmitting.value = true
  try {
    const response = await checkOut(record.recordId, activeDuty.value.dutyId)
    if (response.code !== 200) {
      ElMessage.error(response.msg || "签退失败")
      return
    }
    await loadData()
    await calculateStats()
    await refreshActiveDuty()
    ElMessage.success("已通过后端接口完成签退")
  } catch (error) {
    ElMessage.error("签退失败，请稍后重试")
  } finally {
    checkInSubmitting.value = false
  }
}

const filterByDate = () => {
  if (!selectedDate.value) {
    tableData.value = allData.value
    return
  }

  tableData.value = allData.value.filter(record => {
    const checkInDate = new Date(record.checkInTime)
    const year = checkInDate.getFullYear()
    const month = String(checkInDate.getMonth() + 1).padStart(2, '0')
    const day = String(checkInDate.getDate()).padStart(2, '0')
    const recordDateStr = `${year}-${month}-${day}`
    return recordDateStr === selectedDate.value
  })
}

const handleDateChange = async () => {
  try {
    await loadData()
  } catch (error) {
  }
}

function hasPermi(perm) {
  const permissions = userStore.permissions || []
  return permissions.includes("*:*:*") || permissions.includes(perm)
}

const initializeAttendancePage = async () => {
  await loadStudentVenue()
  await loadData()
  await calculateStats()
  await refreshActiveDuty()
}

onMounted(async () => {
  try {
    await initializeAttendancePage()
  } catch (error) {
  }
})

onActivated(async () => {
  try {
    await initializeAttendancePage()
  } catch (error) {
  }
})

watch(() => userStore.venueId, async (venueId) => {
  const nextVenueId = Number(venueId || "") || null
  if (!nextVenueId || nextVenueId === selectedVenueId.value) {
    return
  }
  selectedVenueId.value = nextVenueId
  lastBoundVenueId.value = nextVenueId
  try {
    await updateStudentInfo({ venueId: nextVenueId })
  } catch (error) {
  }
  await refreshActiveDuty()
  await loadData()
  await calculateStats()
})
</script>
