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
      <el-form-item label="状态" prop="isEnable">
        <el-select
          v-model="queryParams.isEnable"
          placeholder="请选择状态"
          style="width: 100px"
        >
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
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
          v-hasPermi="['manage:dutyTimeConfig:add']"
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
          v-hasPermi="['manage:dutyTimeConfig:edit']"
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
          v-hasPermi="['manage:dutyTimeConfig:remove']"
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
          v-hasPermi="['manage:dutyTimeConfig:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table
      v-loading="loading"
      :data="dutyTimeConfigList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column
        label="配置ID"
        width="80"
        align="center"
        prop="configId"
      />
      <el-table-column label="场馆" width="100" align="center" prop="venueId">
        <template v-slot="scope">
          {{ getVenueName(scope.row.venueId) }}
        </template>
      </el-table-column>
      <el-table-column
        label="开始时间"
        width="120"
        align="center"
        prop="startTime"
      />
      <el-table-column
        label="结束时间"
        width="120"
        align="center"
        prop="endTime"
      />
      <el-table-column label="状态" width="80" align="center" prop="isEnable">
        <template v-slot="scope">
          <el-switch
            v-model="scope.row.isEnable"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
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
            v-hasPermi="['manage:dutyTimeConfig:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['manage:dutyTimeConfig:remove']"
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

    <!-- 新增或修改值班时间配置对话框 -->
    <el-dialog :title="title" v-model="open" width="400px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
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
          <el-time-picker
            v-model="form.startTime"
            format="HH:mm"
            value-format="HH:mm:ss"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker
            v-model="form.endTime"
            format="HH:mm"
            value-format="HH:mm:ss"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="isEnable">
          <el-switch
            v-model="form.isEnable"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            :loading="submitLoading"
            :disabled="submitLoading"
            @click="submitForm"
          >
            确定
          </el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  listDutyTimeConfig,
  getDutyTimeConfig,
  addDutyTimeConfig,
  updateDutyTimeConfig,
  delDutyTimeConfig,
} from "@/api/manage/dutyTimeConfig";
import { listVenue } from "@/api/manage/venue";
import RightToolbar from "@/components/RightToolbar";
import Pagination from "@/components/Pagination";

export default {
  name: "DutyTimeConfig",
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
      // 值班时间配置列表
      dutyTimeConfigList: [],
      // 场馆列表
      venueList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      submitLoading: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        venueId: null,
        isEnable: null,
      },
      // 表单参数
      form: {
        configId: null,
        venueId: null,
        startTime: null,
        endTime: null,
        isEnable: 1,
      },
      // 表单校验
      rules: {
        venueId: [{ required: true, message: "场馆不能为空", trigger: "blur" }],
        startTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" },
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
    this.getVenueList();
  },
  methods: {
    /** 查询值班时间配置列表 */
    getList() {
      this.loading = true;
      listDutyTimeConfig(this.queryParams).then((response) => {
        this.dutyTimeConfigList = response.rows;
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
      this.ids = selection.map((item) => item.configId);
      this.single = selection.length !== 1;
      this.multiple = selection.length < 1;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.resetForm();
      this.open = true;
      this.title = "新增值班时间配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.resetForm();
      const configId = row.configId || this.ids;
      getDutyTimeConfig(configId).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改值班时间配置";
      });
    },
    /** 状态变更操作 */
    handleStatusChange(row) {
      const data = {
        configId: row.configId,
        isEnable: row.isEnable,
      };
      updateDutyTimeConfig(data).then((response) => {
        if (response.code === 200) {
          this.$modal.msgSuccess("状态更新成功");
        } else {
          this.$modal.msgError(response.msg);
          row.isEnable = !row.isEnable;
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      if (this.submitLoading) {
        return;
      }
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.submitLoading = true;
          if (this.form.configId != null) {
            updateDutyTimeConfig(this.form)
              .then(() => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              })
              .finally(() => {
                this.submitLoading = false;
              });
          } else {
            addDutyTimeConfig(this.form)
              .then(() => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              })
              .finally(() => {
                this.submitLoading = false;
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
          configId: null,
          venueId: null,
          startTime: null,
          endTime: null,
          isEnable: 1,
        };
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const configIds = row.configId || this.ids;
      this.$confirm(
        '是否确认删除值班时间配置编号为"' + configIds + '"的数据项?',
        "警告",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        },
      )
        .then(function () {
          return delDutyTimeConfig(configIds);
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
        "manage/dutyTimeConfig/export",
        { ...this.queryParams },
        `dutyTimeConfig_${new Date().getTime()}.xlsx`,
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
