<template>
  <div class="app">
    <header class="header">
      <div class="left">道具交易平台</div>
      <div class="right">
        <span>{{ helloText }}</span>
        <button class="btn" @click="logout">退出登录</button>
      </div>
    </header>

    <main class="container">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const helloText = computed(() => {
  const nickname = localStorage.getItem('nickname')
  return nickname ? `欢迎，${nickname}` : '已登录'
})

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('nickname')
  router.push('/login')
}
</script>

<style scoped>
.app { min-height: 100vh; background: #f6f7fb; font-family: Arial, 'Microsoft YaHei', sans-serif; }
.header { background: #0f172a; color: #fff; padding: 14px 18px; display: flex; justify-content: space-between; align-items: center; gap: 14px; }
.left { font-weight: 700; }
.right { font-size: 14px; opacity: .95; display: flex; gap: 10px; align-items: center; flex-wrap: wrap; }
.btn { border: 0; background: #334155; color: #fff; padding: 8px 12px; border-radius: 8px; cursor: pointer; }
.container { max-width: 1060px; margin: 18px auto; padding: 0 12px 42px; }
</style>

