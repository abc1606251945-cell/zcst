<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>当前时间段无课人员</span>
          <div style="display: flex; gap: 10px; align-items: center">
            <el-date-picker
              v-model="freeDateFilter"
              type="date"
              value-format="YYYY-MM-DD"
              format="YYYY-MM-DD"
              placeholder="选择日期"
              style="width: 140px"
              @change="loadFreeStudents"
            />
            <el-select v-model="selectedPeriodFilter" style="width: 120px">
              <el-option label="1-2节" value="1-2节" />
              <el-option label="3-4节" value="3-4节" />
              <el-option label="5-6节" value="5-6节" />
              <el-option label="7-8节" value="7-8节" />
              <el-option label="9-10节" value="9-10节" />
              <el-option label="11-12节" value="11-12节" />
            </el-select>
            <el-button @click="loadFreeStudents">刷新</el-button>
          </div>
        </div>
      </template>
      <el-alert
        :title="`当前时间：${nowTimeText}，查看日期：${freeDateFilter}，系统判定时段：${currentPeriodText || '非值班时段'}`"
        type="info"
        :closable="false"
        style="margin-bottom: 12px"
      />
      <el-tabs v-model="activeVenueTab">
        <el-tab-pane label="全部场馆" name="all" />
        <el-tab-pane
          v-for="venue in venueOptions"
          :key="venue.value"
          :label="venue.label"
          :name="venue.value"
        />
      </el-tabs>
      <el-table :data="currentFreeStudents" border>
        <el-table-column prop="dutyDate" label="日期" width="120" align="center" />
        <el-table-column prop="venueName" label="场馆" width="180" align="center" />
        <el-table-column prop="studentId" label="学号" width="140" align="center" />
        <el-table-column prop="studentName" label="姓名" width="120" align="center" />
        <el-table-column prop="period" label="无课时段" width="100" align="center" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="StudentDutyFree">
import { ref, computed } from "vue";

const freeStudents = ref([]);
const activeVenueTab = ref("all");
const venueOptions = ref([
  { label: "康一（思齐馆）", value: "康一（思齐馆）" },
  { label: "梅一（弘毅馆）", value: "梅一（弘毅馆）" },
  { label: "桂一（心缘馆）", value: "桂一（心缘馆）" },
  { label: "松三（笃学馆）", value: "松三（笃学馆）" },
  { label: "榕五（知行馆）", value: "榕五（知行馆）" },
  { label: "竹四（国防教育体验馆）", value: "竹四（国防教育体验馆）" },
]);
const currentPeriodText = ref("");
const selectedPeriodFilter = ref("");
const nowTimeText = ref("");
const freeDateFilter = ref("");

function getCurrentPeriod() {
  const now = new Date();
  const hour = now.getHours();
  if (hour >= 8 && hour < 10) return "1-2节";
  if (hour >= 10 && hour < 12) return "3-4节";
  if (hour >= 14 && hour < 16) return "5-6节";
  if (hour >= 16 && hour < 18) return "7-8节";
  if (hour >= 19 && hour < 21) return "9-10节";
  if (hour >= 21 && hour < 23) return "11-12节";
  return "";
}

function formatDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function getDefaultFreeStudents() {
  const today = new Date();
  const tomorrow = new Date(today);
  tomorrow.setDate(today.getDate() + 1);
  const yesterday = new Date(today);
  yesterday.setDate(today.getDate() - 1);
  return [
    { dutyDate: formatDate(today), venueName: "康一（思齐馆）", studentId: "20230011", studentName: "张晨", period: "1-2节" },
    { dutyDate: formatDate(today), venueName: "康一（思齐馆）", studentId: "20230015", studentName: "李航", period: "3-4节" },
    { dutyDate: formatDate(today), venueName: "梅一（弘毅馆）", studentId: "20230021", studentName: "王宁", period: "7-8节" },
    { dutyDate: formatDate(today), venueName: "桂一（心缘馆）", studentId: "20230031", studentName: "赵青", period: "7-8节" },
    { dutyDate: formatDate(tomorrow), venueName: "松三（笃学馆）", studentId: "20230041", studentName: "陈悦", period: "9-10节" },
    { dutyDate: formatDate(yesterday), venueName: "榕五（知行馆）", studentId: "20230051", studentName: "周雨", period: "5-6节" },
    { dutyDate: formatDate(today), venueName: "竹四（国防教育体验馆）", studentId: "20230061", studentName: "吴帆", period: "11-12节" },
  ];
}

function loadFreeStudents() {
  const now = new Date();
  nowTimeText.value = now.toLocaleString();
  if (!freeDateFilter.value) {
    freeDateFilter.value = formatDate(now);
  }
  currentPeriodText.value = getCurrentPeriod();
  if (!selectedPeriodFilter.value) {
    selectedPeriodFilter.value = currentPeriodText.value || "1-2节";
  }
  const raw = localStorage.getItem("venue_free_students");
  freeStudents.value = raw ? JSON.parse(raw) : getDefaultFreeStudents();
}

const currentFreeStudents = computed(() => {
  let list = freeStudents.value;
  if (freeDateFilter.value) {
    list = list.filter((item) => item.dutyDate === freeDateFilter.value);
  }
  if (selectedPeriodFilter.value) {
    list = list.filter((item) => item.period === selectedPeriodFilter.value);
  }
  if (activeVenueTab.value !== "all") {
    list = list.filter((item) => item.venueName === activeVenueTab.value);
  }
  return list;
});

loadFreeStudents();
</script>
