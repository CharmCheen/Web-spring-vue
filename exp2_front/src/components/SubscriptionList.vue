<template>
  <div class="subscription-list">
    <h2>我的关注</h2>
    <div class="user-grid">
      <UserCard
        v-for="subscription in subscriptions"
        :key="subscription.publisherUsername"
        :user="{ username: subscription.publisherUsername }"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import UserCard from './UserCard.vue'

const subscriptions = ref([])
const username = localStorage.getItem('username')

const fetchSubscriptions = async () => {
  try {
    const response = await axios.get(`/api/subscriptions/my/${username}`)
    subscriptions.value = response.data
  } catch (error) {
    ElMessage.error('获取关注列表失败')
  }
}

onMounted(() => {
  fetchSubscriptions()
})
</script>

<style scoped>
.subscription-list {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.user-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}
</style> 