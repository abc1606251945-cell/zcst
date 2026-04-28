<template>
  <div class="complete-info">
    <el-card class="card-container">
      <template #header>
        <div class="card-header">
          <h2>学生信息完善</h2>
          <p>请完成以下信息填写，以便系统为您提供更好的服务</p>
        </div>
      </template>
      
      <!-- 进度条 -->
      <div class="progress-container">
        <el-steps :active="currentStep" finish-status="success">
          <el-step title="基础信息" />
          <el-step title="教育信息" />
          <el-step title="其他信息" />
        </el-steps>
      </div>
      
      <!-- 表单内容 -->
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        class="info-form"
      >
        <!-- 基础信息 -->
        <div v-if="currentStep === 0" class="step-content">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="formData.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-select v-model="formData.gender" placeholder="请选择性别">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="formData.phone" placeholder="请输入联系电话" />
          </el-form-item>
        </div>
        
        <!-- 教育信息 -->
        <div v-if="currentStep === 1" class="step-content">
          <el-form-item label="专业" prop="majorId">
            <el-select v-model="formData.majorId" placeholder="请选择专业">
              <el-option 
                v-for="major in majors" 
                :key="major.majorId" 
                :label="major.majorName" 
                :value="major.majorId" 
              />
            </el-select>
          </el-form-item>
          <el-form-item label="年级" prop="grade">
            <el-input v-model="formData.grade" placeholder="请输入年级，如：2026级" />
          </el-form-item>
          <el-form-item label="班级" prop="className">
            <el-input v-model="formData.className" placeholder="请输入班级，如：软件工程1班" />
          </el-form-item>
        </div>
        
        <!-- 其他信息 -->
        <div v-if="currentStep === 2" class="step-content">
          <el-form-item label="所在场馆" prop="venueId">
            <el-select v-model="formData.venueId" placeholder="请选择所在场馆">
              <el-option label="思齐馆" value="1" />
              <el-option label="弘毅馆" value="2" />
              <el-option label="心缘馆" value="3" />
              <el-option label="笃学馆" value="4" />
              <el-option label="知行馆" value="5" />
              <el-option label="国防教育体验馆" value="6" />
            </el-select>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input 
              v-model="formData.remark" 
              type="textarea" 
              placeholder="请输入其他需要补充的信息" 
              rows="3"
            />
          </el-form-item>
        </div>
        
        <!-- 按钮组 -->
        <div class="button-group">
          <el-button 
            v-if="currentStep > 0" 
            @click="prevStep"
            size="large"
          >
            上一步
          </el-button>
          <el-button 
            v-if="currentStep < 2" 
            @click="nextStep"
            size="large"
          >
            下一步
          </el-button>
          <el-button 
            v-if="currentStep < 2" 
            @click="saveDraft"
            size="large"
            type="info"
          >
            保存草稿
          </el-button>
          <el-button 
            v-if="currentStep === 2" 
            @click="submitForm"
            size="large"
            type="primary"
          >
            提交
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';
import { getStudentInfo, updateStudentInfo } from '@/api/manage/student';

const router = useRouter();
const formRef = ref(null);
const currentStep = ref(0);
const majors = ref([]);

const formData = reactive({
  name: '',
  gender: '',
  phone: '',
  majorId: null,
  grade: '',
  className: '',
  venueId: null,
  remark: '',
});

const formRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度必须介于 2 和 20 之间', trigger: 'blur' },
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' },
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  majorId: [
    { required: true, message: '请选择专业', trigger: 'change' },
  ],
  grade: [
    { required: true, message: '请输入年级', trigger: 'blur' },
  ],
  className: [
    { required: true, message: '请输入班级', trigger: 'blur' },
  ],
  venueId: [
    { required: true, message: '请选择所在场馆', trigger: 'change' },
  ],
};

// 获取学生信息
function getStudentData() {
  getStudentInfo().then(res => {
    if (res.code === 200) {
      const data = res.data;
      formData.name = data.name || '';
      formData.gender = data.gender || '';
      formData.phone = data.phone || '';
      formData.majorId = data.majorId || null;
      formData.grade = data.grade || '';
      formData.className = data.className || '';
      formData.venueId = data.venueId || null;
      formData.remark = data.remark || '';
    }
  });
}

// 获取专业列表
function getMajors() {
  // 这里应该从API获取专业列表，暂时使用模拟数据
  majors.value = [
    { majorId: 1, majorName: '软件工程' },
    { majorId: 2, majorName: '计算机科学与技术' },
    { majorId: 3, majorName: '电子信息工程' },
    { majorId: 4, majorName: '通信工程' },
    { majorId: 5, majorName: '会计学' },
    { majorId: 6, majorName: '金融学' },
  ];
}

// 上一步
function prevStep() {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
}

// 下一步
function nextStep() {
  formRef.value.validate((valid) => {
    if (valid) {
      if (currentStep.value < 2) {
        currentStep.value++;
      }
    }
  });
}

// 保存草稿
function saveDraft() {
  formRef.value.validate((valid) => {
    if (valid) {
      // 保存到本地存储
      localStorage.setItem('studentInfoDraft', JSON.stringify({
        data: formData,
        step: currentStep.value,
        timestamp: new Date().getTime()
      }));
      ElMessage.success('草稿保存成功');
    }
  });
}

// 提交表单
function submitForm() {
  formRef.value.validate((valid) => {
    if (valid) {
      updateStudentInfo(formData).then(res => {
        if (res.code === 200) {
          ElMessageBox.alert('信息完善成功！', '系统提示', {
            type: 'success',
            callback: () => {
              // 清除草稿
              localStorage.removeItem('studentInfoDraft');
              // 跳转到首页
              router.push('/');
            }
          });
        } else {
          ElMessage.error(res.msg || '信息完善失败');
        }
      });
    }
  });
}

// 加载草稿
function loadDraft() {
  const draft = localStorage.getItem('studentInfoDraft');
  if (draft) {
    try {
      const parsedDraft = JSON.parse(draft);
      const now = new Date().getTime();
      const sevenDays = 7 * 24 * 60 * 60 * 1000;
      
      // 检查草稿是否过期（7天）
      if (now - parsedDraft.timestamp < sevenDays) {
        // 恢复表单数据
        Object.assign(formData, parsedDraft.data);
        currentStep.value = parsedDraft.step;
        ElMessage.info('已加载上次保存的草稿');
      } else {
        // 草稿过期，清除
        localStorage.removeItem('studentInfoDraft');
        ElMessage.info('草稿已过期，请重新填写');
      }
    } catch (error) {
      // 解析失败，清除草稿
      localStorage.removeItem('studentInfoDraft');
    }
  }
}

onMounted(() => {
  getStudentData();
  getMajors();
  loadDraft();
});
</script>

<style lang="scss" scoped>
.complete-info {
  padding: 20px;
  min-height: calc(100vh - 60px);
  background-color: #f5f7fa;
}

.card-container {
  max-width: 800px;
  margin: 0 auto;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  text-align: center;
  h2 {
    margin: 0 0 10px 0;
    color: #303133;
  }
  p {
    margin: 0;
    color: #606266;
    font-size: 14px;
  }
}

.progress-container {
  margin: 30px 0;
}

.info-form {
  .step-content {
    margin: 20px 0;
  }
  .button-group {
    margin-top: 40px;
    text-align: center;
    button {
      margin: 0 10px;
    }
  }
}
</style>