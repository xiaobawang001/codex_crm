<template>
  <div>
    <el-row :gutter="12" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon blue"><el-icon :size="22"><TrendCharts /></el-icon></div>
          <div><div class="stat-label">商机总数</div><div class="stat-value">{{ summary.opportunityTotal ?? '-' }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon green"><el-icon :size="22"><Money /></el-icon></div>
          <div><div class="stat-label">预计总金额</div><div class="stat-value amount">{{ formatMoney(summary.opportunityAmount) }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon orange"><el-icon :size="22"><Medal /></el-icon></div>
          <div><div class="stat-label">赢单</div><div class="stat-value">{{ stageCount('win') }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon purple"><el-icon :size="22"><Filter /></el-icon></div>
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
          <el-input v-model="query.keyword" placeholder="商机名称" clearable style="width: 170px" @keyup.enter="load" />
        </el-form-item>
        <el-form-item label="所属客户">
          <el-select v-model="query.customerId" placeholder="全部客户" clearable filterable style="width: 190px">
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="阶段">
          <el-select v-model="query.stage" placeholder="全部阶段" clearable style="width: 130px">
            <el-option v-for="i in dict.byType('opp_stage')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="userStore.isManager" label="负责人">
          <el-select v-model="query.ownerId" placeholder="全部人员" clearable filterable style="width: 130px">
            <el-option v-for="u in users" :key="u.id" :label="u.realName || u.username" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额范围">
          <el-input-number v-model="query.amountMin" :min="0" :controls="false" placeholder="最低" style="width: 100px" />
          <span class="range-sep">-</span>
          <el-input-number v-model="query.amountMax" :min="0" :controls="false" placeholder="最高" style="width: 100px" />
        </el-form-item>
        <el-form-item label="预计成交">
          <el-date-picker v-model="closeRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 250px" />
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
            <el-icon class="header-icon"><TrendCharts /></el-icon> 商机列表
            <el-tag size="small" effect="plain" round class="ml-8">{{ total }} 条</el-tag>
          </span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增商机</el-button>
        </div>
      </template>

      <el-table :data="rows" v-loading="loading" stripe border>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="name" label="商机名称" min-width="170" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="opp-name">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所属客户" min-width="170" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" @click="$router.push(`/customers/${row.customerId}`)">{{ customerName(row.customerId) }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="预计金额" width="130" align="right">
          <template #default="{ row }">
            <span class="amount-text">{{ formatMoney(row.amount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="阶段" width="190">
          <template #default="{ row }">
            <div class="stage-cell">
              <el-progress
                v-if="!['win', 'lose'].includes(row.stage)"
                :percentage="stagePercent(row.stage)"
                :stroke-width="8"
                :color="stageColor(row.stage)"
                :show-text="false"
                class="stage-progress"
              />
              <el-tag size="small" :type="stageTag(row.stage)" effect="dark" round>
                {{ dict.labelOf('opp_stage', row.stage) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预计成交" width="110">
          <template #default="{ row }">
            <span :class="closeDateClass(row.expectedCloseDate)">{{ row.expectedCloseDate || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结果" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.stage === 'win'" class="win-text">赢单 {{ formatMoney(row.winAmount) }}</span>
            <span v-else-if="row.stage === 'lose'" class="lose-text">{{ row.loseReason || '输单' }}</span>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="负责人" width="100">
          <template #default="{ row }">
            <span v-if="row.ownerId" class="owner-cell">
              <el-avatar :size="22" class="owner-avatar">{{ ownerName(row.ownerId).slice(0, 1) }}</el-avatar>
              {{ ownerName(row.ownerId) }}
            </span>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <template v-if="!['win', 'lose'].includes(row.stage)">
              <el-button link type="primary" @click="openStage(row)">推进</el-button>
              <el-button link type="success" @click="openWin(row)">赢单</el-button>
              <el-button link type="warning" @click="openLose(row)">输单</el-button>
              <el-button link type="info" @click="openDialog(row)">编辑</el-button>
            </template>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商机' : '新增商机'" width="540px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="商机名称" required><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="关联客户" required>
          <el-select v-model="form.customerId" filterable style="width: 100%">
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预计金额"><el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" /></el-form-item>
        <el-form-item label="预计成交日期">
          <el-date-picker v-model="form.expectedCloseDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="阶段">
          <el-select v-model="form.stage" style="width: 100%">
            <el-option v-for="i in nonTerminalStages" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
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
import { Plus, Search, Refresh, TrendCharts, Money, Medal, Filter } from '@element-plus/icons-vue'
import { opportunityApi, customerApi, userApi, dashboardApi } from '@/api/modules'
import { useDictStore } from '@/stores/dict'
import { useUserStore } from '@/stores/user'
import { formatMoney } from '@/utils/format'
import type { Opportunity } from '@/types'

const dict = useDictStore()
const userStore = useUserStore()
const loading = ref(false)
const rows = ref<Opportunity[]>([])
const total = ref(0)
const summary = ref<Record<string, number>>({})
const customers = ref<any[]>([])
const users = ref<any[]>([])
const closeRange = ref<string[]>([])
const query = reactive({
  current: 1, size: 10, keyword: '', customerId: undefined as number | undefined,
  stage: '', ownerId: undefined as number | undefined,
  amountMin: undefined as number | undefined, amountMax: undefined as number | undefined,
})
const dialogVisible = ref(false)
const form = ref<Partial<Opportunity>>({})

const nonTerminalStages = computed(() =>
  dict.byType('opp_stage').filter((i) => !['win', 'lose'].includes(i.dictValue)),
)

const STAGE_ORDER = ['contact', 'requirement', 'proposal', 'negotiation']
function stagePercent(stage: string) {
  const idx = STAGE_ORDER.indexOf(stage)
  return idx < 0 ? 0 : ((idx + 1) / STAGE_ORDER.length) * 100
}

function stageColor(stage: string) {
  return stage === 'negotiation' ? '#f56c6c' : stage === 'proposal' ? '#e6a23c' : '#409eff'
}

function stageCount(stage: string) {
  return rows.value.filter((r) => r.stage === stage).length
}

async function load() {
  loading.value = true
  try {
    const data = await opportunityApi.page({
      ...query,
      expectedCloseStart: closeRange.value?.[0],
      expectedCloseEnd: closeRange.value?.[1],
    }) as any
    rows.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function reset() {
  query.keyword = ''
  query.customerId = undefined
  query.stage = ''
  query.ownerId = undefined
  query.amountMin = undefined
  query.amountMax = undefined
  closeRange.value = []
  query.current = 1
  load()
}

function openDialog(row?: Opportunity) {
  form.value = row ? { ...row } : { stage: 'contact' }
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.name || !form.value.customerId) {
    ElMessage.warning('请填写名称与客户')
    return
  }
  if (form.value.id) {
    await opportunityApi.update(form.value.id, form.value)
  } else {
    await opportunityApi.create(form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function openStage(row: Opportunity) {
  const { value } = await ElMessageBox.prompt('输入目标阶段 (requirement/proposal/negotiation)', '推进阶段', {
    inputValue: row.stage,
  })
  await opportunityApi.changeStage(row.id!, value)
  ElMessage.success('阶段已更新')
  load()
}

async function openWin(row: Opportunity) {
  const { value } = await ElMessageBox.prompt('输入赢单金额', '赢单', { inputValue: String(row.amount || '') })
  await opportunityApi.win(row.id!, Number(value) || 0)
  ElMessage.success('已赢单')
  load()
}

async function openLose(row: Opportunity) {
  const { value } = await ElMessageBox.prompt('输入输单原因', '输单')
  await opportunityApi.lose(row.id!, value)
  ElMessage.success('已输单')
  load()
}

async function remove(row: Opportunity) {
  await ElMessageBox.confirm(`确认删除商机「${row.name}」？`, '警告', { type: 'warning' })
  await opportunityApi.remove(row.id!)
  ElMessage.success('删除成功')
  load()
}

function stageTag(stage: string) {
  return stage === 'win' ? 'success' : stage === 'lose' ? 'danger' : stage === 'negotiation' ? 'warning' : 'primary'
}

function closeDateClass(date?: string) {
  if (!date) return 'muted'
  return dayjs(date).isBefore(dayjs(), 'day') ? 'overdue-text' : 'time-text'
}

function customerName(customerId: number) {
  return customers.value.find((c) => c.id === customerId)?.name || `客户#${customerId}`
}

function ownerName(ownerId: number) {
  return users.value.find((u) => u.id === ownerId)?.realName || `#${ownerId}`
}

onMounted(async () => {
  await dict.load()
  summary.value = await dashboardApi.summary() as Record<string, number>
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
.stat-value.amount { font-size: 18px; color: var(--el-color-danger); }
.opp-name { font-weight: 600; }
.amount-text { color: var(--el-color-danger); font-weight: 600; }
.stage-cell { display: flex; align-items: center; gap: 8px; }
.stage-progress { width: 90px; }
.win-text { color: var(--el-color-success); font-weight: 600; }
.lose-text { color: var(--el-color-danger); }
.overdue-text { color: var(--el-color-danger); font-weight: 600; }
.search-hint { font-size: 12px; color: var(--color-muted-foreground); font-weight: 400; }
</style>
