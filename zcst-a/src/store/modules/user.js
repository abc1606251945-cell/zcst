import router from "@/router";
import { ElMessageBox } from "element-plus";
import { login, logout, getInfo } from "@/api/login";
import { getToken, setToken, removeToken } from "@/utils/auth";
import { isHttp, isEmpty } from "@/utils/validate";
import defAva from "@/assets/images/profile.jpg";

const useUserStore = defineStore("user", {
  state: () => ({
    token: getToken(),
    id: "",
    name: "",
    nickName: "",
    avatar: "",
    roles: [],
    permissions: [],
    venueId: "",
    accountType: "",
  }),
  actions: {
    // 登录
    login(userInfo) {
      const username = userInfo.username.trim();
      const password = userInfo.password;
      const code = userInfo.code;
      const uuid = userInfo.uuid;
      return new Promise((resolve, reject) => {
        login(username, password, code, uuid)
          .then((res) => {
            setToken(res.token);
            this.token = res.token;
            resolve();
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
    // 获取用户信息
    getInfo() {
      return new Promise((resolve, reject) => {
        getInfo()
          .then((res) => {
            const user = res.user;
            let avatar = user.avatar || "";
            if (!isHttp(avatar)) {
              // 修复：检查avatar是否为有效的路径，避免undefined导致的错误
              avatar = isEmpty(avatar) || avatar === 'undefined'
                ? defAva
                : import.meta.env.VITE_APP_BASE_API + '/' + avatar.replace(/^\/+/, '');
            }
            if (res.roles && res.roles.length > 0) {
              // 验证返回的roles是否是一个非空数组
              this.roles = res.roles;
              this.permissions = res.permissions;
            } else {
              this.roles = ["ROLE_DEFAULT"];
            }
            this.id = user.userId;
            this.name = user.userName;
            this.nickName = user.nickName;
            this.avatar = avatar;
            this.venueId = user.venueId || "";
            this.accountType = resolveAccountType(user, res, this.roles);
            /* 初始密码提示 */
            if (res.isDefaultModifyPwd) {
              ElMessageBox.confirm(
                "您的密码还是初始密码，请修改密码！",
                "安全提示",
                {
                  confirmButtonText: "确定",
                  cancelButtonText: "取消",
                  type: "warning",
                },
              )
                .then(() => {
                  router.push({
                    name: "Profile",
                    params: { activeTab: "resetPwd" },
                  });
                })
                .catch(() => {});
            }
            /* 过期密码提示 */
            if (!res.isDefaultModifyPwd && res.isPasswordExpired) {
              ElMessageBox.confirm(
                "您的密码已过期，请尽快修改密码！",
                "安全提示",
                {
                  confirmButtonText: "确定",
                  cancelButtonText: "取消",
                  type: "warning",
                },
              )
                .then(() => {
                  router.push({
                    name: "Profile",
                    params: { activeTab: "resetPwd" },
                  });
                })
                .catch(() => {});
            }
            resolve(res);
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
    // 退出系统
    logOut() {
      return new Promise((resolve, reject) => {
        logout(this.token)
          .then(() => {
            this.token = "";
            this.roles = [];
            this.permissions = [];
            this.accountType = "";
            removeToken();
            resolve();
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
  },
});

export default useUserStore;

function resolveAccountType(user, res, roles) {
  const fromUser =
    user?.type ||
    user?.userType ||
    user?.accountType ||
    user?.registerType ||
    "";
  if (fromUser) {
    return String(fromUser).toLowerCase();
  }
  const fromRes =
    res?.type ||
    res?.userType ||
    res?.accountType ||
    res?.registerType ||
    "";
  if (fromRes) {
    return String(fromRes).toLowerCase();
  }
  if (Array.isArray(roles) && roles.length > 0) {
    const normalizedRoles = roles.map((role) => String(role).toLowerCase());
    if (normalizedRoles.includes("admin")) {
      return "admin";
    }
    if (normalizedRoles.includes("student")) {
      return "student";
    }
    if (normalizedRoles.includes("manager") || normalizedRoles.includes("common")) {
      return "manager";
    }
  }
  if (user?.dept?.deptId || user?.dept?.deptName) {
    return "manager";
  }
  const account = String(user?.userName || "").trim();
  if (/^\d{6,20}$/.test(account)) {
    return "student";
  }
  return "manager";
}
