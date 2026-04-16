<template>
  <div class="user-center-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-icon :size="40" color="#fff"><User /></el-icon>
        </div>
        <div class="header-text">
          <h2>用户中心</h2>
          <p>管理您的商品和个人信息</p>
        </div>
      </div>
    </div>

    <div class="content-container">
      <el-row :gutter="24">
        <el-col :xs="24" :md="6">
          <el-card class="user-info-card" shadow="never">
            <div class="user-info">
              <div class="user-avatar">
                <el-icon :size="48" color="#667eea"><UserFilled /></el-icon>
              </div>
              <h3 class="username">{{ userStore.nickname || userStore.userId }}</h3>
              <p class="user-id">用户ID: {{ userStore.userId }}</p>
              <el-tag type="primary" effect="light">普通会员</el-tag>
            </div>
          </el-card>

          <el-card class="menu-card" shadow="never">
            <el-menu :default-active="activeMenu" class="user-menu">
              <el-menu-item index="listings" @click="activeMenu = 'listings'">
                <el-icon><Goods /></el-icon>
                <span>我的商品</span>
              </el-menu-item>
              <el-menu-item index="orders" @click="$router.push('/orders')">
                <el-icon><List /></el-icon>
                <span>我的订单</span>
              </el-menu-item>
              <el-menu-item index="sell" @click="$router.push('/sell')">
                <el-icon><Upload /></el-icon>
                <span>上架新商品</span>
              </el-menu-item>
            </el-menu>
          </el-card>
        </el-col>

        <el-col :xs="24" :md="18">
          <el-card class="main-content-card" shadow="never">
            <template #header>
              <div class="card-header">
                <span>我的商品</span>
                <el-button type="primary" size="small" @click="$router.push('/sell')">
                  <el-icon><Plus /></el-icon>
                  上架新商品
                </el-button>
              </div>
            </template>

            <div v-loading="isLoading">
              <el-empty v-if="!isLoading && userItems.length === 0" description="您还没有上架任何商品">
                <el-button type="primary" @click="$router.push('/sell')">立即上架</el-button>
              </el-empty>

              <div v-else class="items-grid">
                <div v-for="item in userItems" :key="item.id" class="item-card">
                  <div class="item-image" @click="$router.push(`/item/${item.id}`)">
                    <img v-if="item.iconUrl" :src="getImageUrl(item.iconUrl)" class="item-img" />
                    <el-icon v-else :size="60" color="#c0c4cc"><Goods /></el-icon>
                    <div class="item-overlay">
                      <span>查看详情</span>
                    </div>
                  </div>
                  <div class="item-info">
                    <h4 class="item-name" @click="$router.push(`/item/${item.id}`)">{{ item.name }}</h4>
                    <div class="item-meta">
                      <el-tag size="small" type="info">{{ item.game }}</el-tag>
                      <el-tag size="small">{{ item.category }}</el-tag>
                    </div>
                    <div class="item-price">
                      <span class="price-value">¥{{ item.referencePrice || 0 }}</span>
                    </div>
                    <div class="item-actions">
                      <el-button type="primary" size="small" @click="editItem(item)">
                        <el-icon><Edit /></el-icon>
                        编辑
                      </el-button>
                      <el-button type="danger" size="small" @click="deleteItem(item)">
                        <el-icon><Delete /></el-icon>
                        删除
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-dialog v-model="editDialogVisible" title="编辑商品" width="600px" class="edit-dialog">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入商品名称" />
        </el-form-item>

        <el-form-item label="所属游戏" prop="game">
          <el-select v-model="editForm.game" placeholder="请选择游戏" clearable>
            <el-option label="热血传奇" value="热血传奇" />
            <el-option label="魔兽世界" value="魔兽世界" />
            <el-option label="英雄联盟" value="英雄联盟" />
            <el-option label="王者荣耀" value="王者荣耀" />
          </el-select>
        </el-form-item>

        <el-form-item label="商品分类" prop="category">
          <el-select v-model="editForm.category" placeholder="请选择分类" clearable>
            <el-option label="消耗品" value="消耗品" />
            <el-option label="装备" value="装备" />
            <el-option label="宠物" value="宠物" />
            <el-option label="坐骑" value="坐骑" />
            <el-option label="材料" value="材料" />
          </el-select>
        </el-form-item>

        <el-form-item label="参考价格" prop="referencePrice">
          <el-input-number v-model="editForm.referencePrice" :min="0" :precision="2" :step="1" />
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>

        <el-form-item label="商品图片">
          <div class="edit-upload-container">
            <el-upload
              class="edit-avatar-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="beforeImageUpload"
              :http-request="handleImageUpload"
            >
              <img v-if="editImageUrl" :src="getUploadImageUrl(editImageUrl)" class="edit-avatar" />
              <div v-else class="edit-upload-placeholder">
                <el-icon :size="32" color="#c0c4cc"><Plus /></el-icon>
                <span>更换图片</span>
              </div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="isSubmitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { request } from '../api/client.js'
import { useUserStore } from '../store/user.js'
import { User, UserFilled, Goods, List, Upload, Plus, Edit, Delete } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = ref('listings')
const isLoading = ref(false)
const isSubmitting = ref(false)
const isUploading = ref(false)
const userItems = ref([])

