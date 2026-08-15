<template>
  <div>
    <div class="welcome-banner">
      <div class="welcome-left">
        <div class="welcome-title">{{ greeting }}，{{ displayName }}</div>
        <div class="welcome-sub">{{ todayText }} · 高效跟进，赢在每一个商机</div>
      </div>
      <div class="welcome-actions">
        <el-button plain :icon="OfficeBuilding" @click="router.push('/customers')">客户管理</el-button>
        <el-button plain type="success" :icon="TrendCharts" @click="router.push('/opportunities')">商机管理</el-button>
        <el-button plain type="warning" :icon="ChatDotRound" @click="router.push('/follow-ups')">跟进记录</el-button>
      </div>
    </div>

    <el-row :gutter="14" class="mt-16">
      <el-col v-for="card in cards" :key="card.label" :xs="12" :sm="8" :md="4">
        <el-card shadow="never" class="stat-card clickable" @click="router.push(card.to)">
          <div class="stat-icon" :class="card.cls">
            <el-icon :size="20"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-value" :class="{ 'stat-money': card.money }">{{ card.value }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="14" class="mt-16">
      <el-col :xs="24" :md="16">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span><el-icon class="header-icon"><DataLine /></el-icon> 近 12 个月趋势</span>
              <el-tag size="small" effect="plain" round>新增客户 / 商机金额</el-tag>
            </div>
          </template>
          <div ref="trendRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span><el-icon class="header-icon"><PieChart /></el-icon> 客户状态分布</span>
            </div>
          </template>
          <div ref="pieRef" class="chart" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="14" class="mt-16">
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span><el-icon class="header-icon"><Histogram /></el-icon> 商机阶段统计</span>
              <el-tag size="small" effect="plain" round>数量 / 金额</el-tag>
            </div>
          </template>
          <div ref="barRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span><el-icon class="header-icon"><AlarmClock /></el-icon> 今日待跟进</span>
              <el-tag v-if="todoList.length" size="small" type="danger" effect="light" round>{{ todoList.length }} 条</el-tag>
            </div>
          </template>
          <el-empty v-if="todoList.length === 0" description="今天没有待跟进事项，太棒了 🎉" :image-size="90">
            <el-button type="primary" plain size="small" @click="router.push('/follow-ups')">去安排跟进</el-button>
          </el-empty>
          <el-timeline v-else class="todo-timeline">
            <el-timeline-item
              v-for="item in todoList"
              :key="item.id"
              :timestamp="item.nextFollowTime"
              :type="item.overdue ? 'danger' : 'primary'"
              placement="top"
            >
              <div class="todo-content">{{ item.content }}</div>
              <div class="todo-meta">
                客户ID: {{ item.customerId }}
                <span>·</span>
                方式: {{ dict.labelOf('follow_type', item.type) }}
                <el-tag v-if="item.overdue" size="small" type="danger" effect="light" round class="ml-8">已逾期</el-tag>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts'
import {
  OfficeBuilding, TrendCharts, ChatDotRound, CirclePlus, Money,
  AlarmClock, DataLine, PieChart, Histogram,
} from '@element-plus/icons-vue'
import { dashboardApi, followUpApi } from '@/api/modules'
import { useDictStore } from '@/stores/dict'
import { formatMoney } from '@/utils/format'

const router = useRouter()
const userStore = useUserStore()
const dict = useDictStore()
const summary = ref<Record<string, number>>({})
const todoList = ref<any[]>([])
const trendRef = ref<HTMLElement>()
const pieRef = ref<HTMLElement>()
const barRef = ref<HTMLElement>()
let charts: echarts.ECharts[] = []

const FONT = "'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif"

const PALETTE = ['#2563eb', '#3b82f6', '#059669', '#ea580c', '#7c3aed', '#0d9488', '#f59e0b', '#64748b']

const WEEK = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']

const displayName = computed(() => userStore.user?.realName || userStore.user?.username || '伙伴')

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '凌晨好'
  if (h < 12) return '早上好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const todayText = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 ${WEEK[d.getDay()]}`
})

const cards = computed(() => [
  { label: '客户总数', value: summary.value.customerTotal ?? '-', icon: OfficeBuilding, cls: 'blue', to: '/customers' },
  { label: '本月新增客户', value: summary.value.customerMonthNew ?? '-', icon: CirclePlus, cls: 'green', to: '/customers' },
  { label: '商机总数', value: summary.value.opportunityTotal ?? '-', icon: TrendCharts, cls: 'orange', to: '/opportunities' },
  { label: '商机总金额', value: formatMoney(summary.value.opportunityAmount), icon: Money, cls: 'purple', money: true, to: '/opportunities' },
  { label: '今日跟进', value: summary.value.followUpToday ?? '-', icon: ChatDotRound, cls: 'teal', to: '/follow-ups' },
  { label: '待跟进', value: summary.value.followUpTodo ?? '-', icon: AlarmClock, cls: 'red', to: '/follow-ups' },
])

function tooltipStyle() {
  return {
    backgroundColor: '#ffffff',
    borderColor: '#e2e8f0',
    borderWidth: 1,
    padding: [8, 12],
    textStyle: { color: '#0f172a', fontFamily: FONT, fontSize: 13 },
    extraCssText: 'box-shadow: 0 8px 24px rgba(15,23,42,.12); border-radius: 10px;',
  }
}

function axisCommon() {
  return {
    axisLine: { lineStyle: { color: '#e2e8f0' } },
    axisTick: { show: false },
    axisLabel: { color: '#64748b', fontFamily: FONT },
    splitLine: { lineStyle: { color: '#eef2f8' } },
  }
}

