<template>
  <div class="home-layout">
    <Sidebar />

    <div class="main-content">
      <AppHeader />

      <div class="home-content">
        <div class="user-section">
          <el-avatar :size="100" :src="avatarUrl" />
          <h2 class="welcome-msg">欢迎回来，{{ username }}！</h2>
        </div>

        <div class="calendar-section">
          <el-calendar v-model="selectedDate" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Sidebar from '../components/SidebarMenu.vue'
import AppHeader from '../components/AppHeader.vue'

export default {
  name: 'HomePage',
  components: {
    Sidebar,
    AppHeader
  },
  data() {
    return {
      username: '',
      avatarUrl: '',
      selectedDate: new Date()
    }
  },
  created() {
    this.username = this.$route.query.username || '用户'
    this.avatarUrl = this.$route.query.avatar || 'https://i.pravatar.cc/300'
  }
}
</script>

<style scoped>
.home-layout {
  display: flex;
  min-height: 100vh;
  background-color: var(--bg-color);
}

/* 已经交由 Sidebar.vue 控制，无需自定义 .sidebar */

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-color);
}

.home-content {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
  background-color: var(--card-bg);
  border-radius: 16px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.user-section {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-msg {
  margin-top: 20px;
  font-size: 1.5rem;
  color: var(--text-color);
}

.calendar-section {
  border-radius: 8px;
  overflow: hidden;
}

::v-deep(.el-calendar) {
  background-color: var(--card-bg);
  color: var(--text-color);
}

::v-deep(.el-calendar-table thead th) {
  color: var(--text-color);
}

::v-deep(.el-calendar-day) {
  transition: background-color 0.2s;
}

::v-deep(.el-calendar-day:hover) {
  background-color: var(--primary-color);
  color: var(--bg-color);
  cursor: pointer;
}
</style>
