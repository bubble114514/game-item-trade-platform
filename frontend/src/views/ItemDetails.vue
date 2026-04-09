<template>
  <div class="item-details-page">
    <div v-loading="isLoading" class="item-details-container">
      <el-row :gutter="24" v-if="item">
        <!-- 左侧：商品信息 -->
        <el-col :xs="24" :md="16">
          <el-card class="info-card" shadow="never">
            <div class="item-header">
              <div class="item-image">
                <el-icon :size="80" color="#409EFF"><Goods /></el-icon>
              </div>
              <div class="item-title-area">
                <h2>{{ item.name }}</h2>
                <div class="tags">
                  <el-tag>{{ item.game }}</el-tag>
                  <el-tag type="success">{{ item.category }}</el-tag>
                </div>
              </div>
            </div>
            
            <el-divider />
            
            <div class="item-desc-section">
              <h3>商品描述</h3>
              <p class="desc-text">{{ item.description || '暂无描述' }}</p>
            </div>
            
            <div class="item-meta">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="商品ID">{{ item.id }}</el-descriptions-item>
                <el-descriptions-item label="参考价格">
                  <span class="ref-price">¥{{ item.referencePrice ?? 'N/A' }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="所属游戏">{{ item.game }}</el-descriptions-item>
                <el-descriptions-item label="商品分类">{{ item.category }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </el-col>
        
        <!-- 右侧：购买选项 -->
        <el-col :xs="24" :md="8">
          <el-card class="purchase-card" shadow="never">
            <template #header>
              <div class="card-header">
                <span>购买选项 (挂牌列表)</span>
              </div>
            </template>
            
            <div v-if="listings.length > 0" class="listings-list">
              <div v-for="listing in listings" :key="listing.id" class="listing-item">
                <div class="listing-info">
                  <div class="seller">卖家ID: {{ listing.sellerId }}</div>
                  <div class="stock">库存: {{ listing.quantity }}</div>
                </div>
                <div class="listing-action">
                  <div class="price">¥{{ listing.price }}</div>
                  <el-button type="primary" size="small" @click="openBuyDialog(listing)">购买</el-button>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无卖家挂牌出售此商品" :image-size="100" />
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 购买确认弹窗 -->
    <el-dialog
      v-model="buyDialogVisible"
      title="确认购买"
      width="400px"
    >
      <div v-if="selectedListing" class="buy-dialog-content">
        <p><strong>商品：</strong>{{ item?.name }}</p>
        <p><strong>单价：</strong><span class="price">¥{{ selectedListing.price }}</span></p>
        <p><strong>库存：</strong>{{ selectedListing.quantity }}</p>
        
        <div class="quantity-input">
          <span>购买数量：</span>
          <el-input-number 
            v-model="buyQuantity" 
            :min="1" 
            :max="selectedListing.quantity" 
            size="small"
          />
        </div>
        
        <div class="total-price">
          总计：<span class="price">¥{{ (selectedListing.price * buyQuantity).toFixed(2) }}</span>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="buyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmBuy" :loading="isBuying">
            确认下单
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { request } from '../api/client.js'
import { useUserStore } from '../store/user'
import { Goods } from '@element-plus/icons-vue'
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
      // 模拟支付流程，实际应该跳转到支付页面
      mockPay(response.data.id)
    }).catch(() => {
      router.push('/orders')
    })
    
    fetchItemDetails() // 刷新库存
  } catch (error) {
    console.error('购买失败:', error)
  } finally {
    isBuying.value = false
  }
}

async function mockPay(orderId) {
  try {
    await request(`/trade/orders/${orderId}/pay?buyerId=${userStore.userId}`, {
      method: 'POST'
    })
    ElMessage.success('支付成功！')
    router.push('/orders')
  } catch (e) {
    console.error('支付失败:', e)
  }
}

onMounted(() => {
  fetchItemDetails()
})
</script>

<style scoped>
.item-details-page {
  padding: 20px 0;
}

.item-details-container {
  max-width: 1200px;
  margin: 0 auto;
}

.info-card {
  border-radius: 8px;
  margin-bottom: 20px;
}

.item-header {
  display: flex;
  gap: 24px;
  align-items: center;
}

.item-image {
  width: 120px;
  height: 120px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-title-area h2 {
  margin: 0 0 12px 0;
  font-size: 24px;
  color: #303133;
}

.tags {
  display: flex;
  gap: 8px;
}

.item-desc-section {
  margin-bottom: 24px;
}

.item-desc-section h3 {
  font-size: 16px;
  color: #303133;
  margin: 0 0 12px 0;
}

.desc-text {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.ref-price {
  color: #909399;
  text-decoration: line-through;
}

.purchase-card {
  border-radius: 8px;
}

.card-header {
  font-weight: bold;
  color: #303133;
}

.listings-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.listing-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.listing-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.seller {
  font-size: 13px;
  color: #606266;
}

.stock {
  font-size: 12px;
  color: #909399;
}

.listing-action {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #F56C6C;
}

.buy-dialog-content {
  font-size: 14px;
}

.buy-dialog-content p {
  margin: 8px 0;
}

.quantity-input {
  display: flex;
  align-items: center;
  margin: 16px 0;
}

.total-price {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  text-align: right;
  font-size: 16px;
  font-weight: bold;
}

@media (max-width: 768px) {
  .item-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
