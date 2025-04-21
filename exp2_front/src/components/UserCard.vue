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
import { ref, onMounted } from 'vue'
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
    const response = await axios.get(`/api/subscriptions/my/${currentUser}`)
    isSubscribed.value = response.data.some(
      sub => sub.publisherUsername === props.user.username
    )
  } catch (error) {
    ElMessage.error('获取关注状态失败')
  }
}

const subscribe = async () => {
  try {
    await axios.post('/api/subscriptions', {
      subscriberUsername: currentUser,
      publisherUsername: props.user.username
    })
    isSubscribed.value = true
    ElMessage.success('关注成功')
  } catch (error) {
    ElMessage.error('关注失败')
  }
}

const unsubscribe = async () => {
  try {
    await axios.delete('/api/subscriptions', {
      data: {
        subscriberUsername: currentUser,
        publisherUsername: props.user.username
      }
    })
    isSubscribed.value = false
    ElMessage.success('取消关注成功')
  } catch (error) {
    ElMessage.error('取消关注失败')
  }
}

onMounted(() => {
  checkSubscription()
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