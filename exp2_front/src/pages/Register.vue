<template>
  <div class="register-container">
    <!-- 返回按钮 -->
    <div class="back-button">
      <el-button type="text" @click="goToLogin" class="back-btn">
        <i class="el-icon-arrow-left"></i> 返回登录
      </el-button>
    </div>

    <!-- 注册表单区域 -->
    <div class="register-wrapper">
      <div class="register-card">
        <transition name="fade" mode="out-in">
          <div class="card-content">
            <h1 class="register-title">创建账户</h1>
            <p class="register-subtitle">开始您的个性化体验</p>

            <el-form
              :model="registerForm"
              :rules="rules"
              ref="registerForm"
              class="register-form"
            >
              <!-- 头像上传 -->
              <el-form-item prop="avatar">
                <div class="avatar-upload">
                  <el-upload
                    class="avatar-uploader"
                    action="#"
                    :show-file-list="false"
                    :auto-upload="false"
                    :on-change="handleAvatarChange"
                  >
                    <img
                      v-if="registerForm.avatar"
                      :src="registerForm.avatar"
                      class="avatar"
                    />
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                  </el-upload>
                  <span class="avatar-hint">点击上传头像</span>
                </div>
              </el-form-item>

              <!-- 用户名 -->
              <el-form-item prop="username">
                <el-input
                  v-model="registerForm.username"
                  placeholder="用户名"
                  prefix-icon="el-icon-user"
                  size="large"
                  clearable
                />
              </el-form-item>

              <!-- 邮箱 -->
              <el-form-item prop="email">
                <el-input
                  v-model="registerForm.email"
                  placeholder="电子邮箱"
                  prefix-icon="el-icon-message"
                  size="large"
                  clearable
                />
              </el-form-item>

              <!-- 出生日期 -->
              <el-form-item prop="birthdate">
                <el-date-picker
                  v-model="registerForm.birthdate"
                  type="date"
                  placeholder="出生日期"
                  style="width: 100%"
                  size="large"
                />
              </el-form-item>

              <!-- 省市区选择 -->
              <el-form-item prop="region">
                <el-cascader
                  :options="provinceOptions"
                  v-model="registerForm.region"
                  placeholder="请选择省市区"
                  clearable
                  style="width: 100%"
                  @change="handleAreaChange"
                />
              </el-form-item>

              <!-- 详细地址 -->
              <el-form-item prop="address">
                <el-input
                  v-model="registerForm.address"
                  placeholder="详细地址"
                  prefix-icon="el-icon-location"
                  size="large"
                  clearable
                />
              </el-form-item>

              <!-- 邮编 -->
              <el-form-item prop="postcode">
                <el-input
                  v-model="registerForm.postcode"
                  placeholder="邮政编码"
                  prefix-icon="el-icon-postcard"
                  size="large"
                  clearable
                />
              </el-form-item>

              <!-- 密码 -->
              <el-form-item prop="password">
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="密码"
                  prefix-icon="el-icon-lock"
                  size="large"
                  show-password
                />
              </el-form-item>

              <!-- 确认密码 -->
              <el-form-item prop="confirmPassword">
                <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="确认密码"
                  prefix-icon="el-icon-lock"
                  size="large"
                  show-password
                />
              </el-form-item>

              <!-- 注册按钮 -->
              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  @click="submitForm"
                  :loading="loading"
                  class="register-btn"
                >
                  立即注册
                </el-button>
              </el-form-item>
            </el-form>

            <div class="terms">
              注册即表示您同意我们的
              <a href="#">服务条款</a>
              和
              <a href="#">隐私政策</a>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { regionData, codeToText } from 'element-china-area-data'

