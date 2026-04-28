<template>
  <div class="app-container home">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">排班与课表信息</span>
          <div class="actions">
            <el-button
              type="success"
              @click="router.push('/student/attendance/index')"
              >前往考勤管理</el-button
            >
          </div>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="我的值班" name="duty">
          <student-duty-week-grid title="我的值班表" />
        </el-tab-pane>
        <el-tab-pane label="我的课表" name="schedule">
          <div class="schedule-toolbar">
            <el-upload
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :disabled="uploadingSchedule"
              @change="handlePdfUpload"
            >
              <el-button
                type="primary"
                icon="Upload"
                :loading="uploadingSchedule"
                >上传课表照片</el-button
              >
            </el-upload>
          </div>
          <el-alert
            v-if="lastUploadedScheduleUrl"
            style="margin-bottom: 12px"
            type="success"
            :closable="false"
            show-icon
          >
            <template #title>
              课表上传成功：
              <el-link
                :href="lastUploadedScheduleUrl"
                target="_blank"
                type="primary"
                >查看原图</el-link
              >
              <span
                v-if="lastScheduleSyncAt"
                style="margin-left: 12px; color: #606266"
                >最近同步：{{ lastScheduleSyncAt }}</span
              >
            </template>
          </el-alert>
          <el-table
            :data="scheduleData"
            style="width: 100%"
            border
            stripe
            :cell-style="{ height: '64px' }"
          >
            <el-table-column
              prop="time"
              label="课程时间"
              width="150"
              align="center"
              fixed
            />
            <el-table-column
              prop="mon"
              label="星期一"
              min-width="140"
              align="center"
            />
            <el-table-column
              prop="tue"
              label="星期二"
              min-width="140"
              align="center"
            />
            <el-table-column
              prop="wed"
              label="星期三"
              min-width="140"
              align="center"
            />
            <el-table-column
              prop="thu"
              label="星期四"
              min-width="140"
              align="center"
            />
            <el-table-column
              prop="fri"
              label="星期五"
              min-width="140"
              align="center"
            />
            <el-table-column
              prop="sat"
              label="星期六"
              min-width="140"
              align="center"
            />
            <el-table-column
              prop="sun"
              label="星期日"
              min-width="140"
              align="center"
            />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup name="StudentSchedule">
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
import useUserStore from "@/store/modules/user";
import { getToken } from "@/utils/auth";
import { DUTY_PERIODS } from "@/utils/dutyPeriod";
import { parseTime } from "@/utils/ruoyi";
import { uploadSchedule } from "@/api/upload";
import { listMyWeekSchedule } from "@/api/manage/studentSchedule";
import StudentDutyWeekGrid from "@/components/StudentDutyWeekGrid/index.vue";

const router = useRouter();
const userStore = useUserStore();

const activeTab = ref("duty");
const scheduleData = ref(
  DUTY_PERIODS.map((time) => ({
    time,
    mon: "",
    tue: "",
    wed: "",
    thu: "",
    fri: "",
    sat: "",
    sun: "",
  })),
);
const uploadingSchedule = ref(false);
const lastUploadedScheduleUrl = ref("");
const lastScheduleSyncAt = ref("");

const weekdayAliases = {
  mon: ["mon", "monday", "星期一", "周一"],
  tue: ["tue", "tuesday", "星期二", "周二"],
  wed: ["wed", "wednesday", "星期三", "周三"],
  thu: ["thu", "thursday", "星期四", "周四"],
  fri: ["fri", "friday", "星期五", "周五"],
  sat: ["sat", "saturday", "星期六", "周六"],
  sun: ["sun", "sunday", "星期日", "周日", "星期天", "周天"],
};

const emptyScheduleRow = () => ({
  mon: "",
  tue: "",
  wed: "",
  thu: "",
  fri: "",
  sat: "",
  sun: "",
});

const normalizeKey = (value) =>
  String(value || "")
    .trim()
    .toLowerCase();
