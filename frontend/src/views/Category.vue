<template>
  <div class="category-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="40" color="#fff"><Grid /></el-icon>
        </div>
        <div class="header-text">
          <h2>商品分类</h2>
          <p>浏览不同类别的游戏道具</p>
        </div>
      </div>
    </div>

    <div v-loading="isLoading" class="category-container">
      <el-empty v-if="!isLoading && categories.length === 0" description="暂无分类数据" />

      <div v-else class="category-grid">
        <el-card
          v-for="category in categories"
          :key="category.name"
          class="category-card"
          shadow="hover"
          @click="navigateToCategory(category.name)"
        >
          <div class="category-content">
            <div class="category-icon-bg">
              <span class="category-icon">{{ category.icon }}</span>
            </div>
            <h3 class="category-name">{{ category.name }}</h3>
            <p class="category-desc">{{ category.description }}</p>
            <div class="category-count">
              <span class="count-num">{{ category.count }}</span>
              <span class="count-text">个道具</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { request } from '../api/client.js'
import { Grid } from '@element-plus/icons-vue'

const router = useRouter()

const categories = ref([])
const isLoading = ref(false)

// 分类图标映射
const categoryIcons = {
  '武器': '⚔️',
  '防具': '🛡️',
  '消耗品': '🧪',
  '材料': '📦',
  '宠物': '🐶',
  '坐骑': '🐎',
  '其他': '📦'
}

// 分类描述映射
const categoryDescriptions = {
  '武器': '各种游戏中的武器装备',
  '防具': '提供防御属性的装备',
  '消耗品': '各类药水、卷轴等消耗品',
  '材料': '用于合成或升级的材料',
  '宠物': '游戏中的宠物伙伴',
  '坐骑': '各种坐骑和交通工具',
  '其他': '其他游戏道具'
}

async function fetchCategories() {
  isLoading.value = true
  
  try {
    // 获取所有商品
    const response = await request('/item', {
      method: 'GET'
    })
    
    if (response.data) {
      // 按分类分组
      const categoryMap = {}
      response.data.forEach(item => {
        const category = item.category || '其他'
        if (!categoryMap[category]) {
          categoryMap[category] = 0
        }
        categoryMap[category]++
      })
      
      // 转换为分类列表
      categories.value = Object.entries(categoryMap).map(([name, count]) => ({
        name,
        count,
        icon: categoryIcons[name] || '📦',
        description: categoryDescriptions[name] || '其他游戏道具'
      }))
    }
  } catch (e) {
    ElMessage.error(e?.message || '加载分类失败')
    console.error('Failed to fetch categories:', e)
  } finally {
    isLoading.value = false
  }
}

function navigateToCategory(categoryName) {
  router.push(`/listings?category=${encodeURIComponent(categoryName)}`)
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.category-page {
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 32px;
  padding: 24px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.header-text h2 {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: 700;
  color: white;
}

.header-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.category-container {
  min-height: 400px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
}

.category-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px;
  border: none;
  overflow: hidden;
}

.category-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.2);
}

.category-card :deep(.el-card__body) {
  padding: 32px 20px;
}

.category-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.category-icon-bg {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-radius: 50%;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.category-card:hover .category-icon-bg {
  background: linear-gradient(135deg, #667eea30 0%, #764ba230 100%);
  transform: scale(1.1);
}

.category-icon {
  font-size: 40px;
}

.category-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.category-desc {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #909399;
  line-height: 1.5;
  height: 42px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.category-count {
  display: flex;
  align-items: baseline;
  gap: 4px;
  padding: 6px 16px;
  background: linear-gradient(135deg, #667eea10 0%, #764ba210 100%);
  border-radius: 20px;
  transition: all 0.3s ease;
}

.category-card:hover .category-count {
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
}

.count-num {
  font-size: 18px;
  font-weight: 700;
  color: #667eea;
}

.count-text {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }

  .header-icon {
    width: 48px;
    height: 48px;
  }

  .header-text h2 {
    font-size: 20px;
  }

  .category-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }

  .category-card :deep(.el-card__body) {
    padding: 20px 16px;
  }

  .category-icon-bg {
    width: 64px;
    height: 64px;
  }

  .category-icon {
    font-size: 32px;
  }
}
</style>