export default {
  name: 'RegisterPage',

  created() {
    // 确保所有请求都携带凭证
    axios.defaults.withCredentials = true
  },

  data() {
    const validatePass = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        if (this.registerForm.confirmPassword) {
          this.$refs.registerForm.validateField('confirmPassword')
        }
        callback()
      }
    }

    const validatePass2 = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      provinceOptions: regionData,
      registerForm: {
        username: '',
        email: '',
        birthdate: '',
        region: [],
        address: '',
        postcode: '',
        password: '',
        confirmPassword: '',
        avatar: '',
        provinceName: '',
        cityName: '',
        districtName: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 1, max: 50, message: '用户名长度不能超过50个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { validator: validatePass, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validatePass2, trigger: 'blur' }
        ],
        email: [
          { required: false },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    }
  },

  methods: {
    handleAreaChange(selectedValues) {
      if (selectedValues && selectedValues.length > 0) {
        this.registerForm.provinceName = selectedValues[0] ? codeToText[selectedValues[0]] : ''
        this.registerForm.cityName = selectedValues[1] ? codeToText[selectedValues[1]] : ''
        this.registerForm.districtName = selectedValues[2] ? codeToText[selectedValues[2]] : ''
      } else {
        this.registerForm.provinceName = ''
        this.registerForm.cityName = ''
        this.registerForm.districtName = ''
      }
    },

    compressImage(file) {
      return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onload = (e) => {
          const img = new Image();
          img.onload = () => {
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');

            let width = img.width;
            let height = img.height;
            const maxWidth = 200;

            if (width > maxWidth) {
              height = (height * maxWidth) / width;
              width = maxWidth;
            }

            canvas.width = width;
            canvas.height = height;

            ctx.drawImage(img, 0, 0, width, height);

            const base64 = canvas.toDataURL('image/jpeg', 0.8);
            
            const base64Size = Math.round((base64.length * 3) / 4);
            if (base64Size > 1024 * 1024) {
              this.$message.error('图片大小不能超过1MB，请选择更小的图片');
              resolve(null);
              return;
            }

            resolve(base64);
          };
          img.src = e.target.result;
        };
        reader.readAsDataURL(file);
      });
    },

    async handleAvatarChange(file) {
      const isImage = file.raw.type.startsWith('image/');
      const isLt2M = file.raw.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error('只能上传图片文件!');
        return;
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!');
        return;
      }

      try {
        const compressedImage = await this.compressImage(file.raw);
        if (compressedImage) {
          this.registerForm.avatar = compressedImage;
        }
      } catch (error) {
        this.$message.error('图片处理失败，请重试');
        console.error('图片处理错误:', error);
      }
    },

    submitForm() {
      this.$refs.registerForm.validate(valid => {
        if (!valid) return
        this.loading = true

        const payload = {
          username: this.registerForm.username,
          password: this.registerForm.password,
          email: this.registerForm.email || null,
          birthdate: this.registerForm.birthdate ? new Date(this.registerForm.birthdate).toISOString().split('T')[0] : null,
          province: this.registerForm.provinceName || null,
          city: this.registerForm.cityName || null,
          district: this.registerForm.districtName || null,
          address: this.registerForm.address || null,
          postcode: this.registerForm.postcode || null,
          avatar: this.registerForm.avatar || null
        }

        axios
          .post('/api/user/register', payload)
          .then(() => {
            this.loading = false
            this.$message.success('注册成功！')
            this.$router.push('/')
          })
          .catch(err => {
            this.loading = false
            let errorMessage = '注册失败，请重试'
            
            if (err.response) {
              switch (err.response.status) {
                case 400:
                  errorMessage = err.response.data || '请求参数错误'
                  break
                case 403:
                  errorMessage = '注册被拒绝，请检查您的输入'
                  break
                case 409:
                  errorMessage = '用户名已被注册'
                  break
                default:
                  errorMessage = err.response.data || '服务器错误，请稍后重试'
              }
            } else {
              errorMessage = '网络错误，请检查您的网络连接'
            }
            
            this.$message.error(errorMessage)
          })
      })
    },

    goToLogin() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
:root {
  --primary-color: #0066ff;
  --bg-color: #f5f5f7;
  --card-bg: #ffffff;
  --text-color: #1d1d1f;
  --text-light: #86868b;
  --border-color: #d2d2d7;
}

.register-container {
  min-height: 100vh;
  background-color: var(--bg-color);
  display: flex;
  flex-direction: column;
  padding: 40px;
}

.back-button {
  margin-bottom: 20px;
}

.back-btn {
  color: var(--primary-color);
  font-size: 16px;
  padding: 10px 0;
}

.back-btn i {
  margin-right: 5px;
}

.register-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
}

.register-card {
  background-color: var(--card-bg);
  border-radius: 18px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 500px;
  padding: 40px;
  transition: all 0.3s ease;
}

.register-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-color);
  text-align: center;
  margin-bottom: 10px;
}

.register-subtitle {
  font-size: 16px;
  color: var(--text-light);
  text-align: center;
  margin-bottom: 30px;
}

.register-form {
  margin-top: 20px;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.avatar-uploader {
  border: 1px dashed var(--border-color);
  border-radius: 50%;
  width: 100px;
  height: 100px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader:hover {
  border-color: var(--primary-color);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: var(--text-light);
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-hint {
  font-size: 14px;
  color: var(--text-light);
  margin-top: 10px;
}

.el-form-item {
  margin-bottom: 24px;
}

.register-btn {
  width: 100%;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  height: 48px;
  margin-top: 10px;
}

.terms {
  font-size: 12px;
  color: var(--text-light);
  text-align: center;
  margin-top: 20px;
}

.terms a {
  color: var(--primary-color);
  text-decoration: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 768px) {
  .register-container {
    padding: 20px;
  }
  .register-card {
    padding: 30px 20px;
    border-radius: 12px;
  }
  .register-title {
    font-size: 24px;
  }
  .register-subtitle {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .register-card {
    box-shadow: none;
    background-color: transparent;
    padding: 0;
  }
  .back-button {
    margin-bottom: 10px;
  }
}
</style>