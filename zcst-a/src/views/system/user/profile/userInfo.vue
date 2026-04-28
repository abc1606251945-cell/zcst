<template>
   <el-form v-if="isStudent" ref="userRef" :model="form" :rules="rules" label-width="90px" class="student-profile-form">
      <el-row :gutter="36" class="student-profile-grid">
         <el-col :span="9" :xs="24">
            <el-card shadow="never" class="student-panel student-panel-left">
               <div class="student-section-title">个人信息</div>
               <div class="student-avatar-wrap">
                  <userAvatar />
                  <div class="student-avatar-name">{{ form.realName || form.nickName || "-" }}</div>
               </div>
               <el-form-item label="姓名">
                  <el-input v-model="form.realName" readonly />
               </el-form-item>
               <el-form-item label="性别">
                  <el-radio-group v-model="form.sex">
                     <el-radio value="0">男</el-radio>
                     <el-radio value="1">女</el-radio>
                  </el-radio-group>
               </el-form-item>
               <el-form-item label="手机号" prop="phonenumber">
                  <el-input v-model="form.phonenumber" maxlength="11" />
               </el-form-item>
               <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" maxlength="50" />
               </el-form-item>
            </el-card>
         </el-col>
         <el-col :span="15" :xs="24">
            <el-card shadow="never" class="student-panel student-panel-right">
               <div class="student-section-title">基本资料</div>
               <el-form-item label="学号">
                  <el-input v-model="form.studentId" readonly />
               </el-form-item>
               <el-form-item label="学院">
                  <el-input v-model="form.collegeName" maxlength="50" placeholder="请输入学院" />
               </el-form-item>
               <el-form-item label="专业">
                  <el-input v-model="form.majorName" maxlength="50" placeholder="请输入专业" />
               </el-form-item>
               <el-form-item label="值班地点" prop="venueId">
                  <el-select v-model="form.venueId" placeholder="请选择场馆" style="width: 100%">
                     <el-option
                       v-for="item in venueOptions"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value"
                     />
                  </el-select>
               </el-form-item>
               <el-form-item label="修改密码">
                  <el-input model-value="" placeholder="出于安全考虑不显示密码" readonly>
                     <template #suffix>
                        <el-button link type="primary" class="pwd-link-btn" @click="openPasswordDialog">点击修改</el-button>
                     </template>
                  </el-input>
               </el-form-item>
               <el-form-item label="教务系统" class="edu-entry-item">
                  <div class="edu-entry-wrap">
                     <el-button
                       type="primary"
                       class="edu-login-btn"
                       plain
                       @click="openEduDialog"
                     >{{ eduLoginState.loggedIn ? "重新登录" : "登录" }}</el-button>
                     <span class="edu-status-text">{{ eduLoginState.loggedIn ? "已登录" : "未登录" }}</span>
                  </div>
               </el-form-item>
            </el-card>
         </el-col>
      </el-row>
      <el-form-item class="student-submit-row">
         <el-button type="primary" class="student-submit-btn" @click="submit">保存资料</el-button>
      </el-form-item>
   </el-form>

   <el-form v-else ref="userRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="姓名">
         <el-input v-model="form.realName" readonly />
      </el-form-item>
      <el-form-item label="性别">
         <el-radio-group v-model="form.sex">
            <el-radio value="0">男</el-radio>
            <el-radio value="1">女</el-radio>
         </el-radio-group>
      </el-form-item>
      <el-form-item label="职称">
         <el-input :model-value="titleDisplay" readonly />
      </el-form-item>
      <el-form-item label="场馆" prop="venueId">
         <el-select v-model="form.venueId" placeholder="请选择场馆" style="width: 100%">
            <el-option
              v-for="item in venueOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
         </el-select>
      </el-form-item>
      <el-form-item label="手机号码" prop="phonenumber">
         <el-input v-model="form.phonenumber" maxlength="11" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
         <el-input v-model="form.email" maxlength="50" />
      </el-form-item>
      <el-form-item>
         <el-button type="primary" @click="submit">保存</el-button>
      </el-form-item>
   </el-form>

   <el-dialog v-model="passwordDialogVisible" title="修改密码" width="480px" append-to-body destroy-on-close>
      <resetPwd embedded />
   </el-dialog>

   <el-dialog
     v-model="eduDialogVisible"
     title="登录教务系统"
     width="560px"
     append-to-body
     destroy-on-close
   >
      <div class="edu-dialog-box">
         <el-form ref="eduLoginRef" :model="eduLoginForm" :rules="eduLoginRules" label-width="0">
            <el-form-item prop="account">
               <el-input v-model="eduLoginForm.account" placeholder="请输入账号" clearable>
                  <template #prepend>账号</template>
               </el-input>
            </el-form-item>
            <el-form-item prop="password">
               <el-input
                 v-model="eduLoginForm.password"
                 placeholder="请输入教务系统密码"
                 show-password
                 clearable
               >
                  <template #prepend>密码</template>
               </el-input>
            </el-form-item>
            <el-button
              type="primary"
              class="edu-submit-btn"
              :loading="eduSubmitting"
              @click="submitEduLogin"
            >{{ eduSubmitting ? "登录中..." : "登录" }}</el-button>
         </el-form>
      </div>
   </el-dialog>
