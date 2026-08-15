<template>
  <div class="home">
    <header class="nav">
      <div class="nav-inner">
        <div class="logo" role="link" tabindex="0" @click="router.push('/home')" @keyup.enter="router.push('/home')">
          <div class="logo-mark"><el-icon :size="18"><Platform /></el-icon></div>
          <span class="logo-text">云客 CRM</span>
        </div>
        <nav class="nav-links" aria-label="首页导航">
          <a href="#features">核心功能</a>
          <a href="#why">产品优势</a>
          <a href="#cta">开始使用</a>
        </nav>
        <div class="nav-actions">
          <el-button v-if="userStore.isLogin" type="primary" :icon="Odometer" @click="router.push('/dashboard')">进入工作台</el-button>
          <template v-else>
            <el-button @click="router.push('/login')">登录</el-button>
            <el-button type="primary" @click="router.push('/login')">立即体验</el-button>
          </template>
        </div>
      </div>
    </header>

    <section class="hero">
      <div class="hero-inner">
        <div class="hero-text">
          <div class="hero-badge">
            <el-icon><DataAnalysis /></el-icon>
            客户 · 商机 · 跟进 · 一站式管理
          </div>
          <h1>让每一段客户关系<br />都清晰可见</h1>
          <p class="hero-sub">
            云客 CRM 帮助销售团队统一沉淀客户资产、推进商机进程、沉淀跟进记录，
            用数据看板驱动业务增长。
          </p>
          <div class="hero-cta">
            <el-button v-if="userStore.isLogin" type="primary" size="large" :icon="Odometer" @click="router.push('/dashboard')">
              进入工作台
            </el-button>
            <el-button v-else type="primary" size="large" @click="router.push('/login')">立即登录体验</el-button>
            <el-button size="large" class="ghost-btn" @click="scrollTo('#features')">
              了解功能
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
          </div>
          <div class="hero-meta">
            <span><el-icon><CircleCheck /></el-icon> 演示账号开箱即用</span>
            <span><el-icon><CircleCheck /></el-icon> 多角色数据隔离</span>
            <span><el-icon><CircleCheck /></el-icon> 持续迭代更新</span>
          </div>
        </div>

        <div class="hero-visual" aria-hidden="true">
          <div class="mock-card">
            <div class="mock-head">
              <span class="mock-title">经营概览</span>
              <span class="mock-chip">本月</span>
            </div>
            <div class="mock-stats">
              <div class="mock-stat">
                <span class="mock-dot blue" />
                <div><em>客户总数</em><b>1,286</b></div>
              </div>
              <div class="mock-stat">
                <span class="mock-dot green" />
                <div><em>商机金额</em><b>¥ 862万</b></div>
              </div>
              <div class="mock-stat">
                <span class="mock-dot orange" />
                <div><em>本月新增</em><b>+128</b></div>
              </div>
            </div>
            <div class="mock-chart">
              <div v-for="(h, i) in [42, 58, 45, 70, 62, 82, 74, 92, 68, 88, 96, 100]" :key="i" class="bar" :style="{ height: h + '%' }" :class="{ orange: i % 3 === 1, green: i % 3 === 2 }" />
            </div>
            <div class="mock-list">
              <div class="mock-row" v-for="n in 3" :key="n">
                <span class="mock-avatar" :class="'a' + n">{{ ['杭', '苏', '深'][n - 1] }}</span>
                <span class="mock-name">{{ ['杭州某智能制造公司', '苏州精密机械集团', '深圳云创科技'][n - 1] }}</span>
                <em class="mock-tag" :class="'t' + n">{{ ['需求确认', '方案报价', '已成交'][n - 1] }}</em>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section id="features" class="features">
      <div class="section-head">
        <h2>核心功能</h2>
        <p>覆盖销售全流程，让客户管理不再分散</p>
      </div>
      <div class="feature-grid">
        <div v-for="f in features" :key="f.title" class="feature-card">
          <div class="feature-icon" :class="f.cls">
            <el-icon :size="22"><component :is="f.icon" /></el-icon>
          </div>
          <h3>{{ f.title }}</h3>
          <p>{{ f.desc }}</p>
        </div>
      </div>
    </section>

    <section id="why" class="why">
      <div class="section-head">
        <h2>为什么选择云客 CRM</h2>
        <p>为中小团队打造的轻量级客户管理工具</p>
      </div>
      <div class="why-grid">
        <div v-for="w in whys" :key="w.title" class="why-card">
          <div class="why-num">{{ w.num }}</div>
          <h3>{{ w.title }}</h3>
          <p>{{ w.desc }}</p>
        </div>
      </div>
    </section>

    <section id="cta" class="cta">
      <div class="cta-inner">
        <h2>准备好开始了吗？</h2>
        <p>登录即可体验完整的客户、商机与跟进管理流程</p>
        <el-button v-if="userStore.isLogin" type="primary" size="large" :icon="Odometer" @click="router.push('/dashboard')">
          进入工作台
        </el-button>
        <el-button v-else type="primary" size="large" @click="router.push('/login')">立即登录体验</el-button>
      </div>
    </section>

    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-logo">
          <div class="logo-mark small"><el-icon :size="14"><Platform /></el-icon></div>
          <span>云客 CRM</span>
        </div>
        <p>© 2026 云客 CRM · Spring Boot 3 + Vue 3 + PostgreSQL</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  Platform, Odometer, ArrowDown, CircleCheck, DataAnalysis,
  OfficeBuilding, User, TrendCharts, ChatDotRound, Lock, PieChart,
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const features = [
  { title: '客户管理', desc: '客户档案、等级标签、来源渠道统一沉淀，支持公海领取与主管分配。', icon: OfficeBuilding, cls: 'blue' },
  { title: '联系人', desc: '一个客户多位联系人，标记主要联系人，找人不再翻通讯录。', icon: User, cls: 'teal' },
  { title: '商机管理', desc: '阶段化推进商机，金额预测与赢输单一目了然，把握每一笔成交。', icon: TrendCharts, cls: 'orange' },
  { title: '跟进记录', desc: '电话、微信、上门拜访全记录，下次跟进自动提醒，服务不遗漏。', icon: ChatDotRound, cls: 'green' },
  { title: '数据看板', desc: '经营趋势、客户分布、商机阶段统计一屏尽览，用数据驱动决策。', icon: PieChart, cls: 'purple' },
  { title: '权限体系', desc: '管理员、主管、销售多角色协作，数据权限按需隔离，安全可控。', icon: Lock, cls: 'indigo' },
]

