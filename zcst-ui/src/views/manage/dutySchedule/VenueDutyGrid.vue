<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ venueName }} - 值班排班表</span>
          <div class="header-actions">
            <el-date-picker
              v-model="currentWeekStart"
              type="week"
              format="yyyy 第 WW 周"
              placeholder="选择周"
              :picker-options="{ firstDayOfWeek: 1 }"
              @change="handleWeekChange"
            />
            <el-button type="success" plain @click="handleAutoSchedule" v-hasPermi="['manage:dutySchedule:add']">
              自动排班
            </el-button>
          </div>
        </div>
      </template>

      <div class="schedule-grid-container" v-loading="loading">
        <table class="schedule-table">
          <thead>
            <tr>
              <th class="time-column">时间 / 星期</th>
              <th v-for="(day, index) in weekDays" :key="index" class="day-column">
                <div class="day-label">{{ day.label }}</div>
                <div class="day-date">{{ day.dateStr }}</div>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="slot in timeSlots" :key="slot.configId">
              <td class="time-slot-label">
                {{ slot.startTime }} - {{ slot.endTime }}
              </td>
              <td v-for="day in weekDays" :key="day.dateStr" class="schedule-cell">
                <div v-for="(duty, index) in getDutyForSlot(day.dateStr, slot)" :key="duty.dutyId || index" class="duty-item">
                  <el-select
                    v-model="duty.studentId"
                    placeholder="选择学生"
                    size="small"
                    filterable
                    @change="(val) => handleStudentChange(val, duty, day.dateStr, slot)"
                    @focus="loadAvailableStudents(day.dateStr, slot)"
                  >
                    <el-option
                      v-for="student in availableStudentsMap[getSlotKey(day.dateStr, slot)] || []"
                      :key="student.studentId"
                      :label="student.name"
                      :value="student.studentId"
                    >
                      <span style="float: left">{{ student.name }}</span>
                      <span style="float: right; color: #8492a6; font-size: 13px">{{ student.studentId }}</span>
                    </el-option>
                    <!-- 确保当前选中的学生也在列表中，即使他可能有课 -->
                    <el-option
                      v-if="duty.studentId && !isStudentInList(duty.studentId, availableStudentsMap[getSlotKey(day.dateStr, slot)])"
                      :key="duty.studentId"
                      :label="duty.studentName"
                      :value="duty.studentId"
                    />
                  </el-select>
                </div>
                <!-- 如果该时段没人，显示一个添加按钮或者默认显示两个下拉框（根据需求，通常时段有固定人数） -->
                <div v-if="getDutyForSlot(day.dateStr, slot).length < 2" class="duty-item">
                   <el-button type="text" size="small" icon="el-icon-plus" @click="addDutySlot(day.dateStr, slot)">添加人员</el-button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>
  </div>
</template>

