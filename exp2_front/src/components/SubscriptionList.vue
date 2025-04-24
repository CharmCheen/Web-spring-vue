<template>
  <div class="subscription-container">
    <!-- 页面标题和搜索区域 -->
    <div class="hero-section">
      <div class="content-wrapper">
        <h2 class="page-title">关注动态</h2>
        <p class="page-subtitle">探索并关注你感兴趣的创作者</p>
      </div>
    </div>

    <div class="content-section">
      <!-- 搜索区域 -->
      <div class="search-container">
        <div class="search-wrapper">
          <el-input
            v-model="searchQuery"
            placeholder="搜索用户名..."
            class="search-input"
            clearable
            @keyup.enter="executeSearch"
          >
            <template #prefix>
              <i class="el-icon-search"></i>
            </template>
            <template #append>
              <el-button :loading="searching" @click="executeSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 搜索中的加载状态 -->
      <div v-if="searching" class="loading-section">
        <div class="loading-pulse"></div>
        <p class="loading-text">正在搜索用户...</p>
      </div>

      <!-- 搜索错误提示 -->
      <div v-if="searchError" class="error-section">
        <el-alert
          :title="searchError"
          type="error"
          :closable="false"
          effect="dark"
          show-icon
        >
          <template #default>
            <p>请检查网络连接或稍后再试</p>
            <el-button type="text" size="small" @click="searchError = ''">关闭</el-button>
          </template>
        </el-alert>
      </div>

      <!-- 搜索结果区域 -->
      <transition name="fade">
        <div v-if="searchResults.length > 0" class="search-results">
          <h3 class="section-title">搜索结果</h3>
          <div class="user-grid">
            <div v-for="user in searchResults" :key="user.username" class="user-card-wrapper">
              <el-card class="user-search-card" shadow="hover">
                <div class="user-info">
                  <el-avatar :size="60" :src="getAvatarUrl(user.avatar)" class="user-avatar"></el-avatar>
                  <div class="user-details">
                    <div class="username">{{ user.username }}</div>
                    <div v-if="user.email" class="email">{{ user.email }}</div>
                  </div>
                </div>
                <div class="card-action">
                  <el-button
                    v-if="!isUserSubscribed(user.username)"
                    type="primary"
                    round
                    size="small"
                    @click="followUser(user.username)"
                  >
                    关注
                  </el-button>
                  <el-button
                    v-else
                    type="info"
                    round
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
      </transition>

      <!-- 搜索结果为空时的提示 -->
      <transition name="fade">
        <div v-if="!searching && hasSearched && searchResults.length === 0 && !searchError" class="empty-state">
          <div class="empty-state-content">
            <el-empty description="" :image-size="120">
              <template #description>
                <p class="empty-title">未找到匹配的用户</p>
                <p class="empty-subtitle">尝试使用其他关键词搜索</p>
              </template>
            </el-empty>
          </div>
        </div>
      </transition>

      <!-- 我的关注列表区域 -->
      <div class="my-subscriptions">
        <h3 class="section-title">已关注用户</h3>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-section">
          <div class="loading-pulse"></div>
          <p class="loading-text">正在加载关注列表...</p>
        </div>
        
        <!-- 关注列表错误 -->
        <div v-else-if="error" class="error-section">
          <el-alert
            :title="error"
            type="error"
            :closable="false"
            effect="dark"
            show-icon
          >
            <template #default>
              <p>获取关注列表失败，请稍后再试</p>
              <el-button type="text" size="small" @click="fetchSubscriptions">重试</el-button>
            </template>
          </el-alert>
        </div>
        
        <!-- 空关注列表 -->
        <div v-else-if="!subscriptions || subscriptions.length === 0" class="empty-state">
          <div class="empty-state-content">
            <el-empty description="" :image-size="120">
              <template #description>
                <p class="empty-title">暂无关注的用户</p>
                <p class="empty-subtitle">通过搜索来关注你感兴趣的创作者</p>
              </template>
              <el-button type="primary" round @click="searchQuery = ''; executeSearch()">
                探索用户
              </el-button>
            </el-empty>
          </div>
        </div>
        
        <!-- 关注列表内容 -->
        <div v-else class="user-grid">
          <div v-for="sub in subscriptions" :key="sub.publisherUsername" class="user-card-wrapper">
            <el-card class="user-subscription-card" shadow="hover">
              <div class="user-info">
                <el-avatar :size="60" :src="getDefaultAvatarUrl()" class="user-avatar"></el-avatar>
                <div class="user-details">
                  <div class="username">{{ sub.publisherUsername }}</div>
                  <div class="since" v-if="sub.createdAt">关注于 {{ formatDate(sub.createdAt) }}</div>
                </div>
              </div>
              <div class="card-action">
                <el-button
                  type="danger"
                  round
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
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'SubscriptionList',
  setup() {
    const searchQuery = ref('')
    const searchResults = ref([])
    const subscriptions = ref([])
    const username = localStorage.getItem('username')
    const searching = ref(false) 
    const searchError = ref('') 
    const hasSearched = ref(false)
    const loading = ref(false)
    const error = ref('')

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '未知时间';
      try {
        const date = new Date(dateString);
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
      } catch (e) {
        return '未知时间';
      }
    };

    // 获取头像URL
    const getAvatarUrl = (avatar) => {
      if (!avatar) return getDefaultAvatarUrl();
      if (avatar === 'default.jpg') return getDefaultAvatarUrl();
      return `http://localhost:8081/uploads/avatars/${avatar}`;
    };

    // 获取默认头像URL
    const getDefaultAvatarUrl = () => {
      return `http://localhost:8081/uploads/avatars/default.jpg`;
    };

    // 检查用户是否已关注
    const isUserSubscribed = (publisherUsername) => {
      if (!publisherUsername || !subscriptions.value) return false;
      return subscriptions.value.some(sub => 
        sub && sub.publisherUsername === publisherUsername
      );
    };

    // 加载关注列表
    const fetchSubscriptions = async () => {
      loading.value = true;
      error.value = '';
      
      try {
        console.log('获取关注列表中...');
        // 确保username不为空
        if (!username) {
          console.error('用户名为空，无法获取关注列表');
          error.value = '未登录或用户名为空';
          subscriptions.value = [];
          loading.value = false;
          return;
        }

        // 避免URL编码问题，使用直接路径
        const response = await axios.get(`/api/subscriptions/my/${username}`);
        console.log('关注列表响应:', response);
        
        if (Array.isArray(response.data)) {
          // 过滤掉任何无效的订阅记录
          subscriptions.value = response.data.filter(sub => 
            sub && typeof sub === 'object' && sub.publisherUsername
          );
          console.log('过滤后的关注列表:', subscriptions.value);
        } else {
          console.error('返回的关注列表不是数组:', response.data);
          subscriptions.value = [];
          error.value = '获取关注列表格式错误';
        }
      } catch (err) {
        console.error('获取关注列表失败:', err);
        subscriptions.value = [];
        
        if (err.response) {
          console.error('错误详情:', err.response.data);
          if (err.response.status === 404) {
            // 404错误可能意味着用户尚未关注任何人
            subscriptions.value = [];
            error.value = '';
          } else if (err.response.status === 500) {
            error.value = `服务器内部错误，请联系管理员`;
          } else {
            error.value = `获取关注列表失败: ${err.response.status}`;
          }
        } else if (err.request) {
          error.value = '服务器无响应，请检查服务是否运行';
        } else {
          error.value = '请求关注列表时出错: ' + (err.message || '未知错误');
        }
      } finally {
        loading.value = false;
      }
    };

    // 执行搜索
    const executeSearch = async () => {
      searchError.value = '';
      
      if (!searchQuery.value.trim()) {
        searchResults.value = [];
        ElMessage.warning('请输入搜索关键词');
        return;
      }
      
      try {
        searching.value = true;
        hasSearched.value = true;
        
        console.log('搜索请求:', `/api/user/search?query=${encodeURIComponent(searchQuery.value.trim())}`);
        const response = await axios.get(`/api/user/search?query=${encodeURIComponent(searchQuery.value.trim())}`);
        
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
        }
      } catch (err) {
        console.error('搜索用户失败', err);
        searchResults.value = [];
        
        if (err.response) {
          searchError.value = `搜索失败: ${err.response.status}`;
        } else {
          searchError.value = '服务器无响应，请检查网络连接';
        }
      } finally {
        searching.value = false;
      }
    };

    // 关注用户
    const followUser = async (publisherUsername) => {
      try {
        console.log('关注用户:', publisherUsername);
        
        loading.value = true;
        await axios.post(`/api/subscriptions/follow`, {
          subscriberUsername: username,
          publisherUsername
        });
        
        // 在关注列表中添加新用户，避免重新请求整个列表
        const newSubscription = {
          id: Date.now(), // 临时ID
          publisherUsername: publisherUsername,
          subscriberUsername: username,
          createdAt: new Date().toISOString()
        };
        
        // 检查是否已存在，避免重复添加
        const exists = subscriptions.value.some(sub => sub.publisherUsername === publisherUsername);
        if (!exists) {
          subscriptions.value.push(newSubscription);
        }
        
        // 从搜索结果中移除已关注的用户，提供更好的用户体验
        searchResults.value = searchResults.value.filter(user => 
          user.username !== publisherUsername
        );
        
        ElMessage.success('关注成功');
      } catch (err) {
        console.error('关注失败', err);
        
        if (err.response) {
          ElMessage.error(err.response.data || '关注失败');
        } else {
          ElMessage.error('关注失败，请检查网络连接');
        }
      } finally {
        loading.value = false;
      }
    };

    // 取消关注用户
    const unfollowUser = async (publisherUsername) => {
      try {
        console.log('取消关注:', publisherUsername);
        
        loading.value = true;
        // 使用直接路径构造，避免编码问题
        const unsubscribeUrl = `/api/subscriptions/unfollow?subscriberUsername=${username}&publisherUsername=${publisherUsername}`;
        await axios.delete(unsubscribeUrl);
        
        // 直接从本地列表中移除，不重新获取
        subscriptions.value = subscriptions.value.filter(sub => 
          sub.publisherUsername !== publisherUsername
        );
        
        ElMessage.success('取消关注成功');
      } catch (err) {
        console.error('取消关注失败', err);
        
        if (err.response) {
          ElMessage.error(err.response.data || '取消关注失败');
        } else {
          ElMessage.error('取消关注失败，请检查网络连接');
        }
      } finally {
        loading.value = false;
      }
    };

    // 添加页面可见性变化监听，页面变为可见时刷新列表
    const setupVisibilityListener = () => {
      document.addEventListener('visibilitychange', () => {
        if (document.visibilityState === 'visible') {
          console.log('页面变为可见，刷新关注列表');
          fetchSubscriptions();
        }
      });
      
      // 组件卸载时移除事件监听
      onUnmounted(() => {
        document.removeEventListener('visibilitychange', fetchSubscriptions);
      });
    };

    onMounted(() => {
      fetchSubscriptions();
      setupVisibilityListener();
    });

    return {
      searchQuery,
      searchResults,
      subscriptions,
      username,
      loading,
      error,
      formatDate,
      getAvatarUrl,
      getDefaultAvatarUrl,
      isUserSubscribed,
      executeSearch,
      followUser,
      unfollowUser,
      fetchSubscriptions,
      searching,
      searchError,
      hasSearched
    };
  }
}
</script>

