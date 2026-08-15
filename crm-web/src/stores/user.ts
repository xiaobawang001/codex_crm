import { defineStore } from 'pinia'
import type { LoginUser } from '@/types'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('crm_token') || '',
    user: JSON.parse(localStorage.getItem('crm_user') || 'null') as LoginUser | null,
  }),
  getters: {
    isLogin: (state) => !!state.token,
    isAdmin: (state) => !!state.user?.roles.includes('ADMIN'),
    isManager: (state) =>
      !!state.user?.roles.includes('ADMIN') || !!state.user?.roles.includes('MANAGER'),
  },
  actions: {
    setLogin(token: string, user: LoginUser) {
      this.token = token
      this.user = user
      localStorage.setItem('crm_token', token)
      localStorage.setItem('crm_user', JSON.stringify(user))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('crm_token')
      localStorage.removeItem('crm_user')
    },
  },
})
