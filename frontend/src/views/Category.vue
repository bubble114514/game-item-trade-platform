<template>
  <div class="category-page">
    <div class="page-header">
      <h2>商品分类</h2>
      <p class="subtitle">浏览不同类别的游戏道具</p>
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
            <div class="category-icon">
              <span>{{ category.icon }}</span>
            </div>
            <h3 class="category-name">{{ category.name }}</h3>
            <p class="category-desc">{{ category.description }}</p>
            <el-tag size="small" type="info" effect="plain" class="category-count">
              {{ category.count }} 个道具
            </el-tag>
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  color: #303133;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 16px;
}

.category-container {
  min-height: 300px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 24px;
}

.category-card {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
}

.category-card:hover {
  transform: translateY(-5px);
}

.category-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 10px 0;
}

.category-icon {
  font-size: 48px;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 50%;
  margin-bottom: 16px;
}

.category-name {
  margin: 0 0 8px 0;
  font-size: 18px;
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
  border-radius: 12px;
}
</style>
