<template>
  <el-container class="layout">
    <el-aside :width="collapsed ? '64px' : '224px'" class="aside">
      <div class="logo" @click="router.push('/dashboard')">
        <div class="logo-mark">
          <el-icon :size="20"><Platform /></el-icon>
        </div>
        <transition name="logo-fade">
          <span v-show="!collapsed" class="logo-text">云客 CRM</span>
        </transition>
      </div>

      <el-scrollbar class="menu-scroll">
        <el-menu
          :default-active="activeMenu"
          router
          :collapse="collapsed"
          :collapse-transition="false"
          class="side-menu"
        >
          <div v-show="!collapsed" class="menu-group">总览</div>
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>工作台</template>
          </el-menu-item>

          <div v-show="!collapsed" class="menu-group">业务管理</div>
          <el-menu-item index="/customers">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>客户管理</template>
          </el-menu-item>
          <el-menu-item index="/contacts">
            <el-icon><User /></el-icon>
            <template #title>联系人</template>
          </el-menu-item>
          <el-menu-item index="/follow-ups">
            <el-icon><ChatDotRound /></el-icon>
            <template #title>跟进记录</template>
          </el-menu-item>
          <el-menu-item index="/opportunities">
            <el-icon><TrendCharts /></el-icon>
            <template #title>商机管理</template>
          </el-menu-item>

          <template v-if="userStore.isAdmin">
            <div v-show="!collapsed" class="menu-group">系统设置</div>
            <el-menu-item index="/system/users">
              <el-icon><UserFilled /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>
            <el-menu-item index="/system/dict">
              <el-icon><Collection /></el-icon>
              <template #title>字典管理</template>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>

      <div v-show="!collapsed" class="aside-footer">
        <el-tag size="small" effect="plain" round>{{ roleLabel }}</el-tag>
        <span class="version">v0.1.0</span>
      </div>
    </el-aside>

    <el-container class="right">
      <el-header class="header">
        <div class="header-left">
          <el-button text class="collapse-btn" :aria-label="collapsed ? '展开菜单' : '收起菜单'" @click="collapsed = !collapsed">
            <el-icon :size="18"><Expand v-if="collapsed" /><Fold v-else /></el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-tooltip content="刷新当前页">
            <el-button circle text :aria-label="'刷新当前页'" @click="reloadPage">
              <el-icon :size="16"><Refresh /></el-icon>
            </el-button>
          </el-tooltip>
          <el-divider direction="vertical" class="header-divider" />
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info" tabindex="0" role="button" aria-label="用户菜单">
              <el-avatar :size="32" class="avatar">{{ initial }}</el-avatar>
              <span class="user-name">{{ displayName }}</span>
              <el-tag v-if="userStore.isAdmin" size="small" effect="plain" round class="role-tag">管理员</el-tag>
              <el-icon class="caret"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="password">
                  <el-icon><Key /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="$route.fullPath" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>

  <el-dialog v-model="pwdVisible" title="修改密码" width="420px">
    <el-form :model="pwdForm" label-width="90px">
      <el-form-item label="原密码">
        <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pwdVisible = false">取消</el-button>
      <el-button type="primary" @click="submitPassword">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/modules'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const pwdVisible = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '' })

const collapsed = ref(window.innerWidth < 992)
const displayName = computed(() => userStore.user?.realName || userStore.user?.username || '用户')
const initial = computed(() => displayName.value.slice(0, 1))
const roleLabel = computed(() => (userStore.isAdmin ? '系统管理员' : '销售专员'))

const activeMenu = computed(() => {
  const p = route.path
  if (p.startsWith('/customers/')) return '/customers'
  return p
})

function handleCommand(command: string) {
  if (command === 'password') {
    pwdForm.value = { oldPassword: '', newPassword: '' }
    pwdVisible.value = true
  } else if (command === 'logout') {
    authApi.logout().catch(() => {})
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }
}

function reloadPage() {
  window.location.reload()
}

async function submitPassword() {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    ElMessage.warning('请填写完整')
    return
  }
  await authApi.changePassword(pwdForm.value)
  ElMessage.success('密码修改成功，请重新登录')
  userStore.logout()
  router.push('/login')
}

function onResize() {
  collapsed.value = window.innerWidth < 992
}

onMounted(() => window.addEventListener('resize', onResize))
onBeforeUnmount(() => window.removeEventListener('resize', onResize))
</script>

<style scoped>
.layout { height: 100vh; }

.aside {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-right: 1px solid var(--color-border);
  transition: width 0.2s ease;
  overflow: hidden;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 60px;
  padding: 0 16px;
  cursor: pointer;
  border-bottom: 1px solid var(--color-border-lighter, #eef2f8);
  flex-shrink: 0;
  white-space: nowrap;
}

.logo-mark {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(135deg, #2563eb, #60a5fa);
  box-shadow: 0 4px 10px rgba(37, 99, 235, 0.35);
  flex-shrink: 0;
}

.logo-text {
  font-size: 17px;
  font-weight: 700;
  color: var(--color-foreground);
  letter-spacing: 0.02em;
}

.logo-fade-enter-active, .logo-fade-leave-active { transition: opacity 0.15s ease; }
.logo-fade-enter-from, .logo-fade-leave-to { opacity: 0; }

.menu-scroll { flex: 1; }

.side-menu { border-right: none; padding: 8px 10px 16px; }

.menu-group {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  color: #94a3b8;
  padding: 14px 10px 6px;
  text-transform: uppercase;
  white-space: nowrap;
}

.side-menu :deep(.el-menu-item),
.side-menu :deep(.el-sub-menu__title) {
  height: 44px;
  line-height: 44px;
  margin-bottom: 4px;
  border-radius: 10px;
  color: #475569;
  font-weight: 500;
}

.side-menu :deep(.el-menu-item:hover),
.side-menu :deep(.el-sub-menu__title:hover) {
  background: var(--el-color-primary-light-9);
  color: var(--color-primary);
}

.side-menu :deep(.el-menu-item.is-active) {
  background: var(--el-color-primary-light-9);
  color: var(--color-primary);
  font-weight: 600;
  position: relative;
}

.side-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 18px;
  border-radius: 3px;
  background: var(--color-primary);
}

.side-menu :deep(.el-menu) { background: transparent; }

.aside-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-top: 1px solid var(--color-border-lighter, #eef2f8);
  flex-shrink: 0;
}

.version { font-size: 12px; color: #94a3b8; }

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  background: #fff;
  border-bottom: 1px solid var(--color-border);
  padding: 0 20px;
}

.header-left { display: flex; align-items: center; gap: 12px; }

.collapse-btn { padding: 6px; color: #64748b; }

.header-right { display: flex; align-items: center; gap: 6px; }
.header-divider { height: 20px; margin: 0 8px; }

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 10px;
  transition: background 0.15s ease;
  outline: none;
}
.user-info:hover { background: var(--el-color-primary-light-9); }
.user-info:focus-visible { outline: 2px solid var(--color-ring); outline-offset: 2px; }

.avatar {
  background: linear-gradient(135deg, #2563eb, #60a5fa);
  color: #fff;
  font-weight: 600;
}

.user-name { font-size: 14px; font-weight: 500; color: var(--color-foreground); }
.role-tag { margin: 0 2px; }
.caret { color: #94a3b8; font-size: 12px; }

.main {
  background: var(--color-background);
  padding: 20px 22px;
  overflow-y: auto;
}
</style>
