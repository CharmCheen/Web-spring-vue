<template>
  <div class="article-feed">
    <h2>关注动态</h2>
    <div class="article-list">
      <el-card v-for="article in articles" :key="article.articleId" class="article-card">
        <template #header>
          <div class="article-header">
            <span class="title">{{ article.title }}</span>
            <span class="author">作者：{{ article.authorUsername }}</span>
          </div>
        </template>
        <div class="article-content">
          <img v-if="article.coverImage" :src="article.coverImage" class="cover-image">
          <div class="markdown-body" v-html="previewContent(article.content)"></div>
        </div>
        <div class="article-footer">
          <span class="time">{{ formatTime(article.createdAt) }}</span>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import VMdPreview from '@kangc/v-md-editor/lib/preview'
import '@kangc/v-md-editor/lib/style/preview.css'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js'
import '@kangc/v-md-editor/lib/theme/style/github.css'

VMdPreview.use(githubTheme)

const articles = ref([])
const username = localStorage.getItem('username')

const fetchFeed = async () => {
  try {
    const response = await axios.get(`/api/articles/feed/${username}`)
    articles.value = response.data
  } catch (error) {
    ElMessage.error('获取动态失败')
  }
}

const formatTime = (time) => {
  return new Date(time).toLocaleString()
}

const previewContent = (content) => {
  return VMdPreview.themeConfig.markdownParser.render(content)
}

onMounted(() => {
  fetchFeed()
})
</script>

<style scoped>
.article-feed {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.article-card {
  margin-bottom: 20px;
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.author {
  color: #666;
}

.article-content {
  margin: 10px 0;
}

.cover-image {
  max-width: 100%;
  max-height: 300px;
  object-fit: cover;
  margin-bottom: 10px;
}

.article-footer {
  color: #999;
  font-size: 14px;
}

.markdown-body {
  padding: 10px;
}
</style> 