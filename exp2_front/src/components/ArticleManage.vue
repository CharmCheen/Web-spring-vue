<template>
  <div class="article-manage-container">
    <!-- 顶部英雄区域 -->
    <div class="hero-section">
      <div class="content-wrapper">
        <h2 class="page-title">我的文章</h2>
        <p class="page-subtitle">管理你发布的所有内容</p>
      </div>
    </div>

    <div class="content-section">
      <!-- 操作按钮区域 -->
      <div class="actions-container">
        <div class="left-actions">
          <el-input
            placeholder="搜索文章标题..."
            prefix-icon="el-icon-search"
            v-model="searchQuery"
            clearable
            class="search-input"
          ></el-input>
        </div>
        <div class="right-actions">
          <el-button type="primary" icon="el-icon-plus" @click="createArticle">
            发布新文章
          </el-button>
        </div>
      </div>

      <!-- 文章列表区域 -->
      <div class="article-cards-container">
        <transition-group name="fade">
          <div v-if="filteredArticles.length === 0" class="empty-state" key="empty">
            <div class="empty-state-content">
              <el-empty description="" :image-size="120">
                <template #description>
                  <p class="empty-title">你还没有发布文章</p>
                  <p class="empty-subtitle">点击下方按钮开始创作</p>
                </template>
                <el-button type="primary" round @click="createArticle">
                  创建第一篇文章
                </el-button>
              </el-empty>
            </div>
          </div>

          <div v-for="article in filteredArticles" :key="article.articleId" class="article-card">
            <el-card shadow="hover" class="card-content">
              <div class="card-header">
                <div class="article-cover">
                  <img v-if="article.coverImage" :src="article.coverImage" alt="封面"/>
                  <div v-else class="no-cover">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </div>
                <div class="article-info">
                  <h3 class="article-title">{{ article.title }}</h3>
                  <p class="article-meta">
                    <span class="meta-item">
                      <i class="el-icon-date"></i>
                      {{ formatDateTime(article.createdAt) }}
                    </span>
                    <span class="meta-item">
                      <i class="el-icon-document"></i>
                      {{ getContentPreview(article.content) }}
                    </span>
                  </p>
                </div>
              </div>
              <div class="card-actions">
                <el-button size="small" @click="showArticlePreview(article)" type="text" icon="el-icon-view">
                  预览
                </el-button>
                <el-button size="small" @click="editArticle(article)" type="text" icon="el-icon-edit">
                  编辑
                </el-button>
                <el-button size="small" @click="deleteArticle(article)" type="text" icon="el-icon-delete" class="danger-text">
                  删除
                </el-button>
              </div>
            </el-card>
          </div>
        </transition-group>
      </div>

      <!-- 预览对话框 -->
      <el-dialog
        title="文章预览"
        v-model="previewDialogVisible"
        width="70%"
        destroy-on-close
        class="preview-dialog"
      >
        <div class="preview-content">
          <h1 class="preview-title">{{ previewArticle.title }}</h1>
          <div class="preview-meta">
            <span>{{ formatDateTime(previewArticle.createdAt) }}</span>
            <span>作者: {{ previewArticle.authorUsername }}</span>
          </div>
          <div class="preview-cover" v-if="previewArticle.coverImage">
            <img :src="previewArticle.coverImage" alt="封面图片" />
          </div>
          <div class="preview-body">
            <v-md-preview :text="previewArticle.content"></v-md-preview>
          </div>
        </div>
      </el-dialog>

      <!-- 分页控件 -->
      <div class="pagination-container" v-if="articles.length > pageSize">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[8, 16, 24, 32]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="articles.length"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import VMdPreview from '@kangc/v-md-editor/lib/preview'
import '@kangc/v-md-editor/lib/style/preview.css'
import '@kangc/v-md-editor/lib/theme/style/github.css'
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js'

VMdPreview.use(githubTheme)

const router = useRouter()
const articles = ref([])
const username = localStorage.getItem('username')
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(8)
const previewDialogVisible = ref(false)
const previewArticle = ref({
  title: '',
  content: '',
  coverImage: '',
  createdAt: '',
  authorUsername: ''
})
const loading = ref(false)

