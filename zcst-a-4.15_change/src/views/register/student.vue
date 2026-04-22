<template>
  <div class="register student-register">
    <el-form
      ref="registerRef"
      :model="registerForm"
      :rules="registerRules"
      class="register-form"
    >
      <h3 class="title">学生注册</h3>
      <el-form-item prop="studentId">
        <el-input
          v-model="registerForm.studentId"
          type="text"
          size="large"
          auto-complete="off"
          placeholder="学号"
        >
          <template #prefix
            ><svg-icon icon-class="user" class="el-input__icon input-icon"
          /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="studentName">
        <el-input
          v-model="registerForm.studentName"
          type="text"
          size="large"
          auto-complete="off"
          placeholder="学生姓名"
        >
          <template #prefix
            ><svg-icon icon-class="user" class="el-input__icon input-icon"
          /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          size="large"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter="handleRegister"
        >
          <template #prefix
            ><svg-icon icon-class="password" class="el-input__icon input-icon"
          /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          size="large"
          auto-complete="off"
          placeholder="确认密码"
          @keyup.enter="handleRegister"
        >
          <template #prefix
            ><svg-icon icon-class="password" class="el-input__icon input-icon"
          /></template>
        </el-input>
      </el-form-item>

      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          size="large"
          v-model="registerForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter="handleRegister"
        >
          <template #prefix
            ><svg-icon icon-class="validCode" class="el-input__icon input-icon"
          /></template>
        </el-input>
        <div class="register-code">
          <img :src="codeUrl" @click="getCode" class="register-code-img" />
        </div>
      </el-form-item>
      <el-form-item style="width: 100%">
        <el-button
          :loading="loading"
          size="large"
          type="primary"
          style="width: 100%"
          @click.prevent="handleRegister"
        >
          <span v-if="!loading">注册</span>
          <span v-else>注 册 中...</span>
        </el-button>
        <div style="float: right">
          <router-link class="link-type" :to="'/login'"
            >使用已有账户登录</router-link
          >
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-register-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from "element-plus";
import { ref, computed, watch, getCurrentInstance } from "vue";
import { useRouter } from "vue-router";
import { getCodeImg, register } from "@/api/login";
import defaultSettings from "@/settings";

const title = import.meta.env.VITE_APP_TITLE;
const footerContent = defaultSettings.footerContent;
const router = useRouter();
const { proxy } = getCurrentInstance();

const registerForm = ref({
  studentId: "",
  studentName: "",
  password: "",
  confirmPassword: "",
  code: "",
  uuid: "",
});

const equalToPassword = (rule, value, callback) => {
  if (registerForm.value.password !== value) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const registerRules = {
  studentId: [
    {
      required: true,
      trigger: "blur",
      message: "请输入学号",
    },
    {
      min: 6,
      max: 20,
      message: "学号长度必须介于 6 和 20 之间",
      trigger: "blur",
    },
  ],
  studentName: [
    {
      required: true,
      trigger: "blur",
      message: "请输入学生姓名",
    }
  ],
  password: [
    {
      required: true,
      trigger: "blur",
      message: "请输入您的密码",
    },
    {
      min: 6,
      message: "密码长度必须至少6位",
      trigger: "blur",
    },
  ],
  confirmPassword: [
    {
      required: true,
      trigger: "blur",
      message: "请再次输入您的密码",
    },
    {
      required: true,
      validator: equalToPassword,
      trigger: "blur",
    },
  ],
  code: [
    {
      required: true,
      trigger: "change",
      message: "请输入验证码",
    },
  ],
};

const codeUrl = ref("");
const loading = ref(false);
const captchaEnabled = ref(true);

function handleRegister() {
  proxy.$refs.registerRef.validate((valid) => {
    if (valid) {
      loading.value = true;
      // 构建注册数据
      const registerData = {
        ...registerForm.value,
        name: registerForm.value.studentName,
        type: "student",
      };
      register(registerData)
        .then((res) => {
          ElMessageBox.alert(
            "<font color='red'>恭喜你，您的账号 " +
              registerForm.value.studentId +
              " 注册成功！</font>",
            "系统提示",
            {
              dangerouslyUseHTMLString: true,
              type: "success",
            },
          )
            .then(() => {
              router.push("/login");
            })
            .catch(() => {});
        })
        .catch(() => {
          loading.value = false;
          if (captchaEnabled.value) {
            getCode();
          }
        });
    }
  });
}

function getCode() {
  getCodeImg().then((res) => {
    captchaEnabled.value =
      res.captchaEnabled === undefined ? true : res.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img;
      registerForm.value.uuid = res.uuid;
    }
  });
}

getCode();
</script>

<style lang="scss" scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url(../../assets/images/login-background.jpg);
  background-size: cover;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #409eff;
  font-size: 20px;
  font-weight: bold;
}

.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}

.register-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.register-code {
  width: 33%;
  height: 40px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

.register-code-img {
  height: 40px;
  padding-left: 12px;
}

.password-strength {
  margin-bottom: 20px;

  .strength-label {
    font-size: 14px;
    color: #606266;
    margin-bottom: 5px;
  }

  .strength-bars {
    display: flex;
    margin-bottom: 5px;

    .strength-bar {
      flex: 1;
      height: 8px;
      background-color: #ebeef5;
      margin-right: 5px;
      border-radius: 4px;
      transition: all 0.3s ease;

      &:last-child {
        margin-right: 0;
      }

      &.strong {
        background-color: #67c23a;
      }
    }
  }

  .strength-text {
    font-size: 12px;
    color: #909399;
  }
}
</style>
