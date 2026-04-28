<template>
  <div :class="{ 'has-logo': showLogo }" class="sidebar-container">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="getMenuBackground"
        :text-color="getMenuTextColor"
        :unique-opened="true"
        :active-text-color="theme"
        :collapse-transition="false"
        mode="vertical"
        :class="sideTheme"
      >
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path + index"
          :item="route"
          :base-path="route.path"
          :unread-count="notifyUnreadCount"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/assets/styles/variables.module.scss'
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

const route = useRoute()
const appStore = useAppStore()
const settingsStore = useSettingsStore()
const permissionStore = usePermissionStore()

const studyRoomRoute = {
  path: '/student/study-room',
  children: [
    {
      path: 'index',
      meta: { title: '自习场地查询', icon: 'education' }
    }
  ]
}

const sidebarRouters = computed(() => {
  const routes = permissionStore.sidebarRouters || []
  return hasStudyRoomRoute(routes) ? routes : routes.concat(studyRoomRoute)
})
const showLogo = computed(() => settingsStore.sidebarLogo)
const sideTheme = computed(() => settingsStore.sideTheme)
const theme = computed(() => settingsStore.theme)
const isCollapse = computed(() => !appStore.sidebar.opened)

// 获取菜单背景色
const getMenuBackground = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-bg)'
  }
  return sideTheme.value === 'theme-dark' ? variables.menuBg : variables.menuLightBg
})

// 获取菜单文字颜色
const getMenuTextColor = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-text)'
  }
  return sideTheme.value === 'theme-dark' ? variables.menuText : variables.menuLightText
})

const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})

const notifyUnreadCount = ref(0)
let notifyTimer

function updateNotifyUnreadCount() {
  const raw = localStorage.getItem('student_notify_messages')
  if (raw) {
    const list = JSON.parse(raw)
    notifyUnreadCount.value = list.filter((item) => !item.read).length
    return
  }
  const defaultList = [
    { read: false },
    { read: false },
    { read: true },
    { read: true },
    { read: false },
    { read: false },
  ]
  notifyUnreadCount.value = defaultList.filter((item) => !item.read).length
}

function hasStudyRoomRoute(routes = []) {
  return routes.some((item) => {
    if (item.path === '/student/study-room') {
      return true
    }
    return hasStudyRoomRoute(item.children || [])
  })
}

onMounted(() => {
  updateNotifyUnreadCount()
  notifyTimer = setInterval(() => {
    updateNotifyUnreadCount()
  }, 2000)
})

onBeforeUnmount(() => {
  if (notifyTimer) {
    clearInterval(notifyTimer)
  }
})
</script>

<style lang="scss" scoped>
.sidebar-container {
  background-color: v-bind(getMenuBackground);
  
  .scrollbar-wrapper {
    background-color: v-bind(getMenuBackground);
  }

  .el-menu {
    border: none;
    height: 100%;
    width: 100% !important;
    
    .el-menu-item, .el-sub-menu__title {
      &:hover {
        background-color: var(--menu-hover, rgba(0, 0, 0, 0.06)) !important;
      }
    }

    .el-menu-item {
      color: v-bind(getMenuTextColor);
      
      &.is-active {
        color: var(--menu-active-text, #409eff);
        background-color: var(--menu-hover, rgba(0, 0, 0, 0.06)) !important;
      }
    }

    .el-sub-menu__title {
      color: v-bind(getMenuTextColor);
    }
  }
}
</style>
