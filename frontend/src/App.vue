<template>
  <div class="app">
    <Header />
    <CategoryNav />
    <main class="container">
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<script setup>
import Header from './components/Header.vue'
import CategoryNav from './components/CategoryNav.vue'
import Footer from './components/Footer.vue'
</script>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

/* 主内容区 */
.container {
  flex: 1;
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 24px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .container {
    padding: 0 16px;
  }
}

@media (max-width: 540px) {
  .container {
    margin: 16px auto;
  }
}
</style>