const normalizeTimeLabel = (value) => String(value || "").replace(/\s/g, "");

function resolveUploadResult(response) {
  const data = response?.data;
  let fileUrl = "";
  let parsedSchedule = null;
  if (typeof data === "string") {
    fileUrl = data;
  } else if (data && typeof data === "object") {
    fileUrl = data.url || data.fileUrl || data.ossUrl || data.scheduleUrl || "";
    parsedSchedule =
      data.parsedSchedule || data.scheduleData || data.freeSchedule || null;
  }
  if (!parsedSchedule) {
    parsedSchedule = response?.parsedSchedule || response?.scheduleData || null;
  }
  return { fileUrl, parsedSchedule };
}

function pickWeekdayKey(key) {
  const normalized = normalizeKey(key);
  for (const [target, aliases] of Object.entries(weekdayAliases)) {
    if (aliases.map(normalizeKey).includes(normalized)) {
      return target;
    }
  }
  return "";
}

function buildRowFromSource(source) {
  const row = emptyScheduleRow();
  if (!source || typeof source !== "object") {
    return row;
  }
  Object.keys(source).forEach((key) => {
    const weekdayKey = pickWeekdayKey(key);
    if (!weekdayKey) {
      return;
    }
    row[weekdayKey] = source[key] ?? "";
  });
  return row;
}

function applyParsedSchedule(parsedSchedule) {
  if (!parsedSchedule) {
    return false;
  }
  const rowsByTime = new Map();
  DUTY_PERIODS.forEach((time) => {
    rowsByTime.set(normalizeTimeLabel(time), { time, ...emptyScheduleRow() });
  });

  if (Array.isArray(parsedSchedule)) {
    parsedSchedule.forEach((item) => {
      if (!item || typeof item !== "object") {
        return;
      }
      const rawTime = item.time || item.period || item.slot || "";
      const targetTime = normalizeTimeLabel(rawTime);
      if (!rowsByTime.has(targetTime)) {
        return;
      }
      const current = rowsByTime.get(targetTime);
      const nextRow = buildRowFromSource(item);
      rowsByTime.set(targetTime, { ...current, ...nextRow });
    });
  } else if (parsedSchedule && typeof parsedSchedule === "object") {
    Object.keys(parsedSchedule).forEach((time) => {
      const targetTime = normalizeTimeLabel(time);
      if (!rowsByTime.has(targetTime)) {
        return;
      }
      const current = rowsByTime.get(targetTime);
      const nextRow = buildRowFromSource(parsedSchedule[time]);
      rowsByTime.set(targetTime, { ...current, ...nextRow });
    });
  } else {
    return false;
  }

  scheduleData.value = DUTY_PERIODS.map(
    (time) =>
      rowsByTime.get(normalizeTimeLabel(time)) || {
        time,
        ...emptyScheduleRow(),
      },
  );
  lastScheduleSyncAt.value = new Date().toLocaleString();
  return true;
}

function toDate(value) {
  if (!value) {
    return null;
  }
  if (typeof value === "object") {
    return value;
  }
  let time = value;
  if (typeof time === "string" && /^[0-9]+$/.test(time)) {
    time = parseInt(time);
  } else if (typeof time === "string") {
    time = time
      .replace(new RegExp(/-/gm), "/")
      .replace("T", " ")
      .replace(new RegExp(/\.[\d]{3}/gm), "");
  }
  if (typeof time === "number" && time.toString().length === 10) {
    time = time * 1000;
  }
  const d = new Date(time);
  return Number.isNaN(d.getTime()) ? null : d;
}

function weekdayKey(date) {
  const d = toDate(date);
  if (!d) return "";
  const day = d.getDay();
  if (day === 1) return "mon";
  if (day === 2) return "tue";
  if (day === 3) return "wed";
  if (day === 4) return "thu";
  if (day === 5) return "fri";
  if (day === 6) return "sat";
  if (day === 0) return "sun";
  return "";
}

