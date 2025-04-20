<template>
    <div class="form-container">
      <div class="form-box">
        <h2 class="form-title">重置密码</h2>
        <el-form :model="resetForm" :rules="rules" ref="resetForm">
          <el-form-item prop="email">
            <el-input v-model="resetForm.email" placeholder="电子邮箱"></el-input>
          </el-form-item>
          <el-form-item prop="newPassword">
            <el-input v-model="resetForm.newPassword" type="password" placeholder="新密码"></el-input>
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="resetForm.confirmPassword" type="password" placeholder="确认密码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width: 100%" @click="submitForm">重置密码</el-button>
          </el-form-item>
          <div class="form-links">
            <router-link to="/">返回登录</router-link>
          </div>
        </el-form>
      </div>
      <div class="form-image">
        <img src="https://via.placeholder.com/400x300?text=重置密码页面" alt="重置密码" />
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'ForgotPasswordPage',
    data() {
      const validateEmail = (rule, value, callback) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!value) {
          callback(new Error('请输入电子邮箱'))
        } else if (!emailRegex.test(value)) {
          callback(new Error('请输入有效的电子邮箱'))
        } else {
          callback()
        }
      }
      
      const validatePass = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请输入新密码'))
        } else {
          if (this.resetForm.confirmPassword !== '') {
            this.$refs.resetForm.validateField('confirmPassword')
          }
          callback()
        }
      }
      
      const validatePass2 = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请确认新密码'))
        } else if (value !== this.resetForm.newPassword) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      }
  
      return {
        resetForm: {
          email: '',
          newPassword: '',
          confirmPassword: ''
        },
        rules: {
          email: [
            { validator: validateEmail, trigger: 'blur' }
          ],
          newPassword: [
            { validator: validatePass, trigger: 'blur' }
          ],
          confirmPassword: [
            { validator: validatePass2, trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      submitForm() {
        this.$refs.resetForm.validate((valid) => {
          if (valid) {
            // 模拟密码重置成功，跳转到登录页
            this.$router.push('/')
          } else {
            return false
          }
        })
      }
    }
  }
  </script>
  
  <style scoped>
  .form-title {
    text-align: center;
    margin-bottom: 30px;
    color: var(--primary-color);
  }
  
  .form-links {
    display: flex;
    justify-content: center;
    margin-top: 15px;
  }
  
  .form-links a {
    color: var(--primary-color);
    text-decoration: none;
  }
  </style>