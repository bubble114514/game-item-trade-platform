<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h2>用户登录</h2>
          <p>欢迎回到游戏道具交易平台</p>
        </div>
      </template>
      
      <el-form 
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-position="top"
        @keyup.enter="doLogin"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名" 
            :prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="w-100" 
            @click="doLogin"
            :loading="isLoading"
          >
            登录
          </el-button>
        </el-form-item>
        
        <div class="form-actions">
          <el-button link @click="$router.push('/register')">
            新用户注册
          </el-button>
        </div>
      </el-form>
      
      <div class="login-tips">
        <p>测试账号：</p>
        <p>• 管理员：admin / 任意密码</p>
        <p>• 商家：merchant001 / 任意密码</p>
        <p>• 用户：user001 / 任意密码</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { User, Lock } from '@element-plus/icons-vue'
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
  min-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
}

.login-header {
  text-align: center;
}

.login-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.login-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.w-100 {
  width: 100%;
}

.form-actions {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.login-tips {
  margin-top: 24px;
  padding: 16px;
  background: #f4f4f5;
  border-radius: 8px;
}

.login-tips p {
  margin: 4px 0;
  font-size: 13px;
  color: #606266;
}

.login-tips p:first-child {
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}
</style>
