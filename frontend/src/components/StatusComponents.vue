<template>
  <div v-if="loading" class="loading-state">
    <div class="loading-spinner"></div>
    <p>{{ loadingText || '加载中...' }}</p>
  </div>
  <div v-else-if="error" class="error-state">
    <div class="error-icon">⚠️</div>
    <p>{{ error }}</p>
    <button v-if="onRetry" class="btn primary" @click="onRetry">重试</button>
  </div>
  <div v-else-if="empty" class="empty-state">
    <div class="empty-icon">{{ emptyIcon || '📦' }}</div>
    <h3>{{ emptyTitle || '暂无数据' }}</h3>
    <p>{{ emptyMessage || '' }}</p>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: ''
  },
  empty: {
    type: Boolean,
    default: false
  },
  loadingText: {
    type: String,
    default: '加载中...'
  },
  emptyTitle: {
    type: String,
    default: '暂无数据'
  },
  emptyMessage: {
    type: String,
    default: ''
  },
  emptyIcon: {
    type: String,
    default: '📦'
  }
})

const emit = defineEmits(['retry'])

const onRetry = () => {
  emit('retry')
}
</script>

<style scoped>
/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  margin: 20px 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #38bdf8;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  color: #64748b;
  font-size: 16px;
  margin: 0;
}

/* 错误状态 */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: #fef2f2;
  border-radius: 8px;
  border: 1px solid #fecaca;
  text-align: center;
  margin: 20px 0;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-state p {
  color: #991b1b;
  font-size: 16px;
  margin: 0 0 20px 0;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  text-align: center;
  margin: 20px 0;
}

.empty-state .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #94a3b8;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.empty-state p {
  color: #64748b;
  font-size: 16px;
  margin: 0;
}

/* 通用按钮样式 */
.btn {
  padding: 12px 24px;
  border: 0;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.btn:hover::before {
  left: 100%;
}

.btn.primary {
  background: #38bdf8;
  color: #0f172a;
  box-shadow: 0 4px 12px rgba(56, 189, 248, 0.3);
}

.btn.primary:hover:not(:disabled) {
  background: #0ea5e9;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(14, 165, 233, 0.4);
}

.btn.primary:disabled {
  background: #94a3b8;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}
</style>