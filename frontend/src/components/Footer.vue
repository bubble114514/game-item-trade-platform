<template>
  <footer class="footer">
    <div class="footer-content">
      <el-row :gutter="40" class="footer-top">
        <el-col :xs="24" :sm="12" :md="6" class="footer-section">
          <h3>关于我们</h3>
          <p>专业的游戏道具交易平台，为玩家提供安全、便捷的交易服务。</p>
          <p>客服热线：400-123-4567</p>
          <p>工作时间：9:00-22:00</p>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" class="footer-section">
          <h3>帮助中心</h3>
          <el-link :underline="false">交易指南</el-link>
          <el-link :underline="false">常见问题</el-link>
          <el-link :underline="false">安全保障</el-link>
          <el-link :underline="false">投诉建议</el-link>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" class="footer-section">
          <h3>商家服务</h3>
          <el-link :underline="false">商家入驻</el-link>
          <el-link :underline="false">商家后台</el-link>
          <el-link :underline="false">营销活动</el-link>
          <el-link :underline="false">结算中心</el-link>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" class="footer-section">
          <h3>关注我们</h3>
          <div class="social-icons">
            <el-button circle><el-icon><ChatDotRound /></el-icon></el-button>
            <el-button circle><el-icon><Message /></el-icon></el-button>
            <el-button circle><el-icon><Share /></el-icon></el-button>
          </div>
        </el-col>
      </el-row>
      <div class="footer-bottom">
        <p>&copy; 2026 游戏道具商城 - 版权所有</p>
        <div class="footer-links">
          <el-link :underline="false">用户协议</el-link>
          <el-link :underline="false">隐私政策</el-link>
          <el-link :underline="false">营业执照</el-link>
          <el-link :underline="false">ICP备案</el-link>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ChatDotRound, Message, Share } from '@element-plus/icons-vue'
</script>

<style scoped>
/* 页脚 */
.footer {
  background: #212529;
  color: #adb5bd;
  padding: 40px 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-top {
  margin-bottom: 40px;
}

.footer-section {
  margin-bottom: 20px;
}

.footer-section h3 {
  color: #409EFF;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.footer-section p {
  margin: 10px 0;
  font-size: 14px;
  line-height: 1.6;
}

.footer-section .el-link {
  display: block;
  color: #adb5bd;
  margin: 8px 0;
  font-size: 14px;
  justify-content: flex-start;
}

.footer-section .el-link:hover {
  color: #409EFF;
}

.social-icons {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

.social-icons .el-button {
  background: #343a40;
  border-color: #343a40;
  color: #adb5bd;
}

.social-icons .el-button:hover {
  background: #409EFF;
  border-color: #409EFF;
  color: white;
}

.footer-bottom {
  border-top: 1px solid #343a40;
  padding-top: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.footer-bottom p {
  margin: 0;
  font-size: 13px;
  color: #6c757d;
}

.footer-bottom .footer-links {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  justify-content: center;
}

.footer-bottom .footer-links .el-link {
  color: #6c757d;
  font-size: 13px;
}

.footer-bottom .footer-links .el-link:hover {
  color: #409EFF;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .footer-content {
    padding: 0 16px;
  }
}

@media (max-width: 768px) {
  .footer-bottom .footer-links {
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }
}
</style>