const whys = [
  { num: '01', title: '开箱即用', desc: '内置演示账号与 Mock 数据，登录即可完整体验核心流程，无需繁琐配置。' },
  { num: '02', title: '全流程闭环', desc: '从客户建档、跟进互动到商机成交，数据天然打通，形成完整业务闭环。' },
  { num: '03', title: '轻量可扩展', desc: '基于 Spring Boot 3 + Vue 3 的现代化架构，字典与权限可按需扩展。' },
]

function scrollTo(selector: string) {
  document.querySelector(selector)?.scrollIntoView({ behavior: 'smooth' })
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #fff;
  color: var(--color-foreground);
  scroll-behavior: smooth;
}

/* ---------- 顶部导航 ---------- */
.nav {
  position: sticky;
  top: 0;
  z-index: 20;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--color-border);
}
.nav-inner {
  max-width: 1160px;
  margin: 0 auto;
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}
.logo { display: flex; align-items: center; gap: 10px; cursor: pointer; outline: none; }
.logo-mark {
  width: 34px; height: 34px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  color: #fff;
  background: linear-gradient(135deg, #2563eb, #60a5fa);
  box-shadow: 0 4px 10px rgba(37, 99, 235, 0.35);
}
.logo-mark.small { width: 24px; height: 24px; border-radius: 7px; }
.logo-text { font-size: 17px; font-weight: 700; }
.nav-links { display: flex; gap: 28px; }
.nav-links a {
  font-size: 14px; font-weight: 500; color: var(--color-muted-foreground);
  text-decoration: none; transition: color 0.15s ease; padding: 4px 2px;
}
.nav-links a:hover { color: var(--color-primary); }
.nav-actions { display: flex; gap: 10px; }

/* ---------- Hero ---------- */
.hero {
  background:
    radial-gradient(900px 320px at 85% -10%, rgba(37, 99, 235, 0.12), transparent 60%),
    radial-gradient(640px 260px at 8% 100%, rgba(5, 150, 105, 0.10), transparent 60%),
    linear-gradient(180deg, #eef4fe 0%, #f8fafc 60%, #ffffff 100%);
}
.hero-inner {
  max-width: 1160px;
  margin: 0 auto;
  padding: 72px 24px 80px;
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  gap: 56px;
  align-items: center;
}
.hero-badge {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 7px 14px; border-radius: 999px;
  background: var(--el-color-primary-light-9);
  border: 1px solid var(--el-color-primary-light-7);
  color: var(--color-primary);
  font-size: 13px; font-weight: 600;
}
.hero h1 {
  margin: 22px 0 16px;
  font-size: 46px; line-height: 1.22; font-weight: 800;
  letter-spacing: 0.01em;
}
.hero-sub {
  margin: 0 0 28px;
  max-width: 480px;
  font-size: 16px; line-height: 1.8;
  color: var(--color-muted-foreground);
}
.hero-cta { display: flex; gap: 12px; flex-wrap: wrap; }
.ghost-btn { background: #fff; }
.hero-meta {
  margin-top: 26px;
  display: flex; gap: 20px; flex-wrap: wrap;
  font-size: 13px; color: var(--color-muted-foreground);
}
.hero-meta .el-icon { color: var(--color-accent); vertical-align: -2px; margin-right: 2px; }

/* ---------- Hero 产品示意 ---------- */
.hero-visual { position: relative; }
.mock-card {
  background: #fff;
  border: 1px solid var(--color-border);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 24px 64px rgba(15, 23, 42, 0.10);
  transform: perspective(1200px) rotateY(-4deg) rotateX(1deg);
  transition: transform 0.3s ease;
}
.mock-card:hover { transform: perspective(1200px) rotateY(0deg) rotateX(0deg); }
.mock-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.mock-title { font-size: 15px; font-weight: 700; }
.mock-chip {
  font-size: 11px; padding: 3px 10px; border-radius: 999px;
  background: var(--el-color-primary-light-9); color: var(--color-primary); font-weight: 600;
}
.mock-stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; margin-bottom: 16px; }
.mock-stat {
  display: flex; align-items: center; gap: 8px;
  background: #f8fafc; border: 1px solid var(--color-border);
  border-radius: 10px; padding: 10px 12px;
}
.mock-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.mock-dot.blue { background: #2563eb; }
.mock-dot.green { background: #059669; }
.mock-dot.orange { background: #ea580c; }
.mock-stat em { display: block; font-style: normal; font-size: 11px; color: var(--color-muted-foreground); }
.mock-stat b { font-size: 15px; font-weight: 700; font-variant-numeric: tabular-nums; }
.mock-chart {
  display: flex; align-items: flex-end; gap: 8px;
  height: 96px; padding: 0 4px;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 14px;
}
.bar {
  flex: 1; border-radius: 4px 4px 0 0;
  background: linear-gradient(180deg, #60a5fa, #2563eb);
  min-height: 8px;
}
.bar.orange { background: linear-gradient(180deg, #fb923c, #ea580c); }
.bar.green { background: linear-gradient(180deg, #34d399, #059669); }
.mock-list { display: flex; flex-direction: column; gap: 8px; }
.mock-row {
  display: flex; align-items: center; gap: 10px;
  padding: 8px 10px; border-radius: 8px;
  background: #f8fafc;
}
.mock-avatar {
  width: 26px; height: 26px; border-radius: 8px; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; color: #fff; font-weight: 600;
}
.mock-avatar.a1 { background: linear-gradient(135deg, #2563eb, #60a5fa); }
.mock-avatar.a2 { background: linear-gradient(135deg, #059669, #34d399); }
.mock-avatar.a3 { background: linear-gradient(135deg, #7c3aed, #a78bfa); }
.mock-name { flex: 1; font-size: 13px; color: var(--color-foreground); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.mock-tag { font-style: normal; font-size: 11px; font-weight: 600; padding: 2px 8px; border-radius: 999px; flex-shrink: 0; }
.mock-tag.t1 { color: #2563eb; background: var(--el-color-primary-light-9); }
.mock-tag.t2 { color: #ea580c; background: #fef3ec; }
.mock-tag.t3 { color: #059669; background: var(--el-color-success-light-9); }

/* ---------- 通用区块 ---------- */
.section-head { text-align: center; max-width: 560px; margin: 0 auto 40px; }
.section-head h2 { margin: 0 0 10px; font-size: 30px; font-weight: 800; }
.section-head p { margin: 0; font-size: 15px; color: var(--color-muted-foreground); }

/* ---------- 核心功能 ---------- */
.features { padding: 84px 24px; background: #fafbfd; }
.feature-grid {
  max-width: 1160px; margin: 0 auto;
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px;
}
.feature-card {
  background: #fff; border: 1px solid var(--color-border);
  border-radius: 14px; padding: 26px 24px;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}
.feature-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--crm-shadow-md);
  border-color: #d5deee;
}
.feature-icon {
  width: 46px; height: 46px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; margin-bottom: 16px;
}
.feature-icon.blue { background: linear-gradient(135deg, #2563eb, #60a5fa); }
.feature-icon.green { background: linear-gradient(135deg, #059669, #34d399); }
.feature-icon.orange { background: linear-gradient(135deg, #ea580c, #fb923c); }
.feature-icon.purple { background: linear-gradient(135deg, #7c3aed, #a78bfa); }
.feature-icon.teal { background: linear-gradient(135deg, #0d9488, #2dd4bf); }
.feature-icon.indigo { background: linear-gradient(135deg, #4338ca, #818cf8); }
.feature-card h3 { margin: 0 0 8px; font-size: 17px; font-weight: 700; }
.feature-card p { margin: 0; font-size: 13.5px; line-height: 1.75; color: var(--color-muted-foreground); }

/* ---------- 产品优势 ---------- */
.why { padding: 84px 24px; background: #fff; }
.why-grid {
  max-width: 1160px; margin: 0 auto;
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px;
}
.why-card {
  border: 1px dashed var(--color-border);
  border-radius: 14px; padding: 28px 24px;
  background: #fbfcfe;
}
.why-num {
  font-size: 13px; font-weight: 800; letter-spacing: 0.1em;
  color: var(--color-primary);
}
.why-card h3 { margin: 12px 0 8px; font-size: 17px; font-weight: 700; }
.why-card p { margin: 0; font-size: 13.5px; line-height: 1.75; color: var(--color-muted-foreground); }

/* ---------- CTA ---------- */
.cta { padding: 0 24px 84px; background: #fff; }
.cta-inner {
  max-width: 1160px; margin: 0 auto;
  border-radius: 18px; padding: 56px 32px;
  text-align: center; color: #fff;
  background:
    radial-gradient(640px 200px at 90% 0%, rgba(96, 165, 250, 0.45), transparent 60%),
    linear-gradient(135deg, #1e3a8a 0%, #2563eb 60%, #3b82f6 100%);
  box-shadow: 0 20px 48px rgba(37, 99, 235, 0.30);
}
.cta-inner h2 { margin: 0 0 10px; font-size: 28px; font-weight: 800; }
.cta-inner p { margin: 0 0 24px; font-size: 14.5px; opacity: 0.88; }
.cta-inner .el-button--primary { background: #fff; border-color: #fff; color: #2563eb; font-weight: 700; }
.cta-inner .el-button--primary:hover { background: #eef4fe; border-color: #eef4fe; }

/* ---------- 页脚 ---------- */
.footer { border-top: 1px solid var(--color-border); background: #fafbfd; }
.footer-inner {
  max-width: 1160px; margin: 0 auto; padding: 26px 24px;
  display: flex; align-items: center; justify-content: space-between; gap: 16px;
}
.footer-logo { display: flex; align-items: center; gap: 8px; font-weight: 700; font-size: 14px; }
.footer p { margin: 0; font-size: 12.5px; color: var(--color-muted-foreground); }

/* ---------- 响应式 ---------- */
@media (max-width: 992px) {
  .hero-inner { grid-template-columns: 1fr; gap: 40px; padding-top: 48px; }
  .hero-visual { max-width: 560px; }
  .feature-grid, .why-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .nav-links { display: none; }
  .hero h1 { font-size: 34px; }
  .hero { padding: 0; }
  .hero-inner { padding: 40px 20px 56px; }
  .hero-cta .el-button { flex: 1; }
  .feature-grid, .why-grid { grid-template-columns: 1fr; }
  .features, .why { padding: 56px 20px; }
  .cta { padding: 0 20px 56px; }
  .cta-inner { padding: 44px 20px; }
  .footer-inner { flex-direction: column; text-align: center; }
  .mock-card { transform: none; }
}
</style>
