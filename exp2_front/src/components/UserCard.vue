<template>
  <div class="user-card">
    <el-card class="box-card">
      <div class="user-info">
        <el-avatar :size="50" :src="user.avatar"></el-avatar>
        <div class="username">{{ user.username }}</div>
      </div>
      <div class="action">
        <el-button
          v-if="!isSubscribed"
          type="primary"
          size="small"
          @click="subscribe"
        >
          关注
        </el-button>
        <el-button
          v-else
          type="info"
          size="small"
          @click="unsubscribe"
        >
          已关注
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'UserCard'
}
</script>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

const isSubscribed = ref(false)
const currentUser = localStorage.getItem('username')

const checkSubscription = async () => {
  try {
    // 使用复数形式的API路径检查订阅状态
    const response = await axios.get(`/api/subscriptions/check?subscriberUsername=${encodeURIComponent(currentUser)}&publisherUsername=${encodeURIComponent(props.user.username)}`);
    
    // 接口返回的是 { isSubscribed: boolean } 格式
    if (response.data && typeof response.data.isSubscribed === 'boolean') {
      isSubscribed.value = response.data.isSubscribed;
    } else {
      // 兼容旧版API返回格式，检查数组中是否包含当前用户
      const subscriptions = response.data;
      isSubscribed.value = Array.isArray(subscriptions) && 
        subscriptions.some(sub => sub.publisherUsername === props.user.username);
    }
  } catch (error) {
    console.error('获取关注状态失败', error);
    ElMessage.error('获取关注状态失败');
  }
}

// 添加定时刷新功能
const startAutoRefresh = () => {
  // 每30秒刷新一次关注状态
  const refreshInterval = setInterval(() => {
    if (document.visibilityState === 'visible') {
      console.log('自动刷新关注状态')
      checkSubscription()
    }
  }, 30000) // 30秒

  // 监听页面可见性变化
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible') {
      console.log('页面变为可见，刷新关注状态')
      checkSubscription()
    }
  })

  // 组件卸载时清除定时器和事件监听
  onUnmounted(() => {
    clearInterval(refreshInterval)
    document.removeEventListener('visibilitychange', checkSubscription)
  })
}

const subscribe = async () => {
  try {
    // 使用复数形式的API路径发送关注请求
    await axios.post('/api/subscriptions/follow', {
      subscriberUsername: currentUser,
      publisherUsername: props.user.username
    });
    isSubscribed.value = true;
    ElMessage.success('关注成功');
  } catch (error) {
    console.error('关注失败', error);
    ElMessage.error('关注失败');
  }
}

const unsubscribe = async () => {
  try {
    // 使用复数形式的API路径发送取消关注请求
    await axios.delete(`/api/subscriptions/unfollow?subscriberUsername=${encodeURIComponent(currentUser)}&publisherUsername=${encodeURIComponent(props.user.username)}`);
    isSubscribed.value = false;
    ElMessage.success('取消关注成功');
  } catch (error) {
    console.error('取消关注失败', error);
    ElMessage.error('取消关注失败');
  }
}

onMounted(() => {
  checkSubscription()
  startAutoRefresh()
})
</script>

<style scoped>
.user-card {
  margin: 10px;
}

.box-card {
  width: 200px;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.username {
  margin-left: 10px;
  font-weight: bold;
}

.action {
  text-align: right;
}
</style>