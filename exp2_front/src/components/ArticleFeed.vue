<template>
  <div class="article-feed-container">
    <!-- 顶部英雄区域 -->
    <div class="hero-section">
      <div class="content-wrapper">
        <h2 class="page-title">关注动态</h2>
        <p class="page-subtitle">发现你关注的创作者的最新内容</p>
      </div>
    </div>

    <div class="content-section">
      <!-- 文章流区域 -->
      <div class="feed-content">
        <transition-group name="fade">
          <!-- 空状态显示 -->
          <div v-if="articles.length === 0" class="empty-state" key="empty">
            <div class="empty-state-content">
              <el-empty description="" :image-size="120">
                <template #description>
                  <p class="empty-title">暂无动态内容</p>
                  <p class="empty-subtitle">关注更多创作者以获取精彩内容</p>
                </template>
                <el-button type="primary" round @click="goToSubscriptions">
                  去关注创作者
                </el-button>
              </el-empty>
            </div>
          </div>

          <!-- 文章卡片 -->
          <div v-for="article in articles" :key="article.articleId" class="article-item">
            <el-card shadow="hover" class="article-card">
              <div class="article-header">
                <div class="author-info">
                  <el-avatar size="small" :src="getAuthorAvatar()"></el-avatar>
                  <span class="author-name">{{ article.authorUsername }}</span>
                </div>
                <span class="publish-time">{{ formatTime(article.createdAt) }}</span>
              </div>
              
              <div class="article-title-section" @click="previewArticle(article)">
                <h3 class="article-title">{{ article.title }}</h3>
              </div>
              
              <!-- 如果有封面图片则显示 -->
              <div v-if="article.coverImage" class="cover-image-container">
                <img :src="article.coverImage" class="cover-image" @click="previewArticle(article)">
              </div>
              
              <!-- 文章预览 -->
              <div class="article-preview" @click="previewArticle(article)">
                <div class="preview-text">{{ getContentPreview(article.content) }}</div>
              </div>
              
              <!-- 文章操作 -->
              <div class="article-actions">
                <el-button type="text" @click="previewArticle(article)">
                  <i class="el-icon-view"></i>
                  <span>阅读全文</span>
                </el-button>
                <div class="action-divider"></div>
                <el-button type="text" @click="likeArticle()">
                  <i class="el-icon-star-off"></i>
                  <span>收藏</span>
                </el-button>
                <div class="action-divider"></div>
                <el-button type="text" @click="shareArticle()">
                  <i class="el-icon-share"></i>
                  <span>分享</span>
                </el-button>
              </div>
            </el-card>
          </div>
        </transition-group>
      </div>
      
      <!-- 加载更多 -->
      <div class="load-more-container" v-if="articles.length > 0 && hasMoreArticles">
        <el-button plain round @click="loadMoreArticles" :loading="loading">
          加载更多
        </el-button>
      </div>
      
      <!-- 预览对话框 -->
      <el-dialog
        title="文章内容"
        v-model="previewDialogVisible"
        width="70%"
        destroy-on-close
        class="preview-dialog"
      >
        <div class="preview-content">
          <h1 class="preview-title">{{ currentArticle.title }}</h1>
          <div class="preview-meta">
            <span>{{ formatTime(currentArticle.createdAt) }}</span>
            <span>作者: {{ currentArticle.authorUsername }}</span>
          </div>
          <div class="preview-cover" v-if="currentArticle.coverImage">
            <img :src="currentArticle.coverImage" alt="封面图片" />
          </div>
          <div class="preview-body">
            <v-md-preview :text="currentArticle.content"></v-md-preview>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue' // 添加onUnmounted
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import VMdPreview from '@kangc/v-md-editor/lib/preview'
import '@kangc/v-md-editor/lib/style/preview.css'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js'
import '@kangc/v-md-editor/lib/theme/style/github.css'

VMdPreview.use(githubTheme)

const router = useRouter()
const articles = ref([])
const username = localStorage.getItem('username')
const loading = ref(false)
const page = ref(1)
const pageSize = ref(5)
const hasMoreArticles = ref(true)
const previewDialogVisible = ref(false)
const currentArticle = ref({
  title: '',
  content: '',
  authorUsername: '',
  coverImage: '',
  createdAt: ''
})

