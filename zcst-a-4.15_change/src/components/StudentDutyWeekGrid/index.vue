<template>
  <div v-if="embedded" class="grid-panel">
    <div class="card-header">
      <div class="header-left">
        <span class="title">{{ title }}</span>
        <span v-if="rangeLabel" class="range">{{ rangeLabel }}</span>
      </div>
      <div class="header-actions">
        <el-button-group>
          <el-button @click="shiftWeek(-1)">上一周</el-button>
          <el-button @click="resetWeek">本周</el-button>
          <el-button @click="shiftWeek(1)">下一周</el-button>
        </el-button-group>
        <el-date-picker
          v-model="anchorDate"
          type="date"
          format="YYYY-MM-DD"
          placeholder="选择日期（定位到周）"
          @change="refresh"
        />
      </div>
    </div>
    <div class="schedule-grid-container" v-loading="loading">
      <div v-if="!displaySlots.length" class="empty-tip">
        <div class="empty-title">本周暂无值班安排</div>
        <div class="empty-subtitle">如需查看其它周，请切换周次</div>
      </div>
      <table v-else class="schedule-table">
        <thead>
          <tr>
            <th class="time-column">时间 / 星期</th>
            <th v-for="day in weekDays" :key="day.dateStr" class="day-column">
              <div class="day-label">{{ day.label }}</div>
              <div class="day-date">{{ day.dateStr }}</div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(slot, slotIndex) in displaySlots" :key="slot.key">
            <td class="time-slot-label">{{ slot.startTime }}</td>
            <template v-for="day in weekDays" :key="day.dateStr">
              <td
                v-if="getCellMeta(day.dateStr, slotIndex).render"
                :rowspan="getCellMeta(day.dateStr, slotIndex).rowspan"
                class="schedule-cell"
              >
                <div v-if="getCellMeta(day.dateStr, slotIndex).items.length" class="event-layer">
                  <div
                    v-for="(item, index) in getCellMeta(day.dateStr, slotIndex).items"
                    :key="item.dutyId || `${day.dateStr}-${slot.key}-${index}`"
                    :class="['event-item', item.type]"
                    :style="getEventStyle(item, slotIndex, index)"
                  >
                    <div class="event-title">{{ item.title }}</div>
                    <div class="event-location">
                      {{ item.location }}<span v-if="item.timeLabel"> · {{ item.timeLabel }}</span>
                    </div>
                  </div>
                </div>
                <div v-else class="no-duty">-</div>
              </td>
            </template>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
  <el-card v-else>
    <template #header>
      <div class="card-header">
        <div class="header-left">
          <span class="title">{{ title }}</span>
          <span v-if="rangeLabel" class="range">{{ rangeLabel }}</span>
        </div>
        <div class="header-actions">
          <el-button-group>
            <el-button @click="shiftWeek(-1)">上一周</el-button>
            <el-button @click="resetWeek">本周</el-button>
            <el-button @click="shiftWeek(1)">下一周</el-button>
          </el-button-group>
          <el-date-picker
            v-model="anchorDate"
            type="date"
            format="YYYY-MM-DD"
            placeholder="选择日期（定位到周）"
            @change="refresh"
          />
        </div>
      </div>
    </template>
    <div class="schedule-grid-container" v-loading="loading">
      <div v-if="!displaySlots.length" class="empty-tip">
        <div class="empty-title">本周暂无值班安排</div>
        <div class="empty-subtitle">如需查看其它周，请切换周次</div>
      </div>
      <table v-else class="schedule-table">
        <thead>
          <tr>
            <th class="time-column">时间 / 星期</th>
            <th v-for="day in weekDays" :key="day.dateStr" class="day-column">
              <div class="day-label">{{ day.label }}</div>
              <div class="day-date">{{ day.dateStr }}</div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(slot, slotIndex) in displaySlots" :key="slot.key">
            <td class="time-slot-label">{{ slot.startTime }}</td>
            <template v-for="day in weekDays" :key="day.dateStr">
              <td
                v-if="getCellMeta(day.dateStr, slotIndex).render"
                :rowspan="getCellMeta(day.dateStr, slotIndex).rowspan"
                class="schedule-cell"
              >
                <div v-if="getCellMeta(day.dateStr, slotIndex).items.length" class="event-layer">
                  <div
                    v-for="(item, index) in getCellMeta(day.dateStr, slotIndex).items"
                    :key="item.dutyId || `${day.dateStr}-${slot.key}-${index}`"
                    :class="['event-item', item.type]"
                    :style="getEventStyle(item, slotIndex, index)"
                  >
                    <div class="event-title">{{ item.title }}</div>
                    <div class="event-location">
                      {{ item.location }}<span v-if="item.timeLabel"> · {{ item.timeLabel }}</span>
                    </div>
                  </div>
                </div>
                <div v-else class="no-duty">-</div>
              </td>
            </template>
          </tr>
        </tbody>
      </table>
    </div>
  </el-card>
