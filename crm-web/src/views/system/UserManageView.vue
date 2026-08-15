<template>
  <div>
    <el-row :gutter="12" class="stat-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon blue"><el-icon :size="22"><User /></el-icon></div>
          <div><div class="stat-label">用户总数</div><div class="stat-value">{{ total }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon green"><el-icon :size="22"><CircleCheck /></el-icon></div>
          <div><div class="stat-label">启用</div><div class="stat-value">{{ activeCount }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon orange"><el-icon :size="22"><UserFilled /></el-icon></div>
          <div><div class="stat-label">销售人数</div><div class="stat-value">{{ salesCount }}</div></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="search-card">
      <template #header>
        <div class="card-header">
          <span><el-icon class="header-icon"><Filter /></el-icon> 筛选条件</span>
          <span class="search-hint">支持回车快速查询</span>
        </div>
      </template>
      <el-form inline class="search-form">
        <el-form-item label="关键字">
          <el-input v-model="query.keyword" placeholder="用户名 / 姓名" clearable style="width: 180px" @keyup.enter="load" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.roleCode" placeholder="全部角色" clearable style="width: 140px">
            <el-option v-for="r in roles" :key="r.roleCode" :label="r.roleName" :value="r.roleCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button :icon="Refresh" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>
            <el-icon class="header-icon"><User /></el-icon> 用户管理
            <el-tag size="small" effect="plain" round class="ml-8">{{ total }} 条</el-tag>
          </span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增用户</el-button>
        </div>
      </template>

      <el-table :data="rows" v-loading="loading" stripe border>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column label="用户" min-width="160">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="34" :style="{ background: avatarColor(row.username) }">{{ (row.realName || row.username).slice(0, 1) }}</el-avatar>
              <div>
                <div class="user-name">{{ row.realName || '-' }}</div>
                <div class="user-username">{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="200">
          <template #default="{ row }">
            <el-tag v-for="code in row.roleCodes" :key="code" size="small" :type="roleTag(code)" effect="light" round class="role-tag">
              {{ roleName(code) }}
            </el-tag>
            <span v-if="!row.roleCodes?.length" class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号" width="140">
          <template #default="{ row }">
            <span v-if="row.phone" class="mono">{{ row.phone }}</span>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="邮箱" min-width="170" show-overflow-tooltip>
          <template #default="{ row }">{{ row.email || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              :disabled="row.username === 'admin'"
              @change="(val: boolean) => toggleStatus(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            <span class="time-text">{{ formatDateTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="warning" @click="openReset(row)">重置密码</el-button>
            <el-button link type="danger" :disabled="row.username === 'admin'" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pager"
        v-model:current-page="query.current"
        v-model:page-size="query.size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10, 20, 50]"
        @change="load"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名" required><el-input v-model="form.username" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item :label="form.id ? '新密码' : '密码'">
          <el-input v-model="form.password" type="password" show-password :placeholder="form.id ? '留空则不修改' : ''" />
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="form.roleCodes">
            <el-checkbox v-for="r in roles" :key="r.roleCode" :value="r.roleCode">{{ r.roleName }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-if="!form.id" label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resetVisible" title="重置密码" width="400px">
      <el-input v-model="resetPassword" type="password" show-password placeholder="输入新密码" />
      <template #footer>
        <el-button @click="resetVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReset">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Filter, User, CircleCheck, UserFilled } from '@element-plus/icons-vue'
import { userApi } from '@/api/modules'
import { formatDateTime } from '@/utils/format'
import type { UserItem } from '@/types'

const loading = ref(false)
const rows = ref<UserItem[]>([])
const total = ref(0)
const roles = ref<Array<{ id: number; roleCode: string; roleName: string }>>([])
const query = reactive({ current: 1, size: 10, keyword: '', roleCode: '', status: undefined as number | undefined })
const dialogVisible = ref(false)
const form = ref<Record<string, any>>({ status: 1, roleCodes: [] })
const resetVisible = ref(false)
const resetTarget = ref<UserItem>()
const resetPassword = ref('')

const activeCount = computed(() => rows.value.filter((r) => r.status === 1).length)
const salesCount = computed(() => rows.value.filter((r) => r.roleCodes.includes('SALES')).length)

async function load() {
  loading.value = true
  try {
    const data = await userApi.page(query) as any
    rows.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function reset() {
  query.keyword = ''
  query.roleCode = ''
  query.status = undefined
  query.current = 1
  load()
}

function openDialog(row?: UserItem) {
  form.value = row
    ? { ...row, password: '', roleCodes: [...row.roleCodes] }
    : { status: 1, roleCodes: [] }
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.username) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (form.value.id) {
    await userApi.update(form.value.id, form.value)
  } else {
    if (!form.value.password) {
      ElMessage.warning('请输入密码')
      return
    }
    await userApi.create(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function toggleStatus(row: UserItem, enabled: boolean) {
  await userApi.updateStatus(row.id, enabled ? 1 : 0)
  ElMessage.success(enabled ? '已启用' : '已停用')
  load()
}

function openReset(row: UserItem) {
  resetTarget.value = row
  resetPassword.value = ''
  resetVisible.value = true
}

async function submitReset() {
  if (!resetPassword.value) {
    ElMessage.warning('请输入新密码')
    return
  }
  await userApi.resetPassword(resetTarget.value!.id, resetPassword.value)
  ElMessage.success('密码已重置')
  resetVisible.value = false
}

async function remove(row: UserItem) {
  await ElMessageBox.confirm(`确认删除用户「${row.username}」？`, '警告', { type: 'warning' })
  await userApi.remove(row.id)
  ElMessage.success('删除成功')
  load()
}

function roleName(code: string) {
  return roles.value.find((r) => r.roleCode === code)?.roleName || code
}

function roleTag(code: string) {
  return code === 'ADMIN' ? 'danger' : code === 'MANAGER' ? 'warning' : code === 'SALES' ? 'success' : 'info'
}

const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#9264f0', '#00b4a8']
function avatarColor(name: string) {
  let hash = 0
  for (const ch of name) hash = (hash * 31 + ch.charCodeAt(0)) % 997
  return colors[hash % colors.length]
}

onMounted(async () => {
  load()
  roles.value = await userApi.roles() as any
})
</script>

<style scoped>
.user-cell { display: flex; align-items: center; gap: 10px; }
.user-name { font-weight: 600; }
.user-username { color: var(--color-muted-foreground); font-size: 12px; }
.role-tag { margin-right: 4px; }
.search-hint { font-size: 12px; color: var(--color-muted-foreground); font-weight: 400; }
</style>
