<template>
   <div class="app-container">
      <el-row :gutter="20">
         <el-col :span="6" :xs="24">
            <el-card class="box-card">
               <template v-slot:header>
                 <div class="clearfix">
                   <span>个人信息</span>
                 </div>
               </template>
               <div class="profile-left-panel">
                  <div class="text-center">
                     <userAvatar />
                  </div>
                  <div class="profile-user-name">{{ state.user.nickName || state.user.userName || "-" }}</div>
               </div>
            </el-card>
         </el-col>
         <el-col :span="18" :xs="24">
            <el-card>
               <template v-slot:header>
                 <div class="clearfix">
                   <span>基本资料</span>
                 </div>
               </template>
               <userInfo :user="state.user" :post-group="state.postGroup" :show-password-first="showPasswordFirst" />
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="Profile">
import userAvatar from "./userAvatar"
import userInfo from "./userInfo"
import { getUserProfile } from "@/api/system/user"

const route = useRoute()
const showPasswordFirst = ref(false)
const state = reactive({
  user: {},
  roleGroup: {},
  postGroup: {}
})

function getUser() {
  getUserProfile().then(response => {
    state.user = response.data
    state.roleGroup = response.roleGroup
    state.postGroup = response.postGroup
  })
}

onMounted(() => {
  const activeTab = route.params && route.params.activeTab
  showPasswordFirst.value = activeTab === "resetPwd"
  getUser()
})
</script>

<style lang="scss" scoped>
.profile-left-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 8px 0 20px;
}

.profile-user-name {
  margin-top: 16px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
</style>