<style scoped>
.subscription-container {
  background-color: var(--bg-color);
  min-height: 100vh;
}

.hero-section {
  background: linear-gradient(to right, var(--primary-color), var(--primary-color-light));
  padding: 80px 0;
  color: white;
  text-align: center;
  margin-bottom: 40px;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 42px;
  font-weight: 600;
  margin: 0;
  letter-spacing: -0.5px;
  animation: fadeIn 1s ease-out;
}

.page-subtitle {
  font-size: 20px;
  font-weight: 300;
  margin-top: 16px;
  opacity: 0.9;
  animation: fadeIn 1s ease-out 0.2s both;
}

.content-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-container {
  margin-bottom: 40px;
  animation: slideUp 0.8s ease-out;
}

.search-wrapper {
  max-width: 600px;
  margin: 0 auto;
}

.search-input {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 24px;
  margin-top: 0;
  letter-spacing: -0.3px;
  color: var(--text-color);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  padding-bottom: 16px;
}

.user-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-top: 24px;
}

.user-card-wrapper {
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
}

.user-card-wrapper:hover {
  transform: translateY(-6px);
}

.user-search-card,
.user-subscription-card {
  height: 100%;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 16px;
}

.user-avatar {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 2px solid white;
}

.user-details {
  margin-left: 20px;
  flex: 1;
}

