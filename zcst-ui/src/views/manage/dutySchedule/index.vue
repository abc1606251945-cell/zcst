<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>场馆值班表管理</span>
          <div class="card-header-actions">
            <el-button type="primary" plain @click="handleAdd" v-hasPermi="['manage:dutySchedule:add']">
              <el-icon><Plus /></el-icon>
              新增
            </el-button>
            <el-button type="success" plain @click="handleAutoSchedule" v-hasPermi="['manage:dutySchedule:add']">
              <el-icon><Timer /></el-icon>
              自动排班
            </el-button>
            <el-button type="warning" plain @click="handleAutoScheduleByConfig" v-hasPermi="['manage:dutySchedule:add']">
              <el-icon><Timer /></el-icon>
              按配置排班
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索条件 -->
      <el-form :model="queryParams" label-width="100px" class="mb8">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="学号">
              <el-input v-model="queryParams.studentId" placeholder="请输入学号" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="姓名">
              <el-input v-model="queryParams.studentName" placeholder="请输入姓名" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="场馆">
              <el-select v-model="queryParams.venueId" placeholder="请选择场馆" clearable>
                <el-option
                  v-for="venue in venueList"
                  :key="venue.venueId"
                  :label="venue.venueName"
                  :value="venue.venueId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="值班时间">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="handleDateChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">
                查询
              </el-button>
              <el-button icon="RefreshLeft" @click="resetQuery">
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 值班表列表 -->
      <el-table v-loading="loading" :data="dutyScheduleList" border style="width: 100%">
        <el-table-column label="值班ID" prop="dutyId" width="80" />
        <el-table-column label="学号" prop="studentId" width="120" />
        <el-table-column label="姓名" prop="studentName" width="100" />
        <el-table-column label="性别" prop="gender" width="80" />
        <el-table-column label="场馆ID" prop="venueId" width="100" />
        <el-table-column label="值班开始时间" prop="startTime" width="180" />
        <el-table-column label="值班结束时间" prop="endTime" width="180" />
        <el-table-column label="备注" prop="remark" />
        <el-table-column label="创建时间" prop="createdAt" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['manage:dutySchedule:edit']"
            >
              修改
            </el-button>
            <el-button
              size="small"
              type="danger"
              icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['manage:dutySchedule:remove']"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 新增/修改对话框 -->
    <el-dialog
      :title="title"
      v-model="open"
      width="500px"
      append-to-body
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="场馆ID" prop="venueId">
          <el-input v-model="form.venueId" type="number" placeholder="请输入场馆ID" />
        </el-form-item>
        <el-form-item label="值班开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="请选择值班开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="值班结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="请选择值班结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="open = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 自动排班对话框 -->
    <el-dialog
      title="自动排班"
      v-model="autoScheduleOpen"
      width="600px"
      append-to-body
    >
      <el-form :model="autoScheduleForm" label-width="120px">
        <el-form-item label="场馆ID" prop="venueId">
          <el-input v-model="autoScheduleForm.venueId" type="number" placeholder="请输入场馆ID" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="autoScheduleForm.startDate"
            type="date"
            placeholder="请选择开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="autoScheduleForm.endDate"
            type="date"
            placeholder="请选择结束日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="时间段配置">
          <div v-for="(slot, index) in autoScheduleForm.timeSlots" :key="index" class="time-slot-item">
            <el-time-picker
              v-model="slot.startTime"
              format="HH:mm"
              placeholder="开始时间"
              style="width: 100px; margin-right: 10px"
            />
            <span>至</span>
            <el-time-picker
              v-model="slot.endTime"
              format="HH:mm"
              placeholder="结束时间"
              style="width: 100px; margin-left: 10px"
            />
            <el-button type="danger" size="small" @click="removeTimeSlot(index)" style="margin-left: 10px">
              删除
            </el-button>
          </div>
          <el-button type="primary" size="small" @click="addTimeSlot">
            添加时间段
          </el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="autoScheduleOpen = false">取消</el-button>
          <el-button type="primary" @click="submitAutoSchedule">确定排班</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 根据配置自动排班对话框 -->
    <el-dialog
      title="根据配置自动排班"
      v-model="autoScheduleByConfigOpen"
      width="500px"
      append-to-body
    >
      <el-form :model="autoScheduleByConfigForm" label-width="120px">
        <el-form-item label="场馆ID" prop="venueId">
          <el-input v-model="autoScheduleByConfigForm.venueId" type="number" placeholder="请输入场馆ID" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="autoScheduleByConfigForm.startDate"
            type="date"
            placeholder="请选择开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排班周数" prop="weeks">
          <el-input v-model="autoScheduleByConfigForm.weeks" type="number" min="1" max="52" placeholder="请输入排班周数" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="autoScheduleByConfigOpen = false">取消</el-button>
          <el-button type="primary" @click="submitAutoScheduleByConfig">确定排班</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { listDutySchedule, getDutySchedule, addDutySchedule, updateDutySchedule, delDutySchedule, autoSchedule, autoScheduleByConfig } from '@/api/manage/dutySchedule'
