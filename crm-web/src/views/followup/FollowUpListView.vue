<template>
  <div>
    <el-row :gutter="12" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon blue"><el-icon :size="22"><ChatDotRound /></el-icon></div>
          <div><div class="stat-label">跟进总数</div><div class="stat-value">{{ total }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon green"><el-icon :size="22"><Phone /></el-icon></div>
          <div><div class="stat-label">电话跟进</div><div class="stat-value">{{ typeCount('phone') }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon orange"><el-icon :size="22"><MapLocation /></el-icon></div>
          <div><div class="stat-label">上门拜访</div><div class="stat-value">{{ typeCount('visit') }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon purple"><el-icon :size="22"><AlarmClock /></el-icon></div>
          <div><div class="stat-label">已到期未跟进</div><div class="stat-value">{{ overdueCount }}</div></div>
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
        <el-form-item label="所属客户">
          <el-select v-model="query.customerId" placeholder="全部客户" clearable filterable style="width: 210px">
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进方式">
          <el-select v-model="query.type" placeholder="全部方式" clearable style="width: 130px">
            <el-option v-for="i in dict.byType('follow_type')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进人">
          <el-select v-model="query.createBy" placeholder="全部人员" clearable filterable style="width: 130px">
            <el-option v-for="u in users" :key="u.id" :label="u.realName || u.username" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进日期">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 250px" />
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
            <el-icon class="header-icon"><ChatDotRound /></el-icon> 跟进记录
            <el-tag size="small" effect="plain" round class="ml-8">{{ total }} 条</el-tag>
          </span>
          <el-button type="primary" :icon="Plus" @click="dialogVisible = true">新增跟进</el-button>
        </div>
      </template>

      <el-table :data="rows" v-loading="loading" stripe border>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column label="跟进方式" width="100" align="center">
          <template #default="{ row }">
            <el-tooltip :content="dict.labelOf('follow_type', row.type)" placement="top">
              <div class="type-icon" :class="typeClass(row.type)">
                <el-icon :size="16"><component :is="typeIcon(row.type)" /></el-icon>
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="所属客户" min-width="170" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" @click="$router.push(`/customers/${row.customerId}`)">{{ customerName(row.customerId) }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="跟进内容" min-width="280" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="content-text">{{ row.content }}</span>
          </template>
        </el-table-column>
        <el-table-column label="跟进时间" width="160">
          <template #default="{ row }">
            <span class="time-text">{{ formatDateTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="下次跟进" width="160">
          <template #default="{ row }">
            <span v-if="row.nextFollowTime" :class="nextTimeClass(row.nextFollowTime)">
              {{ formatDateTime(row.nextFollowTime) }}
              <el-tag v-if="isOverdue(row.nextFollowTime)" size="small" type="danger" round>已过期</el-tag>
              <el-tag v-else-if="isToday(row.nextFollowTime)" size="small" type="warning" round>今天</el-tag>
            </span>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="跟进人" width="100">
          <template #default="{ row }">
            <span class="owner-cell">
              <el-avatar :size="22" class="owner-avatar">{{ userName(row.createBy).slice(0, 1) }}</el-avatar>
              {{ userName(row.createBy) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑跟进' : '新增跟进'" width="540px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="关联客户" required>
          <el-select v-model="form.customerId" filterable style="width: 100%">
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进方式">
          <el-select v-model="form.type" style="width: 100%">
            <el-option v-for="i in dict.byType('follow_type')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" required>
          <el-input v-model="form.content" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker v-model="form.nextFollowTime" type="datetime" style="width: 100%" value-format="YYYY-MM-DDTHH:mm:ss" />
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
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Filter, ChatDotRound, Phone, MapLocation, Message, AlarmClock } from '@element-plus/icons-vue'
import { followUpApi, customerApi, userApi } from '@/api/modules'
import { useDictStore } from '@/stores/dict'
import { formatDateTime } from '@/utils/format'
import type { FollowUp, Customer } from '@/types'

const dict = useDictStore()
const loading = ref(false)
const rows = ref<FollowUp[]>([])
const total = ref(0)
const customers = ref<Customer[]>([])
const users = ref<any[]>([])
const dateRange = ref<string[]>([])
const query = reactive({ current: 1, size: 10, customerId: undefined as number | undefined, type: '', createBy: undefined as number | undefined })
const dialogVisible = ref(false)
const form = ref<Partial<FollowUp>>({})

const overdueCount = computed(() => rows.value.filter((r) => r.nextFollowTime && isOverdue(r.nextFollowTime)).length)

function typeCount(type: string) {
  return rows.value.filter((r) => r.type === type).length
}

async function load() {
  loading.value = true
  try {
    const data = await followUpApi.page({
      ...query,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1],
    }) as any
    rows.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function reset() {
  query.customerId = undefined
  query.type = ''
  query.createBy = undefined
  dateRange.value = []
  query.current = 1
  load()
}

function openDialog(row?: FollowUp) {
  form.value = row ? { ...row } : {}
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.customerId || !form.value.content) {
    ElMessage.warning('请填写客户与内容')
    return
  }
  if (form.value.id) {
    await followUpApi.update(form.value.id, form.value)
  } else {
    await followUpApi.create(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function remove(row: FollowUp) {
  await ElMessageBox.confirm('确认删除该跟进记录？', '警告', { type: 'warning' })
  await followUpApi.remove(row.id!)
  ElMessage.success('删除成功')
  load()
}

function customerName(customerId: number) {
  return customers.value.find((c) => c.id === customerId)?.name || `客户#${customerId}`
}

function userName(userId?: number) {
  return users.value.find((u) => u.id === userId)?.realName || `#${userId}`
}

function typeIcon(type?: string) {
  return type === 'phone' ? Phone : type === 'wechat' ? ChatDotRound : type === 'visit' ? MapLocation : Message
}

function typeClass(type?: string) {
  return type === 'phone' ? 'type-phone' : type === 'wechat' ? 'type-wechat' : type === 'visit' ? 'type-visit' : 'type-email'
}

function isOverdue(time: string) {
  return dayjs(time).isBefore(dayjs())
}

function isToday(time: string) {
  return dayjs(time).isSame(dayjs(), 'day')
}

function nextTimeClass(time: string) {
  return isOverdue(time) ? 'overdue-text' : isToday(time) ? 'today-text' : 'time-text'
}

onMounted(async () => {
  await dict.load()
  load()
  const [cData, uData] = await Promise.all([
    customerApi.page({ current: 1, size: 100 }),
    userApi.page({ current: 1, size: 100 }),
  ])
  customers.value = (cData as any).records
  users.value = (uData as any).records
})
</script>

<style scoped>
.type-icon { width: 32px; height: 32px; border-radius: 8px; display: inline-flex; align-items: center; justify-content: center; color: #fff; }
.type-phone { background: linear-gradient(135deg, #2563eb, #60a5fa); }
.type-wechat { background: linear-gradient(135deg, #059669, #34d399); }
.type-visit { background: linear-gradient(135deg, #ea580c, #fb923c); }
.type-email { background: linear-gradient(135deg, #7c3aed, #a78bfa); }
.content-text { line-height: 1.5; }
.overdue-text { color: var(--el-color-danger); font-weight: 600; }
.today-text { color: var(--el-color-warning); font-weight: 600; }
.search-hint { font-size: 12px; color: var(--color-muted-foreground); font-weight: 400; }
</style>
