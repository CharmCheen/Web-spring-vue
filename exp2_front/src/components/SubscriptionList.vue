<template>
  <div class="subscription-container">
    <!-- 页面标题和搜索区域 -->
    <div class="header-section">
      <h2 class="page-title">我的关注</h2>
      <div class="search-section">
        <el-input
          v-model="searchQuery"
          placeholder="搜索用户..."
          prefix-icon="el-icon-search"
          clearable
          @keyup.enter="executeSearch"
        >
          <template #append>
            <el-button :loading="searching" @click="executeSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 搜索中的加载状态 -->
    <div v-if="searching" class="loading-section">
      <el-card shadow="hover" class="loading-card">
        <el-skeleton :rows="3" animated />
      </el-card>
    </div>

    <!-- 搜索错误提示 -->
    <div v-if="searchError" class="error-section">
      <el-alert
        :title="searchError"
        type="error"
        :closable="false"
        show-icon
      >
        <template #default>
          <p>请检查网络连接或稍后再试</p>
          <el-button size="small" @click="searchError = ''">关闭</el-button>
        </template>
      </el-alert>
    </div>

    <!-- 搜索结果区域 -->
    <div v-if="searchResults.length > 0" class="search-results">
      <h3>搜索结果</h3>
      <el-divider></el-divider>
      <div class="user-grid">
        <div v-for="user in searchResults" :key="user.username" class="user-card-wrapper">
          <el-card class="user-search-card" shadow="hover">
            <div class="user-info">
              <el-avatar :size="50" :src="getAvatarUrl(user.avatar)"></el-avatar>
              <div class="user-details">
                <div class="username">{{ user.username }}</div>
                <div v-if="user.email" class="email">{{ user.email }}</div>
              </div>
            </div>
            <div class="card-action">
              <el-button
                v-if="!isUserSubscribed(user.username)"
                type="primary"
                size="small"
                @click="followUser(user.username)"
              >
                关注
              </el-button>
              <el-button
                v-else
                type="info"
                size="small"
                @click="unfollowUser(user.username)"
              >
                已关注
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </div>
    
    <!-- 搜索结果为空时的提示 -->
    <div v-if="!searching && hasSearched && searchResults.length === 0 && !searchError" class="empty-search-results">
      <el-empty description="未找到匹配的用户" :image-size="100">
        <template #description>
          <p>尝试使用其他关键词搜索</p>
        </template>
      </el-empty>
    </div>

    <!-- 我的关注列表区域 -->
    <div class="my-subscriptions">
      <h3>已关注用户</h3>
      <el-divider></el-divider>
      <el-empty v-if="subscriptions.length === 0" description="暂无关注的用户"></el-empty>
      <div v-else class="user-grid">
        <div v-for="sub in subscriptions" :key="sub.publisherUsername" class="user-card-wrapper">
          <el-card class="user-subscription-card" shadow="hover">
            <div class="user-info">
              <el-avatar :size="50" :src="getDefaultAvatarUrl()"></el-avatar>
              <div class="user-details">
                <div class="username">{{ sub.publisherUsername }}</div>
                <div class="since">关注于 {{ formatDate(sub.createdAt) }}</div>
              </div>
            </div>
            <div class="card-action">
              <el-button
                type="danger"
                size="small"
                @click="unfollowUser(sub.publisherUsername)"
              >
                取消关注
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'SubscriptionList',
  setup() {
    const searchQuery = ref('')
    const searchResults = ref([])
    const subscriptions = ref([])
    const username = localStorage.getItem('username')
    const searching = ref(false) // 搜索状态标记
    const searchError = ref('') // 错误信息
    const hasSearched = ref(false) // 是否已执行过搜索

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    };

    // 获取头像URL
    const getAvatarUrl = (avatar) => {
      if (!avatar) return getDefaultAvatarUrl();
      if (avatar === 'default.jpg') return getDefaultAvatarUrl();
      return `http://localhost:8081/uploads/avatars/${avatar}`;
    };

    // 获取默认头像URL
    const getDefaultAvatarUrl = () => {
      return 'http://localhost:8081/uploads/avatars/default.jpg';
    };

    // 检查用户是否已关注
    const isUserSubscribed = (publisherUsername) => {
      return subscriptions.value.some(sub => sub.publisherUsername === publisherUsername);
    };

    // 加载关注列表
    const fetchSubscriptions = async () => {
      try {
        const response = await axios.get(`/api/subscriptions/my/${username}`);
        subscriptions.value = response.data;
      } catch (error) {
        console.error('获取关注列表失败', error);
        ElMessage.error('获取关注列表失败');
      }
    };

    // 执行搜索 - 仅点击按钮或按回车时触发
    const executeSearch = async () => {
      // 重置错误状态
      searchError.value = '';
      
      // 如果搜索词为空，清空搜索结果
      if (!searchQuery.value.trim()) {
        searchResults.value = [];
        ElMessage.warning('请输入搜索关键词');
        return;
      }
      
      try {
        searching.value = true; // 开始搜索，显示加载状态
        hasSearched.value = true; // 标记为已搜索过
        
        // 直接使用后端URL避免代理问题
        const backendUrl = 'http://localhost:8081';
        console.log('搜索请求:', `${backendUrl}/api/user/search?query=${encodeURIComponent(searchQuery.value.trim())}`);
        
        // 使用新的用户搜索API
        const response = await axios.get(`${backendUrl}/api/user/search?query=${encodeURIComponent(searchQuery.value.trim())}`);
        console.log('搜索结果:', response);
        
        // 确保返回的数据是数组
        if (Array.isArray(response.data)) {
          searchResults.value = response.data;
          
          if (searchResults.value.length === 0) {
            ElMessage.info('未找到匹配的用户');
          } else {
            ElMessage.success(`找到 ${searchResults.value.length} 个用户`);
          }
        } else {
          console.error('搜索返回的数据格式不正确:', response.data);
          searchResults.value = [];
          searchError.value = '返回数据格式错误';
          ElMessage.error('搜索结果格式不正确');
        }
      } catch (error) {
        console.error('搜索用户失败', error);
        searchResults.value = [];
        
        // 更详细的错误处理
        if (error.response) {
          // 服务器返回了错误状态码
          console.error('错误响应:', error.response);
          searchError.value = `搜索失败: ${error.response.status} ${error.response.statusText}`;
          ElMessage.error(`搜索失败: ${error.response.status}`);
        } else if (error.request) {
          // 请求发送了但没有收到响应
          console.error('请求无响应');
          searchError.value = '服务器无响应，请检查后端服务是否运行';
          ElMessage.error('服务器无响应，请检查网络连接');
        } else {
          // 请求设置时发生错误
          searchError.value = '搜索请求错误';
          ElMessage.error('搜索请求出错');
        }
      } finally {
        searching.value = false; // 结束搜索，隐藏加载状态
      }
    };

    // 关注用户
    const followUser = async (publisherUsername) => {
      try {
        // 首先查找要关注的用户的完整信息
        const userToFollow = searchResults.value.find(user => user.username === publisherUsername);
        if (!userToFollow) {
          ElMessage.error('用户信息不完整，无法关注');
          return;
        }
        
        // 使用完整的后端URL避免代理问题
        const backendUrl = 'http://localhost:8081';
        console.log('关注请求:', `${backendUrl}/api/subscriptions/follow`, {
          subscriberUsername: username,
          publisherUsername
        });
        
        // 发送关注请求到后端
        await axios.post(`${backendUrl}/api/subscriptions/follow`, {
          subscriberUsername: username,
          publisherUsername
        });
        
        ElMessage.success('关注成功');
        
        // 创建一个新的关注记录，包含完整的用户信息
        const newSubscription = {
          subscriberUsername: username,
          publisherUsername: userToFollow.username,
          createdAt: new Date().toISOString(), // 使用当前时间作为关注时间
          userInfo: userToFollow // 保存用户完整信息供下方列表使用
        };
        
        // 将新的关注记录添加到关注列表中，确保实时更新
        subscriptions.value.push(newSubscription);
        
        // 更新搜索结果中的状态
        searchResults.value = searchResults.value.map(user => {
          if (user.username === publisherUsername) {
            return { ...user, isSubscribed: true };
          }
          return user;
        });
      } catch (error) {
        console.error('关注失败', error.response || error);
        
        // 详细的错误处理
        if (error.response) {
          const errorMessage = error.response.data || '关注失败，请稍后重试';
          ElMessage.error(errorMessage);
        } else {
          ElMessage.error('关注失败，请检查网络连接');
        }
      }
    };

    // 取消关注用户
    const unfollowUser = async (publisherUsername) => {
      try {
        await axios.delete(`/api/subscriptions/unfollow?subscriberUsername=${encodeURIComponent(username)}&publisherUsername=${encodeURIComponent(publisherUsername)}`);
        
        ElMessage.success('取消关注成功');
        
        // 从列表中移除被取消关注的用户
        subscriptions.value = subscriptions.value.filter(sub => sub.publisherUsername !== publisherUsername);
        
        // 更新搜索结果中的状态
        searchResults.value = searchResults.value.map(user => {
          if (user.username === publisherUsername) {
            return { ...user, isSubscribed: false };
          }
          return user;
        });
      } catch (error) {
        console.error('取消关注失败', error);
        ElMessage.error('取消关注失败');
      }
    };

    onMounted(() => {
      fetchSubscriptions();
    });

    return {
      searchQuery,
      searchResults,
      subscriptions,
      username,
      formatDate,
      getAvatarUrl,
      getDefaultAvatarUrl,
      isUserSubscribed,
      executeSearch, // 替换原来的 handleSearch
      followUser,
      unfollowUser,
      searching,
      searchError,
      hasSearched // 新增是否已搜索标记
    };
  }
}
</script>

