<template>
  <div class="app-container home" v-if="!isStudent">
    <admin-home />
  </div>

  <div class="app-container home" v-else>
    <el-card>
      <div class="card-header">
        <span class="card-title">课表上传</span>
        <el-upload
          action="#"
          :auto-upload="false"
          :show-file-list="false"
          :disabled="uploadingSchedule"
          @change="handleScheduleUpload"
        >
          <el-button type="primary" icon="Upload" :loading="uploadingSchedule"
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
          课表上传成功，已合并到下方值班表：
          <el-link :href="lastUploadedScheduleUrl" target="_blank" type="primary"
            >查看原图</el-link
          >
          <span v-if="lastScheduleSyncAt" class="sync-at"
            >最近同步：{{ lastScheduleSyncAt }}</span
          >
        </template>
      </el-alert>
      <el-divider class="section-divider" />
      <student-duty-week-grid
        ref="gridRef"
        title="我的本周值班与课表"
        :schedule-rows="scheduleRows"
        include-schedule
        embedded
      />
    </el-card>
    <div class="student-actions">
      <el-button
        type="success"
        @click="router.push('/student/attendance/index')"
        >前往考勤管理</el-button
      >
      <el-button @click="router.push('/student/notify/index')"
        >查看通知</el-button
      >
    </div>
  </div>
</template>

<script setup name="Index">
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
import useUserStore from "@/store/modules/user";
import { getToken } from "@/utils/auth";
import { uploadSchedule } from "@/api/upload";
import StudentDutyWeekGrid from "@/components/StudentDutyWeekGrid/index.vue";
import AdminHome from "@/views/adminHome.vue";

const userStore = useUserStore();
const router = useRouter();
const uploadingSchedule = ref(false);
const lastUploadedScheduleUrl = ref("");
const lastScheduleSyncAt = ref("");
const scheduleRows = ref([]);
const gridRef = ref(null);

const isStudent = computed(() => {
  const roles = userStore.roles || [];
  if (roles.length === 0) {
    return false;
  }
  return roles.some((role) => String(role).toLowerCase() === "student");
});

function resolveUploadResult(response) {
  const data = response?.data;
  let fileUrl = "";
  if (typeof data === "string") {
    fileUrl = data;
  } else if (data && typeof data === "object") {
    fileUrl = data.url || data.fileUrl || data.ossUrl || data.scheduleUrl || "";
  }
  return { fileUrl };
}

const handleScheduleUpload = async (uploadFile) => {
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
      const result = await ElMessageBox.prompt("请输入学号后继续上传", "课表上传", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        inputPattern: /^[0-9A-Za-z_-]{4,20}$/,
        inputErrorMessage: "请输入4-20位学号",
      });
      studentId = result.value.trim();
    } catch {
      return;
    }
  }
  uploadingSchedule.value = true;
  try {
    const response = await uploadSchedule(rawFile, studentId || undefined);
    const { fileUrl } = resolveUploadResult(response);
    if (fileUrl) {
      localStorage.setItem("student_schedule_last_upload_url", fileUrl);
      lastUploadedScheduleUrl.value = fileUrl;
    }
    lastScheduleSyncAt.value = new Date().toLocaleString();
    await gridRef.value?.refresh?.();
    ElMessage.success("课表上传成功，排班与课表已刷新");
  } finally {
    uploadingSchedule.value = false;
  }
};

function getUnreadNotifyCount() {
  const raw = localStorage.getItem("student_notify_messages");
  if (raw) {
    const list = JSON.parse(raw);
    return list.filter((item) => !item.read).length;
  }
  const defaultList = [
    { read: false },
    { read: false },
    { read: true },
    { read: true },
    { read: false },
    { read: false },
  ];
  return defaultList.filter((item) => !item.read).length;
}

function showHomeUnreadNotice() {
  const unreadCount = getUnreadNotifyCount();
  if (unreadCount <= 0) {
    return;
  }
  setTimeout(() => {
    ElMessageBox.confirm(
      `您有 ${unreadCount} 条未读消息需要处理，是否前往消息通知查看？`,
      "消息提醒",
      {
        confirmButtonText: "去查看",
        cancelButtonText: "稍后处理",
        type: "warning",
      },
    )
      .then(() => {
        router.push("/student/notify/index");
      })
      .catch(() => {});
  }, 300);
}

function shouldShowUnreadNoticeByLogin() {
  const token = getToken() || "";
  if (!token) {
    return false;
  }
  const cacheKey = "student_home_unread_notice_token";
  const lastToken = sessionStorage.getItem(cacheKey);
  if (lastToken === token) {
    return false;
  }
  sessionStorage.setItem(cacheKey, token);
  return true;
}

onMounted(() => {
  if (isStudent.value && shouldShowUnreadNoticeByLogin()) {
    showHomeUnreadNotice();
  }
  const lastUpload = localStorage.getItem("student_schedule_last_upload_url");
  if (lastUpload) {
    lastUploadedScheduleUrl.value = lastUpload;
  }
});
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.card-title {
  font-size: 15px;
  font-weight: 700;
}

.sync-at {
  margin-left: 12px;
  color: #606266;
}

.section-divider {
  margin: 16px 0;
}

.student-actions {
  margin-top: 12px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
