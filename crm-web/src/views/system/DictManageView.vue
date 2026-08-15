<template>
  <div>
    <el-row :gutter="12" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon blue"><el-icon :size="22"><Collection /></el-icon></div>
          <div><div class="stat-label">字典类型</div><div class="stat-value">{{ typeCount }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon green"><el-icon :size="22"><Tickets /></el-icon></div>
          <div><div class="stat-label">字典项</div><div class="stat-value">{{ allRows.length }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon orange"><el-icon :size="22"><CircleCheck /></el-icon></div>
          <div><div class="stat-label">启用</div><div class="stat-value">{{ activeCount }}</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon red"><el-icon :size="22"><CircleClose /></el-icon></div>
          <div><div class="stat-label">停用</div><div class="stat-value">{{ allRows.length - activeCount }}</div></div>
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
        <el-form-item label="字典类型">
          <el-select v-model="query.dictType" placeholder="全部类型" clearable filterable style="width: 180px">
            <el-option v-for="t in dictTypeOptions" :key="t" :label="typeLabel(t)" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键字">
          <el-input v-model="query.keyword" placeholder="显示名称 / 实际值" clearable style="width: 200px" @keyup.enter="load" />
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
            <el-icon class="header-icon"><Collection /></el-icon> 字典管理
            <el-tag size="small" effect="plain" round class="ml-8">{{ total }} 条</el-tag>
          </span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">新增字典项</el-button>
        </div>
      </template>

      <el-table :data="rows" v-loading="loading" stripe border>
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column label="字典类型" min-width="150">
          <template #default="{ row }">
            <el-tag size="small" :color="typeColor(row.dictType)" effect="dark" class="type-tag">{{ typeLabel(row.dictType) }}</el-tag>
            <div class="type-code">{{ row.dictType }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="dictLabel" label="显示名称" min-width="140" />
        <el-table-column label="实际值" width="140">
          <template #default="{ row }">
            <span class="mono">{{ row.dictValue }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="90" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'" round>{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑字典项' : '新增字典项'" width="460px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="字典类型" required>
          <el-select v-model="form.dictType" filterable allow-create style="width: 100%">
            <el-option v-for="t in dictTypeOptions" :key="t" :label="typeLabel(t)" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示名称" required><el-input v-model="form.dictLabel" placeholder="如：制造业" /></el-form-item>
        <el-form-item label="实际值" required><el-input v-model="form.dictValue" placeholder="如：manufacturing" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" style="width: 100%" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
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
import { Plus, Search, Refresh, Filter, Collection, Tickets, CircleCheck, CircleClose, Edit, Delete } from '@element-plus/icons-vue'
import { dictApi } from '@/api/modules'
import { useDictStore } from '@/stores/dict'
import type { DictItem } from '@/types'

const dict = useDictStore()
const loading = ref(false)
const allRows = ref<DictItem[]>([])
const rows = ref<DictItem[]>([])
const dictTypeOptions = ref<string[]>([])
const dialogVisible = ref(false)
const form = ref<Partial<DictItem>>({ status: 1, sort: 0 })
const query = reactive({ dictType: '', keyword: '', status: undefined as number | undefined })

const activeCount = computed(() => allRows.value.filter((r) => r.status === 1).length)
const typeCount = computed(() => dictTypeOptions.value.length)
const total = computed(() => rows.value.length)

const TYPE_LABELS: Record<string, string> = {
  industry: '客户行业',
  source: '客户来源',
  level: '客户等级',
  customer_status: '客户状态',
  follow_type: '跟进方式',
  opp_stage: '商机阶段',
}

function typeLabel(type: string) {
  return TYPE_LABELS[type] || dict.labelOf('dict_type_label', type) || type
}

const typeColors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#9264f0', '#00b4a8', '#f7b500', '#00c0c0']
function typeColor(type: string) {
  let hash = 0
  for (const ch of type) hash = (hash * 31 + ch.charCodeAt(0)) % 997
  return typeColors[hash % typeColors.length]
}

async function loadTypes() {
  const all = await dictApi.listAll() as any
  allRows.value = all
  dictTypeOptions.value = [...new Set(all.map((i: DictItem) => i.dictType))].sort()
}

async function load() {
  loading.value = true
  try {
    rows.value = await dictApi.listAll({
      dictType: query.dictType || undefined,
      keyword: query.keyword || undefined,
      status: query.status,
    }) as any
  } finally {
    loading.value = false
  }
}

function reset() {
  query.dictType = ''
  query.keyword = ''
  query.status = undefined
  load()
}

function openDialog(row?: DictItem) {
  form.value = row ? { ...row } : { status: 1, sort: 0 }
  dialogVisible.value = true
}

async function submit() {
  if (!form.value.dictType || !form.value.dictLabel || !form.value.dictValue) {
    ElMessage.warning('请填写完整')
    return
  }
  await dictApi.save(form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await dict.refresh()
  await loadTypes()
  load()
}

async function remove(row: DictItem) {
  await ElMessageBox.confirm(`确认删除字典项「${row.dictLabel}」？`, '警告', { type: 'warning' })
  await dictApi.remove(row.id!)
  ElMessage.success('删除成功')
  await dict.refresh()
  await loadTypes()
  load()
}

onMounted(async () => {
  await loadTypes()
  load()
})
</script>

<style scoped>
.type-tag { font-weight: 600; }
.type-code { color: var(--color-muted-foreground); font-size: 12px; margin-top: 2px; }
.search-hint { font-size: 12px; color: var(--color-muted-foreground); font-weight: 400; }
</style>