<style scoped>
.subscription-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: var(--bg-color);
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  color: var(--text-color);
  margin: 0;
  font-weight: 600;
}

.search-section {
  width: 400px;
}

.search-results,
.my-subscriptions {
  margin-top: 30px;
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.search-results h3,
.my-subscriptions h3 {
  font-size: 18px;
  margin-top: 0;
  color: var(--text-color);
  font-weight: 500;
}

.user-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.user-card-wrapper {
  transition: transform 0.2s ease;
}

.user-card-wrapper:hover {
  transform: translateY(-5px);
}

.user-search-card,
.user-subscription-card {
  height: 100%;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-details {
  margin-left: 15px;
  flex: 1;
}

.username {
  font-weight: 600;
  font-size: 16px;
  color: var(--text-color);
}

.email {
  font-size: 13px;
  color: var(--text-color-light);
  margin-top: 5px;
}

.since {
  font-size: 12px;
  color: var(--text-color-light);
  margin-top: 5px;
}

.card-action {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.loading-section,
.error-section,
.empty-search-results {
  margin-top: 20px;
  margin-bottom: 20px;
}

.loading-card {
  padding: 20px;
  border-radius: 8px;
}

.error-section .el-alert {
  margin-bottom: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-section {
    width: 100%;
    margin-top: 20px;
  }
  
  .user-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}
</style>