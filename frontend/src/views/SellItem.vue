<template>
  <div class="sell-item-page">
    <div class="page-header">
      <h2>上架商品</h2>
      <p class="subtitle">发布您的游戏道具，开始出售</p>
    </div>

    <div class="form-container">
      <el-card class="sell-form-card">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="商品名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入商品名称" />
          </el-form-item>

          <el-form-item label="所属游戏" prop="game">
            <el-select v-model="form.game" placeholder="请选择游戏" clearable>
              <el-option label="热血传奇" value="热血传奇" />
              <el-option label="魔兽世界" value="魔兽世界" />
              <el-option label="英雄联盟" value="英雄联盟" />
              <el-option label="王者荣耀" value="王者荣耀" />
            </el-select>
          </el-form-item>

          <el-form-item label="商品分类" prop="category">
            <el-select v-model="form.category" placeholder="请选择分类" clearable>
              <el-option label="消耗品" value="消耗品" />
              <el-option label="装备" value="装备" />
              <el-option label="宠物" value="宠物" />
              <el-option label="坐骑" value="坐骑" />
              <el-option label="材料" value="材料" />
            </el-select>
          </el-form-item>

          <el-form-item label="参考价格" prop="referencePrice">
            <el-input-number v-model="form.referencePrice" :min="0" :precision="2" :step="1" placeholder="请输入参考价格" />
          </el-form-item>

          <el-form-item label="出售价格" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" placeholder="请输入出售价格" />
          </el-form-item>

          <el-form-item label="库存数量" prop="quantity">
            <el-input-number v-model="form.quantity" :min="1" :step="1" placeholder="请输入库存数量" />
          </el-form-item>

          <el-form-item label="商品描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入商品描述" />
          </el-form-item>

          <el-form-item label="商品图片" prop="iconUrl">
            <div class="upload-container">
              <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :before-upload="beforeImageUpload"
                :http-request="handleImageUpload"
              >
                <img v-if="imageUrl" :src="getUploadImageUrl(imageUrl)" class="avatar" />
                <div v-else class="upload-placeholder">
                  <el-icon :size="48" color="#c0c4cc"><Plus /></el-icon>
                  <span>点击上传</span>
                </div>
              </el-upload>
              <div class="upload-tip">
                <p>支持 JPG、PNG 格式</p>
                <p>建议尺寸 200x200 像素</p>
              </div>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitForm" :loading="isSubmitting" class="submit-btn">
              确认上架
            </el-button>
            <el-button @click="resetForm" class="reset-btn">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { request } from '../api/client.js'
import { useUserStore } from '../store/user.js'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const isSubmitting = ref(false)
const imageUrl = ref('')
const isUploading = ref(false)

const form = reactive({
  name: '',
  game: '',
  category: '',
  referencePrice: 0,
  price: 0,
  quantity: 1,
  description: '',
  iconUrl: ''
})

const rules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 1, max: 50, message: '商品名称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  game: [
    { required: true, message: '请选择所属游戏', trigger: 'change' }
  ],
  category: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入出售价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '出售价格必须大于 0', trigger: 'blur' }
  ],
  quantity: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '库存数量必须大于 0', trigger: 'blur' }
  ]
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
  isUploading.value = true

  try {
    const formData = new FormData()
    formData.append('file', file)

    const response = await request('/upload', {
      method: 'POST',
      body: formData
    })

    if (response.data) {
      imageUrl.value = response.data
      form.iconUrl = response.data
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败，请稍后重试')
  } finally {
    isUploading.value = false
  }
}

async function submitForm() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录再上架商品')
    router.push('/login')
    return
  }

  if (!formRef.value) return

  try {
    await formRef.value.validate()
    isSubmitting.value = true

    const itemData = {
      name: form.name,
      game: form.game,
      category: form.category,
      referencePrice: form.referencePrice,
      description: form.description,
      iconUrl: form.iconUrl
    }

    const itemResponse = await request('/item', {
      method: 'POST',
      body: JSON.stringify(itemData)
    })

    if (itemResponse.data && itemResponse.data.id) {
      const listingData = {
        itemId: itemResponse.data.id,
        sellerId: Number(userStore.userId),
        price: form.price,
        quantity: form.quantity,
        description: form.description
      }

      await request('/item/listings', {
        method: 'POST',
        body: JSON.stringify(listingData)
      })

      ElMessage.success('商品上架成功!')
      router.push('/listings')
    }
  } catch (error) {
    console.error('上架失败:', error)
    ElMessage.error('上架失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

function resetForm() {
  formRef.value.resetFields()
  imageUrl.value = ''
  form.iconUrl = ''
}
</script>

<style scoped>
.sell-item-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px 0 60px;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.page-header .subtitle {
  margin: 0;
  font-size: 15px;
  opacity: 0.9;
}

.form-container {
  max-width: 720px;
  margin: 0 auto;
  padding: 0 24px;
}

.sell-form-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #667eea;
}

:deep(.el-textarea__inner) {
  border-radius: 8px;
}

:deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px #667eea;
}

:deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #667eea;
}

.upload-container {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.avatar-uploader {
  border: 2px dashed #d9d9d9;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.avatar-uploader:hover {
  border-color: #667eea;
}

.avatar-uploader :deep(.el-upload) {
  width: 140px;
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 140px;
  height: 140px;
  object-fit: contain;
  border-radius: 10px;
}

.upload-placeholder {
  width: 140px;
  height: 140px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  border-radius: 10px;
  gap: 8px;
}

.upload-placeholder span {
  font-size: 13px;
  color: #909399;
}

.upload-tip {
  padding-top: 8px;
}

.upload-tip p {
  margin: 0 0 6px 0;
  font-size: 13px;
  color: #909399;
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  padding: 12px 36px;
  font-weight: 500;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #5a71d2 0%, #6a4190 100%);
}

.reset-btn {
  border-radius: 8px;
  margin-left: 12px;
}

:deep(.el-select) {
  width: 100%;
}

@media (max-width: 768px) {
  .page-header h2 {
    font-size: 22px;
  }

  .upload-container {
    flex-direction: column;
    align-items: center;
  }

  .upload-tip {
    text-align: center;
  }
}
</style>