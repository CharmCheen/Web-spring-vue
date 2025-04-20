<template>
  <div class="sidebar-wrapper">
    <el-menu
      class="sidebar-menu"
      background-color="var(--sidebar-bg)"
      text-color="var(--text-color)"
      active-text-color="var(--primary-color)"
      router
      :default-active="$route.path"
    >
      <!-- 页面导航区域 -->
      <div class="sidebar-section">
        <el-menu-item index="/home">
          <i class="el-icon-house"></i>
          <span>主页</span>
        </el-menu-item>
      </div>

      <!-- 功能菜单 -->
      <div class="sidebar-section">
        <el-sub-menu index="function">
          <template #title>
            <i class="el-icon-setting"></i>
            <span>功能菜单</span>
          </template>
          <el-menu-item index="/feature/files">
            <span>文件管理</span>
          </el-menu-item>
          <el-menu-item index="/feature/users">
            <span>用户管理</span>
          </el-menu-item>
        </el-sub-menu>
      </div>

      <!-- 主题切换区域 -->
      <div class="sidebar-section">
        <el-sub-menu index="theme">
          <template #title>
            <i class="el-icon-brush"></i>
            <span>主题切换</span>
          </template>
          <div class="theme-options">
            <div class="theme-button" @click.stop.prevent="setTheme('blue')">蓝色风格</div>
            <div class="theme-button" @click.stop.prevent="setTheme('yellow')">黄色风格</div>
          </div>
        </el-sub-menu>
      </div>

      <!-- 操作区域 -->
      <div class="sidebar-section">
        <el-menu-item index="/" @click="logout">
          <i class="el-icon-switch-button"></i>
          <span>退出登录</span>
        </el-menu-item>
      </div>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'SidebarMenu',
  inject: ['switchTheme'],
  methods: {
    setTheme(themeName) {
      this.switchTheme(themeName)
    },
    logout() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.sidebar-wrapper {
  width: 220px;
  background-color: var(--sidebar-bg);
  height: 100vh;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  transition: background-color 0.3s ease;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
  padding-top: 10px;
  background-color: var(--sidebar-bg);
  transition: background-color 0.3s ease;
}

.sidebar-section {
  margin-bottom: 16px;
  padding: 0 8px;
}

.theme-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px 16px;
}

.theme-button {
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  text-align: center;
  transition: all 0.3s ease;
  background-color: var(--bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

.theme-button:hover {
  background-color: var(--primary-color);
  color: var(--bg-color);
  border-color: var(--primary-color);
}

/* 统一所有菜单项的悬停和激活状态 */
::v-deep(.el-menu-item),
::v-deep(.el-sub-menu__title) {
  transition: all 0.3s ease;
  border-radius: 8px;
  margin: 2px 8px;
}

::v-deep(.el-menu-item:hover),
::v-deep(.el-menu-item.is-active),
::v-deep(.el-sub-menu__title:hover) {
  background-color: var(--primary-color) !important;
  color: var(--bg-color) !important;
}

/* 统一图标和文字颜色 */
::v-deep(.el-menu-item:hover > span),
::v-deep(.el-menu-item:hover > i),
::v-deep(.el-menu-item.is-active > span),
::v-deep(.el-menu-item.is-active > i),
::v-deep(.el-sub-menu__title:hover > span),
::v-deep(.el-sub-menu__title:hover > i) {
  color: var(--bg-color) !important;
}

/* 子菜单项悬停效果 */
::v-deep(.el-sub-menu .el-menu-item:hover) {
  background-color: var(--primary-color-light) !important;
  color: var(--primary-color) !important;
}

::v-deep(.el-sub-menu .el-menu-item:hover > span),
::v-deep(.el-sub-menu .el-menu-item:hover > i) {
  color: var(--primary-color) !important;
}

/* 子菜单展开时的标题样式 */
::v-deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
  color: var(--primary-color) !important;
  background-color: transparent !important;
}

::v-deep(.el-sub-menu.is-opened > .el-sub-menu__title > i) {
  color: var(--primary-color) !important;
}
</style>