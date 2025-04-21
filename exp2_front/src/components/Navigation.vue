<template>
  <el-menu
    :default-active="activeIndex"
    class="el-menu-demo"
    mode="horizontal"
    router
  >
    <el-menu-item index="/feed">动态</el-menu-item>
    <el-menu-item index="/articles/create">发布文章</el-menu-item>
    <el-menu-item index="/articles/manage">我的文章</el-menu-item>
    <el-menu-item index="/subscriptions">我的关注</el-menu-item>
    <el-menu-item index="/login" v-if="!isLoggedIn">登录</el-menu-item>
    <el-menu-item v-else @click="logout">退出</el-menu-item>
  </el-menu>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const activeIndex = computed(() => router.currentRoute.value.path)

const isLoggedIn = computed(() => {
  return !!localStorage.getItem('username')
})

const logout = () => {
  localStorage.removeItem('username')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.el-menu-demo {
  margin-bottom: 20px;
}
</style> 