function formatYmd(date) {
  return parseTime(date, "{y}-{m}-{d}") || "";
}

function formatHm(date) {
  return parseTime(date, "{h}:{i}") || "";
}

function getWeekStart(date) {
  const d = new Date(date);
  const day = d.getDay();
  const diff = d.getDate() - day + (day === 0 ? -6 : 1);
  const start = new Date(d.setDate(diff));
  start.setHours(0, 0, 0, 0);
  return start;
}

async function loadWeekSchedule() {
  try {
    const start = getWeekStart(new Date());
    const end = new Date(start);
    end.setDate(end.getDate() + 6);
    end.setHours(23, 59, 59, 999);
    const res = await listMyWeekSchedule({ from: formatYmd(start), to: formatYmd(end) });
    const rowsByTime = new Map();
    for (const raw of res?.data || []) {
      const startDate = toDate(raw?.startTime);
      const endDate = toDate(raw?.endTime);
      const key = weekdayKey(startDate);
      if (!startDate || !endDate || !key) {
        continue;
      }
      const timeKey = `${formatHm(startDate)}-${formatHm(endDate)}`;
      if (!timeKey.includes("-")) {
        continue;
      }
      const row = rowsByTime.get(timeKey) || { time: timeKey, ...emptyScheduleRow() };
      const title = String(raw?.courseName || "").trim();
      const location = String(raw?.location || "").trim();
      row[key] = title ? `${title}${location ? `\n${location}` : ""}` : row[key];
      rowsByTime.set(timeKey, row);
    }
    scheduleData.value = Array.from(rowsByTime.values()).sort((a, b) => String(a.time).localeCompare(String(b.time)));
    if (scheduleData.value.length === 0) {
      scheduleData.value = DUTY_PERIODS.map((time) => ({ time, ...emptyScheduleRow() }));
    }
    lastScheduleSyncAt.value = new Date().toLocaleString();
  } catch {
    scheduleData.value = DUTY_PERIODS.map((time) => ({ time, ...emptyScheduleRow() }));
  }
}

const handlePdfUpload = async (uploadFile) => {
  const rawFile = uploadFile?.raw;
  if (!rawFile) {
    ElMessage.error("未读取到上传文件");
    return;
  }
  const maxSize = 10 * 1024 * 1024;
  if (rawFile.size > maxSize) {
    ElMessage.error("文件大小不能超过 10MB");
    return;
  }
  let studentId = String(userStore.name || "").trim();
  if (!studentId && !getToken()) {
    try {
      const result = await ElMessageBox.prompt(
        "请输入学号后继续上传",
        "课表上传",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          inputPattern: /^[0-9A-Za-z_-]{4,20}$/,
          inputErrorMessage: "请输入4-20位学号",
        },
      );
      studentId = result.value.trim();
    } catch {
      return;
    }
  }
  uploadingSchedule.value = true;
  try {
    const response = await uploadSchedule(rawFile, studentId || undefined);
    const { fileUrl, parsedSchedule } = resolveUploadResult(response);
    if (fileUrl) {
      localStorage.setItem("student_schedule_last_upload_url", fileUrl);
      lastUploadedScheduleUrl.value = fileUrl;
    }
    const synced = applyParsedSchedule(parsedSchedule);
    await loadWeekSchedule();
    ElMessage.success(synced ? "课表上传成功，排班与课表已刷新" : "课表上传成功");
  } finally {
    uploadingSchedule.value = false;
  }
};

onMounted(() => {
  const lastUpload = localStorage.getItem("student_schedule_last_upload_url");
  if (lastUpload) {
    lastUploadedScheduleUrl.value = lastUpload;
  }
  loadWeekSchedule();
});
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-weight: 700;
  font-size: 16px;
}

.schedule-toolbar {
  margin-bottom: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
