<template>
  <div class="orders-page">
    <div class="page-header">
      <h2>我的订单</h2>
      <p class="subtitle">查看和管理您的交易订单</p>
    </div>

    <el-card class="orders-card">
      <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
        <el-tab-pane 
          v-for="status in orderStatuses" 
          :key="status.value" 
          :label="status.label" 
          :name="status.value"
        >
          <div v-loading="isLoading" class="order-list">
            <template v-if="filteredOrders.length > 0">
              <el-card 
                v-for="order in filteredOrders" 
                :key="order.id" 
                class="order-item-card" 
                shadow="hover"
              >
                <template #header>
                  <div class="order-header">
                    <span class="order-id">订单号: {{ order.id }}</span>
                    <el-tag :type="getStatusType(order.status)" effect="light">
                      {{ getStatusText(order.status) }}
                    </el-tag>
                  </div>
                </template>
                
                <div class="order-body">
                  <div class="item-info">
                    <h4 class="item-name">{{ order.itemName }}</h4>
                    <p class="item-desc">{{ order.itemDesc }}</p>
                  </div>
                  <div class="item-price-qty">
                    <div class="price">¥{{ order.unitPrice.toFixed(2) }}</div>
                    <div class="quantity">x{{ order.quantity }}</div>
                  </div>
                </div>
                
                <div class="order-summary">
                  <el-descriptions :column="3" size="small" border>
                    <el-descriptions-item label="交易模式">
                      {{ order.mode === 'listing' ? '基于挂牌' : '一口价' }}
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">
                      {{ formatDate(order.createTime) }}
                    </el-descriptions-item>
                    <el-descriptions-item label="总金额">
                      <span class="total-amount">¥{{ (order.unitPrice * order.quantity).toFixed(2) }}</span>
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
                
                <div class="order-actions">
                  <el-button 
                    v-if="order.status === 'PENDING_PAYMENT'" 
                    type="primary" 
                    @click="payOrder(order.id)"
                  >
                    去支付
                  </el-button>
                  <el-button 
                    v-else-if="order.status === 'PENDING_DELIVERY'" 
                    type="warning" 
                    @click="remindDelivery(order.id)"
                  >
                    提醒发货
                  </el-button>
                  <el-button 
                    v-else-if="order.status === 'PENDING_RECEIPT'" 
                    type="success" 
                    @click="confirmReceipt(order.id)"
                  >
                    确认收货
                  </el-button>
                  <el-button 
                    v-else-if="order.status === 'COMPLETED'" 
                    @click="viewOrderDetail(order.id)"
                  >
                    查看详情
                  </el-button>
                  <el-button 
                    v-else 
                    disabled
                  >
                    已完成
                  </el-button>
                </div>
              </el-card>
            </template>
            <el-empty v-else description="暂无订单" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { request } from '../api/client.js'
import { useUserStore } from '../store/user.js'

const router = useRouter()
const userStore = useUserStore()

const orders = ref([])
const activeStatus = ref('all')
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
  // 可以根据需要在这里触发重新加载等操作
}

async function payOrder(orderId) {
  try {
    await ElMessageBox.confirm('确认支付该订单吗？', '提示', {
      confirmButtonText: '确认支付',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 模拟支付接口调用
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('支付失败')
    }
  }
}

function remindDelivery(orderId) {
  ElMessage.success('已提醒卖家发货')
}

async function confirmReceipt(orderId) {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 模拟确认收货接口调用
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
    const response = await request(`/trade/orders?userId=${userStore.userId}`, { method: 'GET' })
    orders.value = response.data || []
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
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.orders-card {
  min-height: 500px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.order-item-card {
  border-radius: 8px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-id {
  font-size: 14px;
  color: #606266;
  font-weight: bold;
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
  color: #303133;
}

.item-desc {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.item-price-qty {
  text-align: right;
  margin-left: 20px;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 4px;
}

.quantity {
  font-size: 14px;
  color: #909399;
}

.order-summary {
  margin-bottom: 16px;
}

.total-amount {
  font-weight: bold;
  color: #f56c6c;
  font-size: 16px;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
