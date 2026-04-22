<template>
  <div class="app-container">
    <el-card style="margin-bottom: 16px">
      <template #header>
        <span>值班申请</span>
      </template>
      <el-form ref="applyRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="申请类型" prop="applicationType">
              <el-select v-model="form.applicationType" placeholder="请选择申请类型" style="width: 100%">
                <el-option label="调班申请" value="swap" />
                <el-option label="请假申请" value="leave" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="值班日期" prop="dutyDate">
              <el-date-picker
                v-model="form.dutyDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择值班日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="值班时段" prop="dutyPeriod">
              <el-select v-model="form.dutyPeriod" placeholder="请选择值班时段" style="width: 100%">
                <el-option
                  v-for="option in dutyPeriodOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联学生学号" prop="relatedStudentId">
              <el-input
                v-model="form.relatedStudentId"
                :disabled="form.applicationType !== 'swap'"
                placeholder="调班申请时必填"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联学生姓名" prop="relatedStudentName">
              <el-input
                v-model="form.relatedStudentName"
                :disabled="form.applicationType !== 'swap'"
                placeholder="调班申请时建议填写"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="申请原因" prop="reason">
              <el-input
                v-model="form.reason"
                type="textarea"
                :rows="3"
                maxlength="300"
                show-word-limit
                placeholder="请输入申请原因"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="submitApply">提交申请</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>我的申请记录</span>
          <div style="display: flex; gap: 10px; align-items: center">
            <el-select
              v-model="recordTypeFilter"
              placeholder="申请类型"
              clearable
              style="width: 120px"
              @change="applyRecordFilter"
            >
              <el-option label="全部类型" value="" />
              <el-option label="调班申请" value="swap" />
              <el-option label="请假申请" value="leave" />
            </el-select>
            <el-select
              v-model="recordStatusFilter"
              placeholder="处理状态"
              clearable
              style="width: 120px"
              @change="applyRecordFilter"
            >
              <el-option label="全部状态" value="" />
              <el-option label="待处理" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="已驳回" value="rejected" />
            </el-select>
            <el-date-picker
              v-model="recordDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              format="YYYY-MM-DD"
              style="width: 280px"
              @change="applyRecordFilter"
            />
            <el-button @click="showAllRecords">全部记录</el-button>
          </div>
        </div>
      </template>
      <el-table :data="myApplications" border>
        <el-table-column label="申请类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.applicationType === 'swap' ? 'warning' : 'info'">
              {{ scope.row.applicationType === "swap" ? "调班申请" : "请假申请" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dutyDate" label="值班日期" width="120" align="center" />
        <el-table-column prop="dutyPeriod" label="值班时段" width="130" align="center" />
        <el-table-column prop="relatedStudentId" label="关联学生学号" width="140" align="center" />
        <el-table-column prop="reason" label="申请原因" min-width="180" show-overflow-tooltip />
        <el-table-column prop="statusLabel" label="处理状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'approved' ? 'success' : scope.row.status === 'rejected' ? 'danger' : 'warning'">
              {{ scope.row.statusLabel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewRemark" label="管理端处理意见" min-width="160" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="申请时间" width="170" align="center" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="StudentDuty">
import { ref, reactive, getCurrentInstance } from "vue";
import { ElMessage } from "element-plus";
import useUserStore from "@/store/modules/user";
import { DUTY_PERIOD_OPTIONS, normalizeDutyPeriod } from "@/utils/dutyPeriod";

const { proxy } = getCurrentInstance();
const userStore = useUserStore();
const applyRef = ref();
const storageKey = "duty_applications";

const form = reactive({
  applicationType: "swap",
  dutyDate: "",
  dutyPeriod: "",
  relatedStudentId: "",
  relatedStudentName: "",
  reason: "",
});

const myApplications = ref([]);
const allMyApplications = ref([]);
const recordDateRange = ref([]);
const recordTypeFilter = ref("");
const recordStatusFilter = ref("");
const dutyPeriodOptions = DUTY_PERIOD_OPTIONS;

const rules = {
  applicationType: [{ required: true, message: "请选择申请类型", trigger: "change" }],
  dutyDate: [{ required: true, message: "请选择值班日期", trigger: "change" }],
  dutyPeriod: [{ required: true, message: "请选择值班时段", trigger: "change" }],
  relatedStudentId: [
    {
      validator: (rule, value, callback) => {
        if (form.applicationType === "swap" && !value) {
          callback(new Error("调班申请必须填写关联学生学号"));
          return;
        }
        callback();
      },
      trigger: "blur",
    },
  ],
  reason: [{ required: true, message: "请输入申请原因", trigger: "blur" }],
};

function getApplications() {
  const raw = localStorage.getItem(storageKey);
  return raw ? JSON.parse(raw) : [];
}

function setApplications(list) {
  localStorage.setItem(storageKey, JSON.stringify(list));
}

function formatStatus(status) {
  if (status === "approved") return "已通过";
  if (status === "rejected") return "已驳回";
  return "待处理";
}

function loadMyApplications() {
  const studentId = userStore.name || "";
  allMyApplications.value = getApplications()
    .filter((item) => item.applicantStudentId === studentId)
    .sort((a, b) => b.id - a.id)
    .map((item) => ({
      ...item,
      dutyPeriod: normalizeDutyPeriod(item.dutyPeriod),
      statusLabel: formatStatus(item.status),
    }));
  applyRecordFilter();
}

function applyRecordFilter() {
  let filtered = allMyApplications.value;
  if (recordTypeFilter.value) {
    filtered = filtered.filter((item) => item.applicationType === recordTypeFilter.value);
  }
  if (recordStatusFilter.value) {
    filtered = filtered.filter((item) => item.status === recordStatusFilter.value);
  }
  if (recordDateRange.value && recordDateRange.value.length === 2) {
    const start = new Date(recordDateRange.value[0] + " 00:00:00").getTime();
    const end = new Date(recordDateRange.value[1] + " 23:59:59").getTime();
    filtered = filtered.filter((item) => {
      const dutyTime = new Date(item.dutyDate + " 00:00:00").getTime();
      return dutyTime >= start && dutyTime <= end;
    });
  }
  myApplications.value = filtered;
}

function showAllRecords() {
  recordDateRange.value = [];
  recordTypeFilter.value = "";
  recordStatusFilter.value = "";
  applyRecordFilter();
}

function resetForm() {
  form.applicationType = "swap";
  form.dutyDate = "";
  form.dutyPeriod = "";
  form.relatedStudentId = "";
  form.relatedStudentName = "";
  form.reason = "";
  proxy.resetForm("applyRef");
}

function submitApply() {
  applyRef.value.validate((valid) => {
    if (!valid) return;

    const applicantStudentId = userStore.name || "";
    const applicantName = userStore.nickName || "学生";
    if (!applicantStudentId) {
      ElMessage.error("未获取到当前学生学号，请重新登录后再试");
      return;
    }
    if (form.applicationType === "swap" && form.relatedStudentId === applicantStudentId) {
      ElMessage.warning("关联学生不能是本人");
      return;
    }

    const record = {
      id: Date.now(),
      applicationType: form.applicationType,
      dutyDate: form.dutyDate,
      dutyPeriod: form.dutyPeriod,
      relatedStudentId: form.applicationType === "swap" ? form.relatedStudentId : "",
      relatedStudentName: form.applicationType === "swap" ? form.relatedStudentName : "",
      reason: form.reason,
      applicantStudentId,
      applicantName,
      status: "pending",
      reviewRemark: "",
      createdAt: new Date().toLocaleString(),
      updatedAt: new Date().toLocaleString(),
    };

    const list = getApplications();
    list.push(record);
    setApplications(list);
    ElMessage.success("申请已提交，等待管理端处理");
    loadMyApplications();
    resetForm();
  });
}

loadMyApplications();
</script>
