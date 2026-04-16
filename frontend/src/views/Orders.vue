<template>
  <div class="orders-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="40" color="#fff"><List /></el-icon>
        </div>
        <div class="header-text">
          <h2>我的订单</h2>
          <p>查看和管理您的交易订单</p>
        </div>
      </div>
    </div>

    <div class="orders-container">
      <el-card class="orders-card" shadow="never">
        <div class="tabs-wrapper">
          <el-tabs v-model="orderRole" @tab-change="handleRoleChange" class="custom-tabs">
            <el-tab-pane label="我买入的" name="buyer">
              <template #label>
                <div class="tab-label">
                  <el-icon><ShoppingCart /></el-icon>
                  <span>我买入的</span>
                </div>
              </template>
              <el-tabs v-model="activeStatus" @tab-change="handleTabChange" class="status-tabs">
                <el-tab-pane
                  v-for="status in orderStatuses"
                  :key="status.value"
                  :label="status.label"
                  :name="status.value"
                >
                  <div v-loading="isLoading" class="order-list">
                    <template v-if="filteredOrders.length > 0">
                      <div
                        v-for="order in filteredOrders"
                        :key="order.id"
                        class="order-item-card"
                      >
                        <div class="order-header">
                          <div class="order-id-box">
                            <span class="order-label">订单号</span>
                            <span class="order-id">{{ order.id }}</span>
                          </div>
                          <el-tag :type="getStatusType(order.status)" effect="light" class="status-tag">
                            {{ getStatusText(order.status) }}
                          </el-tag>
                        </div>

                        <div class="order-body">
                          <div class="item-info">
                            <h4 class="item-name">{{ order.itemName }}</h4>
                            <p class="item-desc">{{ order.itemDesc || '暂无描述' }}</p>
                          </div>
                          <div class="item-price-qty">
                            <div class="price">¥{{ order.unitPrice.toFixed(2) }}</div>
                            <div class="quantity">x{{ order.quantity }}</div>
                          </div>
                        </div>

                        <div class="order-summary">
                          <div class="summary-grid">
                            <div class="summary-item">
                              <span class="summary-label">交易模式</span>
                              <span class="summary-value">{{ order.mode === 'listing' ? '基于挂牌' : '一口价' }}</span>
                            </div>
                            <div class="summary-item">
                              <span class="summary-label">创建时间</span>
                              <span class="summary-value">{{ formatDate(order.createTime) }}</span>
                            </div>
                            <div class="summary-item total">
                              <span class="summary-label">总金额</span>
                              <span class="total-amount">¥{{ (order.unitPrice * order.quantity).toFixed(2) }}</span>
                            </div>
                          </div>
                        </div>

                        <div class="order-actions">
                          <el-button
                            v-if="order.status === 'PENDING_PAYMENT'"
                            type="primary"
                            @click="payOrder(order.id)"
                            class="action-btn primary"
                          >
                            <el-icon><Wallet /></el-icon>
                            去支付
                          </el-button>
                          <el-button
                            v-else-if="order.status === 'PENDING_DELIVERY'"
                            type="warning"
                            disabled
                            class="action-btn"
                          >
                            <el-icon><Clock /></el-icon>
                            等待发货
                          </el-button>
                          <el-button
                            v-else-if="order.status === 'PENDING_RECEIPT'"
                            type="success"
                            @click="confirmReceipt(order.id)"
                            class="action-btn success"
                          >
                            <el-icon><Check /></el-icon>
                            确认收货
                          </el-button>
                          <el-button
                            v-else-if="order.status === 'COMPLETED'"
                            @click="viewOrderDetail(order.id)"
                            class="action-btn"
                          >
                            <el-icon><View /></el-icon>
                            查看详情
                          </el-button>
                          <el-button
                            v-else
                            disabled
                            class="action-btn"
                          >
                            已完成
                          </el-button>
                        </div>
                      </div>
                    </template>
                    <el-empty v-else description="暂无订单" />
                  </div>
                </el-tab-pane>
              </el-tabs>
            </el-tab-pane>

            <el-tab-pane label="我卖出的" name="seller">
              <template #label>
                <div class="tab-label">
                  <el-icon><Sell /></el-icon>
                  <span>我卖出的</span>
                </div>
              </template>
              <el-tabs v-model="activeStatus" @tab-change="handleTabChange" class="status-tabs">
                <el-tab-pane
                  v-for="status in orderStatuses"
                  :key="status.value"
                  :label="status.label"
                  :name="status.value"
                >
                  <div v-loading="isLoading" class="order-list">
                    <template v-if="filteredOrders.length > 0">
                      <div
                        v-for="order in filteredOrders"
                        :key="order.id"
                        class="order-item-card"
                      >
                        <div class="order-header">
                          <div class="order-id-box">
                            <span class="order-label">订单号</span>
                            <span class="order-id">{{ order.id }}</span>
                          </div>
                          <el-tag :type="getStatusType(order.status)" effect="light" class="status-tag">
                            {{ getStatusText(order.status) }}
                          </el-tag>
                        </div>

                        <div class="order-body">
                          <div class="item-info">
                            <h4 class="item-name">{{ order.itemName }}</h4>
                            <p class="item-desc">{{ order.itemDesc || '暂无描述' }}</p>
                          </div>
                          <div class="item-price-qty">
                            <div class="price">¥{{ order.unitPrice.toFixed(2) }}</div>
                            <div class="quantity">x{{ order.quantity }}</div>
                          </div>
                        </div>

                        <div class="order-summary">
                          <div class="summary-grid">
                            <div class="summary-item">
                              <span class="summary-label">交易模式</span>
                              <span class="summary-value">{{ order.mode === 'listing' ? '基于挂牌' : '一口价' }}</span>
                            </div>
                            <div class="summary-item">
                              <span class="summary-label">创建时间</span>
                              <span class="summary-value">{{ formatDate(order.createTime) }}</span>
                            </div>
                            <div class="summary-item total">
                              <span class="summary-label">总金额</span>
                              <span class="total-amount">¥{{ (order.unitPrice * order.quantity).toFixed(2) }}</span>
                            </div>
                          </div>
                        </div>

                        <div class="order-actions">
                          <el-button
                            v-if="order.status === 'PENDING_PAYMENT'"
                            disabled
                            class="action-btn"
                          >
                            <el-icon><Clock /></el-icon>
                            等待支付
                          </el-button>
                          <el-button
                            v-else-if="order.status === 'PENDING_DELIVERY'"
                            type="primary"
                            @click="deliverOrder(order.id)"
                            class="action-btn primary"
                          >
                            <el-icon><Upload /></el-icon>
                            去发货
                          </el-button>
                          <el-button
                            v-else-if="order.status === 'PENDING_RECEIPT'"
                            disabled
                            class="action-btn"
                          >
                            <el-icon><Clock /></el-icon>
                            等待确认收货
                          </el-button>
                          <el-button
                            v-else-if="order.status === 'COMPLETED'"
                            @click="viewOrderDetail(order.id)"
                            class="action-btn"
                          >
                            <el-icon><View /></el-icon>
                            查看详情
                          </el-button>
                          <el-button
                            v-else
                            disabled
                            class="action-btn"
                          >
                            已完成
                          </el-button>
                        </div>
                      </div>
                    </template>
                    <el-empty v-else description="暂无订单" />
                  </div>
                </el-tab-pane>
              </el-tabs>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { request } from '../api/client.js'
