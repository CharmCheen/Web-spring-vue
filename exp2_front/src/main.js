// src/main.js
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

// —— Axios 全局配置 ——
// 1. 基准地址
axios.defaults.baseURL = 'http://localhost:8081'
// 2. 跨域携带凭证
axios.defaults.withCredentials = true

// 添加请求拦截器
axios.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    return config
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 添加响应拦截器
axios.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    return response
  },
  error => {
    // 对响应错误做点什么
    if (error.response) {
      switch (error.response.status) {
        case 403:
          error.message = '访问被拒绝，请检查您的输入'
          break
        case 404:
          error.message = '请求的资源不存在'
          break
        case 500:
          error.message = '服务器内部错误'
          break
        default:
          error.message = error.response.data || `请求失败: ${error.response.status}`
      }
    } else {
      error.message = '网络错误，请检查您的网络连接'
    }
    return Promise.reject(error)
  }
)

const app = createApp(App)

// 可在组件里用 this.$axios 发请求
app.config.globalProperties.$axios = axios

app.use(router)
app.use(ElementPlus)
app.mount('#app')
