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
          <el-button type="primary" @click="$router.push('/articles')">去发现更多创作者</el-button>
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

// 获取关注用户的文章
const fetchFollowedArticles = async () => {
  try {
    const response = await axios.get('/api/articles/followed', {
      headers: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    })
    articles.value = response.data
  } catch (error) {
    ElMessage.error('获取关注动态失败')
    console.error('获取关注动态失败:', error)
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
  router.push('/articles/' + articleId)
}

onMounted(() => {
  fetchFollowedArticles()
})
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