</template>

<script setup>
import { updateUserProfile } from "@/api/system/user"
import { getStudentInfo, updateStudentInfo, getAcademicAuthStatus, loginAcademicAuth } from "@/api/manage/student"
import { listVenue } from "@/api/manage/venue"
import useUserStore from "@/store/modules/user"
import resetPwd from "./resetPwd"
import userAvatar from "./userAvatar"

const props = defineProps({
  user: {
    type: Object
  },
  postGroup: {
    type: String,
    default: ""
  },
  showPasswordFirst: {
    type: Boolean,
    default: false
  }
})

const { proxy } = getCurrentInstance()
const userStore = useUserStore()

const form = ref({})
const passwordDialogVisible = ref(false)
const eduDialogVisible = ref(false)
const eduSubmitting = ref(false)
const eduLoginRef = ref()
const eduLoginForm = reactive({
  account: "",
  password: "",
  syncCloud: true,
})
const eduLoginState = reactive({
  loggedIn: false,
})
const roleKeys = computed(() => (userStore.roles || []).map((role) => String(role || "").toLowerCase()))
const isStudent = computed(() => {
  const accountType = String(userStore.accountType || "").toLowerCase()
  if (accountType === "student") {
    return true
  }
  if (accountType === "admin" || accountType === "manage" || accountType === "manager") {
    return false
  }
  if (roleKeys.value.includes("admin") || roleKeys.value.includes("manager")) {
    return false
  }
  if (roleKeys.value.includes("student")) {
    return true
  }
  if (props.user?.dept?.deptId) {
    return false
  }
  return accountType === "student"
})
const canListVenue = computed(() => hasPermi("manage:venue:list"))
const titleDisplay = computed(() => {
  const raw = String(props.postGroup || "").trim()
  if (!raw) {
    return "-"
  }
  return raw
})
const venueOptions = ref([])
const fallbackVenueOptions = [
  { label: "康一(思齐馆)", value: 1 },
  { label: "梅一(弘毅馆)", value: 2 },
  { label: "桂一(心缘馆)", value: 3 },
  { label: "松三(笃学馆)", value: 4 },
  { label: "榕五(知行馆)", value: 5 },
  { label: "竹四(国防教育体验馆)", value: 6 },
]