import { listVenue } from '@/api/manage/venue'
import { Plus, Timer, Search, RefreshLeft, Edit, Delete } from '@element-plus/icons-vue'

export default {
  name: 'DutySchedule',
  components: {
    Plus,
    Timer,
    Search,
    RefreshLeft,
    Edit,
    Delete
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 表格数据
      dutyScheduleList: [],
      // 总条数
      total: 0,
      // 对话框标题
      title: '',
      // 是否显示对话框
      open: false,
      // 自动排班对话框
      autoScheduleOpen: false,
      // 根据配置自动排班对话框
      autoScheduleByConfigOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: '',
        studentName: '',
        venueId: '',
        startTime: null,
        endTime: null
      },
      // 日期范围
      dateRange: [],
      // 表单数据
      form: {},
      // 自动排班表单
      autoScheduleForm: {
        venueId: '',
        startDate: '',
        endDate: '',
        timeSlots: [
          { startTime: '08:00', endTime: '09:40' },
          { startTime: '10:00', endTime: '11:40' },
          { startTime: '14:00', endTime: '15:40' },
          { startTime: '16:00', endTime: '17:40' }
        ]
      },
      // 根据配置自动排班表单
      autoScheduleByConfigForm: {
        venueId: '',
        startDate: '',
        weeks: 19 // 默认19周
      },
      // 场馆列表
      venueList: []
    }
  },
  created() {
    this.getList()
    this.getVenueList()
  },
  methods: {
    /** 查询值班表列表 */
    getList() {
      this.loading = true
      listDutySchedule(this.queryParams).then(response => {
        this.dutyScheduleList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 日期范围变化 */
    handleDateChange(val) {
      if (val) {
        this.queryParams.startTime = val[0]
        this.queryParams.endTime = val[1]
      } else {
        this.queryParams.startTime = null
        this.queryParams.endTime = null
      }
    },
    /** 查询按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        studentId: '',
        studentName: '',
        venueId: '',
        startTime: null,
        endTime: null
      }
      this.getList()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.resetForm()
      this.open = true
      this.title = '新增值班表'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.resetForm()
      const dutyId = row.dutyId
      getDutySchedule(dutyId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改值班表'
      })
    },
    /** 自动排班按钮操作 */
    handleAutoSchedule() {
      this.autoScheduleOpen = true
    },
    /** 根据配置自动排班按钮操作 */
    handleAutoScheduleByConfig() {
      this.autoScheduleByConfigOpen = true
    },
    /** 新增时间段 */
    addTimeSlot() {
      this.autoScheduleForm.timeSlots.push({ startTime: '', endTime: '' })
    },
    /** 删除时间段 */
    removeTimeSlot(index) {
      this.autoScheduleForm.timeSlots.splice(index, 1)
    },
    /** 提交自动排班 */
    submitAutoSchedule() {
      autoSchedule(this.autoScheduleForm).then(response => {
        this.$modal.msgSuccess('自动排班成功')
        this.autoScheduleOpen = false
        this.getList()
      })
    },
    /** 提交根据配置自动排班 */
    submitAutoScheduleByConfig() {
      autoScheduleByConfig(this.autoScheduleByConfigForm).then(response => {
        this.$modal.msgSuccess('自动排班成功')
        this.autoScheduleByConfigOpen = false
        this.getList()
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.dutyId != undefined) {
            updateDutySchedule(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addDutySchedule(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 重置表单 */
    resetForm() {
      this.form = {
        dutyId: undefined,
        studentId: '',
        venueId: '',
        startTime: '',
        endTime: '',
        remark: ''
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const dutyIds = row.dutyId
      this.$modal.confirm('是否确认删除值班表编号为"' + dutyIds + '"的数据项？').then(function() {
        return delDutySchedule(dutyIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    /** 获取场馆列表 */
    getVenueList() {
      listVenue().then(response => {
        this.venueList = response.rows
      })
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header-actions {
  display: flex;
  gap: 10px;
}

.time-slot-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
</style>
