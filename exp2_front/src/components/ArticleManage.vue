<template>
  <div class="article-manage">
    <h2>我的文章</h2>
    <el-table :data="articles" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="createdAt" label="发布时间" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="editArticle(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteArticle(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const articles = ref([])
const username = localStorage.getItem('username')

const fetchArticles = async () => {
  try {
    const response = await axios.get(`/api/articles/author/${username}`)
    articles.value = response.data
  } catch (error) {
    ElMessage.error('获取文章列表失败')
  }
}

const editArticle = (article) => {
  router.push(`/articles/edit/${article.articleId}`)
}

const deleteArticle = async (article) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.delete(`/api/articles/${article.articleId}`)
    ElMessage.success('删除成功')
    fetchArticles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchArticles()
})
</script>

<style scoped>
.article-manage {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}
</style> 