<template>
  <div v-loading="loading">
    <el-page-header class="mb-12" @back="$router.back()">
      <template #content>
        <div class="detail-title">
          <el-avatar :size="30" class="detail-avatar">{{ (customer?.name || '客').slice(0, 1) }}</el-avatar>
          <span class="title">{{ customer?.name }}</span>
          <el-tag size="small" effect="light" round>{{ dict.labelOf('level', customer?.level) }}</el-tag>
          <el-tag v-if="customer?.ownerId" size="small" type="info" effect="plain" round>{{ ownerName(customer.ownerId) }}</el-tag>
          <el-tag v-else size="small" type="warning" effect="light" round>公海</el-tag>
        </div>
      </template>
    </el-page-header>

    <el-card shadow="never" class="mb-12">
      <template #header>
        <div class="card-header">
          <span><el-icon class="header-icon"><OfficeBuilding /></el-icon> 基本信息</span>
          <div>
            <el-button v-if="!customer?.ownerId" type="success" size="small" @click="claim">领取</el-button>
            <el-button v-if="userStore.isManager" type="info" size="small" @click="assignVisible = true">分配</el-button>
            <el-button type="primary" size="small" @click="editVisible = true">编辑</el-button>
            <el-button type="danger" size="small" @click="remove">删除</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="客户名称">{{ customer?.name }}</el-descriptions-item>
        <el-descriptions-item label="等级">{{ dict.labelOf('level', customer?.level) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ dict.labelOf('customer_status', customer?.status) }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ dict.labelOf('industry', customer?.industry) }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{ dict.labelOf('source', customer?.source) }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ customer?.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ customer?.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="网站">{{ customer?.website || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ customer?.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="3">{{ customer?.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">{{ customer?.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane name="contacts">
          <template #label><el-icon class="tab-icon"><User /></el-icon> 联系人</template>
          <div class="tab-toolbar">
            <el-button type="primary" size="small" @click="contactDialog = true">新增联系人</el-button>
          </div>
          <el-table :data="contacts" size="small" stripe>
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="position" label="职位" />
            <el-table-column prop="phone" label="电话" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column label="主要" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.isPrimary === 1" size="small" type="success">主要</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button link type="danger" @click="removeContact(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane name="followups">
          <template #label><el-icon class="tab-icon"><ChatDotRound /></el-icon> 跟进时间线</template>
          <div class="tab-toolbar">
            <el-button type="primary" size="small" @click="followDialog = true">新增跟进</el-button>
          </div>
          <el-timeline>
            <el-timeline-item v-for="f in followUps" :key="f.id" :timestamp="f.createTime" type="primary">
              <div class="follow-content">{{ f.content }}</div>
              <div class="follow-meta">
                方式: {{ dict.labelOf('follow_type', f.type) }}
                <span v-if="f.nextFollowTime"> · 下次跟进: {{ f.nextFollowTime }}</span>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-if="followUps.length === 0" description="暂无跟进记录" :image-size="80" />
        </el-tab-pane>

        <el-tab-pane name="opportunities">
          <template #label><el-icon class="tab-icon"><TrendCharts /></el-icon> 商机</template>
          <div class="tab-toolbar">
            <el-button type="primary" size="small" @click="oppDialog = true">新增商机</el-button>
          </div>
          <el-table :data="opportunities" size="small" stripe>
            <el-table-column prop="name" label="商机名称" />
            <el-table-column prop="amount" label="预计金额" width="130">
              <template #default="{ row }">{{ formatMoney(row.amount) }}</template>
            </el-table-column>
            <el-table-column label="阶段" width="110">
              <template #default="{ row }">
                <el-tag size="small" :type="stageTag(row.stage)">{{ dict.labelOf('opp_stage', row.stage) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="expectedCloseDate" label="预计成交" width="120" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button v-if="!['win','lose'].includes(row.stage)" link type="primary" @click="openStage(row)">推进</el-button>
                <el-button v-if="!['win','lose'].includes(row.stage)" link type="success" @click="openWin(row)">赢单</el-button>
                <el-button v-if="!['win','lose'].includes(row.stage)" link type="warning" @click="openLose(row)">输单</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="editVisible" title="编辑客户" width="560px">
      <el-form :model="customer" label-width="90px">
        <el-form-item label="客户名称" required><el-input v-model="customer!.name" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="行业">
              <el-select v-model="customer!.industry" clearable style="width: 100%">
                <el-option v-for="i in dict.byType('industry')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源">
              <el-select v-model="customer!.source" clearable style="width: 100%">
                <el-option v-for="i in dict.byType('source')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="等级">
              <el-select v-model="customer!.level" clearable style="width: 100%">
                <el-option v-for="i in dict.byType('level')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="customer!.status" style="width: 100%">
                <el-option v-for="i in dict.byType('customer_status')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="电话"><el-input v-model="customer!.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="customer!.email" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="customer!.address" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="customer!.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
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

    <el-dialog v-model="contactDialog" title="新增联系人" width="480px">
      <el-form :model="contactForm" label-width="80px">
        <el-form-item label="姓名" required><el-input v-model="contactForm.name" /></el-form-item>
        <el-form-item label="职位"><el-input v-model="contactForm.position" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="contactForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="contactForm.email" /></el-form-item>
        <el-form-item label="微信"><el-input v-model="contactForm.wechat" /></el-form-item>
        <el-form-item label="主要联系人">
          <el-switch v-model="contactForm.isPrimary" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="contactDialog = false">取消</el-button>
        <el-button type="primary" @click="submitContact">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="followDialog" title="新增跟进" width="520px">
      <el-form :model="followForm" label-width="90px">
        <el-form-item label="跟进方式">
          <el-select v-model="followForm.type" style="width: 100%">
            <el-option v-for="i in dict.byType('follow_type')" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" required>
          <el-input v-model="followForm.content" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker v-model="followForm.nextFollowTime" type="datetime" style="width: 100%" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followDialog = false">取消</el-button>
        <el-button type="primary" @click="submitFollow">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="oppDialog" title="新增商机" width="520px">
      <el-form :model="oppForm" label-width="100px">
        <el-form-item label="商机名称" required><el-input v-model="oppForm.name" /></el-form-item>
        <el-form-item label="预计金额"><el-input-number v-model="oppForm.amount" :min="0" :precision="2" style="width: 100%" /></el-form-item>
        <el-form-item label="预计成交日期">
          <el-date-picker v-model="oppForm.expectedCloseDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="阶段">
          <el-select v-model="oppForm.stage" style="width: 100%">
            <el-option v-for="i in nonTerminalStages" :key="i.dictValue" :label="i.dictLabel" :value="i.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="oppForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="oppDialog = false">取消</el-button>
        <el-button type="primary" @click="submitOpp">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { OfficeBuilding, User, ChatDotRound, TrendCharts } from '@element-plus/icons-vue'
import { customerApi, contactApi, followUpApi, opportunityApi, userApi } from '@/api/modules'
import { useDictStore } from '@/stores/dict'
import { useUserStore } from '@/stores/user'
import { formatMoney } from '@/utils/format'
import type { Customer, Contact, FollowUp, Opportunity } from '@/types'

const route = useRoute()
const dict = useDictStore()
const userStore = useUserStore()
const customerId = Number(route.params.id)
const loading = ref(false)
const activeTab = ref('contacts')

const customer = ref<Customer>()
const contacts = ref<Contact[]>([])
const followUps = ref<FollowUp[]>([])
const opportunities = ref<Opportunity[]>([])
const users = ref<any[]>([])

const editVisible = ref(false)
const assignVisible = ref(false)
const assignUserId = ref<number>()
const contactDialog = ref(false)
const followDialog = ref(false)
const oppDialog = ref(false)

const contactForm = ref<Partial<Contact>>({})
const followForm = ref<Partial<FollowUp>>({})
const oppForm = ref<Partial<Opportunity>>({})

const nonTerminalStages = computed(() =>
  dict.byType('opp_stage').filter((i) => !['win', 'lose'].includes(i.dictValue)),
)

async function load() {
  loading.value = true
  try {
    customer.value = await customerApi.detail(customerId) as Customer
    const contactsData = await contactApi.page({ customerId, current: 1, size: 100 }) as any
    contacts.value = contactsData.records
    const followData = await followUpApi.page({ customerId, current: 1, size: 100 }) as any
    followUps.value = followData.records
    const oppData = await opportunityApi.page({ customerId, current: 1, size: 100 }) as any
    opportunities.value = oppData.records
    const userData = await userApi.page({ current: 1, size: 100 }) as any
    users.value = userData.records
  } finally {
    loading.value = false
  }
}

async function claim() {
  await customerApi.claim(customerId)
  ElMessage.success('领取成功')
  load()
}

async function saveEdit() {
  await customerApi.update(customerId, customer.value!)
  ElMessage.success('保存成功')
  editVisible.value = false
  load()
}

async function remove() {
  await ElMessageBox.confirm(`确认删除客户「${customer.value?.name}」？`, '警告', { type: 'warning' })
  await customerApi.remove(customerId)
  ElMessage.success('删除成功')
  history.back()
}

async function submitAssign() {
  if (!assignUserId.value) return
  await customerApi.assign(customerId, assignUserId.value)
  ElMessage.success('分配成功')
  assignVisible.value = false
  load()
}

async function submitContact() {
  if (!contactForm.value.name) {
    ElMessage.warning('请输入姓名')
    return
  }
  await contactApi.create({ ...contactForm.value, customerId })
  ElMessage.success('保存成功')
  contactDialog.value = false
  contactForm.value = {}
  load()
}

async function removeContact(row: Contact) {
  await ElMessageBox.confirm(`确认删除联系人「${row.name}」？`, '警告', { type: 'warning' })
  await contactApi.remove(row.id!)
  load()
}

async function submitFollow() {
  if (!followForm.value.content) {
    ElMessage.warning('请输入跟进内容')
    return
  }
  await followUpApi.create({ ...followForm.value, customerId })
  ElMessage.success('保存成功')
  followDialog.value = false
  followForm.value = {}
  load()
}

async function submitOpp() {
  if (!oppForm.value.name) {
    ElMessage.warning('请输入商机名称')
    return
  }
  await opportunityApi.create({ ...oppForm.value, customerId, stage: oppForm.value.stage || 'contact' })
  ElMessage.success('保存成功')
  oppDialog.value = false
  oppForm.value = {}
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

function ownerName(ownerId: number) {
  return users.value.find((u) => u.id === ownerId)?.realName || `#${ownerId}`
}

function stageTag(stage: string) {
  return stage === 'win' ? 'success' : stage === 'lose' ? 'danger' : 'primary'
}

onMounted(async () => {
  await dict.load()
  load()
})
</script>

<style scoped>
.title { font-size: 18px; font-weight: 600; }
.detail-title { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.detail-avatar { background: linear-gradient(135deg, #2563eb, #60a5fa); color: #fff; font-weight: 600; }
.tab-toolbar { margin-bottom: 12px; }
.tab-icon { margin-right: 4px; vertical-align: -2px; }
.follow-content { font-size: 14px; }
.follow-meta { color: var(--color-muted-foreground); font-size: 12px; margin-top: 4px; }
</style>