const fetchArticles = async () => {
  try {
    loading.value = true
    const response = await axios.get(`/api/articles/author/${username}`)
    articles.value = response.data
    console.log('获取文章列表成功，共获取', articles.value.length, '篇文章')
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 添加定时刷新功能
const startAutoRefresh = () => {
  // 每30秒刷新一次文章列表
  const refreshInterval = setInterval(() => {
    if (document.visibilityState === 'visible') { 
      // 页面可见时才刷新
      console.log('自动刷新文章列表')
      fetchArticles()
    }
  }, 30000) // 30秒

  // 监听页面可见性变化，页面变为可见时刷新
  document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible') {
      console.log('页面变为可见，刷新文章列表')
      fetchArticles()
    }
  })

  // 组件卸载时清除定时器和事件监听
  onUnmounted(() => {
    clearInterval(refreshInterval)
    document.removeEventListener('visibilitychange', fetchArticles)
  })
}

const filteredArticles = computed(() => {
  const query = searchQuery.value.toLowerCase().trim()
  if (!query) return articles.value
    .slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value)
  
  return articles.value
    .filter(article => article.title.toLowerCase().includes(query))
    .slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value)
})

const createArticle = () => {
  router.push('/articles/create')
}

const editArticle = (article) => {
  router.push(`/articles/edit/${article.articleId}`)
}

const showArticlePreview = (article) => {
  previewArticle.value = { ...article }
  previewDialogVisible.value = true
}

const deleteArticle = async (article) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？该操作不可恢复。', '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    loading.value = true
    await axios.delete(`/api/articles/${article.articleId}`)
    ElMessage.success('文章删除成功')
    // 删除后重新获取文章列表
    await fetchArticles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
      ElMessage.error('删除文章失败')
    }
  } finally {
    loading.value = false
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '未知时间'
  const date = new Date(dateTime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getContentPreview = (content) => {
  if (!content) return '无内容'
  // 去除markdown标记并截取前30个字符
  return content.replace(/[#*`[\]()>_~]/g, '').slice(0, 30) + (content.length > 30 ? '...' : '')
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

onMounted(() => {
  fetchArticles()
  startAutoRefresh()
})
</script>

<style scoped>
.article-manage-container {
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.actions-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  animation: slideUp 0.7s ease-out;
}

.search-input {
  width: 300px;
}

.article-cards-container {
  margin-bottom: 40px;
  min-height: 300px;
}

.article-card {
  margin-bottom: 20px;
  animation: slideUp 0.8s ease-out;
}

.card-content {
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card-content:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
}

.article-cover {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 16px;
  flex-shrink: 0;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-cover {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.no-cover i {
  font-size: 28px;
}

.article-info {
  flex-grow: 1;
  overflow: hidden;
}

.article-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.article-meta {
  display: flex;
  align-items: center;
  color: var(--text-color-light);
  font-size: 14px;
  margin: 0;
}

.meta-item {
  margin-right: 16px;
  display: flex;
  align-items: center;
}

.meta-item i {
  margin-right: 4px;
  font-size: 16px;
}

.card-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 12px;
}

.card-actions .el-button {
  margin-left: 16px;
}

.danger-text {
  color: #f56c6c;
}

.danger-text:hover {
  color: #f56c6c;
  opacity: 0.8;
}

.pagination-container {
  display: flex;
  justify-content: center;
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
  
  .actions-container {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-input,
  .right-actions {
    width: 100%;
    margin-bottom: 16px;
  }
  
  .right-actions .el-button {
    width: 100%;
  }
}

@media (max-width: 600px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .article-cover {
    width: 100%;
    height: 140px;
    margin-right: 0;
    margin-bottom: 16px;
  }
  
  .meta-item {
    margin-right: 12px;
    font-size: 12px;
  }
  
  .card-actions {
    justify-content: space-between;
  }
  
  .card-actions .el-button {
    margin-left: 0;
    padding: 6px;
  }
}
</style>