const rules = ref({
  nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
  email: [{ required: true, message: "邮箱地址不能为空", trigger: "blur" }, { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],
  phonenumber: [{ required: true, message: "手机号码不能为空", trigger: "blur" }, { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }],
  venueId: [{
    trigger: "change",
    validator: (rule, value, callback) => {
      if (!value) {
        callback(new Error("请选择场馆"))
        return
      }
      callback()
    }
  }],
})
const eduLoginRules = {
  account: [{ required: true, message: "请输入账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
}

/** 提交按钮 */
function submit() {
  proxy.$refs.userRef.validate(async valid => {
    if (valid) {
      try {
        await updateUserProfile({
          nickName: form.value.nickName,
          phonenumber: form.value.phonenumber,
          email: form.value.email,
          sex: form.value.sex,
          venueId: form.value.venueId,
        })
        if (isStudent.value) {
          await updateStudentInfo({
            venueId: form.value.venueId,
            collegeName: normalizeStudentText(form.value.collegeName),
            majorName: normalizeStudentText(form.value.majorName),
          })
        }
        userStore.venueId = form.value.venueId
        localStorage.setItem("profile_selected_venue_id", String(form.value.venueId))
        saveProfileExtendedFields()
        proxy.$modal.msgSuccess("保存成功")
        props.user.nickName = form.value.nickName
        props.user.phonenumber = form.value.phonenumber
        props.user.email = form.value.email
        props.user.sex = form.value.sex
        props.user.venueId = form.value.venueId
      } catch (error) {
        const message = error?.response?.data?.msg || "保存失败，请稍后重试"
        proxy.$modal.msgError(message)
      }
    }
  })
}

function normalizeStudentText(value) {
  const text = String(value ?? "").trim()
  if (!text || text === "-") {
    return ""
  }
  return text
}

function openPasswordDialog() {
  passwordDialogVisible.value = true
}

function openEduDialog() {
  eduDialogVisible.value = true
}

function submitEduLogin() {
  if (eduSubmitting.value) {
    return
  }
  eduLoginRef.value.validate(async valid => {
    if (!valid) {
      return
    }
    eduSubmitting.value = true
    try {
      const response = await loginAcademicAuth({
        account: String(eduLoginForm.account || "").trim(),
        password: eduLoginForm.password,
        syncCloud: !!eduLoginForm.syncCloud,
      })
      const ok = response?.code === 200 && response?.data?.loginSuccess !== false
      if (!ok) {
        proxy.$modal.msgError("当前账号密码错误，登录失败")
        return
      }
      eduLoginState.loggedIn = true
      eduDialogVisible.value = false
      proxy.$modal.msgSuccess("教务系统登录成功")
    } catch (error) {
      proxy.$modal.msgError("当前账号密码错误，登录失败")
    } finally {
      eduSubmitting.value = false
    }
  })
}

async function loadAcademicAuthStatus() {
  try {
    const response = await getAcademicAuthStatus()
    if (response?.code === 200) {
      eduLoginState.loggedIn = !!response?.data?.loggedIn
    }
  } catch (error) {
  }
}

async function loadStudentVenue() {
  if (!isStudent.value) {
    form.value.realName = form.value.realName || props.user?.userName || props.user?.nickName || "-"
    loadProfileExtendedFields()
    return
  }
  try {
    const response = await getStudentInfo()
    if (response.code === 200 && response.data) {
      form.value.venueId = response.data.venueId
      form.value.realName = response.data.name || "-"
      form.value.studentId = response.data.studentId || "-"
      form.value.collegeName = response.data.collegeName || "-"
      form.value.majorName = response.data.majorName || "-"
      loadProfileExtendedFields(response.data.studentId)
    }
  } catch (error) {
    form.value.realName = props.user?.userName || props.user?.nickName || "-"
  }
}

async function loadVenueOptions() {
  if (!canListVenue.value) {
    venueOptions.value = fallbackVenueOptions
    return
  }
  try {
    const response = await listVenue()
    const rows = response.rows || []
    if (rows.length > 0) {
      venueOptions.value = rows.map(item => ({
        label: item.venueName,
        value: Number(item.venueId)
      }))
      return
    }
  } catch (error) {
  }
  venueOptions.value = fallbackVenueOptions
}

function hasPermi(perm) {
  const permissions = userStore.permissions || []
  return permissions.includes("*:*:*") || permissions.includes(perm)
}

function getProfileExtendedFieldsKey(studentId) {
  return `profile_extended_fields_${studentId || userStore.name || "unknown"}`
}

function loadProfileExtendedFields(studentId) {
  try {
    const raw = localStorage.getItem(getProfileExtendedFieldsKey(studentId || form.value.studentId))
    if (!raw) {
      return
    }
    const data = JSON.parse(raw)
    form.value.collegeName = data.collegeName || form.value.collegeName
    form.value.majorName = data.majorName || form.value.majorName
  } catch (error) {
  }
}

function saveProfileExtendedFields() {
  try {
    localStorage.setItem(
      getProfileExtendedFieldsKey(form.value.studentId),
      JSON.stringify({
        collegeName: form.value.collegeName || "",
        majorName: form.value.majorName || "",
      })
    )
  } catch (error) {
  }
}

// 回显当前登录用户信息
watch(() => props.user, user => {
  if (user) {
    const fallbackRealName = isStudent.value ? form.value.realName : (user.userName || user.nickName || "-")
    form.value = {
      realName: fallbackRealName,
      studentId: form.value.studentId || "-",
      collegeName: form.value.collegeName || "-",
      majorName: form.value.majorName || "-",
      nickName: user.nickName,
      phonenumber: user.phonenumber,
      email: user.email,
      sex: user.sex,
      venueId: Number(user.venueId || "") || undefined
    }
  }
},{ immediate: true })

watch(() => props.showPasswordFirst, value => {
  if (value) {
    passwordDialogVisible.value = true
  }
}, { immediate: true })

onMounted(() => {
  loadVenueOptions()
  loadStudentVenue()
  if (isStudent.value) {
    loadAcademicAuthStatus()
  }
})
</script>

<style lang="scss" scoped>
.student-profile-form {
  padding: 6px 2px 0;
  background: #f5f7fa;
  border-radius: 10px;

  .student-profile-grid {
    align-items: stretch;
  }

  .student-panel {
    height: 100%;
    min-height: 570px;
    border: 1px solid #e4e7ed;
    border-radius: 12px;
    background: #fff;
    box-shadow: 0 2px 10px rgba(31, 35, 41, 0.05);
    transition: box-shadow 0.25s ease, transform 0.25s ease;

    :deep(.el-card__body) {
      padding: 24px 24px 18px;
    }
  }

  .student-panel:hover {
    box-shadow: 0 4px 14px rgba(31, 35, 41, 0.08);
    transform: translateY(-1px);
  }

  .student-panel-left {
    max-width: 460px;
  }

  .student-panel-right {
    min-width: 0;
  }

  .student-avatar-wrap {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 22px;
    padding: 14px 10px 16px;
    border-radius: 10px;
    background: linear-gradient(180deg, #f8f9fb 0%, #ffffff 100%);
    border: 1px dashed #d5dbe5;
  }

  .student-avatar-name {
    margin-top: 12px;
    font-size: 16px;
    font-weight: 600;
    color: #2f3a4c;
  }

  .student-section-title {
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e4e7ed;
    font-size: 16px;
    font-weight: 600;
    color: #2f3a4c;
    position: relative;
  }

  .student-submit-row {
    margin-top: 20px;
    margin-bottom: 6px;
    justify-content: flex-end;

    :deep(.el-form-item__content) {
      justify-content: flex-end;
    }
  }

  .student-submit-btn {
    min-width: 112px;
    border-radius: 8px;
    font-weight: 600;
    background: #3f5873;
    border-color: #3f5873;
  }

  .student-submit-btn:hover,
  .student-submit-btn:focus {
    background: #4c6786;
    border-color: #4c6786;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper) {
    border-radius: 8px;
    box-shadow: 0 0 0 1px #d8dee9 inset;
  }

  :deep(.el-input__wrapper:hover),
  :deep(.el-select__wrapper:hover) {
    box-shadow: 0 0 0 1px #bfc8d6 inset;
  }

  :deep(.el-input__wrapper.is-focus),
  :deep(.el-select__wrapper.is-focused) {
    box-shadow: 0 0 0 1px #6b7a90 inset;
  }

  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-radio-group) {
    display: flex;
    gap: 20px;
  }

  :deep(.el-dialog) {
    border-radius: 12px;
  }

  :deep(.pwd-link-btn) {
    padding: 0;
    font-weight: 500;
  }

  .edu-entry-wrap {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    gap: 12px;
  }

  .edu-login-btn {
    min-width: 96px;
    border-radius: 8px;
  }

  .edu-status-text {
    color: #909399;
    font-size: 13px;
  }
}

@media (max-width: 991px) {
  .student-profile-form {
    .student-panel {
      min-height: auto;
    }

    .student-panel-left {
      max-width: none;
    }

    .student-profile-grid {
      row-gap: 16px;
    }
  }
}

.edu-dialog-box {
  padding: 2px 0;
}

.edu-submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 18px;
  background: #ff8a65;
  border-color: #ff8a65;
}

.edu-submit-btn:hover,
.edu-submit-btn:focus {
  background: #ff9f80;
  border-color: #ff9f80;
}
</style>
