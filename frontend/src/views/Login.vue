<template>
  <div class="card">
    <h2>用户登录</h2>

    <label>用户名</label>
    <input v-model="username" placeholder="例如：u1" />

    <label>密码</label>
    <input v-model="password" type="password" placeholder="例如：p1" />

    <button class="primary" @click="doLogin">登录</button>
    <button class="secondary" @click="$router.push('/register')">去注册</button>

    <div class="msg" :class="{ ok: loginOk }">{{ message }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { request } from '../api/client.js'

const router = useRouter()

const username = ref('')
const password = ref('')
const message = ref('')
const loginOk = ref(false)

async function doLogin() {
  message.value = ''
  loginOk.value = false

  if (!username.value || !password.value) {
    message.value = '请填写用户名和密码'
    return
  }

  try {
    const json = await request('/user/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username: username.value.trim(), password: password.value })
    })

    const data = json.data || {}
    localStorage.setItem('token', data.token || '')
    localStorage.setItem('userId', String(data.userId || ''))
    localStorage.setItem('nickname', data.nickname || '')
    loginOk.value = true
    message.value = '登录成功，正在跳转...'
    setTimeout(() => router.push('/'), 400)
  } catch (e) {
    message.value = e?.message || '登录失败'
  }
}
</script>

<style scoped>
h2 { margin-top: 0; font-size: 18px; }
.msg { margin-top: 12px; }
</style>

