<template>
  <div class="app-container">
    <el-card style="margin-bottom: 16px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>消息通知设置</span>
          <el-button @click="resetSettings">恢复默认</el-button>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :span="8" v-for="item in notifySettings" :key="item.key" style="margin-bottom: 12px">
          <el-card shadow="hover">
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>{{ item.label }}</span>
              <el-switch v-model="item.enabled" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>通知记录</span>
          <div style="display: flex; gap: 10px; align-items: center">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0">
              <el-tag type="danger">未读</el-tag>
            </el-badge>
            <el-button type="warning" plain @click="mockUnreadMessages">模拟未读消息</el-button>
            <el-button type="primary" plain @click="markAllAsRead" :loading="markAllLoading" :disabled="unreadCount === 0">全部标为已读</el-button>
            <el-select v-model="typeFilter" placeholder="通知类型" clearable style="width: 220px">
              <el-option label="值班前一天提醒" value="值班前一天提醒" />
              <el-option label="值班当天提醒" value="值班当天提醒" />
              <el-option label="巡查提醒" value="巡查提醒" />
              <el-option label="请假审批结果通知" value="请假审批结果通知" />
              <el-option label="调班申请通知" value="调班申请通知" />
              <el-option label="调班确认通知" value="调班确认通知" />
            </el-select>
            <el-select v-model="timeRangeFilter" placeholder="时间范围" clearable style="width: 150px">
              <el-option label="今天" value="today" />
              <el-option label="近3天" value="last3" />
              <el-option label="近7天" value="last7" />
              <el-option label="近30天" value="last30" />
            </el-select>
            <el-button @click="loadNotices">刷新</el-button>
          </div>
        </div>
      </template>
      <el-table :data="filteredNotices" border :row-class-name="getRowClassName">
        <el-table-column label="置顶" width="80" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.pinned" type="warning">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="通知类型" width="160" align="center" />
        <el-table-column prop="content" label="通知内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="time" label="通知时间" width="170" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.read ? 'info' : 'danger'">{{ scope.row.read ? "已读" : "未读" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button link type="primary" @click="togglePin(scope.row)">
              {{ scope.row.pinned ? "取消置顶" : "置顶" }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="StudentNotify">
import { ref, computed } from "vue";
import { ElMessage } from "element-plus";

const typeFilter = ref("");
const timeRangeFilter = ref("");
const markAllLoading = ref(false);
const notifySettings = ref([
  { key: "beforeDuty", label: "值班前一天提醒", enabled: true },
  { key: "onDuty", label: "值班当天提醒", enabled: true },
  { key: "patrol", label: "巡查提醒", enabled: true },
  { key: "leaveResult", label: "请假审批结果通知", enabled: true },
  { key: "swapApply", label: "调班申请通知", enabled: true },
  { key: "swapConfirm", label: "调班确认通知", enabled: true },
]);

const notices = ref([]);

function getDefaultNotices() {
  return [
    { id: 1, title: "值班前一天提醒", type: "值班前一天提醒", content: "您明天在康一（思齐馆）有值班安排，请提前做好准备。", time: "2026-03-22 20:00:00", read: false, pinned: false },
    { id: 2, title: "值班当天提醒", type: "值班当天提醒", content: "今天您在梅一（弘毅馆）7-8节值班，请按时到岗。", time: "2026-03-23 08:00:00", read: false, pinned: false },
    { id: 3, title: "巡查提醒", type: "巡查提醒", content: "请于今日16:00前完成场馆巡查并提交巡查结果。", time: "2026-03-23 14:00:00", read: true, pinned: false },
    { id: 4, title: "请假审批结果通知", type: "请假审批结果通知", content: "您提交的请假申请已通过，处理意见：注意后续排班。", time: "2026-03-22 10:15:00", read: true, pinned: false },
    { id: 5, title: "调班申请通知", type: "调班申请通知", content: "收到同学发起的调班申请，请及时确认是否同意。", time: "2026-03-22 11:30:00", read: false, pinned: false },
    { id: 6, title: "调班确认通知", type: "调班确认通知", content: "您的调班申请已被对方确认，等待管理员最终审批。", time: "2026-03-22 12:00:00", read: false, pinned: false },
  ];
}

function loadNotices() {
  const raw = localStorage.getItem("student_notify_messages");
  const list = raw ? JSON.parse(raw) : getDefaultNotices();
  notices.value = list.map((item) => ({
    ...item,
    pinned: !!item.pinned,
  }));
}

const filteredNotices = computed(() => {
  let list = notices.value;
  if (typeFilter.value) {
    list = list.filter((item) => item.type === typeFilter.value);
  }
  if (timeRangeFilter.value) {
    const now = new Date();
    const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
    const rangeDayMap = {
      today: 1,
      last3: 3,
      last7: 7,
      last30: 30,
    };
    const days = rangeDayMap[timeRangeFilter.value];
    const start = timeRangeFilter.value === "today" ? todayStart : (todayStart - (days - 1) * 24 * 60 * 60 * 1000);
    const end = now.getTime();
    list = list.filter((item) => {
      const noticeTime = new Date(item.time).getTime();
      return !Number.isNaN(noticeTime) && noticeTime >= start && noticeTime <= end;
    });
  }
  list = list.slice().sort((a, b) => {
    if (a.pinned !== b.pinned) return a.pinned ? -1 : 1;
    return new Date(b.time).getTime() - new Date(a.time).getTime();
  });
  return list;
});

const unreadCount = computed(() => notices.value.filter((item) => !item.read).length);

function getRowClassName({ row }) {
  return row.read ? "" : "unread-row";
}

function markAllAsRead() {
  markAllLoading.value = true;
  setTimeout(() => {
    notices.value = notices.value.map((item) => ({
      ...item,
      read: true,
    }));
    localStorage.setItem("student_notify_messages", JSON.stringify(notices.value));
    markAllLoading.value = false;
    ElMessage.success("已将全部通知标记为已读");
  }, 500);
}

function togglePin(row) {
  notices.value = notices.value.map((item) => {
    if (item.id === row.id) {
      return { ...item, pinned: !item.pinned };
    }
    return item;
  });
  localStorage.setItem("student_notify_messages", JSON.stringify(notices.value));
  ElMessage.success(row.pinned ? "已取消置顶" : "已置顶通知");
}

function mockUnreadMessages() {
  const now = new Date();
  const mockNotice = {
    id: Date.now(),
    title: "调班申请通知",
    type: "调班申请通知",
    content: "您收到新的调班申请，请尽快处理确认。",
    time: now.getFullYear() +
      "-" +
      String(now.getMonth() + 1).padStart(2, "0") +
      "-" +
      String(now.getDate()).padStart(2, "0") +
      " " +
      String(now.getHours()).padStart(2, "0") +
      ":" +
      String(now.getMinutes()).padStart(2, "0") +
      ":" +
      String(now.getSeconds()).padStart(2, "0"),
    read: false,
    pinned: true,
  };
  notices.value = notices.value.map((item, index) => ({
    ...item,
    read: index < 2 ? false : item.read,
  }));
  notices.value.unshift(mockNotice);
  localStorage.setItem("student_notify_messages", JSON.stringify(notices.value));
  ElMessage.success("已模拟未读消息场景");
}

function resetSettings() {
  notifySettings.value = notifySettings.value.map((item) => ({
    ...item,
    enabled: true,
  }));
}

loadNotices();
</script>

<style scoped>
:deep(.unread-row td) {
  background-color: #f5f7fa !important;
}
</style>
