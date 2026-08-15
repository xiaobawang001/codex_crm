<template>
  <div class="login-page">
    <div class="bg-shape shape-1" />
    <div class="bg-shape shape-2" />
    <div class="bg-shape shape-3" />

    <div class="login-wrap">
      <div class="brand">
        <div class="brand-mark">
          <el-icon :size="26"><Platform /></el-icon>
        </div>
        <h1 class="brand-title">云客 CRM</h1>
        <p class="brand-sub">客户 · 商机 · 跟进，一站式管理</p>
      </div>

      <el-card class="login-card" shadow="never">
        <h2 class="card-title">欢迎回来</h2>
        <p class="card-sub">登录您的账号继续使用</p>

        <el-form :model="form" size="large" @keyup.enter="submit">
          <el-form-item>
            <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" clearable autofocus />
          </el-form-item>
          <el-form-item>
            <el-input v-model="form.password" type="password" placeholder="密码" show-password :prefix-icon="Lock" />
          </el-form-item>
          <el-button type="primary" size="large" class="submit" :loading="loading" @click="submit">
            {{ loading ? '登录中…' : '登 录' }}
          </el-button>
        </el-form>

        <div class="tip-box">
          <el-icon class="tip-icon"><InfoFilled /></el-icon>
          <div class="tip-text">
            <div>演示账号：admin / admin123（管理员）</div>
            <div>销售账号：sales1 / 123456</div>
          </div>
        </div>
      </el-card>

      <p class="copyright">© 2026 云客 CRM · Spring Boot 3 + Vue 3 + PostgreSQL</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, InfoFilled, Platform } from '@element-plus/icons-vue'
import { authApi } from '@/api/modules'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const form = ref({ username: '', password: '' })

async function submit() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await authApi.login(form.value) as { token: string; user: any }
    userStore.setLogin(data.token, data.user)
    ElMessage.success(`欢迎回来，${data.user.realName || data.user.username}`)
    router.push((route.query.redirect as string) || '/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  overflow: hidden;
  background:
    linear-gradient(160deg, #1e3a8a 0%, #2563eb 45%, #3b82f6 75%, #0ea5e9 100%);
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.35;
  pointer-events: none;
}
.shape-1 { width: 420px; height: 420px; background: #60a5fa; top: -120px; left: -100px; }
.shape-2 { width: 360px; height: 360px; background: #0d9488; bottom: -100px; right: -80px; }
.shape-3 { width: 260px; height: 260px; background: #a78bfa; top: 40%; left: 68%; }

.login-wrap {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.brand { text-align: center; color: #fff; }

.brand-mark {
  width: 64px;
  height: 64px;
  margin: 0 auto 14px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(8px);
  box-shadow: 0 12px 32px rgba(2, 6, 23, 0.25);
}

.brand-title {
  margin: 0;
  font-size: 30px;
  font-weight: 800;
  letter-spacing: 0.04em;
  text-shadow: 0 2px 12px rgba(2, 6, 23, 0.25);
}

.brand-sub { margin: 8px 0 0; font-size: 14px; opacity: 0.85; letter-spacing: 0.06em; }

.login-card {
  width: 400px;
  max-width: 92vw;
  padding: 10px 8px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 24px 64px rgba(2, 6, 23, 0.35) !important;
}

.card-title { margin: 8px 0 4px; font-size: 20px; font-weight: 700; text-align: center; }
.card-sub { margin: 0 0 22px; font-size: 13px; color: var(--color-muted-foreground); text-align: center; }

.submit { width: 100%; margin-top: 4px; font-weight: 600; letter-spacing: 0.2em; }

.tip-box {
  display: flex;
  gap: 8px;
  margin-top: 18px;
  padding: 10px 12px;
  border-radius: 10px;
  background: var(--el-color-primary-light-9);
  border: 1px dashed var(--el-color-primary-light-7);
  color: var(--color-muted-foreground);
}
.tip-icon { color: var(--color-primary); flex-shrink: 0; margin-top: 1px; }
.tip-text { font-size: 12px; line-height: 1.7; }

.copyright { margin: 4px 0 0; font-size: 12px; color: rgba(255, 255, 255, 0.75); letter-spacing: 0.04em; }
</style>
