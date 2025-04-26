<template>
  <div class="sidebar-wrapper">
    <div class="sidebar-logo">
      <i class="el-icon-s-platform"></i>
      <span>系统管理</span>
    </div>
    <el-menu
      class="sidebar-menu"
      background-color="var(--sidebar-bg)"
      text-color="var(--sidebar-text)"
      active-text-color="var(--sidebar-active-text)"
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

      <!-- 文章功能 -->
      <div class="sidebar-section">
        <el-sub-menu index="articles">
          <template #title>
            <i class="el-icon-document"></i>
            <span>文章管理</span>
          </template>
          <el-menu-item index="/followed-articles">
            <span>关注动态</span>
          </el-menu-item>
          <el-menu-item index="/articles/create">
            <span>发布文章</span>
          </el-menu-item>
          <el-menu-item index="/articles/manage">
            <span>我的文章</span>
          </el-menu-item>
          <el-menu-item index="/article-stats">
            <span>文章统计</span>
          </el-menu-item>
          <el-menu-item index="/subscriptions">
            <span>我的关注</span>
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
            <div class="theme-button blue-theme" @click.stop.prevent="setTheme('blue')">
              <div class="color-preview"></div>
              <span>蓝色风格</span>
            </div>
            <div class="theme-button yellow-theme" @click.stop.prevent="setTheme('yellow')">
              <div class="color-preview"></div>
              <span>黄色风格</span>
            </div>
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
      // 清除用户信息
      localStorage.removeItem('user');
      localStorage.removeItem('username');
      // 重定向到登录页
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.sidebar-wrapper {
  width: 220px;
  background: var(--sidebar-bg); /* 直接使用 background 应用渐变 */
  height: 100vh;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  overflow-y: auto; /* 允许内容滚动 */
  overflow-x: hidden;
  font-family: "Microsoft YaHei", "Segoe UI", -apple-system, BlinkMacSystemFont, sans-serif;
}

.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: var(--sidebar-active-text); /* 使用激活文字颜色 */
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  flex-shrink: 0; /* 防止 logo 区域被压缩 */
}

.sidebar-menu {
  border-right: none;
  flex: 1;
  padding-top: 12px;
  background-color: transparent; /* 使菜单背景透明，显示wrapper的渐变 */
  transition: all 0.3s ease;
}

.sidebar-section {
  margin-bottom: 10px;
  padding: 0 8px;
}

.theme-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px 16px;
}

.theme-button {
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  text-align: left;
  transition: all 0.3s ease;
  background-color: rgba(255, 255, 255, 0.05);
  color: var(--sidebar-text);
  border: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  font-size: 14px;
}

.theme-button .color-preview {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  margin-right: 10px;
}

.blue-theme .color-preview {
  background-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.yellow-theme .color-preview {
  background-color: #faad14;
  box-shadow: 0 0 0 2px rgba(250, 173, 20, 0.2);
}

.theme-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

/* 菜单项样式 */
::v-deep(.el-menu) {
  border-right: none;
}

::v-deep(.el-menu-item),
::v-deep(.el-sub-menu__title) {
  height: 52px;
  line-height: 52px;
  transition: all 0.3s ease;
  border-radius: 8px;
  margin: 4px 8px;
  color: var(--sidebar-text);
  font-size: 15px;
  letter-spacing: 0.5px;
  font-weight: 500;
}

::v-deep(.el-menu-item i),
::v-deep(.el-sub-menu__title i) {
  color: var(--sidebar-text);
  margin-right: 12px;
  font-size: 18px;
}

/* 悬停和选中状态 */
::v-deep(.el-menu-item:hover),
::v-deep(.el-menu-item.is-active),
::v-deep(.el-sub-menu__title:hover) {
  background-color: var(--sidebar-hover-bg) !important; /* 使用悬停背景 */
  color: var(--sidebar-active-text) !important;
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.1);
}

::v-deep(.el-menu-item.is-active) {
  background-color: var(--sidebar-active-bg) !important; /* 使用激活背景 */
  position: relative;
  font-weight: 600;
  letter-spacing: 0.7px;
}

::v-deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 24px;
  background-color: var(--sidebar-active-text);
  border-radius: 0 4px 4px 0;
  box-shadow: 1px 0 3px rgba(255, 255, 255, 0.2);
}

/* 子菜单样式 */
::v-deep(.el-menu--inline) {
  background-color: rgba(0, 0, 0, 0.25) !important; /* 调整内联菜单背景使其与渐变协调 */
  border-radius: 8px;
  margin: 0 8px;
  padding: 5px 0;
}

::v-deep(.el-menu--inline .el-menu-item) {
  height: 42px;
  line-height: 42px;
  margin: 3px 8px;
  min-width: auto;
  font-size: 14px;
  font-weight: normal;
}

::v-deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
  color: var(--sidebar-active-text) !important; /* 保持打开时标题高亮 */
  font-weight: 600;
}

::v-deep(.el-sub-menu.is-opened > .el-sub-menu__title i) {
  color: var(--sidebar-active-text) !important; /* 保持打开时图标高亮 */
}

/* 添加过渡效果 */
::v-deep(.el-menu-item),
::v-deep(.el-sub-menu__title),
::v-deep(.el-menu--inline),
.sidebar-wrapper,
.sidebar-logo,
.theme-button {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 图标样式优化 */
::v-deep(.el-menu-item i),
::v-deep(.el-sub-menu__title i) {
  transition: transform 0.3s ease;
}

::v-deep(.el-menu-item:hover i),
::v-deep(.el-menu-item.is-active i),
::v-deep(.el-sub-menu__title:hover i) {
  transform: scale(1.1);
  color: var(--sidebar-active-text) !important;
}
</style>