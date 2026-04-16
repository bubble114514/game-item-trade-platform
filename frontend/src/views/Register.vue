<template>
  <div class="register-page">
    <div class="register-wrapper">
      <div class="register-brand">
        <div class="brand-icon">
          <el-icon :size="48" color="#fff"><Shop /></el-icon>
        </div>
        <h1>道具商城</h1>
        <p>成为我们的一员，开始交易</p>
      </div>

      <el-card class="register-card" shadow="never">
        <div class="register-header">
          <h2>创建账号</h2>
          <p>填写信息完成注册</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="rules"
          label-position="top"
          @keyup.enter="doRegister"
          class="register-form"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名 (3-20个字符)"
              :prefix-icon="User"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码 (至少6个字符)"
              :prefix-icon="Lock"
              show-password
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              :prefix-icon="Lock"
              show-password
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item label="昵称（可选）" prop="nickname">
            <el-input
              v-model="registerForm.nickname"
              placeholder="请输入昵称"
              :prefix-icon="UserFilled"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="register-btn"
              @click="doRegister"
              :loading="isLoading"
              size="large"
            >
              注 册
            </el-button>
          </el-form-item>

          <div class="form-actions">
            <span class="has-account">已有账号？</span>
            <el-button type="primary" link @click="$router.push('/login')" class="login-link">
              立即登录
            </el-button>
          </div>
        </el-form>

        <div class="register-tips">
          <div class="tips-title">
            <el-icon><InfoFilled /></el-icon>
            <span>注册须知</span>
          </div>
          <ul class="tips-list">
            <li>用户名长度为 3-20 个字符</li>
            <li>密码长度至少 6 个字符</li>
            <li>昵称可在注册后修改</li>
          </ul>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { request } from '../api/client.js'
import { User, Lock, UserFilled, Shop, InfoFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const registerFormRef = ref(null)
const isLoading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}

const doRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      isLoading.value = true
      try {
        await request('/user/auth/register', {
          method: 'POST',
          body: JSON.stringify({
            username: registerForm.username.trim(),
            password: registerForm.password,
            nickname: registerForm.nickname.trim() || null
          })
        })
        
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (e) {
        // 错误已经在 request 中处理
      } finally {
        isLoading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-page {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  position: relative;
  overflow: hidden;
}

.register-page::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: pulse 15s infinite linear;
}

@keyframes pulse {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.register-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  z-index: 1;
}

.register-brand {
  text-align: center;
  color: white;
}

.brand-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.register-brand h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 800;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.register-brand p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.register-card {
  width: 100%;
  max-width: 440px;
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.98);
}

.register-header {
  text-align: center;
  padding: 32px 24px 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  border-bottom: 1px solid #ebeef5;
}

.register-header h2 {
  margin: 0 0 8px 0;
  font-size: 26px;
  font-weight: 700;
  color: #303133;
}

.register-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.register-form {
  padding: 24px;
}

.register-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #e8e8f0;
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover),
.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #11998e;
  box-shadow: 0 2px 12px rgba(17, 153, 142, 0.15);
}

.register-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(17, 153, 142, 0.3);
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(17, 153, 142, 0.4);
}

.form-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 8px;
  gap: 4px;
}

.has-account {
  color: #909399;
  font-size: 14px;
}

.login-link {
  font-weight: 600;
  font-size: 14px;
}

.register-tips {
  margin: 0 24px 24px;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf0 100%);
  border-radius: 12px;
  border: 1px solid #e8e8f0;
}

.tips-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #11998e;
  margin-bottom: 12px;
  font-size: 14px;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
}

.tips-list li {
  font-size: 13px;
  color: #606266;
  margin: 6px 0;
  line-height: 1.5;
}

@media (max-width: 480px) {
  .register-card {
    border-radius: 16px;
  }

  .register-header {
    padding: 24px 16px 12px;
  }

  .register-form {
    padding: 16px;
  }

  .register-brand h1 {
    font-size: 26px;
  }
}
</style>