<script>
import { listDutySchedule, updateDutySchedule, addDutySchedule, getAvailableStudents, autoScheduleByConfig } from "@/api/manage/dutySchedule";
import { listDutyTimeConfig } from "@/api/manage/dutyTimeConfig";
import { listVenue } from "@/api/manage/venue";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "VenueDutyGrid",
  props: {
    venueId: {
      type: [Number, String],
      required: true
    }
  },
  data() {
    return {
      loading: false,
      venueName: "",
      currentWeekStart: new Date(),
      timeSlots: [],
      dutySchedules: [],
      availableStudentsMap: {}, // key: date_startTime_endTime, value: students[]
      weekDays: []
    };
  },
  created() {
    this.initData();
  },
  methods: {
    async initData() {
      this.loading = true;
      try {
        await this.getVenueInfo();
        await this.getTimeSlots();
        this.generateWeekDays();
        await this.getDutySchedules();
      } finally {
        this.loading = false;
      }
    },
    getVenueInfo() {
      return listVenue({ venueId: this.venueId }).then(response => {
        const venue = response.rows.find(v => v.venueId == this.venueId);
        if (venue) this.venueName = venue.venueName;
      });
    },
    getTimeSlots() {
      return listDutyTimeConfig({ venueId: this.venueId, isEnable: 1 }).then(response => {
        this.timeSlots = response.rows;
      });
    },
    generateWeekDays() {
      const now = new Date(this.currentWeekStart);
      const day = now.getDay();
      const diff = now.getDate() - day + (day === 0 ? -6 : 1); // 调整到周一
      const startOfWeek = new Date(now.setDate(diff));
      
      const days = [];
      const labels = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"];
      for (let i = 0; i < 7; i++) {
        const d = new Date(startOfWeek);
        d.setDate(startOfWeek.getDate() + i);
        days.push({
          label: labels[i],
          dateStr: parseTime(d, '{y}-{m}-{d}')
        });
      }
      this.weekDays = days;
    },
    getDutySchedules() {
      const startTime = this.weekDays[0].dateStr + " 00:00:00";
      const endTime = this.weekDays[6].dateStr + " 23:59:59";
      return listDutySchedule({
        venueId: this.venueId,
        startTime,
        endTime,
        pageNum: 1,
        pageSize: 1000
      }).then(response => {
        this.dutySchedules = response.rows;
      });
    },
    getDutyForSlot(dateStr, slot) {
      return this.dutySchedules.filter(d => {
        const dStart = parseTime(d.startTime, '{y}-{m}-{d} {h}:{i}');
        const sStart = dateStr + " " + slot.startTime.substring(0, 5);
        return dStart === sStart;
      });
    },
    getSlotKey(dateStr, slot) {
      return `${dateStr}_${slot.startTime}_${slot.endTime}`;
    },
    loadAvailableStudents(dateStr, slot) {
      const key = this.getSlotKey(dateStr, slot);
      if (this.availableStudentsMap[key]) return;

      const startTime = dateStr + " " + slot.startTime;
      const endTime = dateStr + " " + slot.endTime;
      
      getAvailableStudents({
        venueId: this.venueId,
        startTime,
        endTime
      }).then(response => {
        this.$set(this.availableStudentsMap, key, response.data);
      });
    },
    isStudentInList(studentId, list) {
      if (!list) return false;
      return list.some(s => s.studentId === studentId);
    },
    handleStudentChange(studentId, duty, dateStr, slot) {
      if (duty.dutyId) {
        // 更新
        const data = {
          dutyId: duty.dutyId,
          studentId: studentId,
          venueId: this.venueId,
          startTime: duty.startTime,
          endTime: duty.endTime
        };
        updateDutySchedule(data).then(() => {
          this.$modal.msgSuccess("更新成功");
          this.getDutySchedules();
        });
      } else {
        // 新增
        const data = {
          studentId: studentId,
          venueId: this.venueId,
          startTime: dateStr + " " + slot.startTime,
          endTime: dateStr + " " + slot.endTime
        };
        addDutySchedule(data).then(() => {
          this.$modal.msgSuccess("添加成功");
          this.getDutySchedules();
        });
      }
    },
    addDutySlot(dateStr, slot) {
      // 临时添加一个空的排班项，等待选择学生
      this.dutySchedules.push({
        studentId: "",
        studentName: "",
        venueId: this.venueId,
        startTime: dateStr + " " + slot.startTime,
        endTime: dateStr + " " + slot.endTime
      });
      this.loadAvailableStudents(dateStr, slot);
    },
    handleWeekChange() {
      this.generateWeekDays();
      this.getDutySchedules();
      this.availableStudentsMap = {};
    },
    handleAutoSchedule() {
      this.$confirm("自动排班将根据现有时间配置重新分配学生，是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.loading = true;
        autoScheduleByConfig({
          venueId: this.venueId,
          startDate: this.weekDays[0].dateStr,
          weeks: 1
        }).then(() => {
          this.$modal.msgSuccess("自动排班成功");
          this.getDutySchedules();
        }).finally(() => {
          this.loading = false;
        });
      });
    }
  }
};
</script>

<style scoped lang="scss">
.schedule-grid-container {
  overflow-x: auto;
  margin-top: 20px;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;

  th, td {
    border: 1px solid #ebeef5;
    padding: 12px 8px;
    text-align: center;
  }

  thead {
    background-color: #f8f9fb;
    
    th {
      color: #606266;
      font-weight: 600;
    }
  }

  .time-column {
    width: 150px;
  }

  .day-column {
    min-width: 120px;
    
    .day-label {
      font-size: 16px;
      margin-bottom: 4px;
    }
    
    .day-date {
      font-size: 12px;
      color: #909399;
      font-weight: normal;
    }
  }

  .time-slot-label {
    background-color: #fdfdfd;
    font-weight: 500;
    color: #303133;
  }

  .schedule-cell {
    height: 80px;
    vertical-align: top;
    
    .duty-item {
      margin-bottom: 8px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .header-actions {
    display: flex;
    gap: 15px;
  }
}
</style>