</template>

<script setup name="StudentDutyWeekGrid">
import { computed, onMounted, ref, watch } from "vue"
import { listMyDutySchedule } from "@/api/manage/dutySchedule"
import { listMyWeekSchedule } from "@/api/manage/studentSchedule"
import { listVenueSimple } from "@/api/manage/venue"
import { parseTime } from "@/utils/ruoyi"

const props = defineProps({
  title: {
    type: String,
    default: "我的值班表",
  },
  embedded: {
    type: Boolean,
    default: false,
  },
  includeSchedule: {
    type: Boolean,
    default: false,
  },
  scheduleRows: {
    type: Array,
    default: () => [],
  },
  useDemoDuty: {
    type: Boolean,
    default: false,
  },
})

const loading = ref(false)
const anchorDate = ref(new Date())
const venuesById = ref({})
const dutyItems = ref([])
const weekScheduleItems = ref([])
const HOUR_START = 6
const HOUR_END = 23
const ROW_HEIGHT = 72

const weekdayLabels = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"]
const weekdayKeys = ["mon", "tue", "wed", "thu", "fri", "sat", "sun"]

function pad2(value) {
  return String(value).padStart(2, "0")
}

function formatYmd(date) {
  const d = new Date(date)
  return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`
}

function formatHm(date) {
  const hm = parseTime(date, "{h}:{i}")
  return hm || ""
}

function getWeekStart(date) {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - day + (day === 0 ? -6 : 1)
  const start = new Date(d.setDate(diff))
  start.setHours(0, 0, 0, 0)
  return start
}

function addDays(date, days) {
  const d = new Date(date)
  d.setDate(d.getDate() + days)
  return d
}

const weekStart = computed(() => getWeekStart(anchorDate.value))
const weekEnd = computed(() => {
  const end = addDays(weekStart.value, 6)
  end.setHours(23, 59, 59, 999)
  return end
})

const rangeLabel = computed(() => `${formatYmd(weekStart.value)} ~ ${formatYmd(weekEnd.value)}`)

const weekDays = computed(() => {
  const days = []
  for (let i = 0; i < 7; i++) {
    const d = addDays(weekStart.value, i)
    days.push({ label: weekdayLabels[i], dateStr: formatYmd(d) })
  }
  return days
})

function toMinutes(hm) {
  const [h, m] = String(hm || "").split(":")
  const hh = Number(h)
  const mm = Number(m)
  if (Number.isNaN(hh) || Number.isNaN(mm)) {
    return -1
  }
  return hh * 60 + mm
}

function toHmByMinutes(total) {
  const h = Math.floor(total / 60)
  const m = total % 60
  return `${pad2(h)}:${pad2(m)}`
}

function buildTimeLabel(startMin, endMin) {
  if (startMin < 0 || endMin <= startMin) {
    return ""
  }
  return `${toHmByMinutes(startMin)}-${toHmByMinutes(endMin)}`
}

function getEventStyle(item, slotIndex, index) {
  const slotStart = (HOUR_START + slotIndex) * 60
  const topOffset = Math.max(0, item.startMin - slotStart)
  const rawDuration = Math.max(30, item.endMin - item.startMin)
  const extendedDuration = item.endMin % 60 === 0 ? rawDuration + 60 : rawDuration
  const top = (topOffset / 60) * ROW_HEIGHT + 3 + index * 4
  const height = (extendedDuration / 60) * ROW_HEIGHT - 6
  return {
    top: `${top}px`,
    height: `${Math.max(24, height)}px`,
  }
}

function normalizeSlotFromPeriod(period) {
  const normalized = String(period || "").replace(/\s/g, "")
  if (!normalized.includes("-")) {
    return null
  }
  const [startRaw, endRaw] = normalized.split("-")
  const start = String(startRaw || "").slice(0, 5)
  const end = String(endRaw || "").slice(0, 5)
  const startMin = toMinutes(start)
  const endMin = toMinutes(end)
  if (!start || !end || startMin < 0 || endMin <= startMin) {
    return null
  }
  return { startTime: start, endTime: end, startMin, endMin, key: `${start}-${end}` }
}

function createHourlySlots(startHour = HOUR_START, endHour = HOUR_END) {
  const slots = []
  for (let h = startHour; h < endHour; h++) {
    const start = `${pad2(h)}:00`
    const end = `${pad2(h + 1)}:00`
    const startMin = h * 60
    const endMin = (h + 1) * 60
    slots.push({ key: `${start}-${end}`, startTime: start, endTime: end, startMin, endMin })
  }
  return slots
}

const weekDayMap = computed(() => ({
  mon: weekDays.value[0]?.dateStr || "",
  tue: weekDays.value[1]?.dateStr || "",
  wed: weekDays.value[2]?.dateStr || "",
  thu: weekDays.value[3]?.dateStr || "",
  fri: weekDays.value[4]?.dateStr || "",
  sat: weekDays.value[5]?.dateStr || "",
  sun: weekDays.value[6]?.dateStr || "",
}))

const displaySlots = computed(() => createHourlySlots(HOUR_START, HOUR_END))

async function loadVenues() {
  try {
    const res = await listVenueSimple()
    const map = {}
    for (const v of res?.data || []) {
      map[v.venueId] = v.venueName
    }
    venuesById.value = map
  } catch {
    venuesById.value = {}
  }
}

function normalizeDutyList(list) {
  const normalized = []
  for (const raw of list || []) {
    const dateStr = parseTime(raw.startTime, "{y}-{m}-{d}") || ""
    const startHm = formatHm(raw.startTime)
    const endHm = formatHm(raw.endTime)
    const startMin = toMinutes(startHm)
    const endMin = toMinutes(endHm)
    if (!dateStr || startMin < 0 || endMin <= startMin) {
      continue
    }
    const location = venuesById.value[raw.venueId] || `场馆 ${raw.venueId || ""}`
    normalized.push({
      dutyId: raw.dutyId,
      type: "duty",
      title: "值班",
      location,
      timeLabel: buildTimeLabel(startMin, endMin),
      dateStr,
      startMin,
      endMin,
    })
  }
  return normalized
}

function normalizeWeekSchedule(list) {
  const normalized = []
  for (const raw of list || []) {
    const dateStr = parseTime(raw.startTime, "{y}-{m}-{d}") || ""
    const startHm = formatHm(raw.startTime)
    const endHm = formatHm(raw.endTime)
    const startMin = toMinutes(startHm)
    const endMin = toMinutes(endHm)
    if (!dateStr || startMin < 0 || endMin <= startMin) {
      continue
    }
    const title = String(raw.courseName || "").trim()
    if (!title) {
      continue
    }
    const location = String(raw.location || "").trim() || "地点待定"
    normalized.push({
      dutyId: `course-${raw.scheduleId || ""}-${title}-${startMin}`,
      type: "course",
      title,
      location,
      timeLabel: buildTimeLabel(startMin, endMin),
      dateStr,
      startMin,
      endMin,
    })
  }
  return normalized
}

function buildDemoDutyItems() {
  const day2 = formatYmd(addDays(weekStart.value, 1))
  const day4 = formatYmd(addDays(weekStart.value, 3))
  return [
    {
      dutyId: "demo-duty-1",
      type: "duty",
      title: "值班",
      location: "图书馆一楼服务台",
      timeLabel: "11:00-12:00",
      dateStr: day2,
      startMin: 11 * 60,
      endMin: 12 * 60,
    },
    {
      dutyId: "demo-duty-2",
      type: "duty",
      title: "值班",
      location: "信息楼 B105",
      timeLabel: "18:00-20:00",
      dateStr: day4,
      startMin: 18 * 60,
      endMin: 20 * 60,
    },
  ]
}

function parseScheduleCell(value) {
  if (value === undefined || value === null || value === "") {
    return null
  }
  if (typeof value === "object") {
    const title = String(
      value.courseName || value.title || value.name || value.course || value.text || "",
    ).trim()
    const location = String(
      value.location || value.place || value.classroom || value.room || value.venueName || "",
    ).trim()
    if (!title) {
      return null
    }
    return { title, location: location || "地点待定" }
  }
  const text = String(value).trim()
  if (!text) {
    return null
  }
  const lines = text
    .split(/\r?\n|\|/)
    .map((item) => item.trim())
    .filter(Boolean)
  const title = lines[0] || text
  const location = lines.slice(1).join(" ") || "地点待定"
  return { title, location }
}

const scheduleItems = computed(() => {
  if (!props.includeSchedule) {
    return []
  }
  const list = []
  for (const row of props.scheduleRows || []) {
    const slot = normalizeSlotFromPeriod(row?.time)
    if (!slot) {
      continue
    }
    for (const dayKey of weekdayKeys) {
      const dateStr = weekDayMap.value[dayKey]
      if (!dateStr) {
        continue
      }
      const parsed = parseScheduleCell(row?.[dayKey])
      if (!parsed) {
        continue
      }
      list.push({
        dutyId: `course-${dayKey}-${slot.key}-${parsed.title}`,
        type: "course",
        title: parsed.title,
        location: parsed.location,
        timeLabel: `${slot.startTime}-${slot.endTime}`,
        dateStr,
        startMin: slot.startMin,
        endMin: slot.endMin,
      })
    }
  }
  return list
})

const allItems = computed(() =>
  [...scheduleItems.value, ...(props.includeSchedule ? weekScheduleItems.value : []), ...dutyItems.value].sort(
    (a, b) => a.startMin - b.startMin,
  ),
)

const dayCellMeta = computed(() => {
  const result = new Map()
  for (const day of weekDays.value) {
    const metas = displaySlots.value.map(() => ({
      render: true,
      rowspan: 1,
      items: [],
    }))
    const dayItems = allItems.value.filter((item) => item.dateStr === day.dateStr)
    dayItems.forEach((item) => {
      let startIndex = Math.floor((item.startMin - HOUR_START * 60) / 60)
      let endIndex = Math.ceil((item.endMin - HOUR_START * 60) / 60)
      if (item.endMin % 60 === 0) {
        endIndex += 1
      }
      if (startIndex < 0) {
        startIndex = 0
      }
      if (endIndex > metas.length) {
        endIndex = metas.length
      }
      if (startIndex >= metas.length || endIndex <= startIndex) {
        return
      }
      const span = Math.max(1, endIndex - startIndex)
      if (!metas[startIndex].render) {
        return
      }
      metas[startIndex].rowspan = span
      metas[startIndex].items.push(item)
      for (let i = startIndex + 1; i < startIndex + span && i < metas.length; i++) {
        metas[i].render = false
      }
    })
    result.set(day.dateStr, metas)
  }
  return result
})

function getCellMeta(dateStr, slotIndex) {
  const dayMeta = dayCellMeta.value.get(dateStr) || []
  return dayMeta[slotIndex] || { render: true, rowspan: 1, items: [] }
}

async function refresh() {
  loading.value = true
  try {
    await loadVenues()
    const params = { from: formatYmd(weekStart.value), to: formatYmd(weekEnd.value) }
    const [dutyRes, scheduleRes] = await Promise.all([
      listMyDutySchedule(params).catch(() => ({ data: [] })),
      props.includeSchedule ? listMyWeekSchedule(params).catch(() => ({ data: [] })) : Promise.resolve({ data: [] }),
    ])
    const normalizedDuties = normalizeDutyList(dutyRes?.data || [])
    dutyItems.value = normalizedDuties.length ? normalizedDuties : props.useDemoDuty ? buildDemoDutyItems() : []
    weekScheduleItems.value = props.includeSchedule ? normalizeWeekSchedule(scheduleRes?.data || []) : []
  } finally {
    loading.value = false
  }
}

function shiftWeek(delta) {
  anchorDate.value = addDays(anchorDate.value, delta * 7)
  refresh()
}

function resetWeek() {
  anchorDate.value = new Date()
  refresh()
}

onMounted(() => {
  refresh()
})

watch(
  () => props.useDemoDuty,
  () => {
    refresh()
  },
)

watch(
  () => props.includeSchedule,
  () => {
    refresh()
  },
)

defineExpose({ refresh })
</script>

<style scoped lang="scss">
.grid-panel {
  width: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.header-left {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.title {
  font-weight: 700;
  font-size: 16px;
}

.range {
  color: #909399;
  font-size: 12px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.schedule-grid-container {
  overflow-x: auto;
  margin-top: 18px;
}

.empty-tip {
  padding: 48px 0;
  text-align: center;
}

.empty-title {
  font-size: 14px;
  color: #303133;
  font-weight: 600;
  margin-bottom: 6px;
}

.empty-subtitle {
  font-size: 12px;
  color: #909399;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 980px;
}

.schedule-table th,
.schedule-table td {
  border: 1px solid #ebeef5;
}

.time-column {
  width: 160px;
  background: #fafafa;
}

.day-column {
  text-align: center;
  min-width: 120px;
  background: #fafafa;
}

.day-label {
  font-weight: 600;
}

.day-date {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.time-slot-label {
  padding: 10px 12px;
  background: #fafafa;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
}

.schedule-cell {
  vertical-align: top;
  padding: 8px;
  min-height: 64px;
  position: relative;
}

.no-duty {
  color: #c0c4cc;
  text-align: center;
  padding: 12px 0;
}

.event-layer {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 72px;
}

.event-item {
  position: absolute;
  left: 6px;
  right: 6px;
  padding: 8px 10px;
  border-radius: 8px;
  box-sizing: border-box;
  overflow: hidden;
  border: 1px solid transparent;
}

.event-item.duty {
  background: #ecf5ff;
  border-color: #d9ecff;
}

.event-item.course {
  background: #f0f9eb;
  border-color: #e1f3d8;
}

.event-title {
  font-size: 13px;
  font-weight: 700;
  color: #303133;
  line-height: 18px;
}

.event-location {
  margin-top: 4px;
  font-size: 12px;
  color: #606266;
  line-height: 16px;
  word-break: break-word;
}
</style>