const fetchFeed = async (reset = false) => {
  if (!username) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (reset) {
    page.value = 1
    articles.value = []
  }
  
  loading.value = true
  try {
    console.log('获取文章Feed，username:', username, 'page:', page.value, 'size:', pageSize.value)
    
    // 使用直接的URL路径，避免编码问题
    const response = await axios.get(`/api/articles/feed/${username}?page=${page.value}&size=${pageSize.value}`)
    
    console.log('获取Feed响应:', response.data)
    
    if (Array.isArray(response.data)) {
      if (reset) {
        articles.value = response.data
      } else {
        // 合并数组，避免重复
        const newArticles = response.data.filter(newArticle => 
          !articles.value.some(existingArticle => 
            existingArticle.articleId === newArticle.articleId
          )
        )
        articles.value = [...articles.value, ...newArticles]
      }
      
      // 如果返回的文章数量小于页面大小，说明没有更多内容
      hasMoreArticles.value = response.data.length >= pageSize.value
      
      if (response.data.length > 0) {
        page.value++
      }
      
      console.log('文章加载成功，当前共有', articles.value.length, '篇文章')
    } else {
      console.error('文章Feed返回的不是数组:', response.data)
      ElMessage.warning('获取文章格式异常')
    }
  } catch (error) {
    console.error('获取动态失败:', error)
    if (error.response && error.response.status === 404) {
      // 404错误，可能是没有关注任何用户
      ElMessage.info('你还没有关注任何用户，或关注的用户没有发布文章')
      articles.value = []
    } else {
      ElMessage.error('获取动态失败: ' + (error.response?.data || error.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
}

const loadMoreArticles = () => {
  fetchFeed(false)
}

const formatTime = (time) => {
  if (!time) return '未知时间'
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getAuthorAvatar = () => { // 移除未使用的参数
  // 假设有默认头像路径
  return '/uploads/avatars/default.jpg'
}

const getContentPreview = (content) => {
  if (!content) return '无内容'
  // 去除markdown标记并截取前100个字符
  const plainText = content.replace(/[#*`[\]()>_~]/g, '')
  return plainText.slice(0, 150) + (plainText.length > 150 ? '...' : '')
}

const previewArticle = (article) => {
  currentArticle.value = { ...article }
  previewDialogVisible.value = true
}

const goToSubscriptions = () => {
  router.push('/subscriptions')
}

const likeArticle = () => { // 移除未使用的参数
  ElMessage.success('收藏功能即将上线')
}

const shareArticle = () => { // 移除未使用的参数
  ElMessage.success('分享功能即将上线')
}

// 定期刷新动态
const startAutoRefresh = () => {
  // 每30秒刷新一次动态
  const refreshInterval = setInterval(() => {
    if (!previewDialogVisible.value && document.visibilityState === 'visible') { 
      // 不在预览状态且页面可见时才刷新
      console.log('自动刷新文章Feed')
      fetchFeed(true)
    }
  }, 30000) // 30秒

  // 监听页面可见性变化，页面变为可见时刷新
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible') {
      console.log('页面变为可见，刷新文章Feed')
      fetchFeed(true)
    }
  })

  // 组件卸载时清除定时器和事件监听
  onUnmounted(() => {
    clearInterval(refreshInterval)
    document.removeEventListener('visibilitychange', fetchFeed)
  })
}

onMounted(() => {
  fetchFeed(true)
  startAutoRefresh()
})
</script>

<style scoped>
.article-feed-container {
  min-height: 100vh;
  background-color: var(--bg-color);
}

.hero-section {
  background: linear-gradient(to right, var(--primary-color), var(--primary-color-light));
  padding: 60px 0;
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
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px;
}

.feed-content {
  margin-bottom: 40px;
}

.article-item {
  margin-bottom: 24px;
  animation: slideUp 0.8s ease-out;
}

.article-card {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.07);
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  align-items: center;
}

.author-name {
  margin-left: 8px;
  font-weight: 500;
  font-size: 14px;
}

.publish-time {
  font-size: 14px;
  color: var(--text-color-light);
}

.article-title-section {
  cursor: pointer;
}

.article-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: var(--text-color);
  transition: color 0.2s;
}

.article-title:hover {
  color: var(--primary-color);
}

.cover-image-container {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.cover-image {
  width: 100%;
  max-height: 360px;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.cover-image:hover {
  transform: scale(1.02);
}

.article-preview {
  margin-bottom: 16px;
  cursor: pointer;
}

.preview-text {
  font-size: 15px;
  color: var(--text-color-light);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

.article-actions {
  display: flex;
  align-items: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 16px;
}

.article-actions .el-button {
  font-size: 14px;
  padding: 8px 0;
}

.article-actions .el-button i {
  margin-right: 4px;
}

.action-divider {
  height: 16px;
  width: 1px;
  background-color: rgba(0, 0, 0, 0.1);
  margin: 0 16px;
}

.load-more-container {
  text-align: center;
  margin: 40px 0 60px 0;
}

.empty-state {
  padding: 60px 0;
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

/* 预览对话框样式 */
:deep(.preview-dialog .el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

:deep(.preview-dialog .el-dialog__body) {
  padding: 24px;
}

.preview-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-color);
}

.preview-meta {
  display: flex;
  justify-content: space-between;
  color: var(--text-color-light);
  font-size: 14px;
  margin-bottom: 24px;
}

.preview-cover {
  width: 100%;
  max-height: 400px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 24px;
}

.preview-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-body {
  padding: 16px 0;
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

.fade-enter-active, .fade-leave-active {
  transition: all 0.5s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 响应式设计 */
@media (max-width: 900px) {
  .hero-section {
    padding: 40px 0;
  }
  
  .page-title {
    font-size: 32px;
  }
  
  .page-subtitle {
    font-size: 18px;
  }
  
  .article-actions {
    flex-wrap: wrap;
    justify-content: space-around;
  }
  
  .action-divider {
    display: none;
  }
}

@media (max-width: 600px) {
  .hero-section {
    padding: 30px 0;
  }
  
  .page-title {
    font-size: 28px;
  }
  
  .article-title {
    font-size: 18px;
  }
  
  .cover-image {
    max-height: 240px;
  }
  
  .preview-text {
    -webkit-line-clamp: 2;
    font-size: 14px;
  }
  
  .article-actions .el-button {
    font-size: 12px;
  }
}
</style>