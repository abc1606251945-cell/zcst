<template>
   <el-form ref="userRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item v-if="!isStudent" label="姓名">
         <el-input v-model="form.realName" readonly />
      </el-form-item>
      <el-form-item v-if="!isStudent" label="性别">
         <el-radio-group v-model="form.sex">
            <el-radio value="0">男</el-radio>
            <el-radio value="1">女</el-radio>
         </el-radio-group>
      </el-form-item>
      <el-form-item v-if="!isStudent" label="职称">
         <el-input :model-value="titleDisplay" readonly />
      </el-form-item>
      <el-form-item v-if="!isStudent" label="场馆" prop="venueId">
         <el-select v-model="form.venueId" placeholder="请选择场馆" style="width: 100%">
            <el-option
              v-for="item in venueOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
         </el-select>
      </el-form-item>
      <el-form-item v-if="!isStudent" label="手机号码" prop="phonenumber">
         <el-input v-model="form.phonenumber" maxlength="11" />
      </el-form-item>
      <el-form-item v-if="!isStudent" label="邮箱" prop="email">
         <el-input v-model="form.email" maxlength="50" />
      </el-form-item>
      <el-form-item v-if="isStudent" label="学号">
         <el-input v-model="form.studentId" readonly />
      </el-form-item>
      <el-form-item v-if="isStudent" label="学院">
         <el-input v-model="form.collegeName" maxlength="50" placeholder="请输入学院" />
      </el-form-item>
      <el-form-item v-if="isStudent" label="专业">
         <el-input v-model="form.majorName" maxlength="50" placeholder="请输入专业" />
      </el-form-item>
      <el-form-item v-if="isStudent" label="用户昵称" prop="nickName">
         <el-input v-model="form.nickName" maxlength="30" />
      </el-form-item>
      <el-form-item v-if="isStudent" label="手机号码" prop="phonenumber">
         <el-input v-model="form.phonenumber" maxlength="11" />
      </el-form-item>
      <el-form-item v-if="isStudent" label="邮箱" prop="email">
         <el-input v-model="form.email" maxlength="50" />
      </el-form-item>
      <el-form-item v-if="isStudent" label="性别">
         <el-radio-group v-model="form.sex">
            <el-radio value="0">男</el-radio>
            <el-radio value="1">女</el-radio>
         </el-radio-group>
      </el-form-item>
      <el-form-item v-if="isStudent" label="场馆" prop="venueId">
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
         <el-input model-value="出于安全原因，系统不显示当前密码明文" readonly />
         <el-button link type="primary" style="margin-left: 12px" @click="togglePasswordForm">
            {{ showPasswordForm ? "收起修改" : "修改密码" }}
         </el-button>
      </el-form-item>
      <el-collapse-transition>
         <div v-show="showPasswordForm" style="padding: 4px 0 12px;">
            <resetPwd embedded />
         </div>
      </el-collapse-transition>
      <el-form-item>
         <el-button type="primary" @click="submit">保存</el-button>
      </el-form-item>
   </el-form>
</template>

<script setup>
import { updateUserProfile } from "@/api/system/user"
import { getStudentInfo, updateStudentInfo } from "@/api/manage/student"
import { listVenue } from "@/api/manage/venue"
import useUserStore from "@/store/modules/user"
import resetPwd from "./resetPwd"

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
const showPasswordForm = ref(false)
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
          try {
            await updateStudentInfo({ venueId: form.value.venueId })
          } catch (error) {
            proxy.$modal.msgWarning("个人信息已保存，场馆同步稍后自动重试")
          }
        }
        userStore.venueId = form.value.venueId
        localStorage.setItem("profile_selected_venue_id", String(form.value.venueId))
        saveProfileExtendedFields()
        proxy.$modal.msgSuccess("修改成功")
        props.user.nickName = form.value.nickName
        props.user.phonenumber = form.value.phonenumber
        props.user.email = form.value.email
        props.user.sex = form.value.sex
        props.user.venueId = form.value.venueId
      } catch (error) {
      }
    }
  })
}

function togglePasswordForm() {
  showPasswordForm.value = !showPasswordForm.value
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
    showPasswordForm.value = true
  }
}, { immediate: true })

onMounted(() => {
  loadVenueOptions()
  loadStudentVenue()
})
</script>
