<template>
  <div class="register-page">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <h2>用户注册</h2>
          <p>创建新账号，开始游戏道具交易</p>
        </div>
      </template>
      
      <el-form 
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        label-position="top"
        @keyup.enter="doRegister"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="registerForm.username" 
            placeholder="请输入用户名" 
            :prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码" 
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="昵称（可选）" prop="nickname">
          <el-input 
            v-model="registerForm.nickname" 
            placeholder="请输入昵称" 
            :prefix-icon="UserFilled"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="w-100" 
            @click="doRegister"
            :loading="isLoading"
          >
            注册
          </el-button>
        </el-form-item>
        
        <div class="form-actions">
          <el-button link @click="$router.push('/login')">
            返回登录
          </el-button>
        </div>
      </el-form>
      
      <div class="register-tips">
        <p>注册须知：</p>
        <p>• 用户名长度为3-20个字符</p>
        <p>• 密码长度至少6个字符</p>
        <p>• 昵称可在注册后修改</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { request } from '../api/client.js'
import { User, Lock, UserFilled } from '@element-plus/icons-vue'
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
  min-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.register-card {
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
}

.register-header {
  text-align: center;
}

.register-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.register-header p {
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

.register-tips {
  margin-top: 24px;
  padding: 16px;
  background: #f4f4f5;
  border-radius: 8px;
}

.register-tips p {
  margin: 4px 0;
  font-size: 13px;
  color: #606266;
}

.register-tips p:first-child {
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}
</style>
