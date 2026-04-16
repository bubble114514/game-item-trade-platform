<template>
  <footer class="footer">
    <div class="footer-top">
      <div class="footer-content">
        <el-row :gutter="40" class="footer-grid">
          <el-col :xs="24" :sm="12" :md="6" class="footer-section">
            <div class="section-brand">
              <div class="brand-icon">
                <el-icon :size="28" color="#667eea"><Shop /></el-icon>
              </div>
              <h3>道具商城</h3>
            </div>
            <p class="brand-desc">专业的游戏道具交易平台，为玩家提供安全、便捷的交易服务。</p>
            <div class="social-icons">
              <el-button circle class="social-btn"><el-icon><ChatDotRound /></el-icon></el-button>
              <el-button circle class="social-btn"><el-icon><Message /></el-icon></el-button>
              <el-button circle class="social-btn"><el-icon><Share /></el-icon></el-button>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" class="footer-section">
            <h3>
              <el-icon><Service /></el-icon>
              帮助中心
            </h3>
            <ul class="link-list">
              <li><el-link :underline="false">交易指南</el-link></li>
              <li><el-link :underline="false">常见问题</el-link></li>
              <li><el-link :underline="false">安全保障</el-link></li>
              <li><el-link :underline="false">投诉建议</el-link></li>
            </ul>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" class="footer-section">
            <h3>
              <el-icon><Shop /></el-icon>
              商家服务
            </h3>
            <ul class="link-list">
              <li><el-link :underline="false">商家入驻</el-link></li>
              <li><el-link :underline="false">商家后台</el-link></li>
              <li><el-link :underline="false">营销活动</el-link></li>
              <li><el-link :underline="false">结算中心</el-link></li>
            </ul>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6" class="footer-section">
            <h3>
              <el-icon><Phone /></el-icon>
              联系我们
            </h3>
            <div class="contact-info">
              <div class="contact-item">
                <span class="contact-label">客服热线</span>
                <span class="contact-value">400-123-4567</span>
              </div>
              <div class="contact-item">
                <span class="contact-label">工作时间</span>
                <span class="contact-value">9:00 - 22:00</span>
              </div>
              <div class="contact-item">
                <span class="contact-label">邮箱</span>
                <span class="contact-value">support@gameitem.com</span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <div class="footer-bottom">
      <div class="footer-content">
        <p class="copyright">&copy; 2026 游戏道具商城 - 专业的游戏道具交易平台</p>
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
import { ChatDotRound, Message, Share, Service, Shop, Phone } from '@element-plus/icons-vue'
</script>

<style scoped>
.footer {
  background: #1a1a2e;
  color: #a0a0a0;
  margin-top: auto;
}

.footer-top {
  padding: 60px 0 40px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
}

.footer-content {
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-grid {
  width: 100%;
}

.footer-section {
  margin-bottom: 24px;
}

.section-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.brand-icon {
  width: 48px;
  height: 48px;
  background: rgba(102, 126, 234, 0.15);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.section-brand h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #fff;
}

.brand-desc {
  margin: 0 0 20px 0;
  font-size: 14px;
  line-height: 1.7;
  color: #888;
}

.social-icons {
  display: flex;
  gap: 10px;
}

.social-btn {
  width: 36px;
  height: 36px;
  background: rgba(102, 126, 234, 0.15);
  border: none;
  border-radius: 10px;
  color: #667eea;
  transition: all 0.3s ease;
}

.social-btn:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  transform: translateY(-3px);
}

.footer-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #667eea;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 600;
}

.link-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.link-list li {
  margin-bottom: 10px;
}

.link-list .el-link {
  color: #888;
  font-size: 14px;
  transition: all 0.3s ease;
  justify-content: flex-start;
}

.link-list .el-link:hover {
  color: #667eea;
  padding-left: 6px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.contact-label {
  font-size: 12px;
  color: #666;
}

.contact-value {
  font-size: 14px;
  color: #a0a0a0;
}

.footer-bottom {
  padding: 20px 0;
  background: #0f0f1a;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.footer-bottom .footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  text-align: center;
}

.copyright {
  margin: 0;
  font-size: 13px;
  color: #666;
}

.footer-links {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
}

.footer-links .el-link {
  color: #666;
  font-size: 12px;
  transition: all 0.3s ease;
}

.footer-links .el-link:hover {
  color: #667eea;
}

@media (max-width: 768px) {
  .footer-top {
    padding: 40px 0 30px;
  }

  .section-brand {
    margin-bottom: 12px;
  }

  .brand-desc {
    margin-bottom: 16px;
  }

  .footer-links {
    gap: 12px;
  }
}
</style>
