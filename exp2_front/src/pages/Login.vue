<template>
  <div class="form-container">
    <div class="login-wrapper">
      <!-- 左侧展示区域 -->
      <div class="form-left">
        <div class="left-content">
          <h2>欢迎回来</h2>
          <p>登录您的账户，发现更多精彩内容</p>
          <div class="illustration-placeholder">
            <img v-if="userAvatar" :src="userAvatar" class="avatar-preview" alt="用户头像">
            <i v-else class="el-icon-user-solid" style="font-size: 80px; color: rgba(255,255,255,0.8);"></i>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录表单区域 -->
      <div class="form-right">
        <div class="form-box">
          <div class="logo-placeholder">
            <i class="el-icon-s-platform" style="font-size: 40px; color: var(--primary-color);"></i>
          </div>
          <h2 class="form-title">用户登录</h2>
          
          <el-form :model="loginForm" :rules="rules" ref="loginForm">
            <el-form-item prop="account">
              <el-input 
                v-model="loginForm.account" 
                placeholder="用户名或邮箱" 
                prefix-icon="el-icon-user"
                size="large"
              ></el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码" 
                prefix-icon="el-icon-lock"
                size="large"
                show-password
              ></el-input>
            </el-form-item>
            
            <el-form-item>
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <router-link to="/forgot-password" class="forgot-password">忘记密码?</router-link>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                size="large"
                @click="submitForm"
                :loading="loading"
                style="width: 100%"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="register-link">
            还没有账号? <router-link to="/register">立即注册</router-link>
          </div>
          
          <div class="social-login">
            <div class="divider">
              <span class="divider-line"></span>
              <span class="divider-text">或使用第三方登录</span>
              <span class="divider-line"></span>
            </div>
            <div class="social-icons">
              <el-button circle>
                <img src="@/assets/images/qq_icon.png" alt="icon1" style="width: 24px; height: 24px;">
              </el-button>
              <el-button circle>
                <img src="@/assets/images/wechat_icon.png" alt="icon1" style="width: 24px; height: 24px;">
              </el-button>
              <el-button circle>
                <img src="@/assets/images/alipay_icon.png" alt="icon1" style="width: 24px; height: 24px;">
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  name: 'LoginPage',
  data() {
    return {
      loginForm: {
        account: '',
        password: ''
      },
      rememberMe: false,
      loading: false,
      userAvatar: null,
      rules: {
        account: [
          { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    submitForm() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true
          axios.post('/api/user/login', {
            account: this.loginForm.account,
            password: this.loginForm.password
          })
          .then(response => {
            this.loading = false
            // 处理头像URL
            const userData = response.data;
            if (userData.avatar) {
              // 如果是默认头像
              if (userData.avatar === 'default.jpg') {
                userData.avatarUrl = '/uploads/avatars/default.jpg';
              } else {
                // 如果是用户上传的头像
                userData.avatarUrl = `/uploads/avatars/${userData.avatar}`;
              }
            }
            // 存储用户信息
            localStorage.setItem('user', JSON.stringify(userData))
            this.$message.success('登录成功！')
            // 登录成功后跳转到主页
            this.$router.push('/home')
          })
          .catch(err => {
            this.loading = false
            let errorMessage = err.response?.data || '登录失败，请重试'
            this.$message.error(errorMessage)
          })
        }
      })
    },
    // 当输入账号时尝试获取头像
    async handleAccountInput() {
      if (this.loginForm.account) {
        try {
          const response = await axios.get(`/api/user/avatar/${this.loginForm.account}`);
          if (response.data && response.data.avatar) {
            this.userAvatar = response.data.avatar === 'default.jpg' 
              ? '/uploads/avatars/default.jpg'
              : `/uploads/avatars/${response.data.avatar}`;
          }
        } catch (error) {
          this.userAvatar = null;
        }
      } else {
        this.userAvatar = null;
      }
    }
  },
  watch: {
    'loginForm.account': {
      handler: 'handleAccountInput',
      immediate: true
    }
  }
}
</script>

<style scoped>
/* 基础样式 */
:root {
  --primary-color: #409EFF;
  --secondary-color: #67C23A;
  --danger-color: #F56C6C;
  --text-color: #303133;
  --text-light: #909399;
  --bg-color: #f5f7fa;
  --card-bg: #ffffff;
}

/* 整体容器 */
.form-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-color);
  padding: 20px;
}

/* 登录包装器 */
.login-wrapper {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

/* 左侧区域 */
.form-left {
  flex: 1;
  background: linear-gradient(135deg, var(--primary-color), #66b1ff);
  color: white;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.left-content {
  max-width: 400px;
  margin: 0 auto;
}

.left-content h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
}

.left-content p {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 2rem;
}

.illustration-placeholder {
  width: 100%;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 2rem;
}

/* 右侧区域 */
.form-right {
  flex: 1;
  background-color: var(--card-bg);
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.logo-placeholder {
  text-align: center;
  margin-bottom: 30px;
}

.form-title {
  text-align: center;
  margin-bottom: 30px;
  color: var(--text-color);
  font-size: 1.5rem;
}

/* 表单元素样式 */
.el-form-item {
  margin-bottom: 22px;
}

.forgot-password {
  float: right;
  color: var(--text-light);
  text-decoration: none;
  font-size: 0.9rem;
}

.forgot-password:hover {
  color: var(--primary-color);
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: var(--text-light);
}

.register-link a {
  color: var(--primary-color);
  text-decoration: none;
}

/* 社交登录样式 */
.social-login {
  margin-top: 30px;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.divider-line {
  flex: 1;
  height: 1px;
  background-color: #ebeef5;
}

.divider-text {
  padding: 0 15px;
  color: var(--text-light);
  font-size: 0.9rem;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.social-icons .el-button {
  font-size: 1.2rem;
}

/* 响应式设计 */
@media (max-width: 800px) {
  .login-wrapper {
    flex-direction: column;
    min-height: auto;
  }
  
  .form-left {
    padding: 30px 20px;
    text-align: center;
  }
  
  .left-content {
    max-width: 100%;
  }
  
  .form-right {
    padding: 30px 20px;
  }
}

/* 动画效果 */
.form-box {
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.avatar-preview {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(255, 255, 255, 0.2);
}
</style>