const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editImageUrl = ref('')
const editingItem = ref(null)

const editForm = reactive({
  name: '',
  game: '',
  category: '',
  referencePrice: 0,
  description: '',
  iconUrl: ''
})

const editRules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 1, max: 50, message: '商品名称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  game: [
    { required: true, message: '请选择所属游戏', trigger: 'change' }
  ],
  category: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ]
}

function getImageUrl(iconUrl) {
  if (!iconUrl) return ''
  const filename = iconUrl.split(/[/\\]/).pop()
  return `/api/uploads/img/${filename}`
}

function getUploadImageUrl(iconUrl) {
  if (!iconUrl) return ''
  if (iconUrl.startsWith('http') || iconUrl.startsWith('/')) {
    return iconUrl
  }
  const filename = iconUrl.split(/[/\\]/).pop()
  return `/api/uploads/img/${filename}`
}

function beforeImageUpload(file) {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

async function handleImageUpload(options) {
  const { file } = options
  try {
    const formData = new FormData()
    formData.append('file', file)

    const response = await request('/upload', {
      method: 'POST',
      body: formData
    })

    if (response.data) {
      editImageUrl.value = response.data
      editForm.iconUrl = response.data
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败，请稍后重试')
  }
}

async function fetchUserItems() {
  if (!userStore.userId) return

  isLoading.value = true
  try {
    const response = await request(`/item/seller/${userStore.userId}/listings`, { method: 'GET' })
    const listings = response.data?.content || []

    const itemsMap = new Map()
    for (const listing of listings) {
      if (!itemsMap.has(listing.itemId)) {
        try {
          const itemResponse = await request(`/item/${listing.itemId}`, { method: 'GET' })
          if (itemResponse.data) {
            itemsMap.set(listing.itemId, {
              ...itemResponse.data,
              listingId: listing.id,
              listingPrice: listing.price,
              listingQuantity: listing.quantity,
              listingStatus: listing.status
            })
          }
        } catch (e) {
          console.error('获取商品详情失败:', e)
        }
      }
    }
    userItems.value = Array.from(itemsMap.values())
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    isLoading.value = false
  }
}

function editItem(item) {
  editingItem.value = item
  editForm.name = item.name || ''
  editForm.game = item.game || ''
  editForm.category = item.category || ''
  editForm.referencePrice = item.referencePrice || 0
  editForm.description = item.description || ''
  editForm.iconUrl = item.iconUrl || ''
  editImageUrl.value = item.iconUrl || ''
  editDialogVisible.value = true
}

async function submitEdit() {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    isSubmitting.value = true

    const itemData = {
      name: editForm.name,
      game: editForm.game,
      category: editForm.category,
      referencePrice: editForm.referencePrice,
      description: editForm.description,
      iconUrl: editForm.iconUrl
    }

    await request(`/item/${editingItem.value.id}`, {
      method: 'PUT',
      body: JSON.stringify(itemData)
    })

    ElMessage.success('商品信息修改成功!')
    editDialogVisible.value = false
    fetchUserItems()
  } catch (error) {
    console.error('修改失败:', error)
    ElMessage.error('修改失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

async function deleteItem(item) {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品「${item.name}」吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await request(`/item/listings/${item.listingId}/cancel?sellerId=${userStore.userId}`, {
      method: 'POST'
    })

    ElMessage.success('商品删除成功')
    fetchUserItems()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  fetchUserItems()
})
</script>

<style scoped>
.user-center-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px 0 60px;
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

.content-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.user-info-card {
  border-radius: 16px;
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 10px 0;
}

.user-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.username {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.user-id {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #909399;
}

.menu-card {
  border-radius: 16px;
}

.user-menu {
  border: none;
}

.user-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  border-radius: 8px;
  margin-bottom: 4px;
}

.user-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  color: #667eea;
}

.main-content-card {
  border-radius: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.item-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.item-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
}

.item-image {
  position: relative;
  width: 100%;
  height: 160px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.item-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.item-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.item-image:hover .item-overlay {
  opacity: 1;
}

.item-overlay span {
  color: white;
  font-size: 14px;
  padding: 8px 16px;
  background: rgba(102, 126, 234, 0.8);
  border-radius: 20px;
}

.item-info {
  padding: 16px;
}

.item-name {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-name:hover {
  color: #667eea;
}

.item-meta {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}

.item-price {
  margin-bottom: 12px;
}

.price-value {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}

.item-actions {
  display: flex;
  gap: 8px;
}

.item-actions .el-button {
  flex: 1;
  border-radius: 6px;
}

.edit-dialog :deep(.el-dialog__body) {
  padding-top: 20px;
}

.edit-upload-container {
  display: flex;
  align-items: center;
}

.edit-avatar-uploader {
  width: 120px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-avatar-uploader:hover {
  border-color: #667eea;
}

.edit-upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #909399;
  font-size: 12px;
}

.edit-avatar {
  width: 100%;
  height: 100%;
  object-fit: contain;
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

  .items-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .item-info {
    padding: 12px;
  }

  .item-actions {
    flex-direction: column;
  }
}
</style>
