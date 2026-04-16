<template>
  <header class="header">
    <div class="top-bar">
      <div class="top-bar-content">
        <div class="top-links">
          <el-link :underline="false">关于我们</el-link>
          <el-link :underline="false">帮助中心</el-link>
          <el-link :underline="false">联系客服</el-link>
        </div>
        <div class="top-user">
          <template v-if="userStore.token">
            <span class="welcome">欢迎，{{ userStore.nickname || userStore.userId }}</span>
            <el-button type="danger" link @click="handleLogout">退出</el-button>
          </template>
          <template v-else>
            <el-button type="primary" link @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')" size="small">注册</el-button>
          </template>
        </div>
      </div>
    </div>

    <div class="main-nav">
      <div class="main-nav-content">
        <div class="logo">
          <router-link to="/" class="logo-link">
            <div class="logo-icon">
              <el-icon :size="32" color="#fff"><Shop /></el-icon>
            </div>
            <h1>道具商城</h1>
          </router-link>
        </div>

        <div class="search-container">
          <el-input
            v-model="searchQuery"
            placeholder="搜索游戏道具..."
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>

        <div class="nav-icons">
          <div class="icon-item">
            <router-link to="/sell" class="icon-link">
              <div class="icon-circle">
                <el-icon :size="22"><Upload /></el-icon>
              </div>
              <span class="icon-text">上架</span>
            </router-link>
          </div>
          <div class="icon-item">
            <router-link to="/orders" class="icon-link">
              <div class="icon-circle">
                <el-icon :size="22"><List /></el-icon>
              </div>
              <span class="icon-text">订单</span>
            </router-link>
          </div>
          <div class="icon-item">
            <router-link to="/user" class="icon-link">
              <div class="icon-circle">
                <el-icon :size="22"><User /></el-icon>
              </div>
              <span class="icon-text">我的</span>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { Search, List, Star, ShoppingCart, Upload, Shop, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const searchQuery = ref('')

const handleLogout = async () => {
  await userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ path: '/search', query: { q: searchQuery.value.trim() } })
  }
}
</script>

<style scoped>
.header {
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.top-bar {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 8px 0;
}

.top-bar-content {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.top-links {
  display: flex;
  gap: 20px;
}

.top-links :deep(.el-link__inner) {
  color: rgba(255,255,255,0.7);
  font-size: 13px;
}

.top-links :deep(.el-link__inner:hover) {
  color: #fff;
}

.top-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome {
  font-size: 13px;
  color: rgba(255,255,255,0.8);
}

.main-nav {
  background: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.main-nav-content {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 72px;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-link {
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: transform 0.3s ease;
}

.logo-link:hover {
  transform: translateY(-2px);
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.logo h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
}

.search-container {
  flex: 1;
  max-width: 560px;
  margin: 0 40px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  padding: 4px 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e8e8f0;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.15);
}

.search-input :deep(.el-input-group__append) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 0 24px 24px 0;
  padding: 0 20px;
}

.search-input :deep(.el-input-group__append:hover) {
  background: linear-gradient(135deg, #5a71d2 0%, #6a4190 100%);
}

.nav-icons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.icon-item {
  position: relative;
}

.icon-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #606266;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 12px;
}

.icon-link:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.08);
}

.icon-circle {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.icon-link:hover .icon-circle {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: scale(1.1);
}

.icon-text {
  font-size: 12px;
  font-weight: 500;
  margin-top: 4px;
}

@media (max-width: 1200px) {
  .main-nav-content {
    padding: 0 16px;
  }

  .search-container {
    margin: 0 20px;
  }
}

@media (max-width: 768px) {
  .top-bar-content {
    padding: 0 16px;
    flex-direction: column;
    gap: 8px;
  }

  .top-links {
    gap: 16px;
  }

  .main-nav-content {
    height: auto;
    padding: 16px;
    flex-direction: column;
    gap: 16px;
  }

  .search-container {
    width: 100%;
    max-width: none;
    margin: 0;
  }

  .nav-icons {
    width: 100%;
    justify-content: space-around;
  }
}

@media (max-width: 540px) {
  .logo h1 {
    font-size: 20px;
  }

  .icon-text {
    font-size: 11px;
  }

  .icon-link {
    padding: 6px 12px;
  }
}
</style>