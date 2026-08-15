<template>
  <div>
    <el-row :gutter="12" class="stat-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon blue"><el-icon :size="22"><User /></el-icon></div>
          <div><div class="stat-label">联系人总数</div><div class="stat-value">{{ total }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon green"><el-icon :size="22"><Star /></el-icon></div>
          <div><div class="stat-label">主要联系人</div><div class="stat-value">{{ primaryCount }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon orange"><el-icon :size="22"><OfficeBuilding /></el-icon></div>
          <div><div class="stat-label">覆盖客户</div><div class="stat-value">{{ customerCount }}</div></div>
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
          <el-input v-model="query.keyword" placeholder="姓名 / 电话" clearable style="width: 180px" @keyup.enter="load" />
        </el-form-item>
        <el-form-item label="所属客户">
          <el-select v-model="query.customerId" placeholder="全部客户" clearable filterable style="width: 210px">
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="query.position" placeholder="如：采购经理" clearable style="width: 160px" @keyup.enter="load" />
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
            <el-icon class="header-icon"><User /></el-icon> 联系人列表
            <el-tag size="small" effect="plain" round class="ml-8">{{ total }} 条</el-tag>
          </span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增联系人</el-button>
        </div>
      </template>

      <el-table :data="rows" v-loading="loading" stripe border>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column label="联系人" min-width="150">
          <template #default="{ row }">
            <div class="contact-cell">
              <el-avatar :size="34" :style="{ background: avatarColor(row.name) }">{{ row.name.slice(0, 1) }}</el-avatar>
              <div>
                <div class="contact-name">
                  {{ row.name }}
                  <el-tag v-if="row.isPrimary === 1" size="small" type="success" effect="dark" round class="primary-tag">主要</el-tag>
                </div>
                <div class="contact-position">{{ row.position || '未填写职位' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="所属客户" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" @click="$router.push(`/customers/${row.customerId}`)">{{ customerName(row.customerId) }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="电话" width="140">
          <template #default="{ row }">
            <span v-if="row.phone" class="mono">{{ row.phone }}</span>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="邮箱" min-width="170" show-overflow-tooltip>
          <template #default="{ row }">{{ row.email || '-' }}</template>
        </el-table-column>
        <el-table-column label="微信" width="130">
          <template #default="{ row }">{{ row.wechat || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑联系人' : '新增联系人'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="所属客户" required>
          <el-select v-model="form.customerId" filterable style="width: 100%">
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名" required><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="职位"><el-input v-model="form.position" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="微信"><el-input v-model="form.wechat" /></el-form-item>
        <el-form-item label="主要联系人">
          <el-switch v-model="form.isPrimary" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Filter, User, Star, OfficeBuilding } from '@element-plus/icons-vue'
import { contactApi, customerApi } from '@/api/modules'
import type { Contact, Customer } from '@/types'

const loading = ref(false)
const rows = ref<Contact[]>([])
const total = ref(0)
const customers = ref<Customer[]>([])
const query = reactive({ current: 1, size: 10, keyword: '', customerId: undefined as number | undefined, position: '' })
const dialogVisible = ref(false)
const form = ref<Partial<Contact>>({})

const primaryCount = computed(() => rows.value.filter((r) => r.isPrimary === 1).length)
const customerCount = computed(() => new Set(rows.value.map((r) => r.customerId)).size)

async function load() {
  loading.value = true
  try {
    const data = await contactApi.page(query) as any
    rows.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function reset() {
  query.keyword = ''
  query.customerId = undefined
  query.position = ''
  query.current = 1
  load()
}

function openDialog(row?: Contact) {
  form.value = row ? { ...row } : {}
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.customerId || !form.value.name) {
    ElMessage.warning('请填写客户与姓名')
    return
  }
  if (form.value.id) {
    await contactApi.update(form.value.id, form.value)
  } else {
    await contactApi.create(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function remove(row: Contact) {
  await ElMessageBox.confirm(`确认删除联系人「${row.name}」？`, '警告', { type: 'warning' })
  await contactApi.remove(row.id!)
  ElMessage.success('删除成功')
  load()
}

function customerName(customerId: number) {
  return customers.value.find((c) => c.id === customerId)?.name || `客户#${customerId}`
}

const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#9264f0', '#00b4a8']
function avatarColor(name: string) {
  let hash = 0
  for (const ch of name) hash = (hash * 31 + ch.charCodeAt(0)) % 997
  return colors[hash % colors.length]
}

onMounted(async () => {
  load()
  const data = await customerApi.page({ current: 1, size: 100 }) as any
  customers.value = data.records
})
</script>

<style scoped>
.contact-cell { display: flex; align-items: center; gap: 10px; }
.contact-name { font-weight: 600; }
.primary-tag { margin-left: 4px; }
.contact-position { color: var(--color-muted-foreground); font-size: 12px; }
.search-hint { font-size: 12px; color: var(--color-muted-foreground); font-weight: 400; }
</style>
