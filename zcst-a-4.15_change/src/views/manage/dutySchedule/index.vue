<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="场馆" prop="venueId">
        <el-select
          v-model="queryParams.venueId"
          placeholder="请选择场馆"
          style="width: 120px"
        >
          <el-option
            v-for="venue in venueList"
            :key="venue.venueId"
            :label="venue.venueName"
            :value="venue.venueId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="queryParams.startTime"
          type="datetime"
          placeholder="选择开始时间"
          style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="queryParams.endTime"
          type="datetime"
          placeholder="选择结束时间"
          style="width: 180px"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['manage:dutySchedule:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['manage:dutySchedule:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['manage:dutySchedule:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['manage:dutySchedule:export']"
          >导出</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-s-grid"
          size="mini"
          @click="handleAutoSchedule"
          v-hasPermi="['manage:dutySchedule:add']"
          >按配置排班</el-button
        >
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table
      v-loading="loading"
      :data="dutyScheduleList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="值班ID" width="80" align="center" prop="dutyId" />
      <el-table-column
        label="学号"
        width="120"
        align="center"
        prop="studentId"
      />
      <el-table-column label="场馆" width="100" align="center" prop="venueId">
        <template v-slot="scope">
          {{ getVenueName(scope.row.venueId) }}
        </template>
      </el-table-column>
      <el-table-column
        label="开始时间"
        width="180"
        align="center"
        prop="startTime"
      />
      <el-table-column
        label="结束时间"
        width="180"
        align="center"
        prop="endTime"
      />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column
        label="创建时间"
        width="180"
        align="center"
        prop="createdAt"
      />
      <el-table-column label="操作" width="130" align="center">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['manage:dutySchedule:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['manage:dutySchedule:remove']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="handlePagination"
    />

    <!-- 新增或修改值班表对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="场馆" prop="venueId">
          <el-select v-model="form.venueId" placeholder="请选择场馆">
            <el-option
              v-for="venue in venueList"
              :key="venue.venueId"
              :label="venue.venueName"
              :value="venue.venueId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 自动排班对话框 -->
    <el-dialog
      title="按配置自动排班"
      v-model="autoScheduleOpen"
      width="500px"
      append-to-body
    >
      <el-form
        ref="autoScheduleForm"
        :model="autoScheduleForm"
        :rules="autoScheduleRules"
        label-width="80px"
      >
        <el-form-item label="场馆" prop="venueId">
          <el-select
            v-model="autoScheduleForm.venueId"
            placeholder="请选择场馆"
            @change="handleVenueChange"
          >
            <el-option
              v-for="venue in venueList"
              :key="venue.venueId"
              :label="venue.venueName"
              :value="venue.venueId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="autoScheduleForm.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排班周数" prop="weeks">
          <el-input-number
            v-model="autoScheduleForm.weeks"
            :min="1"
            :max="52"
            :step="1"
          />
        </el-form-item>
        <el-form-item label="值班时段">
          <el-table :data="dutyTimeConfigList" border style="width: 100%">
            <el-table-column label="开始时间" width="100" prop="startTime" />
            <el-table-column label="结束时间" width="100" prop="endTime" />
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            :loading="autoScheduleSubmitting"
            :disabled="autoScheduleSubmitting"
            @click="submitAutoSchedule"
            >开始排班</el-button
          >
          <el-button @click="cancelAutoSchedule">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  listDutySchedule,
  getDutySchedule,
  addDutySchedule,
  updateDutySchedule,
  delDutySchedule,
  autoScheduleByConfig,
} from "@/api/manage/dutySchedule";
import { listVenue } from "@/api/manage/venue";
import { getDutyTimeConfigByVenue } from "@/api/manage/dutyTimeConfig";
import RightToolbar from "@/components/RightToolbar";
import Pagination from "@/components/Pagination";

export default {
  name: "DutySchedule",
  components: {
    RightToolbar,
    Pagination,
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 值班表列表
      dutyScheduleList: [],
      // 场馆列表
      venueList: [],
      // 值班时间配置列表
      dutyTimeConfigList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 自动排班对话框
      autoScheduleOpen: false,
      autoScheduleSubmitting: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        venueId: null,
        startTime: null,
        endTime: null,
      },
      // 表单参数
      form: {
        dutyId: null,
        studentId: null,
        venueId: null,
        startTime: null,
        endTime: null,
        remark: null,
      },
      // 自动排班表单
      autoScheduleForm: {
        venueId: null,
        startDate: new Date(),
        weeks: 19,
      },
      // 表单校验
      rules: {
        studentId: [
          { required: true, message: "学号不能为空", trigger: "blur" },
        ],
        venueId: [{ required: true, message: "场馆不能为空", trigger: "blur" }],
        startTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" },
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" },
        ],
      },
      // 自动排班表单校验
      autoScheduleRules: {
        venueId: [{ required: true, message: "场馆不能为空", trigger: "blur" }],
        startDate: [
          { required: true, message: "开始日期不能为空", trigger: "blur" },
        ],
        weeks: [
          { required: true, message: "排班周数不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
    this.getVenueList();
  },
  methods: {
    /** 查询值班表列表 */
    getList() {
      this.loading = true;
      listDutySchedule(this.queryParams).then((response) => {
        this.dutyScheduleList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询场馆列表 */
    getVenueList() {
      listVenue().then((response) => {
        this.venueList = response.rows;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.dutyId);
      this.single = selection.length !== 1;
      this.multiple = selection.length < 1;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.resetForm();
      this.open = true;
      this.title = "新增值班表";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.resetForm();
      const dutyId = row.dutyId || this.ids;
      getDutySchedule(dutyId).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改值班表";
      });
    },
    /** 自动排班按钮操作 */
    handleAutoSchedule() {
      this.autoScheduleOpen = true;
      this.autoScheduleForm.venueId = null;
      this.autoScheduleForm.startDate = new Date();
      this.autoScheduleForm.weeks = 19;
      this.dutyTimeConfigList = [];
    },
    /** 场馆变更事件 */
    handleVenueChange(venueId) {
      if (venueId) {
        getDutyTimeConfigByVenue(venueId).then((response) => {
          this.dutyTimeConfigList = response.data || [];
        });
      } else {
        this.dutyTimeConfigList = [];
      }
    },
    /** 提交自动排班 */
    submitAutoSchedule() {
      if (this.autoScheduleSubmitting) {
        return;
      }
      this.$refs.autoScheduleForm.validate((valid) => {
        if (valid) {
          this.autoScheduleSubmitting = true;
          autoScheduleByConfig(this.autoScheduleForm)
            .then(() => {
              this.$modal.msgSuccess("排班成功");
              this.autoScheduleOpen = false;
              this.getList();
            })
            .catch(() => {})
            .finally(() => {
              this.autoScheduleSubmitting = false;
            });
        }
      });
    },
    /** 取消自动排班 */
    cancelAutoSchedule() {
      this.autoScheduleOpen = false;
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this.form.dutyId != null) {
            updateDutySchedule(this.form).then((response) => {
              if (response.code === 200) {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          } else {
            addDutySchedule(this.form).then((response) => {
              if (response.code === 200) {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              } else {
                this.$modal.msgError(response.msg);
              }
            });
          }
        }
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.resetForm();
    },
    /** 重置表单 */
    resetForm(formName) {
      if (formName) {
        this.$refs[formName].resetFields();
      } else {
        this.form = {
          dutyId: null,
          studentId: null,
          venueId: null,
          startTime: null,
          endTime: null,
          remark: null,
        };
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const dutyIds = row.dutyId || this.ids;
      this.$confirm(
        '是否确认删除值班表编号为"' + dutyIds + '"的数据项?',
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        },
      )
        .then(function () {
          return delDutySchedule(dutyIds);
        })
        .then((response) => {
          if (response.code === 200) {
            this.$modal.msgSuccess("删除成功");
            this.getList();
          } else {
            this.$modal.msgError(response.msg);
          }
        });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "manage/dutySchedule/export",
        { ...this.queryParams },
        `dutySchedule_${new Date().getTime()}.xlsx`,
      );
    },
    /** 分页事件处理 */
    handlePagination(pagination) {
      this.queryParams.pageNum = pagination.page;
      this.queryParams.pageSize = pagination.limit;
      this.getList();
    },
    /** 获取场馆名称 */
    getVenueName(venueId) {
      const venue = this.venueList.find((item) => item.venueId === venueId);
      return venue ? venue.venueName : "";
    },
  },
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.dialog-footer {
  text-align: center;
}
</style>
