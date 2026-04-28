<template>
  <section class="study-room-panel">
    <div class="panel-title">
      <span>选择时间</span>
    </div>

    <div class="schedule-content">
      <div class="print-row">
        <div class="room-selector" role="radiogroup" aria-label="选择自习场地">
          <button
            v-for="room in rooms"
            :key="room"
            class="room-option"
            :class="{ active: selectedRoom === room }"
            type="button"
            role="radio"
            :aria-checked="selectedRoom === room"
            @click="selectRoom(room)"
          >
            {{ room }}
          </button>
        </div>
      </div>

      <div class="week-center">
        <div class="date-range">{{ weekRangeText }}</div>
        <div class="week-toolbar">
          <button class="nav-button" type="button" @click="changeWeek('month', -1)">◀ 上月</button>
          <button class="nav-button" type="button" @click="changeWeek('week', -1)">‹ 上周</button>
          <button class="nav-button current" type="button" @click="goCurrentWeek">本周</button>
          <button class="nav-button" type="button" @click="changeWeek('week', 1)">下周 ›</button>
          <button class="nav-button" type="button" @click="changeWeek('month', 1)">下月 ▶</button>
        </div>
      </div>
    </div>

    <div v-loading="loading" class="schedule-wrap">
      <table class="study-table">
        <thead>
          <tr>
            <th class="time-col">时间段</th>
            <th v-for="day in weekDays" :key="day.date">
              {{ day.headerText }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="slot in timeSlots" :key="slot.key">
            <td class="time-cell">
              <strong>{{ slot.label }}</strong>
              <span>{{ slot.range }}</span>
            </td>
            <td
              v-for="day in weekDays"
              :key="`${day.date}-${slot.key}`"
              class="state-cell"
              :class="{ occupied: isOccupied(day.date, slot.key) }"
              :title="isOccupied(day.date, slot.key) ? `${selectedRoom} 已占用` : ''"
            >
              <span v-if="isOccupied(day.date, slot.key)" class="sr-only">已占用</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue"
import { listStudyRoomOccupancy } from "@/api/manage/studyRoom"

const rooms = ["101", "108"]
const weekNames = ["星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"]
const timeSlots = [
  { label: "上午", range: "08:20-10:00", key: "08:20-10:00" },
  { label: "中午", range: "10:20-12:00", key: "10:20-12:00" },
  { label: "下午", range: "12:00-14:00", key: "12:00-14:00" },
  { label: "下午", range: "14:00-15:40", key: "14:00-15:40" },
  { label: "下午", range: "16:00-17:40", key: "16:00-17:40" },
  { label: "晚上", range: "18:00-22:00", key: "18:00-22:00" },
]

const selectedRoom = ref("101")
const anchorDate = ref(new Date())
const loading = ref(false)
const occupiedMap = ref(new Set())

const weekStart = computed(() => getMonday(anchorDate.value))
const weekDays = computed(() => {
  return Array.from({ length: 7 }, (_, index) => {
    const date = addDays(weekStart.value, index)
    return {
      date: formatDate(date),
      weekday: weekNames[index],
      headerText: `${weekNames[index]}(${date.getMonth() + 1}月${date.getDate()}日)`,
      shortDate: formatShortDate(date),
    }
  })
})
const weekRangeText = computed(() => {
  const start = weekDays.value[0]?.date || ""
  const end = weekDays.value[6]?.date || ""
  return `${start} 至 ${end}`
})

function selectRoom(room) {
  if (selectedRoom.value !== room) {
    selectedRoom.value = room
  }
}

function changeWeek(type, step) {
  anchorDate.value = type === "month"
    ? addMonths(anchorDate.value, step)
    : addDays(anchorDate.value, step * 7)
}

function goCurrentWeek() {
  anchorDate.value = new Date()
}

function isOccupied(date, slotKey) {
  return occupiedMap.value.has(`${date}|${slotKey}`)
}

async function loadOccupancy() {
  loading.value = true
  try {
    const response = await listStudyRoomOccupancy({
      CJID: selectedRoom.value,
      startDate: weekDays.value[0].date,
      endDate: weekDays.value[6].date,
    })
    const rows = normalizeRows(response)
    occupiedMap.value = buildOccupiedMap(rows)
  } catch {
    occupiedMap.value = new Set()
  } finally {
    loading.value = false
  }
}

function normalizeRows(response) {
  if (Array.isArray(response)) {
    return response
  }
  if (Array.isArray(response?.data)) {
    return response.data
  }
  if (Array.isArray(response?.rows)) {
    return response.rows
  }
  return []
}

function buildOccupiedMap(rows) {
  const map = new Set()
  rows
    .filter((item) => String(item?.CJID ?? item?.cjid ?? "") === selectedRoom.value)
    .forEach((item) => {
      const parsed = parseUsageTime(item.SYJSSJ || item.syjssj)
      if (parsed) {
        map.add(`${parsed.date}|${parsed.slotKey}`)
      }
    })
  return map
}

function parseUsageTime(value) {
  const text = String(value || "").trim()
  const matched = text.match(/^(\d{4}-\d{2}-\d{2})\s+(\d{2}:\d{2})至\d{4}-\d{2}-\d{2}\s+(\d{2}:\d{2})$/)
  if (!matched) {
    return null
  }
  const slotKey = `${matched[2]}-${matched[3]}`
  const hasSlot = timeSlots.some((slot) => slot.key === slotKey)
  return hasSlot ? { date: matched[1], slotKey } : null
}

function getMonday(date) {
  const copied = new Date(date)
  copied.setHours(0, 0, 0, 0)
  const day = copied.getDay() || 7
  return addDays(copied, 1 - day)
}

function addDays(date, days) {
  const copied = new Date(date)
  copied.setDate(copied.getDate() + days)
  return copied
}

function addMonths(date, months) {
  const copied = new Date(date)
  copied.setMonth(copied.getMonth() + months)
  return copied
}

function formatDate(date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, "0")
  const day = `${date.getDate()}`.padStart(2, "0")
  return `${year}-${month}-${day}`
}

