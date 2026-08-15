<template>
  <div>
    <el-row :gutter="12" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon blue"><el-icon :size="22"><OfficeBuilding /></el-icon></div>
          <div><div class="stat-label">客户总数</div><div class="stat-value">{{ summary.customerTotal ?? '-' }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon green"><el-icon :size="22"><TrendCharts /></el-icon></div>
          <div><div class="stat-label">本月新增</div><div class="stat-value">{{ summary.customerMonthNew ?? '-' }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon orange"><el-icon :size="22"><User /></el-icon></div>
          <div><div class="stat-label">我的客户</div><div class="stat-value">{{ myCount }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon purple"><el-icon :size="22"><Search /></el-icon></div>
          <div><div class="stat-label">当前筛选</div><div class="stat-value">{{ total }}</div></div>
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
          <el-input v-model="query.keyword" placeholder="客户名称 / 电话" clearable style="width: 170px" @keyup.enter="load" />
        </el-form-item>
        <el-form-item label="行业">
          <el-select v-model="query.industry" placeholder="全部行业" clearable style="width: 130px">
            <el-option v-for="i in dict.byType('industry')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源">
          <el-select v-model="query.source" placeholder="全部来源" clearable style="width: 130px">
            <el-option v-for="i in dict.byType('source')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="query.level" placeholder="全部等级" clearable style="width: 110px">
            <el-option v-for="i in dict.byType('level')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option v-for="i in dict.byType('customer_status')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="userStore.isManager" label="负责人">
          <el-select v-model="query.ownerId" placeholder="全部负责人" clearable filterable style="width: 130px">
            <el-option v-for="u in users" :key="u.id" :label="u.realName || u.username" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker v-model="createRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 250px" />
        </el-form-item>
        <el-form-item>
          <el-radio-group v-model="onlyMine" @change="load">
            <el-radio-button :value="false">全部</el-radio-button>
            <el-radio-button :value="true">我的客户</el-radio-button>
          </el-radio-group>
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
            <el-icon class="header-icon"><OfficeBuilding /></el-icon> 客户列表
            <el-tag size="small" effect="plain" round class="ml-8">{{ total }} 条</el-tag>
          </span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增客户</el-button>
        </div>
      </template>

      <el-table :data="rows" v-loading="loading" stripe border>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column label="客户名称" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="customer-cell">
              <el-avatar :size="30" class="customer-avatar">{{ row.name.slice(0, 1) }}</el-avatar>
              <el-link type="primary" @click="$router.push(`/customers/${row.id}`)">{{ row.name }}</el-link>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.level" size="small" effect="dark" :type="levelTag(row.level)" round>{{ row.level }}</el-tag>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="statusTag(row.status)" effect="light">{{ dict.labelOf('customer_status', row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="行业" width="110">
          <template #default="{ row }">
            <el-tag size="small" type="info" effect="plain">{{ dict.labelOf('industry', row.industry) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="来源" width="100">
          <template #default="{ row }">{{ dict.labelOf('source', row.source) }}</template>
        </el-table-column>
        <el-table-column label="电话" width="130">
          <template #default="{ row }">
            <span v-if="row.phone" class="mono">{{ row.phone }}</span>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="负责人" width="110">
          <template #default="{ row }">
            <span v-if="row.ownerId" class="owner-cell">
              <el-avatar :size="22" class="owner-avatar">{{ ownerName(row.ownerId).slice(0, 1) }}</el-avatar>
              {{ ownerName(row.ownerId) }}
            </span>
            <el-tag v-else size="small" type="warning" effect="dark" round>公海</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            <span class="time-text">{{ formatDateTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push(`/customers/${row.id}`)">详情</el-button>
            <el-button v-if="!row.ownerId" link type="success" @click="handleClaim(row)">领取</el-button>
            <el-button link type="warning" @click="openDialog(row)">编辑</el-button>
            <el-button v-if="userStore.isManager" link type="info" @click="openAssign(row)">分配</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑客户' : '新增客户'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="客户名称" required><el-input v-model="form.name" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="行业">
              <el-select v-model="form.industry" clearable style="width: 100%">
                <el-option v-for="i in dict.byType('industry')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源">
              <el-select v-model="form.source" clearable style="width: 100%">
                <el-option v-for="i in dict.byType('source')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="等级">
              <el-select v-model="form.level" clearable style="width: 100%">
                <el-option v-for="i in dict.byType('level')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option v-for="i in dict.byType('customer_status')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assignVisible" title="分配客户" width="400px">
      <el-select v-model="assignUserId" placeholder="选择目标用户" style="width: 100%">
        <el-option v-for="u in users" :key="u.id" :label="u.realName || u.username" :value="u.id" />
      </el-select>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Filter, OfficeBuilding, TrendCharts, User } from '@element-plus/icons-vue'
import { customerApi, userApi, dashboardApi } from '@/api/modules'
import { useDictStore } from '@/stores/dict'
import { useUserStore } from '@/stores/user'
import { formatDateTime } from '@/utils/format'
import type { Customer } from '@/types'

const dict = useDictStore()
const userStore = useUserStore()
const loading = ref(false)
const rows = ref<Customer[]>([])
const total = ref(0)
const myCount = ref(0)
const summary = ref<Record<string, number>>({})
const users = ref<any[]>([])
const onlyMine = ref(false)
const createRange = ref<string[]>([])
const query = reactive({ current: 1, size: 10, keyword: '', status: '', source: '', industry: '', level: '', ownerId: undefined as number | undefined })

const dialogVisible = ref(false)
const assignVisible = ref(false)
const assignUserId = ref<number>()
const assignTarget = ref<Customer>()
const form = ref<Partial<Customer>>({ status: 'potential' })

async function load() {
  loading.value = true
  try {
    const data = await customerApi.page({
      ...query,
      onlyMine: onlyMine.value,
      createTimeStart: createRange.value?.[0],
      createTimeEnd: createRange.value?.[1],
    }) as any
    rows.value = data.records
    total.value = data.total
    if (onlyMine.value) {
      myCount.value = data.total
    } else {
      myCount.value = (await customerApi.page({ current: 1, size: 1, onlyMine: true }) as any).total
    }
  } finally {
    loading.value = false
  }
}

function reset() {
  query.keyword = ''
  query.status = ''
  query.source = ''
  query.industry = ''
  query.level = ''
  query.ownerId = undefined
  createRange.value = []
  onlyMine.value = false
  query.current = 1
  load()
}

function openDialog(row?: Customer) {
  form.value = row ? { ...row } : { status: 'potential' }
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.name) {
    ElMessage.warning('请输入客户名称')
    return
  }
  if (form.value.id) {
    await customerApi.update(form.value.id, form.value)
  } else {
    await customerApi.create(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function handleClaim(row: Customer) {
  await ElMessageBox.confirm(`确认领取客户「${row.name}」？`, '提示', { type: 'warning' })
  await customerApi.claim(row.id!)
  ElMessage.success('领取成功')
  load()
}

async function handleDelete(row: Customer) {
  await ElMessageBox.confirm(`确认删除客户「${row.name}」？`, '警告', { type: 'warning' })
  await customerApi.remove(row.id!)
  ElMessage.success('删除成功')
  load()
}

async function openAssign(row: Customer) {
  assignTarget.value = row
  assignUserId.value = undefined
  assignVisible.value = true
}

async function submitAssign() {
  if (!assignUserId.value) {
    ElMessage.warning('请选择用户')
    return
  }
  await customerApi.assign(assignTarget.value!.id!, assignUserId.value)
  ElMessage.success('分配成功')
  assignVisible.value = false
  load()
}

function levelTag(level: string) {
  return level === 'A' ? 'danger' : level === 'B' ? 'warning' : 'info'
}

function statusTag(status?: string) {
  return status === 'cooperated' ? 'success' : status === 'lost' ? 'danger' : status === 'paused' ? 'warning' : 'info'
}

function ownerName(ownerId: number) {
  return users.value.find((u) => u.id === ownerId)?.realName || `#${ownerId}`
}

onMounted(async () => {
  await dict.load()
  summary.value = await dashboardApi.summary() as Record<string, number>
  const userData = await userApi.page({ current: 1, size: 100 }) as any
  users.value = userData.records
  load()
})
</script>

<style scoped>
.customer-cell { display: flex; align-items: center; gap: 8px; }
.customer-avatar { background: linear-gradient(135deg, #2563eb, #60a5fa); font-size: 13px; }
.search-hint { font-size: 12px; color: var(--color-muted-foreground); font-weight: 400; }
</style>
