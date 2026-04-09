<template>
  <div class="category-nav">
    <div class="category-nav-content">
      <el-menu
        :default-active="activeIndex"
        class="category-menu"
        mode="horizontal"
        :ellipsis="false"
        router
      >
        <el-menu-item index="/category">
          <span class="category-icon">🎮</span>
          <span class="category-name">全部游戏</span>
        </el-menu-item>
        <el-menu-item index="/listings?category=游戏币">
          <span class="category-icon">💰</span>
          <span class="category-name">游戏币</span>
        </el-menu-item>
        <el-menu-item index="/listings?category=装备">
          <span class="category-icon">⚔️</span>
          <span class="category-name">装备</span>
        </el-menu-item>
        <el-menu-item index="/listings?category=账号">
          <span class="category-icon">👤</span>
          <span class="category-name">账号</span>
        </el-menu-item>
        <el-menu-item index="/listings">
          <span class="category-icon">🏷️</span>
          <span class="category-name">挂牌列表</span>
        </el-menu-item>
      </el-menu>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeIndex = computed(() => {
  if (route.path === '/listings' && route.query.category) {
    return `/listings?category=${route.query.category}`
  }
  return route.path
})
</script>

<style scoped>
.category-nav {
  background: white;
  border-bottom: 1px solid #e9ecef;
}

.category-nav-content {
  max-width: 1200px;
  margin: 0 auto;
}

.category-menu {
  border-bottom: none;
  display: flex;
  justify-content: center;
}

.el-menu-item {
  font-size: 15px;
  font-weight: 500;
}

.category-icon {
  margin-right: 6px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .category-menu {
    overflow-x: auto;
    justify-content: flex-start;
  }
  
  .el-menu-item {
    padding: 0 12px;
  }
}
</style>
