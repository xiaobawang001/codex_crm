import { defineStore } from 'pinia'
import { dictApi } from '@/api/modules'
import type { DictItem } from '@/types'

export const useDictStore = defineStore('dict', {
  state: () => ({
    items: [] as DictItem[],
    loaded: false,
  }),
  getters: {
    byType: (state) => (type: string) => state.items.filter((i) => i.dictType === type),
    labelOf: (state) => (type: string, value?: string) => {
      const item = state.items.find((i) => i.dictType === type && i.dictValue === value)
      return item ? item.dictLabel : value || '-'
    },
  },
  actions: {
    async load() {
      if (this.loaded) return
      this.items = await dictApi.listAll()
      this.loaded = true
    },
    async refresh() {
      this.items = await dictApi.listAll()
      this.loaded = true
    },
  },
})
