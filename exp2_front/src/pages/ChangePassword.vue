<template>
  <div class="change-password-container">
    <div class="card">
      <h2 class="title">修改密码</h2>
      <el-form 
        :model="passwordForm" 
        :rules="rules" 
        ref="passwordForm" 
        label-width="100px"
        class="password-form"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            placeholder="请输入当前密码"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">确认修改</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'ChangePassword',
  data() {
    // 密码确认校验
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致!'))
      } else {
        callback()
      }
    }
    
    // 新密码校验
    const validateNewPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入新密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else if (value === this.passwordForm.oldPassword) {
        callback(new Error('新密码不能与旧密码相同'))
      } else {
        // 如果确认密码已输入，则再校验一次
        if (this.passwordForm.confirmPassword !== '') {
          this.$refs.passwordForm.validateField('confirmPassword')
        }
        callback()
      }
    }
    
    return {
      loading: false,
      passwordForm: {
        username: localStorage.getItem('username') || '',
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
      },
      rules: {
        oldPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { validator: validateNewPassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    submitForm() {
      // 首先确保用户已登录
      if (!this.passwordForm.username) {
        ElMessage.error('您尚未登录，请先登录')
        this.$router.push('/login')
        return
      }
      
      // 表单验证
      this.$refs.passwordForm.validate(async valid => {
        if (!valid) return
        
        this.loading = true
        try {
          await axios.post('/api/user/change-password', {
            username: this.passwordForm.username,
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          })
          
          ElMessage.success('密码修改成功，请重新登录')
          
          // 清除登录状态，返回登录页
          localStorage.removeItem('token')
          localStorage.removeItem('username')
          localStorage.removeItem('user')
          
          this.$router.push('/login')
        } catch (error) {
          let errorMessage = '密码修改失败，请重试'
          
          if (error.response) {
            switch (error.response.status) {
              case 400:
                errorMessage = error.response.data || '请求参数错误'
                break
              case 401:
                errorMessage = '旧密码不正确'
                break
              default:
                errorMessage = error.response.data || '服务器错误，请稍后重试'
            }
          } else {
            errorMessage = '网络错误，请检查您的网络连接'
          }
          
          ElMessage.error(errorMessage)
        } finally {
          this.loading = false
        }
      })
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.change-password-container {
  min-height: 100vh;
  background-color: var(--bg-color);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.card {
  width: 100%;
  max-width: 500px;
  background-color: var(--card-bg);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: var(--primary-color);
}

.password-form {
  margin-top: 20px;
}

/* 适配移动设备 */
@media (max-width: 550px) {
  .card {
    padding: 20px 15px;
  }
  
  .el-form-item__label {
    float: none;
    text-align: left;
    padding: 0 0 8px;
    line-height: 1;
  }
  
  .el-form-item {
    margin-bottom: 22px;
  }
}
</style>