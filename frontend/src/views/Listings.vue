<template>
  <div class="listings-page">
    <div class="page-header">
      <h1>挂牌列表</h1>
      <p>浏览所有可购买的游戏道具</p>
    </div>
    
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="游戏">
          <el-select v-model="gameValue" placeholder="全部游戏" clearable value-key="value">
            <el-option label="热血传奇" value="热血传奇" />
            <el-option label="魔兽世界" value="魔兽世界" />
            <el-option label="英雄联盟" value="英雄联盟" />
            <el-option label="王者荣耀" value="王者荣耀" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="分类">
          <el-select v-model="categoryValue" placeholder="全部分类" clearable value-key="value">
            <el-option label="武器" value="武器" />
            <el-option label="防具" value="防具" />
            <el-option label="消耗品" value="消耗品" />
            <el-option label="材料" value="材料" />
            <el-option label="宠物" value="宠物" />
            <el-option label="坐骑" value="坐骑" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="价格区间">
          <div class="price-range">
            <el-input-number v-model="filters.minPrice" :min="0" :controls="false" placeholder="最低价" />
            <span class="separator">-</span>
            <el-input-number v-model="filters.maxPrice" :min="0" :controls="false" placeholder="最高价" />
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="applyFilters" :icon="Search">筛选</el-button>
          <el-button @click="resetFilters" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <div v-loading="isLoading" class="listings-container">
      <el-empty v-if="!isLoading && filteredListings.length === 0" description="暂无符合条件的挂牌" />
      
      <el-row :gutter="20" v-else>
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="listing in filteredListings" :key="listing.id">
          <el-card shadow="hover" class="listing-card">
            <div class="listing-header">
              <h3 class="item-name" :title="listing.itemName">{{ listing.itemName }}</h3>
              <el-tag size="small" effect="plain">{{ listing.category }}</el-tag>
            </div>
            
            <div class="listing-body">
              <p class="item-game"><el-icon><Monitor /></el-icon> {{ listing.game }}</p>
              <p class="item-desc" :title="listing.itemDesc">{{ listing.itemDesc }}</p>
              
              <div class="price-stock">
                <div class="price">¥{{ listing.price }}</div>
                <div class="stock">库存: {{ listing.quantity }}</div>
              </div>
            </div>
            
            <div class="listing-footer">
              <div class="seller-info">
                <span class="seller"><el-icon><User /></el-icon> 卖家: {{ listing.sellerId }}</span>
                <span class="time">{{ formatDate(listing.createTime) }}</span>
              </div>
              <el-button type="primary" @click="buyListing(listing)">购买</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { request } from '../api/client.js'
import { Search, Refresh, Monitor, User } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const listings = ref([])
const isLoading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const isUpdatingFromRoute = ref(false)

// 中间变量，用于绑定el-select组件
const gameValue = ref(route.query.game ? decodeURIComponent(route.query.game) : '')
const categoryValue = ref(route.query.category ? decodeURIComponent(route.query.category) : '')

// 过滤条件，用于前端过滤
const filters = ref({
  game: gameValue.value,
  category: categoryValue.value,
  minPrice: undefined,
  maxPrice: undefined
})

// 前端过滤（因为后端可能没有提供完整的过滤API）
const filteredListings = computed(() => {
  return listings.value.filter(listing => {
    if (filters.value.game && listing.game !== filters.value.game) return false
    if (filters.value.category && listing.category !== filters.value.category) return false
    if (filters.value.minPrice !== undefined && listing.price < filters.value.minPrice) return false
    if (filters.value.maxPrice !== undefined && listing.price > filters.value.maxPrice) return false
    return true
  })
})

function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function fetchListings() {
  isLoading.value = true
  try {
    const response = await request(`/item/listings?page=${currentPage.value - 1}&size=${pageSize.value}`, { method: 'GET' })
    
    if (response.data && response.data.content) {
      // 获取所有的商品信息用于补充挂牌数据
      const itemsRes = await request('/item', { method: 'GET' })
      const itemMap = {}
      if (itemsRes.data) {
        itemsRes.data.forEach(gameItem => {
          itemMap[gameItem.id] = gameItem
        })
      }

      listings.value = response.data.content.map(item => {
        const gameItem = itemMap[item.itemId] || {}
        return {
          id: item.id,
          itemId: item.itemId,
          itemName: gameItem.name || '未知商品',
          itemDesc: item.description || gameItem.description || '暂无描述',
          game: gameItem.game || '未知游戏',
          category: gameItem.category || '其他',
          price: item.price,
          quantity: item.quantity || 1,
          sellerId: item.sellerId,
          createTime: item.createdAt || item.createTime
        }
      })
      total.value = response.data.totalElements || 0
    }
  } catch (error) {
    console.error('获取挂牌列表失败:', error)
  } finally {
    isLoading.value = false
  }
}

