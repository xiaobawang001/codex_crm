import request from './request'
import type { Customer, Contact, FollowUp, Opportunity, DictItem, UserItem, PageResult } from '@/types'

export const authApi = {
  login: (data: { username: string; password: string }) => request.post('/auth/login', data),
  logout: () => request.post('/auth/logout'),
  changePassword: (data: { oldPassword: string; newPassword: string }) => request.put('/auth/password', data),
}

export const customerApi = {
  page: (params: Record<string, unknown>) => request.get<PageResult<Customer>>('/customers', { params }),
  detail: (id: number) => request.get<Customer>(`/customers/${id}`),
  create: (data: Partial<Customer>) => request.post('/customers', data),
  update: (id: number, data: Partial<Customer>) => request.put(`/customers/${id}`, data),
  remove: (id: number) => request.delete(`/customers/${id}`),
  claim: (id: number) => request.put(`/customers/${id}/claim`),
  assign: (id: number, userId: number) => request.put(`/customers/${id}/assign`, { userId }),
}

export const contactApi = {
  page: (params: Record<string, unknown>) => request.get<PageResult<Contact>>('/contacts', { params }),
  create: (data: Partial<Contact>) => request.post('/contacts', data),
  update: (id: number, data: Partial<Contact>) => request.put(`/contacts/${id}`, data),
  remove: (id: number) => request.delete(`/contacts/${id}`),
}

export const followUpApi = {
  page: (params: Record<string, unknown>) => request.get<PageResult<FollowUp>>('/follow-ups', { params }),
  todo: () => request.get<FollowUp[]>('/follow-ups/todo'),
  create: (data: Partial<FollowUp>) => request.post('/follow-ups', data),
  update: (id: number, data: Partial<FollowUp>) => request.put(`/follow-ups/${id}`, data),
  remove: (id: number) => request.delete(`/follow-ups/${id}`),
}

export const opportunityApi = {
  page: (params: Record<string, unknown>) => request.get<PageResult<Opportunity>>('/opportunities', { params }),
  create: (data: Partial<Opportunity>) => request.post('/opportunities', data),
  update: (id: number, data: Partial<Opportunity>) => request.put(`/opportunities/${id}`, data),
  remove: (id: number) => request.delete(`/opportunities/${id}`),
  changeStage: (id: number, stage: string) => request.put(`/opportunities/${id}/stage`, { stage }),
  win: (id: number, winAmount: number) => request.put(`/opportunities/${id}/win`, { winAmount }),
  lose: (id: number, loseReason: string) => request.put(`/opportunities/${id}/lose`, { loseReason }),
}

export const dashboardApi = {
  summary: () => request.get<Record<string, number>>('/dashboard/summary'),
  customerStats: () => request.get<Array<{ name: string; cnt: number }>>('/dashboard/customer-stats'),
  sourceStats: () => request.get<Array<{ name: string; cnt: number }>>('/dashboard/source-stats'),
  opportunityStats: () =>
    request.get<Array<{ stage: string; cnt: number; amount: number }>>('/dashboard/opportunity-stats'),
  trend: () =>
    request.get<Array<{ month: string; customerCount: number; opportunityAmount: number }>>('/dashboard/trend'),
}

export const dictApi = {
  listByType: (dictType: string) => request.get<DictItem[]>(`/dict/${dictType}`),
  listAll: (params?: Record<string, unknown>) => request.get<DictItem[]>('/dict/all/list', { params }),
  save: (data: Partial<DictItem>) => request.post('/dict', data),
  remove: (id: number) => request.delete(`/dict/${id}`),
}

export const userApi = {
  page: (params: Record<string, unknown>) => request.get<PageResult<UserItem>>('/users', { params }),
  roles: () => request.get<Array<{ id: number; roleCode: string; roleName: string }>>('/roles'),
  create: (data: Record<string, unknown>) => request.post('/users', data),
  update: (id: number, data: Record<string, unknown>) => request.put(`/users/${id}`, data),
  updateStatus: (id: number, status: number) => request.put(`/users/${id}/status`, null, { params: { status } }),
  resetPassword: (id: number, password: string) => request.put(`/users/${id}/reset-password`, { password }),
  remove: (id: number) => request.delete(`/users/${id}`),
}
