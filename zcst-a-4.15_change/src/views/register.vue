<template>
  <div class="register">
    <el-form
      ref="registerRef"
      :model="registerForm"
      :rules="registerRules"
      class="register-form"
    >
      <h3 class="title">{{ title }}</h3>
      <!-- 学生注册表单 -->
      <template v-if="registerType === 'student'">
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
        <el-form-item prop="name">
          <el-input
            v-model="registerForm.name"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="姓名"
          >
            <template #prefix
              ><svg-icon icon-class="user" class="el-input__icon input-icon"
            /></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="gender">
          <el-select
            v-model="registerForm.gender"
            size="large"
            placeholder="性别"
          >
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="联系电话"
          >
            <template #prefix
              ><svg-icon icon-class="phone" class="el-input__icon input-icon"
            /></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="venueId">
          <el-select
            v-model="registerForm.venueId"
            size="large"
            placeholder="所在场馆"
          >
            <el-option label="思齐馆" value="1"></el-option>
            <el-option label="弘毅馆" value="2"></el-option>
            <el-option label="心缘馆" value="3"></el-option>
            <el-option label="笃学馆" value="4"></el-option>
            <el-option label="知行馆" value="5"></el-option>
            <el-option label="国防教育体验馆" value="6"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="grade">
          <el-input
            v-model="registerForm.grade"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="年级"
          >
            <template #prefix
              ><svg-icon icon-class="user" class="el-input__icon input-icon"
            /></template>
          </el-input>
        </el-form-item>
      </template>
      <!-- 管理人员注册表单 -->
      <template v-else>
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="用户昵称"
          >
            <template #prefix
              ><svg-icon icon-class="user" class="el-input__icon input-icon"
            /></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="name">
          <el-input
            v-model="registerForm.name"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="姓名"
          >
            <template #prefix
              ><svg-icon icon-class="user" class="el-input__icon input-icon"
            /></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="gender">
          <el-select
            v-model="registerForm.gender"
            size="large"
            placeholder="性别"
          >
            <el-option label="男" value="0"></el-option>
            <el-option label="女" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="deptId">
          <el-select
            v-model="registerForm.deptId"
            size="large"
            placeholder="部门岗位"
          >
            <el-option label="思齐馆-值班" value="103"></el-option>
            <el-option label="思齐馆-市场部门" value="104"></el-option>
            <el-option label="思齐馆-测试部门" value="105"></el-option>
            <el-option label="弘毅馆-值班" value="201"></el-option>
            <el-option label="心缘馆-值班" value="301"></el-option>
            <el-option label="笃学馆-值班" value="401"></el-option>
            <el-option label="知行馆-值班" value="501"></el-option>
            <el-option label="国防教育体验馆-值班" value="601"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="venueId">
          <el-select
            v-model="registerForm.venueId"
            size="large"
            placeholder="所在场馆"
          >
            <el-option label="思齐馆" value="1"></el-option>
            <el-option label="弘毅馆" value="2"></el-option>
            <el-option label="心缘馆" value="3"></el-option>
            <el-option label="笃学馆" value="4"></el-option>
            <el-option label="知行馆" value="5"></el-option>
            <el-option label="国防教育体验馆" value="6"></el-option>
          </el-select>
        </el-form-item>
      </template>
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
          <span v-if="!loading">{{
            registerType === "student" ? "学生注册" : "管理人员注册"
          }}</span>
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
import { computed, ref } from "vue";
import { getCodeImg, register } from "@/api/login";
import defaultSettings from "@/settings";

const title = import.meta.env.VITE_APP_TITLE;
const footerContent = defaultSettings.footerContent;
const router = useRouter();
const route = useRoute();
const { proxy } = getCurrentInstance();

// 获取注册类型，默认为学生注册
const registerType = ref(route.query.type || "student");

const registerForm = ref({
  // 学生注册字段
  studentId: "",
  name: "",
  gender: "",
  phone: "",
  venueId: "",
  grade: "",
  // 管理人员注册字段
  username: "",
  deptId: "",
  // 通用字段
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

// 动态生成表单验证规则
const registerRules = computed(() => {
  if (registerType.value === "student") {
    return {
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
      name: [
        {
          required: true,
          trigger: "blur",
          message: "请输入姓名",
        },
        {
          min: 2,
          max: 20,
          message: "姓名长度必须介于 2 和 20 之间",
          trigger: "blur",
        },
      ],
      gender: [
        {
          required: true,
          trigger: "change",
          message: "请选择性别",
        },
      ],
      phone: [
        {
          required: true,
          trigger: "blur",
          message: "请输入联系电话",
        },
        {
          pattern: /^1[3-9]\d{9}$/,
          message: "请输入正确的手机号码",
          trigger: "blur",
        },
      ],
      venueId: [
        {
          required: true,
          trigger: "change",
          message: "请选择所在场馆",
        },
      ],
      grade: [
        {
          required: true,
          trigger: "blur",
          message: "请输入年级",
        },
      ],
      password: [
        {
          required: true,
          trigger: "blur",
          message: "请输入您的密码",
        },
        {
          min: 5,
          max: 20,
          message: "用户密码长度必须介于 5 和 20 之间",
          trigger: "blur",
        },
        {
          pattern: /^[^<>'\"|\\]+$/,
          message: "不能包含非法字符：< > \" ' \\ |",
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
  } else {
    return {
      username: [
        {
          required: true,
          trigger: "blur",
          message: "请输入用户昵称",
        },
        {
          min: 2,
          max: 20,
          message: "用户昵称长度必须介于 2 和 20 之间",
          trigger: "blur",
        },
      ],
      name: [
        {
          required: true,
          trigger: "blur",
          message: "请输入姓名",
        },
        {
          min: 2,
          max: 20,
          message: "姓名长度必须介于 2 和 20 之间",
          trigger: "blur",
        },
      ],
      gender: [
        {
          required: true,
          trigger: "change",
          message: "请选择性别",
        },
      ],
      deptId: [
        {
          required: true,
          trigger: "change",
          message: "请选择部门岗位",
        },
      ],
      venueId: [
        {
          required: true,
          trigger: "change",
          message: "请选择所在场馆",
        },
      ],
      password: [
        {
          required: true,
          trigger: "blur",
          message: "请输入您的密码",
        },
        {
          min: 5,
          max: 20,
          message: "用户密码长度必须介于 5 和 20 之间",
          trigger: "blur",
        },
        {
          pattern: /^[^<>'\"|\\]+$/,
          message: "不能包含非法字符：< > \" ' \\ |",
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
  }
});

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
        type: registerType.value,
      };
      register(registerData)
        .then((res) => {
          const username =
            registerType.value === "student"
              ? registerForm.value.studentId
              : registerForm.value.username;
          ElMessageBox.alert(
            "<font color='red'>恭喜你，您的账号 " +
              username +
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
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
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
</style>
