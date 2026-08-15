import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { public: true, title: '首页' },
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      redirect: '/home',
      children: [
        { path: 'dashboard', name: 'dashboard', component: () => import('@/views/DashboardView.vue'), meta: { title: '工作台' } },
        { path: 'customers', name: 'customers', component: () => import('@/views/customer/CustomerListView.vue'), meta: { title: '客户管理' } },
        { path: 'customers/:id', name: 'customer-detail', component: () => import('@/views/customer/CustomerDetailView.vue'), meta: { title: '客户详情' } },
        { path: 'contacts', name: 'contacts', component: () => import('@/views/contact/ContactListView.vue'), meta: { title: '联系人' } },
        { path: 'follow-ups', name: 'follow-ups', component: () => import('@/views/followup/FollowUpListView.vue'), meta: { title: '跟进记录' } },
        { path: 'opportunities', name: 'opportunities', component: () => import('@/views/opportunity/OpportunityListView.vue'), meta: { title: '商机管理' } },
        { path: 'system/users', name: 'system-users', component: () => import('@/views/system/UserManageView.vue'), meta: { title: '用户管理', admin: true } },
        { path: 'system/dict', name: 'system-dict', component: () => import('@/views/system/DictManageView.vue'), meta: { title: '字典管理', admin: true } },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.meta.public) {
    return true
  }
  if (!userStore.isLogin) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.admin && !userStore.isAdmin) {
    return { path: '/dashboard' }
  }
  return true
})

export default router
