export interface Result<T = unknown> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

export interface LoginUser {
  id: number
  username: string
  realName: string
  roles: string[]
}

export interface Customer {
  id?: number
  name: string
  industry?: string
  source?: string
  level?: string
  status?: string
  phone?: string
  email?: string
  address?: string
  website?: string
  ownerId?: number | null
  remark?: string
  createTime?: string
}

export interface Contact {
  id?: number
  customerId: number
  name: string
  phone?: string
  email?: string
  position?: string
  wechat?: string
  isPrimary?: number
  remark?: string
}

export interface FollowUp {
  id?: number
  customerId: number
  contactId?: number | null
  type?: string
  content: string
  nextFollowTime?: string | null
  createBy?: number
  createTime?: string
}

export interface Opportunity {
  id?: number
  name: string
  customerId: number
  amount?: number
  stage?: string
  expectedCloseDate?: string
  winAmount?: number
  loseReason?: string
  ownerId?: number | null
  remark?: string
  createTime?: string
}

export interface DictItem {
  id?: number
  dictType: string
  dictLabel: string
  dictValue: string
  sort?: number
  status?: number
}

export interface UserItem {
  id: number
  username: string
  realName: string
  phone?: string
  email?: string
  status: number
  createTime?: string
  roleCodes: string[]
}