async function load() {
  summary.value = await dashboardApi.summary() as Record<string, number>
  const trend = await dashboardApi.trend() as any[]
  const customerStats = await dashboardApi.customerStats() as any[]
  const oppStats = await dashboardApi.opportunityStats() as any[]
  renderTrend(trend)
  renderPie(customerStats)
  renderBar(oppStats)
  todoList.value = (await followUpApi.todo() as any[]).map((t) => ({
    ...t,
    overdue: t.nextFollowTime && new Date(t.nextFollowTime) < new Date(),
  }))
}

function renderTrend(data: any[]) {
  const chart = echarts.init(trendRef.value!)
  chart.setOption({
    textStyle: { fontFamily: FONT },
    tooltip: { trigger: 'axis', ...tooltipStyle() },
    legend: {
      data: ['新增客户', '商机金额'],
      top: 0,
      right: 0,
      textStyle: { color: '#475569', fontFamily: FONT },
    },
    grid: { left: 10, right: 10, top: 44, bottom: 0, containLabel: true },
    xAxis: { type: 'category', data: data.map((d) => d.month), boundaryGap: false, ...axisCommon() },
    yAxis: [
      { type: 'value', name: '客户数', ...axisCommon() },
      { type: 'value', name: '金额', ...axisCommon(), splitLine: { show: false } },
    ],
    series: [
      {
        name: '新增客户', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6,
        data: data.map((d) => d.customerCount),
        lineStyle: { width: 3, color: '#2563eb' },
        itemStyle: { color: '#2563eb', borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(37, 99, 235, 0.22)' },
            { offset: 1, color: 'rgba(37, 99, 235, 0.01)' },
          ]),
        },
      },
      {
        name: '商机金额', type: 'bar', yAxisIndex: 1,
        data: data.map((d) => d.opportunityAmount),
        barWidth: 14,
        itemStyle: { color: '#ea580c', borderRadius: [5, 5, 0, 0] },
      },
    ],
  })
  charts.push(chart)
}

function renderPie(data: any[]) {
  const chart = echarts.init(pieRef.value!)
  chart.setOption({
    textStyle: { fontFamily: FONT },
    tooltip: { trigger: 'item', ...tooltipStyle(), formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, icon: 'circle', textStyle: { color: '#475569', fontFamily: FONT } },
    series: [
      {
        type: 'pie',
        radius: ['42%', '66%'],
        center: ['50%', '44%'],
        itemStyle: { borderColor: '#fff', borderWidth: 3, borderRadius: 6 },
        label: { show: false },
        emphasis: { scaleSize: 6 },
        data: data.map((d, i) => ({ name: d.name, value: d.cnt, itemStyle: { color: PALETTE[i % PALETTE.length] } })),
      },
    ],
  })
  charts.push(chart)
}

function renderBar(data: any[]) {
  const chart = echarts.init(barRef.value!)
  chart.setOption({
    textStyle: { fontFamily: FONT },
    tooltip: { trigger: 'axis', ...tooltipStyle() },
    legend: {
      data: ['商机数', '金额'],
      top: 0,
      right: 0,
      textStyle: { color: '#475569', fontFamily: FONT },
    },
    grid: { left: 10, right: 10, top: 44, bottom: 0, containLabel: true },
    xAxis: { type: 'category', data: data.map((d) => dict.labelOf('opp_stage', d.stage)), ...axisCommon() },
    yAxis: { type: 'value', ...axisCommon() },
    series: [
      {
        name: '商机数', type: 'bar', barWidth: 16,
        data: data.map((d) => d.cnt),
        itemStyle: { color: '#2563eb', borderRadius: [5, 5, 0, 0] },
      },
      {
        name: '金额', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6,
        data: data.map((d) => d.amount),
        lineStyle: { width: 2.5, color: '#059669' },
        itemStyle: { color: '#059669', borderColor: '#fff', borderWidth: 2 },
      },
    ],
  })
  charts.push(chart)
}

function resize() {
  charts.forEach((c) => c.resize())
}

onMounted(async () => {
  await dict.load()
  await load()
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  charts.forEach((c) => c.dispose())
})
</script>

<style scoped>
.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  padding: 20px 24px;
  border-radius: 14px;
  border: 1px solid var(--color-border);
  background:
    radial-gradient(720px 120px at 88% 0%, rgba(37, 99, 235, 0.10), transparent 60%),
    radial-gradient(520px 120px at 12% 100%, rgba(5, 150, 105, 0.10), transparent 60%),
    linear-gradient(120deg, #eef4fe 0%, #f8fafc 55%, #eefaf5 100%);
}

.welcome-title { font-size: 20px; font-weight: 700; color: var(--color-foreground); }
.welcome-sub { margin-top: 6px; font-size: 13px; color: var(--color-muted-foreground); }
.welcome-actions { display: flex; gap: 10px; flex-wrap: wrap; }

.stat-card { cursor: pointer; }
.stat-body { min-width: 0; }
.stat-value.stat-money { font-size: 18px; }

.chart-card { margin-bottom: 0; }
.chart { height: 300px; }

.todo-timeline { padding-left: 4px; }
.todo-content { font-size: 14px; font-weight: 500; color: var(--color-foreground); }
.todo-meta { color: var(--color-muted-foreground); font-size: 12px; margin-top: 4px; }

@media (max-width: 768px) {
  .welcome-banner { flex-direction: column; align-items: flex-start; }
  .welcome-actions { width: 100%; }
  .welcome-actions .el-button { flex: 1; }
  .chart { height: 240px; }
}
</style>
