<template>
  <div class="role-select">
    <div class="container">
      <h2 class="title">选择注册角色</h2>
      <div class="role-cards">
        <div class="role-card student-card" @click="redirectToRegister('student')">
          <div class="role-icon">
            <svg-icon icon-class="user" />
          </div>
          <h3>学生注册</h3>
          <p>使用学号和密码注册，注册后需完善个人信息</p>
          <el-button type="primary" size="large" style="margin-top: 20px">立即注册</el-button>
        </div>
        <div class="role-card admin-card" @click="redirectToRegister('admin')">
          <div class="role-icon">
            <svg-icon icon-class="peoples" />
          </div>
          <h3>管理员注册</h3>
          <p>使用用户名和密码注册，需选择所属部门和场馆</p>
          <el-button type="success" size="large" style="margin-top: 20px">立即注册</el-button>
        </div>
      </div>
      <div class="login-link">
        <router-link to="/login">已有账号？立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

function redirectToRegister(role) {
  router.push(`/register/${role}`);
}
</script>

<style lang="scss" scoped>
.role-select {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url(../../assets/images/login-background.jpg);
  background-size: cover;
}

.container {
  width: 800px;
  padding: 40px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  text-align: center;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 40px;
}

.role-cards {
  display: flex;
  justify-content: space-around;
  margin-bottom: 40px;
}

.role-card {
  width: 350px;
  padding: 30px;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 2px solid #e8e8e8;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  }
  
  &.student-card {
    border-color: #409eff;
    
    .role-icon {
      color: #409eff;
      font-size: 48px;
      margin-bottom: 20px;
    }
    
    h3 {
      color: #409eff;
      margin-bottom: 10px;
    }
  }
  
  &.admin-card {
    border-color: #67c23a;
    
    .role-icon {
      color: #67c23a;
      font-size: 48px;
      margin-bottom: 20px;
    }
    
    h3 {
      color: #67c23a;
      margin-bottom: 10px;
    }
  }
  
  p {
    color: #666;
    margin-bottom: 20px;
    line-height: 1.5;
  }
}

.login-link {
  margin-top: 20px;
  
  a {
    color: #409eff;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
}
</style>