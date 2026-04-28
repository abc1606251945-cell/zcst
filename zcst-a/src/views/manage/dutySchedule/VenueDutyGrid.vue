<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ venueName }} - 值班排班表</span>
          <div class="header-actions">
            <el-date-picker
              v-model="currentWeekStart"
              type="date"
              format="YYYY-MM-DD"
              placeholder="选择日期（定位到周）"
              @change="handleWeekChange"
            />
            <el-button
              v-if="!readOnly"
              type="success"
              plain
              @click="openAutoSchedule"
              v-hasPermi="['manage:dutySchedule:add']"
            >
              自动排班
            </el-button>
          </div>
        </div>
      </template>

      <div class="schedule-grid-container" v-loading="loading">
        <div v-if="!displaySlots.length" class="empty-tip">
          <div class="empty-title">当前周没有可展示的排班数据</div>
          <div class="empty-subtitle">
            请先配置值班时段，或检查当前选择的周是否有值班记录
          </div>
        </div>

        <table v-else class="schedule-table">
          <thead>
            <tr>
              <th class="time-column">时间 / 星期</th>
              <th
                v-for="(day, index) in weekDays"
                :key="index"
                class="day-column"
              >
                <div class="day-label">{{ day.label }}</div>
                <div class="day-date">{{ day.dateStr }}</div>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="slot in displaySlots"
              :key="slot.configId || `${slot.startTime}-${slot.endTime}`"
            >
              <td class="time-slot-label">
                {{ slot.startTime }} - {{ slot.endTime }}
              </td>
              <td
                v-for="day in weekDays"
                :key="day.dateStr"
                class="schedule-cell"
              >
                <div v-if="getCellDuties(day.dateStr, slot).length > 0">
                  <div
                    v-for="(duty, index) in getCellDuties(day.dateStr, slot)"
                    :key="duty.dutyId || duty._tempKey || index"
                    class="duty-item"
                  >
                    <div v-if="readOnly" class="read-only-duty">
                      {{ formatDutyLabel(duty) }}
                    </div>
                    <el-select
                      v-else
                      v-model="duty.studentId"
                      placeholder="选择学生"
                      size="small"
                      filterable
                      :teleported="false"
                      @change="(val) => handleStudentChange(val, duty, day.dateStr, slot)"
                      @focus="loadAvailableStudents(day.dateStr, slot)"
                    >
                      <el-option
                        v-for="student in availableStudentsMap[getSlotKey(day.dateStr, slot)] || []"
                        :key="student.studentId"
                        :label="formatStudentLabel(student)"
                        :value="student.studentId"
                      >
                        <span style="float: left">{{ student.name }}</span>
                        <span style="float: right; color: #8492a6; font-size: 13px"
                          >{{ student.phone }}</span
                        >
                      </el-option>
                      <el-option
                        v-if="duty.studentId && !isStudentInList(duty.studentId, availableStudentsMap[getSlotKey(day.dateStr, slot)])"
                        :key="duty.studentId"
                        :label="formatDutyLabel(duty)"
                        :value="duty.studentId"
                      />
                    </el-select>
                  </div>
                  <div v-if="!readOnly" class="duty-item">
                    <el-button
                      type="text"
                      size="small"
                      icon="el-icon-plus"
                      :disabled="isEmergencySlotAdded(day.dateStr, slot)"
                      @click="addEmergencyDutySlot(day.dateStr, slot)"
                      >添加值班人员</el-button
                    >
                  </div>
                </div>
                <div v-else class="no-duty">-</div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>

    <el-dialog
      v-if="!readOnly"
      title="自动重排"
      v-model="autoScheduleOpen"
      width="520px"
      append-to-body
    >
      <el-form label-width="90px">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="autoScheduleRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div>
          <el-button @click="autoScheduleOpen = false">取消</el-button>
          <el-button
            type="primary"
            :loading="autoScheduleLoading"
            @click="submitAutoSchedule"
            >确定</el-button
          >
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  listDutySchedule,
  updateDutySchedule,
  addDutySchedule,
  getAvailableStudents,
  autoScheduleByConfig,
} from "@/api/manage/dutySchedule";
import { listDutyTimeConfig } from "@/api/manage/dutyTimeConfig";
import { listVenue } from "@/api/manage/venue";
import { parseTime } from "@/utils/ruoyi";

