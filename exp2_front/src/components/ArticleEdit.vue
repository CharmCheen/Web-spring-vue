<template>
  <div class="article-edit">
    <el-form :model="article" label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="article.title" placeholder="请输入文章标题"></el-input>
      </el-form-item>
      <el-form-item label="封面">
        <el-upload
          class="upload-demo"
          action="/api/upload"
          :on-success="handleUploadSuccess"
          :before-upload="beforeUpload"
        >
          <el-button type="primary">更换封面</el-button>
        </el-upload>
        <img v-if="article.coverImage" :src="article.coverImage" class="preview-image">
      </el-form-item>
      <el-form-item label="内容">
        <div class="editor-container">
          <v-md-editor
            v-model="article.content"
            height="500px"
            :toolbar="toolbar"
            @save="updateArticle"
          ></v-md-editor>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="updateArticle">保存修改</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import VMdEditor from '@kangc/v-md-editor'
import '@kangc/v-md-editor/lib/style/preview.css'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js'
import '@kangc/v-md-editor/lib/theme/style/github.css'

VMdEditor.use(githubTheme)

const route = useRoute()
const router = useRouter()
const articleId = route.params.id

const article = ref({
  articleId: null,
  title: '',
  content: '',
  coverImage: '',
  authorUsername: localStorage.getItem('username')
})

const toolbar = {
  h1: true,
  h2: true,
  h3: true,
  h4: true,
  h5: true,
  h6: true,
  bold: true,
  italic: true,
  strikethrough: true,
  quote: true,
  ul: true,
  ol: true,
  link: true,
  image: true,
  code: true,
  table: true,
  save: true
}

const fetchArticle = async () => {
  try {
    const response = await axios.get(`/api/articles/${articleId}`)
    article.value = response.data
  } catch (error) {
    ElMessage.error('获取文章失败')
  }
}

const handleUploadSuccess = (response) => {
  article.value.coverImage = response.url
  ElMessage.success('封面上传成功')
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  return true
}

const updateArticle = async () => {
  try {
    await axios.put(`/api/articles/${articleId}`, article.value)
    ElMessage.success('文章更新成功')
    router.push('/articles/manage')
  } catch (error) {
    ElMessage.error('文章更新失败')
  }
}

const goBack = () => {
  router.push('/articles/manage')
}

onMounted(() => {
  fetchArticle()
})
</script>

<style scoped>
.article-edit {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
}

.preview-image {
  max-width: 200px;
  max-height: 200px;
  margin-top: 10px;
  object-fit: cover;
}

.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
</style> 