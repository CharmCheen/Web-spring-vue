<template>
  <div class="home-layout">
    <div class="main-content">
      <AppHeader />
      <div class="home-content">
        <div class="user-section">
          <el-avatar :size="100" :src="avatarUrl" />
          <h2 class="welcome-msg">欢迎回来，{{ username }}！</h2>
          <div class="user-info-card">
            <el-descriptions title="个人信息" :column="1" border>
              <el-descriptions-item label="用户名">{{ username }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ email || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="出生日期">{{ birthdate || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="地址">{{ address || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="邮编">{{ postcode || '未填写' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
        <div class="calendar-section">
          <el-calendar v-model="selectedDate" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppHeader from '../components/AppHeader.vue'

export default {
  name: 'HomePage',
  components: {
    AppHeader
  },
  data() {
    return {
      username: '',
      avatarUrl: '', // 头像 URL
      email: '',
      birthdate: '',
      address: '',
      postcode: '',
      selectedDate: new Date()
    }
  },
  created() {
    // 从 localStorage 读取用户信息
    const userStr = localStorage.getItem('user')
    // 定义后端基础 URL，请根据实际情况修改或使用环境变量
    const backendBaseUrl = 'http://localhost:8081'; 

    if (userStr) {
      const user = JSON.parse(userStr)
      this.username = user.username || '用户'
      this.email = user.email || ''
      this.birthdate = user.birthdate || ''
      this.address = user.address || ''
      this.postcode = user.postcode || ''
      
      // 修正头像 URL 构造逻辑
      if (user.avatar && user.avatar !== 'default.jpg') {
        // 如果用户有头像且不是默认头像，则拼接完整 URL
        this.avatarUrl = `${backendBaseUrl}/uploads/avatars/${user.avatar}`
      } else {
        // 否则使用默认头像的 URL
        this.avatarUrl = `${backendBaseUrl}/uploads/avatars/default.jpg`
      }
    } else {
      // 如果 localStorage 中没有用户信息，也使用默认头像
      this.username = '用户'
      this.avatarUrl = `${backendBaseUrl}/uploads/avatars/default.jpg`
    }
  }
}
</script>

<style scoped>
.home-layout {
  display: flex;
  min-height: 100vh;
  background-color: var(--bg-color);
}

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

.user-info-card {
  margin: 32px auto 0 auto;
  max-width: 400px;
  background: var(--card-bg);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 24px 16px 8px 16px;
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