.username {
  font-weight: 600;
  font-size: 18px;
  color: var(--text-color);
  margin-bottom: 4px;
}

.email {
  font-size: 14px;
  color: var(--text-color-light);
}

.since {
  font-size: 13px;
  color: var(--text-color-light);
}

.card-action {
  padding: 0 16px 16px 16px;
  display: flex;
  justify-content: flex-end;
}

.my-subscriptions {
  margin-top: 60px;
  padding-bottom: 60px;
}

.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.loading-pulse {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: var(--primary-color);
  animation: pulse 1.5s infinite ease-in-out;
}

.loading-text {
  margin-top: 16px;
  color: var(--text-color-light);
  font-size: 14px;
}

.error-section {
  margin: 20px 0;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.empty-state-content {
  max-width: 500px;
  margin: 0 auto;
  padding: 40px 20px;
  border-radius: 12px;
  background-color: rgba(0, 0, 0, 0.02);
}

.empty-title {
  font-weight: 500;
  font-size: 18px;
  margin-bottom: 8px;
  color: var(--text-color);
}

.empty-subtitle {
  font-size: 14px;
  color: var(--text-color-light);
  margin-bottom: 24px;
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { 
    transform: translateY(20px);
    opacity: 0;
  }
  to { 
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes pulse {
  0% {
    transform: scale(0.8);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.4;
  }
  100% {
    transform: scale(0.8);
    opacity: 0.8;
  }
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s ease, transform 0.5s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-section {
    padding: 60px 0;
  }
  
  .page-title {
    font-size: 32px;
  }
  
  .page-subtitle {
    font-size: 18px;
  }
  
  .user-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 40px 0;
  }
  
  .page-title {
    font-size: 28px;
  }
  
  .search-wrapper {
    width: 100%;
  }
  
  .user-grid {
    grid-template-columns: 1fr;
  }
}
</style>