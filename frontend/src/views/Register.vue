<template>
  <div class="card">
    <h2>用户注册</h2>

    <label>用户名</label>
    <input v-model="username" placeholder="例如：u1" />

    <label>密码</label>
    <input v-model="password" type="password" placeholder="例如：p1" />

    <label>昵称（可选）</label>
    <input v-model="nickname" placeholder="例如：玩家1" />

    <button class="primary" @click="doRegister">注册</button>
    <button class="secondary" @click="$router.push('/login')">返回登录</button>

    <div class="msg" :class="{ ok: ok }">{{ message }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { request } from '../api/client.js'

const router = useRouter()

const username = ref('')
const password = ref('')
const nickname = ref('')
const message = ref('')
const ok = ref(false)

async function doRegister() {
  message.value = ''
  ok.value = false

  if (!username.value || !password.value) {
    message.value = '请填写用户名和密码'
    return
  }

  try {
    await request('/user/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        username: username.value.trim(),
        password: password.value,
        nickname: nickname.value.trim() || null
      })
    })

    ok.value = true
    message.value = '注册成功，去登录吧！'
    setTimeout(() => router.push('/login'), 800)
  } catch (e) {
    message.value = e?.message || '注册失败'
  }
}
</script>

<style scoped>
h2 { margin-top: 0; font-size: 18px; }
.msg { margin-top: 12px; }
</style>

