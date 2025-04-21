<template>
  <div class="article-create-container">
    <div class="article-create-wrapper">
      <div class="article-create-header">
        <h2>发布新文章</h2>
        <p>分享你的知识和见解</p>
      </div>
      <el-form :model="article" label-width="80px" class="article-form">
        <el-form-item label="标题" prop="title">
          <el-input 
            v-model="article.title" 
            placeholder="请输入文章标题"
            size="large"
            class="title-input"
          ></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <div class="editor-container">
            <v-md-editor
              v-model="article.content"
              height="500px"
              :toolbar="toolbar"
              :mode="mode"
              @change="handleChange"
              @save="submitArticle"
              class="markdown-editor"
            ></v-md-editor>
          </div>
        </el-form-item>
        <el-form-item class="submit-buttons">
          <div class="btn-group">
            <el-button type="primary" size="large" @click="submitArticle" :loading="loading">
              发布文章
            </el-button>
            <el-button size="large" @click="cancel">取消</el-button>
          </div>
        </el-form-item>
      </el-form>
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
  authorUsername: localStorage.getItem('username')
})

const mode = ref('edit')
const loading = ref(false)

const toolbar = {
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
}

const handleChange = (text) => {
  console.log('内容变化:', text)
}

const submitArticle = async () => {
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
  padding: 32px 0 32px 0;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.article-create-wrapper {
  width: 100%;
  max-width: 900px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  padding: 36px 40px 32px 40px;
  margin: 0 auto;
}

.article-create-header {
  margin-bottom: 32px;
  text-align: center;
}

.article-create-header h2 {
  font-size: 26px;
  color: #303133;
  margin-bottom: 8px;
}

.article-create-header p {
  font-size: 15px;
  color: #909399;
}

.article-form {
  max-width: 760px;
  margin: 0 auto;
}

.title-input {
  font-size: 17px;
}

.editor-container {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
  transition: all 0.3s;
  background: #fafbfc;
  margin: 0;
  padding: 0;
}

.editor-container:hover {
  border-color: #409eff;
}

.markdown-editor {
  width: 100%;
  min-width: 0;
}

:deep(.v-md-editor) {
  min-height: 350px;
  height: 500px;
}

:deep(.v-md-editor__toolbar) {
  background-color: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
  padding: 8px 0;
}

:deep(.v-md-editor__toolbar-item) {
  color: #606266;
  margin: 0 8px;
}

:deep(.v-md-editor__toolbar-item:hover) {
  color: #409eff;
}

:deep(.v-md-editor__preview) {
  padding: 20px;
}

.submit-buttons {
  margin-top: 32px;
  padding-right: 0;
}

.btn-group {
  display: flex;
  justify-content: flex-end;
  gap: 18px;
}

.submit-buttons .el-button {
  min-width: 120px;
}

@media (max-width: 900px) {
  .article-create-wrapper {
    padding: 20px 8px;
  }
  .article-form {
    max-width: 100%;
    padding: 0 2px;
  }
}
@media (max-width: 600px) {
  .article-create-wrapper {
    padding: 10px 0;
    border-radius: 0;
    box-shadow: none;
  }
  .article-form {
    padding: 0 2px;
  }
  .submit-buttons .btn-group {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  .submit-buttons {
    text-align: center;
    padding-right: 0;
  }
}
</style>