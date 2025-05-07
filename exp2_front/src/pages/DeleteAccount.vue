<template>
  <div class="delete-account-container">
    <div class="card">
      <h2 class="title">注销账户</h2>
      <div class="warning-box">
        <el-alert
          title="警告：此操作不可逆"
          type="error"
          description="注销账户后，您的所有数据将被永久删除，且无法恢复。"
          show-icon
          :closable="false"
        />
      </div>
      <el-form 
        :model="deleteForm" 
        :rules="rules" 
        ref="deleteForm" 
        label-width="100px"
        class="delete-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="deleteForm.username" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="deleteForm.password" 
            type="password" 
            placeholder="请输入密码以确认"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="confirm">
          <el-checkbox v-model="deleteForm.confirm">我确认要永久注销我的账户</el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button type="danger" @click="confirmDelete" :loading="loading">确认注销</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'DeleteAccount',
  data() {
    return {
      loading: false,
      deleteForm: {
        username: localStorage.getItem('username') || '',
        password: '',
        confirm: false
      },
      rules: {
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        confirm: [
          { 
            type: 'boolean', 
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error('请确认注销账户'))
              } else {
                callback()
              }
            },
            trigger: 'change' 
          }
        ]
      }
    }
  },
  created() {
    // 验证用户是否已登录
    if (!this.deleteForm.username) {
      this.$router.push('/login')
      ElMessage.warning('请先登录')
    }
  },
  methods: {
    confirmDelete() {
      this.$refs.deleteForm.validate(valid => {
        if (!valid) return
        
        // 二次确认
        ElMessageBox.confirm(
          '您确定要永久注销账户吗？此操作不可恢复！',
          '确认注销账户',
          {
            confirmButtonText: '确认注销',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          this.submitDelete()
        }).catch(() => {
          // 取消操作
        })
      })
    },
    
    async submitDelete() {
      this.loading = true
      try {
        await axios.post('/api/user/delete-account', {
          username: this.deleteForm.username,
          password: this.deleteForm.password
        })
        
        ElMessage.success('账户已成功注销')
        
        // 清除登录状态
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        localStorage.removeItem('user')
        
        // 返回登录页
        this.$router.push('/login')
      } catch (error) {
        let errorMessage = '注销账户失败，请重试'
        
        if (error.response) {
          switch (error.response.status) {
            case 400:
              errorMessage = error.response.data || '请求参数错误'
              break
            case 401:
              errorMessage = '密码不正确'
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
    },
    
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.delete-account-container {
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
  margin-bottom: 20px;
  color: #f56c6c;
}

.delete-form {
  margin-top: 20px;
}

.warning-box {
  margin-bottom: 30px;
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