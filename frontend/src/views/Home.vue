<template>
  <div class="home-page">
    <el-carousel height="420px" class="hero-carousel" :interval="5000" trigger="click">
      <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
        <div class="carousel-content" :style="{ background: item.bg }">
          <div class="carousel-text">
            <h2>{{ item.title }}</h2>
            <p>{{ item.desc }}</p>
            <el-button type="primary" size="large" @click="$router.push(item.link)" class="carousel-btn">
              {{ item.btnText }}
            </el-button>
          </div>
          <div class="carousel-image">
            <el-icon :size="140" color="rgba(255,255,255,0.9)"><component :is="item.icon" /></el-icon>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="quick-categories">
      <el-row :gutter="16">
        <el-col :xs="12" :sm="8" :md="4" v-for="cat in quickCategories" :key="cat.name">
          <div class="category-card" @click="$router.push(`/category/${cat.name}`)">
            <div class="category-icon-bg">
              <span class="category-icon">{{ cat.icon }}</span>
            </div>
            <div class="category-name">{{ cat.name }}</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="section-container">
      <div class="section-header">
        <div class="section-title">
          <el-icon color="#667eea" :size="28"><Goods /></el-icon>
          <h2>热销商品</h2>
        </div>
        <el-button link @click="$router.push('/listings')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
      </div>

      <div v-loading="isLoading" class="products-grid">
        <el-empty v-if="!isLoading && hotItems.length === 0" description="暂无热销商品" />
        <el-row :gutter="20" v-else>
          <el-col :xs="24" :sm="12" :md="6" v-for="item in hotItems" :key="item.id">
            <div class="product-card" @click="$router.push(`/item/${item.itemId}`)">
              <div class="product-image">
                <img v-if="item.iconUrl" :src="getImageUrl(item.iconUrl)" class="item-img" />
                <el-icon v-else :size="60" color="#909399"><Goods /></el-icon>
                <div v-if="item.discount" class="discount-badge">-{{ item.discount }}%</div>
                <div v-if="item.isNew" class="new-badge">新品</div>
              </div>
              <div class="product-info">
                <h3 class="product-name" :title="item.name">{{ item.name }}</h3>
                <p class="product-game">{{ item.game }}</p>
                <div class="product-price-row">
                  <span class="price">¥{{ item.price }}</span>
                  <span v-if="item.originalPrice" class="original-price">¥{{ item.originalPrice }}</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <div class="section-container">
      <div class="section-header">
        <div class="section-title">
          <el-icon color="#67C23A" :size="28"><Present /></el-icon>
          <h2>新品推荐</h2>
        </div>
        <el-button link @click="$router.push('/listings')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
      </div>

      <div v-loading="isLoading" class="products-grid">
        <el-empty v-if="!isLoading && newItems.length === 0" description="暂无新品推荐" />
        <el-row :gutter="20" v-else>
          <el-col :xs="24" :sm="12" :md="6" v-for="item in newItems" :key="item.id">
            <div class="product-card" @click="$router.push(`/item/${item.itemId}`)">
              <div class="product-image">
                <img v-if="item.iconUrl" :src="getImageUrl(item.iconUrl)" class="item-img" />
                <el-icon v-else :size="60" color="#909399"><Goods /></el-icon>
                <div class="new-badge">新品</div>
              </div>
              <div class="product-info">
                <h3 class="product-name" :title="item.name">{{ item.name }}</h3>
                <p class="product-game">{{ item.game }}</p>
                <div class="product-price-row">
                  <span class="price">¥{{ item.price }}</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <div class="section-container">
      <div class="section-header">
        <div class="section-title">
          <el-icon color="#E6A23C" :size="28"><Trophy /></el-icon>
          <h2>热门游戏</h2>
        </div>
        <el-button link @click="$router.push('/category')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
      </div>
      <el-row :gutter="16">
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
import { ArrowRight, Goods, Present, Trophy } from '@element-plus/icons-vue'

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
    bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    title: '新品上架',
    desc: '最新游戏道具抢先体验',
    btnText: '查看详情',
    link: '/listings',
    icon: 'Present',
    bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    title: '安全交易',
    desc: '多重保障，放心交易',
    btnText: '了解更多',
    link: '/category',
    icon: 'Medal',
    bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
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

function getImageUrl(iconUrl) {
  if (!iconUrl) return ''
  const filename = iconUrl.split(/[/\\]/).pop()
  return `/api/uploads/img/${filename}`
}

async function fetchItems() {
  isLoading.value = true
  try {
    const res = await request('/item/listings?size=8', { method: 'GET' })
    if (res.data && res.data.content) {
      const itemsRes = await request('/item', { method: 'GET' })
      const itemMap = {}
      if (itemsRes.data) {
        itemsRes.data.forEach(gameItem => {
          itemMap[gameItem.id] = gameItem
        })
      }

      const items = res.data.content.map((item, index) => {
        const gameItem = itemMap[item.itemId] || {}
        return {
          id: item.id,
          itemId: item.itemId,
          name: gameItem.name || '未知商品',
          game: gameItem.game || '未知游戏',
          price: item.price,
          originalPrice: gameItem.referencePrice,
          discount: gameItem.referencePrice ? Math.round((1 - item.price / gameItem.referencePrice) * 100) : 0,
          iconUrl: gameItem.iconUrl || null,
          isNew: index >= 4
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
  gap: 32px;
  margin-bottom: 60px;
}

.hero-carousel {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);
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
  font-weight: 800;
}

.carousel-text p {
  font-size: 20px;
  margin: 0 0 32px 0;
  opacity: 0.9;
}

.carousel-btn {
  background: white;
  color: #667eea;
  border: none;
  font-weight: 600;
  padding: 12px 32px;
  border-radius: 30px;
}

.carousel-btn:hover {
  background: rgba(255,255,255,0.9);
  transform: scale(1.05);
}

.quick-categories {
  margin-top: -16px;
}

.category-card {
  text-align: center;
  cursor: pointer;
  padding: 20px 10px;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.category-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.15);
}

.category-icon-bg {
  width: 64px;
  height: 64px;
  margin: 0 auto 12px;
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-icon {
  font-size: 28px;
}

.category-name {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.section-container {
  background: white;
  padding: 28px;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f5;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title h2 {
  margin: 0;
  font-size: 22px;
  color: #1a1a2e;
  font-weight: 600;
}

.product-card {
  cursor: pointer;
  margin-bottom: 16px;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: #fff;
  border: 1px solid #f0f0f5;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.product-image {
  height: 160px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.item-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}

.product-card:hover .item-img {
  transform: scale(1.08);
}

.discount-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 10px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
}

.new-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 10px;
  background: linear-gradient(135deg, #67C23A 0%, #529b2e 100%);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
}

.product-info {
  padding: 16px;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-game {
  margin: 0 0 12px 0;
  font-size: 12px;
  color: #909399;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.price {
  font-size: 22px;
  font-weight: bold;
  color: #ff6b6b;
}

.original-price {
  font-size: 13px;
  color: #c0c4cc;
  text-decoration: line-through;
}

.game-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  border-radius: 16px;
  padding: 24px 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 16px;
}

.game-card:hover {
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
}

.game-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.game-name {
  margin: 0;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 20px;
}

:deep(.el-button link) {
  color: #667eea;
}

@media (max-width: 768px) {
  .carousel-content {
    padding: 0 30px;
  }

  .carousel-text h2 {
    font-size: 28px;
  }

  .carousel-image {
    display: none;
  }

  .hero-carousel {
    border-radius: 12px;
  }
}
</style>