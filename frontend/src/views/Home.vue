<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <el-carousel height="400px" class="hero-carousel">
      <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
        <div class="carousel-content" :style="{ background: item.bg }">
          <div class="carousel-text">
            <h2>{{ item.title }}</h2>
            <p>{{ item.desc }}</p>
            <el-button type="primary" size="large" @click="$router.push(item.link)">
              {{ item.btnText }}
            </el-button>
          </div>
          <div class="carousel-image">
            <el-icon :size="120" color="#fff"><component :is="item.icon" /></el-icon>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    
    <!-- 快捷分类 -->
    <div class="quick-categories">
      <el-row :gutter="20">
        <el-col :span="4" v-for="cat in quickCategories" :key="cat.name">
          <el-card shadow="hover" class="category-card" @click="$router.push(`/category/${cat.name}`)">
            <div class="category-icon">{{ cat.icon }}</div>
            <div class="category-name">{{ cat.name }}</div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 热销商品 -->
    <div class="section-container">
      <div class="section-header">
        <h2>热销商品</h2>
        <el-button link @click="$router.push('/listings')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
      </div>
      
      <div v-loading="isLoading" class="products-grid">
        <el-empty v-if="!isLoading && hotItems.length === 0" description="暂无热销商品" />
        <el-row :gutter="20" v-else>
          <el-col :xs="24" :sm="12" :md="6" v-for="item in hotItems" :key="item.id">
            <el-card shadow="hover" class="product-card" @click="$router.push(`/item/${item.itemId}`)">
              <div class="product-image">
                <el-icon :size="60" color="#409EFF"><Goods /></el-icon>
                <el-tag v-if="item.discount" type="danger" effect="dark" class="discount-badge">
                  -{{ item.discount }}%
                </el-tag>
              </div>
              <div class="product-info">
                <h3 class="product-name" :title="item.name">{{ item.name }}</h3>
                <p class="product-game">{{ item.game }}</p>
                <div class="product-price-row">
                  <span class="price">¥{{ item.price }}</span>
                  <span v-if="item.originalPrice" class="original-price">¥{{ item.originalPrice }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
    
    <!-- 新品推荐 -->
    <div class="section-container">
      <div class="section-header">
        <h2>新品推荐</h2>
        <el-button link @click="$router.push('/listings')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
      </div>
      
      <div v-loading="isLoading" class="products-grid">
        <el-empty v-if="!isLoading && newItems.length === 0" description="暂无新品推荐" />
        <el-row :gutter="20" v-else>
          <el-col :xs="24" :sm="12" :md="6" v-for="item in newItems" :key="item.id">
            <el-card shadow="hover" class="product-card" @click="$router.push(`/item/${item.itemId}`)">
              <div class="product-image">
                <el-icon :size="60" color="#67C23A"><Present /></el-icon>
                <el-tag type="success" effect="dark" class="new-badge">新品</el-tag>
              </div>
              <div class="product-info">
                <h3 class="product-name" :title="item.name">{{ item.name }}</h3>
                <p class="product-game">{{ item.game }}</p>
                <div class="product-price-row">
                  <span class="price">¥{{ item.price }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
    
    <!-- 热门游戏 -->
    <div class="section-container">
      <div class="section-header">
        <h2>热门游戏</h2>
        <el-button link @click="$router.push('/category')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
      </div>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="4" v-for="game in hotGames" :key="game.name">
          <div class="game-card" @click="$router.push(`/listings?game=${game.name}`)">
            <div class="game-icon">{{ game.icon }}</div>
            <h3 class="game-name">{{ game.name }}</h3>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { request } from '../api/client.js'
import { ArrowRight, Goods, Present, Trophy, Medal, Star } from '@element-plus/icons-vue'

const isLoading = ref(false)
const hotItems = ref([])
const newItems = ref([])

const carouselItems = [
  {
    title: '限时特惠',
    desc: '全场游戏道具低至5折',
    btnText: '立即抢购',
    link: '/listings',
    icon: 'Trophy',
    bg: 'linear-gradient(135deg, #409EFF 0%, #79bbff 100%)'
  },
  {
    title: '新品上架',
    desc: '最新游戏道具抢先体验',
    btnText: '查看详情',
    link: '/listings',
    icon: 'Present',
    bg: 'linear-gradient(135deg, #67C23A 0%, #95d475 100%)'
  },
  {
    title: '安全交易',
    desc: '多重保障，放心交易',
    btnText: '了解更多',
    link: '/category',
    icon: 'Medal',
    bg: 'linear-gradient(135deg, #E6A23C 0%, #eebe77 100%)'
  }
]

const quickCategories = [
  { name: '游戏币', icon: '💰' },
  { name: '装备', icon: '⚔️' },
  { name: '账号', icon: '👤' },
  { name: '宠物', icon: '🐾' },
  { name: '材料', icon: '📦' },
  { name: '其他', icon: '🎁' }
]

const hotGames = [
  { name: '热血传奇', icon: '⚔️' },
  { name: '英雄联盟', icon: '🎮' },
  { name: '王者荣耀', icon: '🎯' },
  { name: '魔兽世界', icon: '🛡️' },
  { name: 'CS:GO', icon: '🔫' },
  { name: 'DOTA 2', icon: '🏆' }
]

async function fetchItems() {
  isLoading.value = true
  try {
    const res = await request('/item/listings?size=8', { method: 'GET' })
    if (res.data && res.data.content) {
      // 获取所有的商品信息用于补充挂牌数据
      const itemsRes = await request('/item', { method: 'GET' })
      const itemMap = {}
      if (itemsRes.data) {
        itemsRes.data.forEach(gameItem => {
          itemMap[gameItem.id] = gameItem
        })
      }

      const items = res.data.content.map(item => {
        const gameItem = itemMap[item.itemId] || {}
        return {
          id: item.id,
          itemId: item.itemId,
          name: gameItem.name || '未知商品',
          game: gameItem.game || '未知游戏',
          price: item.price,
          originalPrice: gameItem.referencePrice,
          discount: gameItem.referencePrice ? Math.round((1 - item.price / gameItem.referencePrice) * 100) : 0
        }
      })
      
      hotItems.value = items.slice(0, 4)
      newItems.value = items.slice(4, 8)
    }
  } catch (e) {
    console.error('Failed to fetch items:', e)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchItems()
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 40px;
  margin-bottom: 60px;
}

.hero-carousel {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.carousel-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 80px;
  color: white;
}

.carousel-text h2 {
  font-size: 48px;
  margin: 0 0 16px 0;
  font-weight: bold;
}

.carousel-text p {
  font-size: 20px;
  margin: 0 0 32px 0;
  opacity: 0.9;
}

.quick-categories {
  margin-top: -20px;
}

.category-card {
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s;
}

.category-card:hover {
  transform: translateY(-5px);
}

.category-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.category-name {
  font-size: 14px;
  color: #606266;
}

.section-container {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 16px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.product-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  height: 160px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border-radius: 8px;
  margin-bottom: 16px;
}

.discount-badge, .new-badge {
  position: absolute;
  top: 8px;
  right: 8px;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-game {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #909399;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #F56C6C;
}

.original-price {
  font-size: 13px;
  color: #909399;
  text-decoration: line-through;
}

.game-card {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.game-card:hover {
  background: #ecf5ff;
  transform: translateY(-5px);
}

.game-icon {
  font-size: 40px;
  margin-bottom: 12px;
}

.game-name {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

@media (max-width: 768px) {
  .carousel-content {
    padding: 0 40px;
  }
  
  .carousel-text h2 {
    font-size: 32px;
  }
  
  .carousel-image {
    display: none;
  }
}
</style>