function applyFilters() {
  // 更新路由参数，保持过滤条件
  router.replace({
    path: '/listings',
    query: {
      game: filters.value.game || undefined,
      category: filters.value.category || undefined
    }
  })
  // 前端过滤，不需要重新获取数据
}

function resetFilters() {
  filters.value = {
    game: '',
    category: '',
    minPrice: undefined,
    maxPrice: undefined
  }
  // 重置路由参数
  router.replace({
    path: '/listings',
    query: {}
  })
  // 前端过滤，不需要重新获取数据
}

function handleSizeChange(val) {
  pageSize.value = val
  fetchListings()
}

function handleCurrentChange(val) {
  currentPage.value = val
  fetchListings()
}

function buyListing(listing) {
  if (listing.itemId) {
    router.push(`/item/${listing.itemId}`)
  }
}

// 监听路由参数变化
watch(() => route.query, (newQuery) => {
  console.log('Route query changed:', newQuery)
  isUpdatingFromRoute.value = true
  try {
    // 先清空值，再设置新值，触发el-select的更新
    gameValue.value = ''
    categoryValue.value = ''
    // 使用nextTick确保DOM更新后再设置新值
    nextTick(() => {
      gameValue.value = newQuery.game ? decodeURIComponent(newQuery.game) : ''
      categoryValue.value = newQuery.category ? decodeURIComponent(newQuery.category) : ''
      // 更新过滤条件
      filters.value.game = gameValue.value
      filters.value.category = categoryValue.value
      console.log('Game value updated:', gameValue.value)
      console.log('Category value updated:', categoryValue.value)
      console.log('Filters updated:', filters.value)
    })
  } finally {
    isUpdatingFromRoute.value = false
  }
}, { deep: true })

// 监听gameValue变化，自动更新过滤条件和路由参数
watch(gameValue, (newGame) => {
  // 更新过滤条件
  filters.value.game = newGame
  
  if (!isUpdatingFromRoute.value) {
    router.replace({
      path: '/listings',
      query: {
        game: newGame || undefined,
        category: categoryValue.value || undefined
      }
    })
  }
})

// 监听categoryValue变化，自动更新过滤条件和路由参数
watch(categoryValue, (newCategory) => {
  // 更新过滤条件
  filters.value.category = newCategory
  
  if (!isUpdatingFromRoute.value) {
    router.replace({
      path: '/listings',
      query: {
        game: gameValue.value || undefined,
        category: newCategory || undefined
      }
    })
  }
})

onMounted(() => {
  // 手动更新gameValue和categoryValue，确保从路由参数中获取正确的值
  console.log('Initial route query:', route.query)
  gameValue.value = route.query.game ? decodeURIComponent(route.query.game) : ''
  categoryValue.value = route.query.category ? decodeURIComponent(route.query.category) : ''
  // 更新过滤条件
  filters.value.game = gameValue.value
  filters.value.category = categoryValue.value
  console.log('Initial gameValue:', gameValue.value)
  console.log('Initial categoryValue:', categoryValue.value)
  console.log('Initial filters:', filters.value)
  fetchListings()
})
</script>

<style scoped>
.listings-page {
  padding: 20px 0;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.custom-select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.custom-select:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 确保el-select组件有足够的宽度 */
:deep(.el-select) {
  min-width: 150px;
}

/* 确保el-select组件的输入框有足够的宽度 */
:deep(.el-select__input) {
  min-width: 100px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
}

.filter-card {
  margin-bottom: 24px;
  border-radius: 8px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.separator {
  color: #909399;
}

.listings-container {
  min-height: 400px;
}

.listing-card {
  margin-bottom: 20px;
  border-radius: 8px;
  transition: all 0.3s;
}

.listing-card:hover {
  transform: translateY(-5px);
}

.listing-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.item-name {
  margin: 0;
  font-size: 16px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  margin-right: 10px;
}

.listing-body {
  margin-bottom: 16px;
}

.item-game {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 4px;
}

.item-desc {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #909399;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 36px;
}

.price-stock {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #F56C6C;
}

.stock {
  font-size: 13px;
  color: #909399;
}

.listing-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.seller-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.seller {
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 4px;
}

.time {
  font-size: 12px;
  color: #C0C4CC;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