import { useUserStore } from '../store/user.js'
import { List, ShoppingCart, Sell, Wallet, Clock, Check, View, Upload } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const orders = ref([])
const activeStatus = ref('all')
const orderRole = ref('buyer')
const isLoading = ref(false)

const orderStatuses = [
  { value: 'all', label: '全部' },
  { value: 'PENDING_PAYMENT', label: '待支付' },
  { value: 'PENDING_DELIVERY', label: '待发货' },
  { value: 'PENDING_RECEIPT', label: '待收货' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'CANCELLED', label: '已取消' }
]

const filteredOrders = computed(() => {
  if (activeStatus.value === 'all') {
    return orders.value
  }
  return orders.value.filter(order => order.status === activeStatus.value)
})

function getStatusText(status) {
  const statusMap = {
    'PENDING_PAYMENT': '待支付',
    'PENDING_DELIVERY': '待发货',
    'PENDING_RECEIPT': '待收货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

function getStatusType(status) {
  const typeMap = {
    'PENDING_PAYMENT': 'warning',
    'PENDING_DELIVERY': 'primary',
    'PENDING_RECEIPT': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

function formatDate(dateString) {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function handleTabChange(tab) {
  // 状态切换时不需要重新加载，因为filteredOrders已经是computed属性
}

function handleRoleChange(role) {
  activeStatus.value = 'all'
  fetchOrders()
}

async function deliverOrder(orderId) {
  try {
    await ElMessageBox.confirm('确认发货吗？', '提示', {
      confirmButtonText: '确认发货',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用后端发货接口
    await request(`/trade/orders/${orderId}/deliver?sellerId=${userStore.userId}`, {
      method: 'POST'
    })
    ElMessage.success('发货成功')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('发货失败')
    }
  }
}

async function payOrder(orderId) {
  try {
    await ElMessageBox.confirm('确认支付该订单吗？', '提示', {
      confirmButtonText: '确认支付',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用后端支付接口
    await request(`/trade/orders/${orderId}/pay?buyerId=${userStore.userId}`, {
      method: 'POST'
    })
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('支付失败')
    }
  }
}

async function remindDelivery(orderId) {
  try {
    // 调用后端提醒发货接口
    await request(`/trade/orders/${orderId}/deliver?sellerId=${userStore.userId}`, {
      method: 'POST'
    })
    ElMessage.success('已提醒卖家发货')
  } catch (e) {
    ElMessage.error('提醒发货失败')
  }
}

async function confirmReceipt(orderId) {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用后端确认收货接口
    await request(`/trade/orders/${orderId}/complete?buyerId=${userStore.userId}`, {
      method: 'POST'
    })
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

function viewOrderDetail(orderId) {
  ElMessage.info('订单详情功能开发中')
}

async function fetchOrders() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  isLoading.value = true
  try {
    let response
    // 根据当前角色调用不同的API
    if (orderRole.value === 'buyer') {
      response = await request(`/trade/orders/buyer/${userStore.userId}`, { method: 'GET' })
    } else {
      response = await request(`/trade/orders/seller/${userStore.userId}`, { method: 'GET' })
    }
    // 后端返回的是分页数据，需要从content字段获取订单列表
    orders.value = response.data?.content || []
  } catch (error) {
    console.error('获取订单失败:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-page {
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
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

.orders-container {
  min-height: 500px;
}

.orders-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.tabs-wrapper {
  padding: 8px 0;
}

.custom-tabs :deep(.el-tabs__header) {
  margin: 0 0 16px 0;
  padding: 0 16px;
}

.custom-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background: #ebeef5;
}

.custom-tabs :deep(.el-tabs__item) {
  height: 48px;
  line-height: 48px;
  font-size: 15px;
  color: #606266;
  padding: 0 24px;
}

.custom-tabs :deep(.el-tabs__item.is-active) {
  color: #667eea;
  font-weight: 600;
}

.custom-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-tabs :deep(.el-tabs__header) {
  margin: 0 0 16px 0;
  padding: 0 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.status-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.status-tabs :deep(.el-tabs__item) {
  height: 40px;
  line-height: 40px;
  font-size: 13px;
  color: #606266;
  padding: 0 16px;
}

.status-tabs :deep(.el-tabs__item.is-active) {
  color: #667eea;
  font-weight: 600;
}

.status-tabs :deep(.el-tabs__active-bar) {
  height: 2px;
  background: #667eea;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.order-item-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #ebeef5;
  padding: 20px;
  transition: all 0.3s ease;
}

.order-item-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #ebeef5;
}

.order-id-box {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.order-label {
  font-size: 12px;
  color: #909399;
}

.order-id {
  font-size: 14px;
  color: #303133;
  font-weight: 600;
  font-family: monospace;
}

.status-tag {
  border-radius: 20px;
}

.order-body {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.item-info {
  flex: 1;
}

.item-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.item-desc {
  margin: 0;
  font-size: 14px;
  color: #909399;
  line-height: 1.5;
}

.item-price-qty {
  text-align: right;
  margin-left: 20px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.quantity {
  font-size: 14px;
  color: #909399;
}

.order-summary {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.summary-item.total {
  text-align: right;
}

.summary-label {
  font-size: 12px;
  color: #909399;
}

.summary-value {
  font-size: 14px;
  color: #303133;
}

.total-amount {
  font-weight: bold;
  color: #f56c6c;
  font-size: 20px;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.action-btn {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
}

.action-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.action-btn.success {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border: none;
  color: white;
}

.action-btn.success:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(17, 153, 142, 0.4);
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

  .summary-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .summary-item.total {
    text-align: left;
  }

  .order-body {
    flex-direction: column;
    gap: 12px;
  }

  .item-price-qty {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin-left: 0;
  }

  .order-actions {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
