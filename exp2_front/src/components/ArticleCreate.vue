<template>
  <div class="article-create-container">
    <!-- 顶部英雄区域 -->
    <div class="hero-section">
      <div class="content-wrapper">
        <h2 class="page-title">创作新内容</h2>
        <p class="page-subtitle">分享你的想法、知识和经验，连接你的读者</p>
      </div>
    </div>

    <div class="content-section">
      <!-- 编辑区域 -->
      <div class="article-create-wrapper">
        <el-form :model="article" label-width="0" class="article-form">
          <div class="form-section title-section">
            <h3 class="section-title">文章标题</h3>
            <el-input 
              v-model="article.title" 
              placeholder="输入一个引人注目的标题..."
              size="large"
              class="title-input"
              maxlength="100"
              show-word-limit
            ></el-input>
          </div>
          
          <div class="form-section cover-section">
            <h3 class="section-title">封面图片</h3>
            <p class="section-description">一张好的封面图片能吸引更多读者</p>
            <el-upload
              class="cover-uploader"
              action="/api/upload"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
            >
              <div v-if="!article.coverImage" class="upload-placeholder">
                <i class="el-icon-plus"></i>
                <p>点击上传封面图片</p>
              </div>
              <img v-else :src="article.coverImage" class="cover-preview">
            </el-upload>
          </div>

          <div class="form-section content-section">
            <h3 class="section-title">文章内容</h3>
            <p class="section-description">使用Markdown格式编写你的文章</p>
            <div class="editor-container">
              <v-md-editor
                v-model="article.content"
                height="500px"
                :toolbar="toolbar"
                :mode="mode"
                @change="handleChange"
                @save="submitArticle"
                :disabled-menus="[]"
                @init-complete="handleEditorInitComplete"
                class="markdown-editor"
              ></v-md-editor>
            </div>
          </div>

          <div class="form-actions">
            <div class="btn-group">
              <el-button plain @click="cancel">取消</el-button>
              <el-button type="primary" @click="submitArticle" :loading="loading">
                发布文章
              </el-button>
            </div>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import VMdEditor from '@kangc/v-md-editor'
import '@kangc/v-md-editor/lib/style/base-editor.css'
import '@kangc/v-md-editor/lib/theme/style/github.css'
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js'
import createEmojiPlugin from '@kangc/v-md-editor/lib/plugins/emoji/index'
import '@kangc/v-md-editor/lib/plugins/emoji/emoji.css'
import createKatexPlugin from '@kangc/v-md-editor/lib/plugins/katex/cdn'
import createTodoListPlugin from '@kangc/v-md-editor/lib/plugins/todo-list/index'
import '@kangc/v-md-editor/lib/plugins/todo-list/todo-list.css'
import hljs from 'highlight.js'
import { useRouter } from 'vue-router'

const router = useRouter()

VMdEditor.use(githubTheme, {
  Hljs: hljs,
  codeHighlightExtensionMap: {
    vue: 'xml',
    typescript: 'typescript',
    javascript: 'javascript',
  },
})

VMdEditor.use(createEmojiPlugin())
VMdEditor.use(createKatexPlugin())
VMdEditor.use(createTodoListPlugin())

const article = ref({
  title: '',
  content: '',
  coverImage: '',
  authorUsername: localStorage.getItem('username')
})

const mode = ref('edit')
const loading = ref(false)

const editorRef = ref(null)
const isEditorReady = ref(false)

const toolbar = ref({
  left: [
    'undo',
    'redo',
    '|',
    'bold',
    'italic',
    'strikethrough',
    'quote',
    'unordered-list',
    'ordered-list',
    'task-list',
    'code',
    'code-block',
    'table',
    '|',
    'heading',
    'emoji',
    'link',
    'image',
    'formula',
    '|',
    'preview',
    'fullscreen'
  ],
  right: ['save']
})

const handleEditorInitComplete = (editor) => {
  editorRef.value = editor
  isEditorReady.value = true
  console.log('编辑器初始化完成')
}

const handleChange = (text) => {
  if (!isEditorReady.value) {
    console.log('编辑器未就绪，忽略变更')
    return
  }
  console.log('内容变化:', text)
}

// 处理封面上传成功
const handleUploadSuccess = (response) => {
  article.value.coverImage = response.url
  ElMessage.success('封面上传成功')
}

// 上传前的验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB！')
    return false
  }
  
  return true
}

const submitArticle = async () => {
  if (!isEditorReady.value) {
    ElMessage.warning('编辑器正在初始化，请稍候再试')
    return
  }

  if (!article.value.title) {
    ElMessage.warning('请输入文章标题')
    return
  }
  if (!article.value.content) {
    ElMessage.warning('请输入文章内容')
    return
  }
  
  loading.value = true
  try {
    await axios.post('/api/articles', article.value)
    ElMessage.success('文章发布成功')
    // 清空表单
    article.value = {
      title: '',
      content: '',
      coverImage: '',
      authorUsername: localStorage.getItem('username')
    }
    // 跳转到文章列表页
    router.push('/articles/manage')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '文章发布失败')
  } finally {
    loading.value = false
  }
}

const cancel = () => {
  router.push('/articles/manage')
}
</script>

<style scoped>
.article-create-container {
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

.article-create-wrapper {
  width: 100%;
  max-width: 900px;
  background-color: var(--card-bg);
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.07);
  padding: 40px;
  margin: 0 auto 60px;
  animation: slideUp 0.8s ease-out;
}

.article-form {
  width: 100%;
}

.form-section {
  margin-bottom: 36px;
}

.section-title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--text-color);
}

.section-description {
  color: var(--text-color-light);
  margin-bottom: 16px;
  font-size: 15px;
}

.title-input {
  width: 100%;
}

.cover-section {
  display: flex;
  flex-direction: column;
}

.cover-uploader {
  display: flex;
  justify-content: center;
  width: 100%;
}

.upload-placeholder {
  width: 100%;
  height: 240px;
  background-color: rgba(0, 0, 0, 0.02);
  border: 2px dashed #d9d9d9;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: var(--text-color-light);
  transition: all 0.3s;
}

.upload-placeholder:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.upload-placeholder i {
  font-size: 40px;
  margin-bottom: 10px;
}

.cover-preview {
  width: 100%;
  height: 240px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.editor-container {
  position: relative;
  min-height: 500px;
}

.editor-container:hover {
  border-color: var(--primary-color);
}

:deep(.v-md-editor) {
  min-height: 500px;
}

:deep(.v-md-editor__toolbar) {
  background-color: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
}

:deep(.v-md-editor__toolbar-item) {
  color: #606266;
}

:deep(.v-md-editor__toolbar-item:hover) {
  color: var(--primary-color);
}

:deep(.v-md-textarea-editor) {
  min-height: 450px;
}

:deep(.v-md-editor__menu-bar) {
  position: sticky;
  top: 0;
  z-index: 1;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 48px;
}

.btn-group {
  display: flex;
  gap: 16px;
}

.btn-group .el-button {
  min-width: 120px;
  height: 44px;
  font-size: 16px;
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
  
  .article-create-wrapper {
    padding: 30px 20px;
  }
}

@media (max-width: 600px) {
  .article-create-wrapper {
    padding: 20px 15px;
    border-radius: 12px;
  }
  
  .form-actions {
    margin-top: 32px;
  }
  
  .btn-group {
    width: 100%;
    flex-direction: column-reverse;
    align-items: stretch;
  }
  
  .upload-placeholder,
  .cover-preview {
    height: 180px;
  }
}
</style>