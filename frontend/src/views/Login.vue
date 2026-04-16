<template>
  <div class="login-page">
    <div class="login-wrapper">
      <div class="login-brand">
        <div class="brand-icon">
          <el-icon :size="48" color="#fff"><Shop /></el-icon>
        </div>
        <h1>道具商城</h1>
        <p>专业的游戏道具交易平台</p>
      </div>

      <el-card class="login-card" shadow="never">
        <div class="login-header">
          <h2>欢迎登录</h2>
          <p>登录后享受更多服务</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          label-position="top"
          @keyup.enter="doLogin"
          class="login-form"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              @click="doLogin"
              :loading="isLoading"
              size="large"
            >
              登 录
            </el-button>
          </el-form-item>

          <div class="form-actions">
            <span class="no-account">还没有账号？</span>
            <el-button type="primary" link @click="$router.push('/register')" class="register-link">
              立即注册
            </el-button>
          </div>
        </el-form>

        <div class="login-tips">
          <div class="tips-title">
            <el-icon><InfoFilled /></el-icon>
            <span>测试账号</span>
          </div>
          <div class="tips-grid">
            <div class="tip-item">
              <span class="tip-label">管理员</span>
              <span class="tip-value">admin</span>
            </div>
            <div class="tip-item">
              <span class="tip-label">商家</span>
              <span class="tip-value">merchant001</span>
            </div>
            <div class="tip-item">
              <span class="tip-label">用户</span>
              <span class="tip-value">user001</span>
            </div>
          </div>
          <p class="tips-note">任意密码即可登录</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { User, Lock, Shop, InfoFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const isLoading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const doLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      isLoading.value = true
      try {
        await userStore.login(loginForm.username.trim(), loginForm.password)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (e) {
        // 错误已经在 request 中通过 ElMessage 提示了
      } finally {
        isLoading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-page::before {
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

.login-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  z-index: 1;
}

.login-brand {
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

.login-brand h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 800;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.login-brand p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.98);
}

.login-header {
  text-align: center;
  padding: 32px 24px 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  border-bottom: 1px solid #ebeef5;
}

.login-header h2 {
  margin: 0 0 8px 0;
  font-size: 26px;
  font-weight: 700;
  color: #303133;
}

.login-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.login-form {
  padding: 24px;
}

.login-form :deep(.el-form-item__label) {
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
  border-color: #667eea;
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.15);
}

.login-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.form-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 8px;
  gap: 4px;
}

.no-account {
  color: #909399;
  font-size: 14px;
}

.register-link {
  font-weight: 600;
  font-size: 14px;
}

.login-tips {
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
  color: #409EFF;
  margin-bottom: 12px;
  font-size: 14px;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 8px;
}

.tip-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.tip-label {
  font-size: 12px;
  color: #909399;
}

.tip-value {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
}

.tips-note {
  margin: 0;
  font-size: 12px;
  color: #c0c4cc;
  text-align: center;
}

@media (max-width: 480px) {
  .login-card {
    border-radius: 16px;
  }

  .login-header {
    padding: 24px 16px 12px;
  }

  .login-form {
    padding: 16px;
  }

  .login-brand h1 {
    font-size: 26px;
  }
}
</style>
