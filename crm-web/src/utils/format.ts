import dayjs from 'dayjs'

export function formatDateTime(value?: string | null): string {
  return value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-'
}

export function formatDate(value?: string | null): string {
  return value ? dayjs(value).format('YYYY-MM-DD') : '-'
}

export function formatMoney(value?: number | null): string {
  if (value === null || value === undefined) return '-'
  return '¥' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}
