<template>
  <div class="search-page">
    <div class="search-header">
      <div class="search-header-content">
        <h2>搜索结果</h2>
        <p class="subtitle" v-if="searchQuery">
          关键词 "<span class="keyword">{{ searchQuery }}</span>"，
          找到 <span class="count">{{ total }}</span> 个相关道具
        </p>
      </div>
      <div class="filter-section">
        <el-input-number v-model="minPrice" :min="0" :precision="2" placeholder="最低价" controls-position="right" />
        <span class="filter-separator">-</span>
        <el-input-number v-model="maxPrice" :min="0" :precision="2" placeholder="最高价" controls-position="right" />
        <el-button type="primary" @click="applyPriceFilter">筛选</el-button>
        <el-button @click="resetPriceFilter">重置</el-button>
      </div>
    </div>

    <div class="search-content" v-loading="isLoading">
      <el-empty v-if="!isLoading && searchResults.length === 0" description="未找到相关道具，请尝试其他关键词" />

      <template v-else-if="searchResults.length > 0">
        <div class="results-grid">
          <div
            v-for="item in searchResults"
            :key="item.id"
            class="result-card"
            @click="$router.push(`/item/${item.id}`)"
          >
            <div class="card-image">
              <img v-if="item.iconUrl" :src="getImageUrl(item.iconUrl)" class="card-img" />
              <el-icon v-else :size="80" color="#909399"><Goods /></el-icon>
              <div class="category-tag" v-if="item.category">{{ item.category }}</div>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ item.name }}</h3>
              <div class="card-meta">
                <span class="game-name"><el-icon><Monitor /></el-icon> {{ item.game || '未知游戏' }}</span>
              </div>
              <p class="card-desc" v-if="item.description">{{ item.description }}</p>
              <p class="card-desc" v-else>暂无描述</p>
            </div>
            <div class="card-footer">
              <div class="price-info">
                <span class="price-value">¥{{ item.referencePrice || 0 }}</span>
              </div>
              <el-button type="primary" size="small" @click.stop="$router.push(`/item/${item.id}`)">查看详情</el-button>
            </div>
          </div>
        </div>

        <div class="pagination-wrapper" v-if="total > pageSize">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { request } from '../api/client.js'
import { Goods, Monitor } from '@element-plus/icons-vue'

const route = useRoute()

const searchQuery = ref('')
const searchResults = ref([])
const isLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const minPrice = ref(null)
const maxPrice = ref(null)

function getImageUrl(iconUrl) {
  if (!iconUrl) return ''
  const filename = iconUrl.split(/[/\\]/).pop()
  return `/api/uploads/img/${filename}`
}

async function performSearch() {
  if (!searchQuery.value.trim()) {
    searchResults.value = []
    total.value = 0
    return
  }

  isLoading.value = true
  try {
    let url = `/search/items?q=${encodeURIComponent(searchQuery.value)}&size=${pageSize.value}`
    if (minPrice.value !== null) {
      url += `&minPrice=${minPrice.value}`
    }
    if (maxPrice.value !== null) {
      url += `&maxPrice=${maxPrice.value}`
    }
    const response = await request(url)
    if (response.data) {
      searchResults.value = response.data
      total.value = response.data.length
    } else {
      searchResults.value = []
      total.value = 0
    }
  } catch (e) {
    console.error('Search failed:', e)
    searchResults.value = []
    total.value = 0
  } finally {
    isLoading.value = false
  }
}

function applyPriceFilter() {
  currentPage.value = 1
  performSearch()
}

function resetPriceFilter() {
  minPrice.value = null
  maxPrice.value = null
  currentPage.value = 1
  performSearch()
}

function handlePageChange(page) {
  currentPage.value = page
  performSearch()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

watch(() => route.query.q, (newQuery) => {
  if (newQuery) {
    searchQuery.value = newQuery
    performSearch()
  }
}, { immediate: true })

onMounted(() => {
  if (route.query.q) {
    searchQuery.value = route.query.q
    performSearch()
  }
})
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.search-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px 40px;
  margin-bottom: 28px;
  border-radius: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.25);
}

.search-header-content {
  flex: 1;
}

.search-header h2 {
  margin: 0 0 12px 0;
  font-size: 28px;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  font-size: 15px;
  opacity: 0.95;
}

.subtitle .keyword {
  font-weight: 600;
  color: #ffd700;
}

.subtitle .count {
  font-weight: 700;
  font-size: 18px;
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-separator {
  color: #fff;
  margin: 0 4px;
  font-weight: 500;
}

.filter-section :deep(.el-input-number) {
  width: 120px;
}

.filter-section :deep(.el-input-number .el-input__inner) {
  background: rgba(255, 255, 255, 0.95);
  border: none;
  border-radius: 8px;
}

.filter-section :deep(.el-button--primary) {
  background: white;
  color: #667eea;
  border: none;
  font-weight: 600;
}

.filter-section :deep(.el-button--primary:hover) {
  background: rgba(255, 255, 255, 0.9);
}

.filter-section :deep(.el-button:not(.el-button--primary)) {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
}

.filter-section :deep(.el-button:not(.el-button--primary):hover) {
  background: rgba(255, 255, 255, 0.3);
}

.search-content {
  width: 100%;
  padding: 0 40px 40px;
  min-height: 500px;
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.result-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f0f5;
}

.result-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.card-image {
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  position: relative;
  overflow: hidden;
}

.card-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}

.result-card:hover .card-img {
  transform: scale(1.05);
}

.category-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  background: rgba(102, 126, 234, 0.9);
  color: white;
  font-size: 12px;
  font-weight: 500;
  border-radius: 20px;
  backdrop-filter: blur(4px);
}

.card-body {
  padding: 20px;
  flex: 1;
}

.card-title {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.game-name {
  font-size: 13px;
  color: #667eea;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.card-desc {
  margin: 0;
  font-size: 13px;
  color: #909399;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 42px;
}

.card-footer {
  padding: 16px 20px;
  background: #fafafa;
  border-top: 1px solid #f0f0f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-info {
  display: flex;
  flex-direction: column;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
  line-height: 1;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

:deep(.el-pagination) {
  background: white;
  padding: 16px 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

@media (max-width: 768px) {
  .search-header {
    flex-direction: column;
    gap: 20px;
    padding: 24px 20px;
    align-items: flex-start;
  }

  .filter-section {
    width: 100%;
    flex-wrap: wrap;
  }

  .search-content {
    padding: 0 20px 20px;
  }

  .results-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }
}
</style>