const toHm = (value) => {
  if (value === null || value === undefined) return "";
  if (value instanceof Date) return parseTime(value, "{h}:{i}");
  const s = String(value);
  if (!s) return "";
  if (s.includes(" ")) return parseTime(s, "{h}:{i}");
  return s.length >= 5 ? s.slice(0, 5) : s;
};

export default {
  name: "VenueDutyGrid",
  props: {
    venueId: {
      type: [Number, String],
      required: true,
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    displaySlots() {
      const map = new Map();
      const addSlot = (slot, prefer) => {
        const start = toHm(slot?.startTime);
        const end = toHm(slot?.endTime);
        if (!start || !end) return;
        const key = `${start}-${end}`;
        const normalized = { ...slot, startTime: start, endTime: end };
        if (!map.has(key) || prefer) map.set(key, normalized);
      };
      this.derivedSlots.forEach((s) => addSlot(s, false));
      this.timeSlots.forEach((s) => addSlot(s, true));
      const arr = Array.from(map.values());
      arr.sort((a, b) => {
        const aKey = `${toHm(a.startTime)}-${toHm(a.endTime)}`;
        const bKey = `${toHm(b.startTime)}-${toHm(b.endTime)}`;
        return aKey.localeCompare(bKey);
      });
      return arr;
    },
  },
  data() {
    return {
      loading: false,
      venueName: "",
      currentWeekStart: new Date(),
      timeSlots: [],
      dutySchedules: [],
      derivedSlots: [],
      availableStudentsMap: {},
      weekDays: [],
      cellStates: {},
      autoScheduleOpen: false,
      autoScheduleRange: [],
      autoScheduleLoading: false,
    };
  },
  created() {
    this.initData();
  },
  activated() {
    this.initData();
  },
  methods: {
    async initData() {
      this.loading = true;
      try {
        this.cellStates = {};
        this.availableStudentsMap = {};
        await this.getVenueInfo().catch((err) => {
          console.warn("获取场馆信息失败:", err);
        });
        await this.getTimeSlots().catch((err) => {
          console.warn("获取值班时间配置失败:", err);
        });
        this.generateWeekDays();
        await this.getDutySchedules().catch((err) => {
          console.warn("获取值班表失败:", err);
        });
        this.buildDerivedSlots();
      } catch (error) {
        console.error("初始化数据失败:", error);
      } finally {
        this.loading = false;
      }
    },
    getVenueInfo() {
      return listVenue().then((response) => {
        const list = response.rows ?? response.data ?? [];
        const venue = list.find(
          (v) => v.venueId == this.venueId,
        );
        if (venue) this.venueName = venue.venueName;
      });
    },
    getTimeSlots() {
      return listDutyTimeConfig({ venueId: this.venueId, isEnable: 1 }).then(
        (response) => {
          const list = response.rows ?? response.data ?? [];
          this.timeSlots = list.map((row) => ({
            ...row,
            startTime: toHm(row?.startTime),
            endTime: toHm(row?.endTime),
          }));
        },
      );
    },
    generateWeekDays() {
      const now = new Date(this.currentWeekStart);
      const day = now.getDay();
      const diff = now.getDate() - day + (day === 0 ? -6 : 1);
      const startOfWeek = new Date(now.setDate(diff));

      const labels = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"];
      const days = [];
      for (let i = 0; i < 7; i++) {
        const d = new Date(startOfWeek);
        d.setDate(startOfWeek.getDate() + i);
        days.push({
          label: labels[i],
          dateStr: parseTime(d, "{y}-{m}-{d}"),
        });
      }
      this.weekDays = days;
    },
    getDutySchedules() {
      if (!this.weekDays.length) {
        this.dutySchedules = [];
        return Promise.resolve();
      }
      return listDutySchedule({
        venueId: this.venueId,
        startTime: this.weekDays[0].dateStr + " 00:00:00",
        endTime: this.weekDays[6].dateStr + " 23:59:59",
        pageNum: 1,
        pageSize: 1000,
      }).then((response) => {
        this.dutySchedules = response.rows ?? response.data ?? [];
      });
    },
    getDutyForSlot(dateStr, slot) {
      return this.dutySchedules.filter((d) => {
        const dStart = parseTime(d.startTime, "{y}-{m}-{d}");
        if (dStart !== dateStr) return false;
        const slotStart = toHm(slot?.startTime);
        const slotEnd = toHm(slot?.endTime);
        const dutyStart = toHm(d?.startTime);
        const dutyEnd = toHm(d?.endTime);
        if (!slotEnd) return dutyStart === slotStart;
        return dutyStart === slotStart && dutyEnd === slotEnd;
      });
    },
    getSlotKey(dateStr, slot) {
      return `${dateStr}_${slot.startTime}_${slot.endTime}`;
    },
    getCellState(dateStr, slot) {
      const key = this.getSlotKey(dateStr, slot);
      if (!this.cellStates[key]) {
        this.cellStates[key] = { extraCount: 0, placeholders: [] };
      }
      return this.cellStates[key];
    },
    createPlaceholderDuty(dateStr, slot, index) {
      return {
        _tempKey: `${this.getSlotKey(dateStr, slot)}_${index}`,
        dutyId: null,
        studentId: "",
        studentName: "",
        studentPhone: "",
        venueId: Number(this.venueId),
        startTime: this.buildDateTime(dateStr, slot.startTime),
        endTime: this.buildDateTime(dateStr, slot.endTime),
      };
    },
    getCellDuties(dateStr, slot) {
      const existing = this.getDutyForSlot(dateStr, slot);
      return existing;
    },
    isEmergencySlotAdded(dateStr, slot) {
      return false;
    },
    addEmergencyDutySlot(dateStr, slot) {
      // 直接添加新的值班记录
      const duty = {
        _tempKey: `${this.getSlotKey(dateStr, slot)}_new`,
        dutyId: null,
        studentId: "",
        studentName: "",
        studentPhone: "",
        venueId: Number(this.venueId),
        startTime: this.buildDateTime(dateStr, slot.startTime),
        endTime: this.buildDateTime(dateStr, slot.endTime),
      };
      // 将新的值班记录添加到dutySchedules中
      this.dutySchedules.push(duty);
      // 加载可用学生列表
      this.loadAvailableStudents(dateStr, slot);
    },
    formatStudentLabel(student) {
      const name = student && student.name ? String(student.name) : "";
      const phone = student && student.phone ? String(student.phone) : "";
      return (
        (name + " " + phone).trim() ||
        (student && student.studentId ? String(student.studentId) : "")
      );
    },
    formatDutyLabel(duty) {
      const name = duty && duty.studentName ? String(duty.studentName) : "";
      const phone = duty && duty.studentPhone ? String(duty.studentPhone) : "";
      return (
        (name + " " + phone).trim() ||
        (duty && duty.studentId ? String(duty.studentId) : "")
      );
    },
    buildDateTime(dateStr, timeStr) {
      const t = String(timeStr || "");
      const time = t.length === 5 ? `${t}:00` : t;
      return `${dateStr} ${time}`;
    },
    buildDerivedSlots() {
      const map = new Map();
      for (const d of this.dutySchedules) {
        const start = toHm(d.startTime);
        const end = toHm(d.endTime);
        if (!start || !end) continue;
        const key = `${start}-${end}`;
        if (!map.has(key)) {
          map.set(key, {
            startTime: start,
            endTime: end,
          });
        }
      }
      const arr = Array.from(map.values());
      arr.sort((a, b) =>
        String(a.startTime).localeCompare(String(b.startTime)),
      );
      this.derivedSlots = arr;
    },
    loadAvailableStudents(dateStr, slot) {
      const key = this.getSlotKey(dateStr, slot);
      if (this.availableStudentsMap[key]) return;

      const startTime = this.buildDateTime(dateStr, slot.startTime);
      const endTime = this.buildDateTime(dateStr, slot.endTime);

      getAvailableStudents({
        venueId: Number(this.venueId),
        startTime,
        endTime,
      }).then((response) => {
        this.availableStudentsMap[key] = response.data || [];
      });
    },
    isStudentInList(studentId, list) {
      if (!list) return false;
      return list.some((s) => s.studentId === studentId);
    },
    handleStudentChange(studentId, duty, dateStr, slot) {
      if (!studentId) return;
      if (duty.dutyId) {
        const data = {
          dutyId: duty.dutyId,
          studentId: studentId,
          venueId: Number(this.venueId),
          startTime: duty.startTime,
          endTime: duty.endTime,
        };
        updateDutySchedule(data).then(() => {
          this.$modal.msgSuccess("更新成功");
          this.getDutySchedules().then(() => {
            this.buildDerivedSlots();
          });
        });
      } else {
        const data = {
          studentId: studentId,
          venueId: Number(this.venueId),
          startTime: this.buildDateTime(dateStr, slot.startTime),
          endTime: this.buildDateTime(dateStr, slot.endTime),
        };
        addDutySchedule(data).then(() => {
          this.$modal.msgSuccess("添加成功");
          const state = this.getCellState(dateStr, slot);
          state.extraCount = 0;
          this.getDutySchedules().then(() => {
            this.buildDerivedSlots();
          });
        });
      }
    },
    handleWeekChange() {
      this.generateWeekDays();
      this.availableStudentsMap = {};
      this.cellStates = {};
      this.getDutySchedules().then(() => {
        this.buildDerivedSlots();
      });
    },

    openAutoSchedule() {
      if (this.weekDays.length) {
        this.autoScheduleRange = [
          this.weekDays[0].dateStr,
          this.weekDays[6].dateStr,
        ];
      } else {
        const today = parseTime(new Date(), "{y}-{m}-{d}");
        this.autoScheduleRange = [today, today];
      }
      this.autoScheduleOpen = true;
    },

    submitAutoSchedule() {
      if (!this.autoScheduleRange || this.autoScheduleRange.length !== 2) {
        this.$modal.msgError("请选择开始和结束日期");
        return;
      }
      const [start, end] = this.autoScheduleRange;
      this.$confirm(
        "将先删除所选日期范围内该场馆的旧值班记录，再按当前时段配置重新排班，是否继续?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        },
      ).then(() => {
        this.autoScheduleLoading = true;
        autoScheduleByConfig({
          venueId: Number(this.venueId),
          startDate: start + " 00:00:00",
          endDate: end + " 23:59:59",
        })
          .then(() => {
            this.$modal.msgSuccess("自动重排成功");
            this.autoScheduleOpen = false;
            return this.getDutySchedules();
          })
          .then(() => {
            this.buildDerivedSlots();
          })
          .finally(() => {
            this.autoScheduleLoading = false;
          });
      });
    },
  },
};
</script>

<style scoped lang="scss">
.schedule-grid-container {
  overflow-x: auto;
  margin-top: 20px;
}

.empty-tip {
  padding: 32px 16px;
  text-align: center;
  color: #606266;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.empty-subtitle {
  font-size: 13px;
  color: #909399;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;

  th,
  td {
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
    height: 92px;
    vertical-align: top;

    .duty-item {
      margin-bottom: 8px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .read-only-duty {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 32px;
      padding: 0 6px;
      color: #303133;
      font-size: 13px;
      line-height: 18px;
      word-break: break-word;
    }

    .no-duty {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      color: #909399;
      font-size: 14px;
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
