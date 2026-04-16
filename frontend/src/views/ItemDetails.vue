<template>
  <div class="item-details-page">
    <div v-loading="isLoading" class="item-details-container" v-if="item">
      <el-row :gutter="32">
        <el-col :xs="24" :md="14">
          <div class="item-main">
            <div class="item-image-wrapper">
              <img v-if="item.iconUrl" :src="getImageUrl(item.iconUrl)" class="item-img" />
              <div v-else class="img-placeholder">
                <el-icon :size="100" color="#c0c4cc"><Goods /></el-icon>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :md="10">
          <div class="item-info">
            <div class="item-category-tag">{{ item.category }}</div>
            <h1 class="item-title">{{ item.name }}</h1>
            <div class="item-game">
              <el-icon><Monitor /></el-icon>
              <span>{{ item.game }}</span>
            </div>

            <div class="item-desc-section">
              <h3>商品描述</h3>
              <p class="desc-text">{{ item.description || '暂无描述' }}</p>
            </div>

            <div class="item-meta-grid">
              <div class="meta-item">
                <span class="meta-label">参考价</span>
                <span class="meta-value price">¥{{ item.referencePrice || 0 }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">商品ID</span>
                <span class="meta-value">{{ item.id }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="32" class="listings-section">
        <el-col :span="24">
          <div class="section-header">
            <el-icon color="#667eea" :size="24"><List /></el-icon>
            <h2>挂牌列表 ({{ listings.length }})</h2>
          </div>

          <div v-if="listings.length > 0" class="listings-grid">
            <div v-for="listing in listings" :key="listing.id" class="listing-card">
              <div class="listing-info">
                <div class="seller-info">
                  <el-icon><User /></el-icon>
                  <span>卖家: {{ listing.sellerId }}</span>
                </div>
                <div class="stock-info">
                  <el-icon><Box /></el-icon>
                  <span>库存: {{ listing.quantity }}</span>
                </div>
              </div>
              <div class="listing-price">
                <span class="price-value">¥{{ listing.price }}</span>
              </div>
              <el-button type="primary" @click="openBuyDialog(listing)">立即购买</el-button>
            </div>
          </div>
          <el-empty v-else description="暂无卖家挂牌出售此商品" />
        </el-col>
      </el-row>
    </div>

    <el-dialog v-model="buyDialogVisible" title="确认购买" width="420px" class="buy-dialog">
      <div v-if="selectedListing" class="buy-dialog-content">
        <div class="buy-item-info">
          <img v-if="item.iconUrl" :src="getImageUrl(item.iconUrl)" class="buy-item-img" />
          <div class="buy-item-detail">
            <h4>{{ item?.name }}</h4>
            <p class="buy-price">¥{{ selectedListing.price }}</p>
          </div>
        </div>

        <div class="buy-form">
          <div class="form-item">
            <label>购买数量</label>
            <el-input-number v-model="buyQuantity" :min="1" :max="selectedListing.quantity" size="default" />
          </div>
          <div class="form-item total">
            <label>总计</label>
            <span class="total-price">¥{{ (selectedListing.price * buyQuantity).toFixed(2) }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="buyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBuy" :loading="isBuying">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { request } from '../api/client.js'
import { useUserStore } from '../store/user'
import { Goods, Monitor, List, User, Box } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const item = ref(null)
const listings = ref([])
const isLoading = ref(false)

const buyDialogVisible = ref(false)
const selectedListing = ref(null)
const buyQuantity = ref(1)
const isBuying = ref(false)

function getImageUrl(iconUrl) {
  if (!iconUrl) return ''
  const filename = iconUrl.split(/[/\\]/).pop()
  return `/api/uploads/img/${filename}`
}

async function fetchItemDetails() {
  const itemId = route.params.id
  isLoading.value = true
  try {
    const itemResponse = await request(`/item/${itemId}`, { method: 'GET' })
    item.value = itemResponse.data

    const listingsResponse = await request(`/item/${itemId}/listings`, { method: 'GET' })
    listings.value = listingsResponse.data?.content || []
  } catch (error) {
    console.error('加载商品详情失败:', error)
  } finally {
    isLoading.value = false
  }
}

function openBuyDialog(listing) {
  if (!userStore.token) {
    ElMessage.warning('请先登录再进行购买')
    router.push('/login')
    return
  }
  selectedListing.value = listing
  buyQuantity.value = 1
  buyDialogVisible.value = true
}

async function confirmBuy() {
  if (!selectedListing.value) return

  isBuying.value = true
  try {
    const body = {
      listingId: selectedListing.value.id,
      buyerId: Number(userStore.userId),
      quantity: buyQuantity.value,
    }

    const response = await request('/trade/orders/fixed/listing', {
      method: 'POST',
      body: JSON.stringify(body),
    })

    buyDialogVisible.value = false

    ElMessageBox.confirm(
      `下单成功！订单号：${response.data.id}。是否立即前往支付？`,
      '购买成功',
      {
        confirmButtonText: '去支付',
        cancelButtonText: '查看订单',
        type: 'success',
      }
    ).then(() => {
      mockPay(response.data.id)
    }).catch(() => {
      router.push('/orders')
    })

    fetchItemDetails()
  } catch (error) {
    console.error('购买失败:', error)
  } finally {
    isBuying.value = false
  }
}

async function mockPay(orderId) {
  try {
    await request(`/trade/orders/${orderId}/pay`, { method: 'POST' })
    ElMessage.success('支付成功')
    router.push('/orders')
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

onMounted(() => {
  fetchItemDetails()
})
</script>

<style scoped>
.item-details-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px 0 60px;
}

.item-details-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.item-main {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.item-image-wrapper {
  width: 100%;
  height: 400px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.img-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.item-info {
  padding: 8px 0;
}

.item-category-tag {
  display: inline-block;
  padding: 6px 16px;
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
  color: #667eea;
  font-size: 13px;
  font-weight: 500;
  border-radius: 20px;
  margin-bottom: 16px;
}

.item-title {
  margin: 0 0 16px 0;
  font-size: 32px;
  font-weight: 700;
  color: #1a1a2e;
  line-height: 1.3;
}

.item-game {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  color: #667eea;
  margin-bottom: 24px;
}

.item-desc-section {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
}

.item-desc-section h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #303133;
}

.desc-text {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.7;
}

.item-meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.meta-item {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.meta-label {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.meta-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.meta-value.price {
  color: #ff6b6b;
  font-size: 24px;
}

.listings-section {
  margin-top: 32px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 22px;
  color: #1a1a2e;
}

.listings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.listing-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f0f5;
}

.listing-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.12);
  border-color: #667eea;
}

.listing-info {
  flex: 1;
}

.seller-info,
.stock-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
  margin-bottom: 6px;
}

.listing-price {
  margin-right: 12px;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
}

.buy-dialog-content {
  padding: 8px 0;
}

.buy-item-info {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 12px;
  margin-bottom: 20px;
}

.buy-item-img {
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: 8px;
  background: white;
}

.buy-item-detail h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.buy-price {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #ff6b6b;
}

.buy-form {
  padding: 0 8px;
}

.form-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.form-item label {
  font-size: 14px;
  color: #606266;
}

.form-item.total {
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #5a71d2 0%, #6a4190 100%);
}

@media (max-width: 768px) {
  .item-title {
    font-size: 24px;
  }

  .item-meta-grid {
    grid-template-columns: 1fr;
  }

  .listings-grid {
    grid-template-columns: 1fr;
  }
}
</style>