<template>
  <div class="app-container">
    <el-container>
      <el-aside v-if="showSidebar" width="220px">
        <SidebarMenu />
      </el-aside>
      <el-container>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, provide, ref, watch } from 'vue' // 确保导入了 computed
import { useRoute } from 'vue-router' // 确保导入了 useRoute
import SidebarMenu from './components/SidebarMenu.vue'

const route = useRoute() // 重新定义 route

// 重新定义 showSidebar 计算属性
const showSidebar = computed(() => {
  const noSidebarRoutes = ['Login', 'Register', 'ForgotPassword']
  // route.name 可能为 null 或 undefined，需要检查
  return route.name ? !noSidebarRoutes.includes(route.name.toString()) : true
})

// --- 主题切换逻辑 (保持不变) ---
const currentTheme = ref(localStorage.getItem('theme') || 'blue')

const switchTheme = (themeName) => {
  currentTheme.value = themeName
  localStorage.setItem('theme', themeName)
}

provide('switchTheme', switchTheme)

watch(currentTheme, (newTheme, oldTheme) => {
  if (oldTheme) {
    document.body.classList.remove(`theme-${oldTheme}`)
  }
  document.body.classList.add(`theme-${newTheme}`)
}, { immediate: true })
// --- 主题切换逻辑结束 ---
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.app-container {
  min-height: 100vh;
}

.el-container {
  min-height: 100vh;
}

.el-aside {
  background-color: var(--sidebar-bg);
  color: var(--sidebar-text);
  transition: width 0.3s;
  width: 220px !important;
}

.el-main {
  background-color: #f5f7fa;
  padding: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .el-aside {
    width: 64px !important;
  }
}
</style>
