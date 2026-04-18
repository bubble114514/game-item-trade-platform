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
                          <div class="order-info-row">
                            <span class="order-id">订单号: {{ order.orderNo }}</span>
                            <span class="order-time">{{ formatDate(order.createdAt) }}</span>
                          </div>
                          <el-tag :type="getStatusType(order.status)" effect="light" class="status-tag">
                            {{ getStatusText(order.status) }}
                          </el-tag>
                        </div>

                        <div class="order-body">
                          <div class="item-main">
                            <div class="item-image" v-if="order.itemIcon">
                              <img :src="getImageUrl(order.itemIcon)" @error="handleImageError($event, order)" />
                            </div>
                            <div class="item-image placeholder" v-else>
                              <el-icon :size="40" color="#c0c4cc"><Goods /></el-icon>
                            </div>
                            <div class="item-details">
                              <h4 class="item-name">{{ order.itemName || '商品 #' + order.itemId }}</h4>
                              <div class="item-meta">
                                <span class="game-tag">{{ order.game || '游戏道具' }}</span>
                              </div>
                            </div>
                          </div>
                          <div class="price-section">
                            <div class="unit-price">¥{{ order.unitPrice }}</div>
                            <div class="quantity">x{{ order.quantity }}</div>
                          </div>
                        </div>

                        <div class="order-footer">
                          <div class="total-row">
                            <span class="total-label">合计:</span>
                            <span class="total-amount">¥{{ order.totalAmount }}</span>
                          </div>
                          <div class="actions">
                            <el-button
                              v-if="order.status === 'CREATED'"
                              type="primary"
                              @click="payOrder(order)"
                              class="action-btn primary"
                            >
                              <el-icon><Wallet /></el-icon>
                              支付
                            </el-button>
                            <el-button
                              v-else-if="order.status === 'PAID'"
                              type="warning"
                              disabled
                              class="action-btn"
                            >
                              <el-icon><Clock /></el-icon>
                              待发货
                            </el-button>
                            <el-button
                              v-else-if="order.status === 'DELIVERED'"
                              type="success"
                              @click="confirmReceipt(order)"
                              class="action-btn success"
                            >
                              <el-icon><Check /></el-icon>
                              确认收货
                            </el-button>
                            <el-button
                              v-else-if="order.status === 'COMPLETED'"
                              class="action-btn"
                            >
                              <el-icon><CircleCheck /></el-icon>
                              已完成
                            </el-button>
                            <el-button
                              v-else-if="order.status === 'CANCELLED'"
                              class="action-btn"
                            >
                              已取消
                            </el-button>
                          </div>
                        </div>
                      </div>
                    </template>
                    <el-empty v-else description="暂无订单" />
                  </div>
                  <div class="pagination-wrapper" v-if="total > 0">
                    <el-pagination
                      v-model:current-page="currentPage"
                      v-model:page-size="pageSize"
                      :page-sizes="[5, 10, 20, 50]"
                      :total="total"
                      layout="total, sizes, prev, pager, next, jumper"
                      @size-change="handleSizeChange"
                      @current-change="handlePageChange"
                    />
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
                          <div class="order-info-row">
                            <span class="order-id">订单号: {{ order.orderNo }}</span>
                            <span class="order-time">{{ formatDate(order.createdAt) }}</span>
                          </div>
                          <el-tag :type="getStatusType(order.status)" effect="light" class="status-tag">
                            {{ getStatusText(order.status) }}
                          </el-tag>
                        </div>

                        <div class="order-body">
                          <div class="item-main">
                            <div class="item-image" v-if="order.itemIcon">
                              <img :src="getImageUrl(order.itemIcon)" @error="handleImageError($event, order)" />
                            </div>
                            <div class="item-image placeholder" v-else>
                              <el-icon :size="40" color="#c0c4cc"><Goods /></el-icon>
                            </div>
                            <div class="item-details">
                              <h4 class="item-name">{{ order.itemName || '商品 #' + order.itemId }}</h4>
                              <div class="item-meta">
                                <span class="game-tag">{{ order.game || '游戏道具' }}</span>
                              </div>
                            </div>
                          </div>
                          <div class="price-section">
                            <div class="unit-price">¥{{ order.unitPrice }}</div>
                            <div class="quantity">x{{ order.quantity }}</div>
                          </div>
                        </div>

                        <div class="order-footer">
                          <div class="total-row">
                            <span class="total-label">合计:</span>
                            <span class="total-amount">¥{{ order.totalAmount }}</span>
                          </div>
                          <div class="actions">
                            <el-button
                              v-if="order.status === 'CREATED'"
                              disabled
                              class="action-btn"
                            >
                              <el-icon><Clock /></el-icon>
                              等待支付
                            </el-button>
                            <el-button
                              v-else-if="order.status === 'PAID'"
                              type="primary"
                              @click="deliverOrder(order)"
                              class="action-btn primary"
                            >
                              <el-icon><Upload /></el-icon>
                              发货
                            </el-button>
                            <el-button
                              v-else-if="order.status === 'DELIVERED'"
                              disabled
                              class="action-btn"
                            >
                              <el-icon><Clock /></el-icon>
                              待确认收货
                            </el-button>
                            <el-button
                              v-else-if="order.status === 'COMPLETED'"
                              class="action-btn"
                            >
                              <el-icon><CircleCheck /></el-icon>
                              已完成
                            </el-button>
                            <el-button
                              v-else-if="order.status === 'CANCELLED'"
                              class="action-btn"
                            >
                              已取消
                            </el-button>
                          </div>
                        </div>
                      </div>
                    </template>
                    <el-empty v-else description="暂无订单" />
                  </div>
                  <div class="pagination-wrapper" v-if="total > 0">
                    <el-pagination
                      v-model:current-page="currentPage"
                      v-model:page-size="pageSize"
                      :page-sizes="[5, 10, 20, 50]"
                      :total="total"
                      layout="total, sizes, prev, pager, next, jumper"
                      @size-change="handleSizeChange"
                      @current-change="handlePageChange"
                    />
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
import { List, ShoppingCart, Sell, Wallet, Clock, Check, View, Upload, Goods, CircleCheck } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const orders = ref([])
const activeStatus = ref('all')
const orderRole = ref('buyer')
const isLoading = ref(false)
const itemCache = new Map()
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const orderStatuses = [
  { value: 'all', label: '全部' },
  { value: 'CREATED', label: '待支付' },
  { value: 'PAID', label: '待发货' },
  { value: 'DELIVERED', label: '待收货' },
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
    'CREATED': '待支付',
    'PAID': '待发货',
    'DELIVERED': '待收货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

function getStatusType(status) {
  const typeMap = {
    'CREATED': 'warning',
    'PAID': 'primary',
    'DELIVERED': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

function formatDate(dateValue) {
  if (!dateValue) return '未知'
  try {
    const date = new Date(dateValue)
    if (isNaN(date.getTime())) return '未知'
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return '未知'
  }
}

function getImageUrl(iconUrl) {
  if (!iconUrl) return ''
  if (iconUrl.startsWith('http') || iconUrl.startsWith('/')) {
    return iconUrl
  }
  const filename = iconUrl.split(/[/\\]/).pop()
  return `/api/uploads/img/${filename}`
}

function handleTabChange() {}

function handleRoleChange() {
  activeStatus.value = 'all'
  currentPage.value = 1
  fetchOrders()
}

function handlePageChange() {
  fetchOrders()
}

function handleSizeChange() {
  currentPage.value = 1
  fetchOrders()
}

async function fetchItemDetails(itemId) {
  if (itemCache.has(itemId)) {
    return itemCache.get(itemId)
  }
  try {
    const response = await request(`/item/${itemId}`, { method: 'GET' })
    const item = response.data || null
    itemCache.set(itemId, item)
    return item
  } catch {
    return null
  }
}

function handleImageError(event, order) {
  event.target.style.display = 'none'
  event.target.parentElement.classList.add('placeholder')
}

async function deliverOrder(order) {
  try {
    await ElMessageBox.confirm(`确定要给订单 ${order.orderNo} 发货吗？`, '发货确认', {
      confirmButtonText: '确认发货',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await request(`/trade/orders/${order.id}/deliver?id=${userStore.userId}`, {
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

async function payOrder(order) {
  try {
    await ElMessageBox.confirm(`确定要支付订单 ${order.orderNo} 吗？`, '支付确认', {
      confirmButtonText: '确认支付',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await request(`/trade/orders/${order.id}/pay?id=${userStore.userId}`, {
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

async function confirmReceipt(order) {
  try {
    await ElMessageBox.confirm(`确定已收到订单 ${order.orderNo} 的商品吗？`, '确认收货', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await request(`/trade/orders/${order.id}/complete?id=${userStore.userId}`, {
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

async function fetchOrders() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  let currentUserId = userStore.userId
  console.log('初始 userId:', currentUserId)
  console.log('localStorage userId:', localStorage.getItem('userId'))
  
  if (!currentUserId) {
    try {
      const res = await request('/user/auth/current', { method: 'GET' })
      console.log('/user/auth/current 响应:', res)
      if (res.data?.userId) {
        userStore.userId = res.data.userId
        localStorage.setItem('userId', res.data.userId)
        currentUserId = res.data.userId
      }
    } catch (e) {
      console.error('获取用户信息失败:', e)
    }
  }

  console.log('最终使用的 userId:', currentUserId)

  if (!currentUserId) {
    ElMessage.error('无法获取用户信息，请重新登录')
    return
  }

  isLoading.value = true
  try {
    let response
    const pageParams = `?page=${currentPage.value - 1}&size=${pageSize.value}`
    const url = orderRole.value === 'buyer'
      ? `/trade/orders/buyer/${currentUserId}${pageParams}`
      : `/trade/orders/seller/${currentUserId}${pageParams}`
    console.log('请求订单列表:', url)
    response = await request(url, { method: 'GET' })
    console.log('订单响应:', response)

    total.value = response.data?.totalElements || 0
    const orderList = response.data?.content || []

    const ordersWithItem = await Promise.all(
      orderList.map(async (order) => {
        const item = await fetchItemDetails(order.itemId)
        return {
          ...order,
          itemName: item?.name || '',
          itemIcon: item?.iconUrl || '',
          game: item?.game || ''
        }
      })
    )

    orders.value = ordersWithItem
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

.tab-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
}

.status-tabs :deep(.el-tabs__item) {
  font-size: 14px;
  padding: 0 16px;
  height: 36px;
  line-height: 36px;
}

.status-tabs :deep(.el-tabs__nav) {
  height: 36px;
}

.order-list {
  min-height: 200px;
}

.order-item-card {
  background: white;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.order-item-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-info-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-id {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  font-family: monospace;
}

.order-time {
  font-size: 12px;
  color: #909399;
}

.status-tag {
  border-radius: 20px;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.item-main {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.item-details {
  flex: 1;
  min-width: 0;
}

.item-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-meta {
  display: flex;
  gap: 8px;
}

.game-tag {
  display: inline-block;
  padding: 2px 10px;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  color: #667eea;
  font-size: 12px;
  border-radius: 10px;
}

.price-section {
  text-align: right;
  flex-shrink: 0;
  margin-left: 20px;
}

.unit-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}

.quantity {
  font-size: 14px;
  color: #909399;
  text-align: right;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.total-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.total-label {
  font-size: 14px;
  color: #606266;
}

.total-amount {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
}

.actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  border-radius: 20px;
  padding: 8px 20px;
  font-size: 14px;
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
}

.action-btn.primary:hover {
  opacity: 0.9;
}

.action-btn.success {
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
  border: none;
  color: white;
}

.action-btn.success:hover {
  opacity: 0.9;
}

@media (max-width: 768px) {
  .order-body {
    flex-direction: column;
    align-items: flex-start;
  }

  .price-section {
    margin-left: 0;
    margin-top: 12px;
    text-align: left;
  }

  .order-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .actions {
    width: 100%;
    justify-content: flex-end;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 16px 0;
}
</style>