function formatShortDate(date) {
  const month = `${date.getMonth() + 1}`.padStart(2, "0")
  const day = `${date.getDate()}`.padStart(2, "0")
  return `${month}-${day}`
}

watch([selectedRoom, weekRangeText], loadOccupancy)
onMounted(loadOccupancy)
</script>

<style scoped lang="scss">
.study-room-panel {
  overflow: hidden;
  border: 1px solid #dcdfe6;
  border-radius: 2px;
  background: #fff;
  height: calc(100vh - 124px);
  display: flex;
  flex-direction: column;
}

.panel-title {
  height: 44px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e5e5e5;
  background: #fafafa;
  color: #303133;
  font-size: 18px;
  font-weight: 500;
  flex: 0 0 auto;
}

.schedule-content {
  padding: 12px 14px 14px;
  flex: 0 0 auto;
}

.print-row {
  min-height: 36px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.room-selector {
  display: inline-flex;
  gap: 10px;
  align-items: center;
}

.room-option,
.nav-button {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  line-height: 1;
  transition: background-color 0.16s ease, border-color 0.16s ease, color 0.16s ease, box-shadow 0.16s ease;
}

.room-option {
  min-width: 66px;
  height: 34px;
  padding: 0 14px;
  background: #fff;
  color: #303133;

  &:hover {
    border-color: #1f5f5b;
    color: #1f5f5b;
  }

  &.active {
    border-color: #1f5f5b;
    background: #1f5f5b;
    color: #fff;
    box-shadow: 0 4px 12px rgba(31, 95, 91, 0.22);
  }
}

.week-center {
  margin-top: 10px;
  text-align: center;
}

.date-range {
  margin-bottom: 12px;
  color: #303133;
  font-size: 21px;
  font-weight: 800;
}

.week-toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
}

.nav-button {
  min-width: 68px;
  height: 34px;
  padding: 0 12px;
  background: #f7f7f7;
  color: #4a4a4a;
  font-size: 15px;

  &:hover {
    border-color: #14b8d4;
    color: #14b8d4;
  }

  &.current {
    min-width: 108px;
    border-color: #12b8d7;
    background: #12b8d7;
    color: #fff;
  }
}

.schedule-wrap {
  overflow-x: auto;
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  background: #fff;
  flex: 1 1 auto;
  min-height: 0;
}

.study-table {
  width: 100%;
  min-width: 0;
  height: 100%;
  border-collapse: collapse;
  table-layout: fixed;

  th,
  td {
    border-right: 1px solid #dcdfe6;
    border-bottom: 1px solid #dcdfe6;
  }

  th {
    height: 48px;
    padding: 0 6px;
    background: #fff;
    color: #2f3033;
    font-size: 16px;
    font-weight: 800;
  }

  tr:last-child td {
    border-bottom: 0;
  }

  th:last-child,
  td:last-child {
    border-right: 0;
  }
}

.time-col {
  width: 118px;
}

.time-cell {
  height: 58px;
  padding: 0 8px;
  background: #fff;
  color: #2f3033;
  text-align: center;

  strong,
  span {
    display: block;
  }

  strong {
    margin-bottom: 4px;
    font-size: 17px;
    font-weight: 500;
  }

  span {
    color: #2f3033;
    font-size: 16px;
  }
}

.state-cell {
  height: 58px;
  min-width: 0;
  text-align: center;
  background: #fff;
  transition: background-color 0.18s ease;

  &.occupied {
    background: #f56c6c;
  }
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

@media (max-width: 768px) {
  .panel-title {
    padding: 0 16px;
  }

  .schedule-content {
    padding: 18px 12px;
  }

  .print-row {
    flex-direction: column;
  }

  .week-toolbar {
    gap: 10px;
    flex-wrap: wrap;
  }

  .date-range {
    font-size: 18px;
  }
}

@media print {
  .room-selector,
  .week-toolbar {
    display: none;
  }

  .study-room-panel {
    border: 0;
  }

  .schedule-content {
    min-height: auto;
    padding: 16px 0;
  }
}
</style>
