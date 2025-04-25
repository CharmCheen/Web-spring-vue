<template>
  <div class="followed-articles-container">
    <div class="hero-section">
      <div class="content-wrapper">
        <h2 class="page-title">关注动态</h2>
        <p class="page-subtitle">发现你关注的创作者的最新内容</p>
      </div>
    </div>

    <div class="content-section">
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>
      
      <div v-else-if="articles.length === 0" class="empty-state">
        <el-empty description="暂无关注的创作者发布新文章">
          <el-button type="primary" @click="goToLogin">去发现更多创作者</el-button>
        </el-empty>
      </div>
      
      <div v-else class="articles-grid">
        <el-card v-for="article in articles" :key="article.id" class="article-card">
          <div class="article-cover" v-if="article.coverImage">
            <img :src="article.coverImage" :alt="article.title">
          </div>
          <div class="article-content">
            <h3 class="article-title" @click="viewArticle(article.id)">{{ article.title }}</h3>
            <div class="article-meta">
              <span class="author">{{ article.authorUsername }}</span>
              <span class="date">{{ formatDate(article.createTime) }}</span>
            </div>
            <p class="article-excerpt" v-if="article.content">{{ article.content.substring(0, 100) }}...</p>
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog
      v-model="previewDialogVisible"
      title="文章预览"
      width="80%"
      :before-close="closePreviewDialog"
    >
      <div v-if="currentArticle" class="preview-content">
        <h3>{{ currentArticle.title }}</h3>
        <div class="article-meta">
          <span class="author">作者：{{ currentArticle.authorUsername }}</span>
          <span class="date">发布于：{{ formatDate(currentArticle.createTime) }}</span>
        </div>
        <div class="article-cover" v-if="currentArticle.coverImage">
          <img :src="currentArticle.coverImage" :alt="currentArticle.title">
        </div>
        <div class="article-body">
          {{ currentArticle.content }}
        </div>
      </div>
      <div v-else class="preview-error">
        <el-empty description="文章内容加载失败" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const articles = ref([])
const loading = ref(true)
const currentArticle = ref(null)
const previewDialogVisible = ref(false)

// 获取关注用户的文章
const fetchFollowedArticles = async () => {
  try {
    console.log('开始获取关注动态')
    const token = localStorage.getItem('token')
    const username = localStorage.getItem('username')
    
    // 如果未登录，跳转到登录页
    if (!token || !username) {
      ElMessage.warning('请先登录后继续操作')
      router.push('/login')
      return
    }
    
    console.log('当前用户:', username)
    const response = await axios.get('/api/articles/followed', {
      headers: {
        'Authorization': token
      }
    })
    console.log('获取到的数据:', response.data)
    if (Array.isArray(response.data)) {
      articles.value = response.data
    } else {
      articles.value = []
      ElMessage.warning('暂无关注动态')
    }
  } catch (error) {
    console.error('获取动态详细错误:', error.response || error)
    if (error.response?.status === 401) {
      // 如果是未授权错误，清除本地存储并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      router.push('/login')
    } else {
      ElMessage.error(error.response?.data?.message || '获取动态失败')
    }
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 查看文章详情
const viewArticle = (articleId) => {
  const token = localStorage.getItem('token')
  const username = localStorage.getItem('username')
  
  // 如果未登录，跳转到登录页
  if (!token || !username) {
    ElMessage.warning('请先登录后继续操作')
    router.push('/login')
    return
  }

  // 找到当前点击的文章
  const article = articles.value.find(a => a.id === articleId)
  if (article) {
    currentArticle.value = article
    previewDialogVisible.value = true
  }
}

// 修改模板部分
const goToLogin = () => {
  router.push('/login')
}

// 关闭预览对话框
const closePreviewDialog = () => {
  currentArticle.value = null
  previewDialogVisible.value = false
}

onMounted(() => {
  fetchFollowedArticles()
})

return {
  articles,
  loading,
  formatDate,
  viewArticle,
  goToLogin,
  currentArticle,
  previewDialogVisible
}
</script>

<style scoped>
.followed-articles-container {
  min-height: 100vh;
  background-color: var(--bg-color, #f6f8fa);
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
}

.page-subtitle {
  font-size: 20px;
  font-weight: 300;
  margin-top: 16px;
  opacity: 0.9;
}

.content-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 60px;
}

.loading-state {
  padding: 20px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  padding: 20px 0;
}

.article-card {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.article-cover {
  height: 200px;
  overflow: hidden;
  margin: -20px -20px 20px;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-content {
  padding: 0 10px;
}

.article-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 12px;
  color: var(--text-color);
  line-height: 1.4;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  color: var(--text-color-light);
  font-size: 14px;
}

.article-excerpt {
  color: var(--text-color-secondary);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.preview-content {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

.preview-content h3 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--el-text-color-primary);
}

.preview-content .article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.preview-content .article-cover {
  margin: 0 -20px 24px;
  height: 300px;
  overflow: hidden;
}

.preview-content .article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-content .article-body {
  font-size: 16px;
  line-height: 1.8;
  color: var(--el-text-color-regular);
  white-space: pre-wrap;
}

.preview-error {
  padding: 40px 20px;
  text-align: center;
}

:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

:deep(.el-dialog__body) {
  padding: 0;
}

:deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 40px 0;
  }
  
  .page-title {
    font-size: 32px;
  }
  
  .page-subtitle {
    font-size: 18px;
  }
  
  .articles-grid {
    grid-template-columns: 1fr;
  }
}
</style> 