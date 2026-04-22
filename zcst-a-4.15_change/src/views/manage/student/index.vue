<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学号" prop="studentId">
        <el-input
          v-model="queryParams.studentId"
          placeholder="请输入学号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入姓名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-input
          v-model="queryParams.gender"
          placeholder="请输入性别"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="专业ID" prop="majorId">
        <el-input
          v-model="queryParams.majorId"
          placeholder="请输入专业ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="年级" prop="grade">
        <el-input
          v-model="queryParams.grade"
          placeholder="请输入年级"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号码" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="场馆" prop="venueId">
        <el-select v-model="queryParams.venueId" placeholder="请选择场馆" clearable>
          <el-option
            v-for="dict in zcst_venue_name"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="queryParams.password"
          placeholder="请输入密码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createdAt">
        <el-date-picker clearable
          v-model="queryParams.createdAt"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="更新时间" prop="updatedAt">
        <el-date-picker clearable
          v-model="queryParams.updatedAt"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择更新时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['manage:student:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['manage:student:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['manage:student:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['manage:student:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="studentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="学号" align="center" prop="studentId" />
      <el-table-column label="姓名" align="center" prop="name" />
      <el-table-column label="性别" align="center" prop="gender" />
      <el-table-column label="专业ID" align="center" prop="majorId" />
      <el-table-column label="年级" align="center" prop="grade" />
      <el-table-column label="手机号码" align="center" prop="phone" />
      <el-table-column label="场馆" align="center" prop="venueId">
        <template #default="scope">
          <dict-tag :options="zcst_venue_name" :value="scope.row.venueId"/>
        </template>
      </el-table-column>
      <el-table-column label="密码" align="center" prop="password" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['manage:student:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['manage:student:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="handlePagination"
    />

    <el-card style="margin-top: 16px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>值班申请处理</span>
          <el-select v-model="dutyFilterStatus" style="width: 140px" @change="loadDutyApplications">
            <el-option label="全部状态" value="" />
            <el-option label="待处理" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
        </div>
      </template>
      <el-table :data="dutyApplications" border>
        <el-table-column label="申请类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.applicationType === 'swap' ? 'warning' : 'info'">
              {{ scope.row.applicationType === "swap" ? "调班申请" : "请假申请" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicantStudentId" label="申请人学号" width="120" align="center" />
        <el-table-column prop="applicantName" label="申请人姓名" width="120" align="center" />
        <el-table-column prop="relatedStudentId" label="关联学生学号" width="130" align="center" />
        <el-table-column prop="dutyDate" label="值班日期" width="110" align="center" />
        <el-table-column prop="dutyPeriod" label="值班时段" width="130" align="center" />
        <el-table-column prop="reason" label="申请原因" min-width="170" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'approved' ? 'success' : scope.row.status === 'rejected' ? 'danger' : 'warning'">
              {{ dutyStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewRemark" label="处理意见" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button
              link
              type="success"
              :disabled="scope.row.status !== 'pending'"
              @click="handleDutyReview(scope.row, 'approved')"
            >
              通过
            </el-button>
            <el-button
              link
              type="danger"
              :disabled="scope.row.status !== 'pending'"
              @click="handleDutyReview(scope.row, 'rejected')"
            >
              驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加或修改学生管理对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="studentRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-input v-model="form.gender" placeholder="请输入性别" />
        </el-form-item>
        <el-form-item label="专业ID" prop="majorId">
          <el-input v-model="form.majorId" placeholder="请输入专业ID" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="form.grade" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="场馆" prop="venueId">
          <el-select v-model="form.venueId" placeholder="请选择场馆">
            <el-option
              v-for="dict in zcst_venue_name"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Student">
import { listStudent, getStudent, delStudent, addStudent, updateStudent } from "@/api/manage/student"
import { normalizeDutyPeriod } from "@/utils/dutyPeriod"

const { proxy } = getCurrentInstance()
const { zcst_venue_name } = proxy.useDict('zcst_venue_name')

const studentList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const dutyApplications = ref([])
const dutyFilterStatus = ref("")
const dutyStorageKey = "duty_applications"

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    studentId: null,
    name: null,
    gender: null,
    majorId: null,
    grade: null,
    phone: null,
    venueId: null,
    password: null,
    createdAt: null,
    updatedAt: null
  },
  rules: {
    name: [
      { required: true, message: "姓名不能为空", trigger: "blur" }
    ],
    gender: [
      { required: true, message: "性别不能为空", trigger: "blur" }
    ],
    majorId: [
      { required: true, message: "专业ID不能为空", trigger: "blur" }
    ],
    grade: [
      { required: true, message: "年级不能为空", trigger: "blur" }
    ],
    phone: [
      { required: true, message: "手机号码不能为空", trigger: "blur" }
    ],
    venueId: [
      { required: true, message: "场馆不能为空", trigger: "blur" }
    ],
    password: [
      { required: true, message: "密码不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 分页功能方法 */
function handlePagination(param) {
  queryParams.value.pageNum = param.page
  queryParams.value.pageSize = param.limit
  getList()
}



/** 查询学生管理列表 */
function getList() {
  loading.value = true
  listStudent(queryParams.value).then(response => {
    studentList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    studentId: null,
    name: null,
    gender: null,
    majorId: null,
    grade: null,
    phone: null,
    venueId: null,
    password: null,
    createdAt: null,
    updatedAt: null
  }
  proxy.resetForm("studentRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.studentId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加学生管理"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _studentId = row.studentId || ids.value
  getStudent(_studentId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改学生管理"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["studentRef"].validate(valid => {
    if (valid) {
      if (form.value.studentId != null) {
        updateStudent(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addStudent(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _studentIds = row.studentId || ids.value
  proxy.$modal.confirm('是否确认删除学生管理编号为"' + _studentIds + '"的数据项？').then(function() {
    return delStudent(_studentIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('manage/student/export', {
    ...queryParams.value
  }, `student_${new Date().getTime()}.xlsx`)
}

function dutyStatusLabel(status) {
  if (status === "approved") return "已通过"
  if (status === "rejected") return "已驳回"
  return "待处理"
}

function loadDutyApplications() {
  const raw = localStorage.getItem(dutyStorageKey)
  const list = raw ? JSON.parse(raw) : []
  let result = list
  if (dutyFilterStatus.value) {
    result = list.filter((item) => item.status === dutyFilterStatus.value)
  }
  dutyApplications.value = result
    .map((item) => ({
      ...item,
      dutyPeriod: normalizeDutyPeriod(item.dutyPeriod)
    }))
    .sort((a, b) => b.id - a.id)
}

function handleDutyReview(row, status) {
  const label = status === "approved" ? "通过" : "驳回"
  proxy.$modal.prompt(`请输入${label}意见（可选）`).then(({ value }) => {
    const raw = localStorage.getItem(dutyStorageKey)
    const list = raw ? JSON.parse(raw) : []
    const index = list.findIndex((item) => item.id === row.id)
    if (index === -1) {
      proxy.$modal.msgError("申请记录不存在")
      return
    }
    list[index].status = status
    list[index].reviewRemark = value || ""
    list[index].updatedAt = new Date().toLocaleString()
    localStorage.setItem(dutyStorageKey, JSON.stringify(list))
    proxy.$modal.msgSuccess(`已${label}该申请`)
    loadDutyApplications()
  }).catch(() => {})
}

getList()
loadDutyApplications()
</script>
