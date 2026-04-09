<template>
  <header class="header">
    <!-- 顶部信息栏 -->
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
    
    <!-- 主导航栏 -->
    <div class="main-nav">
      <div class="main-nav-content">
        <!-- Logo -->
        <div class="logo">
          <router-link to="/" class="logo-link">
            <h1>道具商城</h1>
          </router-link>
        </div>
        
        <!-- 搜索框 -->
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
        
        <!-- 功能图标 -->
        <div class="nav-icons">
          <div class="icon-item">
            <router-link to="/orders" class="icon-link">
              <el-icon :size="20"><List /></el-icon>
              <span class="icon-text">订单</span>
            </router-link>
          </div>
          <div class="icon-item">
            <a href="#" class="icon-link">
              <el-icon :size="20"><Star /></el-icon>
              <span class="icon-text">收藏</span>
            </a>
          </div>
          <div class="icon-item">
            <a href="#" class="icon-link">
              <el-badge :value="0" class="cart-badge">
                <el-icon :size="20"><ShoppingCart /></el-icon>
              </el-badge>
              <span class="icon-text">购物车</span>
            </a>
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
import { Search, List, Star, ShoppingCart } from '@element-plus/icons-vue'
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
    // 假设有一个搜索结果页，或者在首页处理
    router.push({ path: '/', query: { q: searchQuery.value.trim() } })
  }
}
</script>

<style scoped>
.header {
  background: #fff;
}

/* 顶部信息栏 */
.top-bar {
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  padding: 8px 0;
}

.top-bar-content {
  max-width: 1200px;
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

.top-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome {
  font-size: 14px;
  color: #495057;
}

/* 主导航栏 */
.main-nav {
  background: white;
  border-bottom: 1px solid #e9ecef;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.main-nav-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 80px;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-link {
  text-decoration: none;
  display: flex;
  align-items: center;
  transition: transform 0.3s ease;
}

.logo-link:hover {
  transform: translateY(-2px);
}

.logo h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #409EFF;
  background: linear-gradient(135deg, #409EFF, #79bbff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 搜索框 */
.search-container {
  flex: 1;
  max-width: 600px;
  margin: 0 40px;
}

.search-input :deep(.el-input-group__append) {
  background-color: #409EFF;
  color: white;
  border-color: #409EFF;
}

.search-input :deep(.el-input-group__append:hover) {
  background-color: #79bbff;
  border-color: #79bbff;
}

/* 功能图标 */
.nav-icons {
  display: flex;
  gap: 24px;
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
  padding: 8px 12px;
  border-radius: 8px;
}

.icon-link:hover {
  color: #409EFF;
  background: #ecf5ff;
  transform: translateY(-2px);
}

.icon-text {
  font-size: 12px;
  font-weight: 500;
  margin-top: 4px;
}

.cart-badge {
  margin-bottom: 4px;
}

/* 响应式设计 */
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
    font-size: 24px;
  }
  
  .icon-text {
    font-size: 11px;
  }
}
</style>
