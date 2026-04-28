<template>
  <div class="app-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="场馆ID">
          <el-input v-model="queryParams.venueId" placeholder="请输入场馆ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="operate-card">
      <el-row>
        <el-col :span="24">
          <el-button type="primary" plain @click="handleAdd">
            <i class="el-icon-plus"></i> 新增
          </el-button>
          <el-button type="danger" plain @click="handleDelete">
            <i class="el-icon-delete"></i> 删除
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="配置ID" prop="configId" width="100" align="center" />
        <el-table-column label="场馆ID" prop="venueId" width="100" align="center" />
        <el-table-column label="开始时间" prop="startTime" width="120" align="center" />
        <el-table-column label="结束时间" prop="endTime" width="120" align="center" />
        <el-table-column label="是否启用" prop="isEnable" width="100" align="center">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.isEnable" :active-value="1" :inactive-value="0" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdAt" width="180" align="center" />
        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleEdit(scope.row)" icon="el-icon-edit">编辑</el-button>
            <el-button type="danger" size="mini" @click="handleDelete(scope.row.configId)" icon="el-icon-delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="场馆ID" prop="venueId">
          <el-input v-model="form.venueId" placeholder="请输入场馆ID" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker v-model="form.startTime" format="HH:mm" placeholder="请选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker v-model="form.endTime" format="HH:mm" placeholder="请选择结束时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="form.isEnable" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDutyTimeConfig, getDutyTimeConfig, delDutyTimeConfig, addDutyTimeConfig, updateDutyTimeConfig } from "@/api/manage/dutyTimeConfig";

export default {
  name: "DutyTimeConfig",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 配置表格数据
      configList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        venueId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        venueId: [
          { required: true, message: "场馆ID不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询配置列表 */
    getList() {
      this.loading = true;
      listDutyTimeConfig(this.queryParams).then(response => {
        this.configList = response.rows;
        this.total = response.total;
        this.loading = false;
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
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        configId: null,
        venueId: null,
        startTime: null,
        endTime: null,
        isEnable: 1
      };
      this.resetForm("form");
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "新增值班时间配置";
    },
    /** 修改按钮操作 */
    handleEdit(row) {
      this.reset();
      const configId = row.configId || row;
      getDutyTimeConfig(configId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改值班时间配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.configId != null) {
            updateDutyTimeConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDutyTimeConfig(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const configIds = row.configId || this.ids;
      this.$modal.confirm("是否确认删除值班时间配置?").then(function() {
        return delDutyTimeConfig(configIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 状态修改 */
    handleStatusChange(row) {
      const isEnable = row.isEnable;
      updateDutyTimeConfig(row).then(response => {
        this.$modal.msgSuccess("状态修改成功");
      }).catch(() => {
        row.isEnable = !isEnable;
        this.$modal.msgError("状态修改失败");
      });
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.configId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    }
  }
};
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.operate-card {
  margin-bottom: 16px;
}

.table-card {
  margin-bottom: 16px;
}

.dialog-footer {
  text-align: right;
}
</style>
