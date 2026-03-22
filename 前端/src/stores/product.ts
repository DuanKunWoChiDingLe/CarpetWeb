// src/stores/product.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Product } from '@/types/product'
import { productApi } from '@/api/product'

export const useProductStore = defineStore('product', () => {
  // 状态：所有商品（用于前端搜索建议等）和当前展示列表
  const allProducts = ref<Product[]>([])          // 将从 API 获取
  const products = ref<Product[]>([])             // 当前展示的商品列表（分页后）
  const filter = ref({
    keyword: '',
    brand: '',
    types: [] as string[],
    minPrice: null as number | null,
    maxPrice: null as number | null
  })
  const sortBy = ref<'default' | 'priceAsc' | 'priceDesc' | 'newest' | 'sales'>('default')
  const page = ref(1)
  const pageSize = 12
  const hasMore = ref(true)
  const loading = ref(false)

  // 计算属性：已选条件数量
  const filterCount = computed(() => {
    let count = 0
    if (filter.value.brand) count++
    if (filter.value.types.length) count++
    if (filter.value.minPrice != null) count++
    if (filter.value.maxPrice != null) count++
    if (filter.value.keyword) count++
    return count
  })

  // 过滤和排序（纯函数）
  function filterProducts(products: Product[], filterParams: typeof filter.value, sort: typeof sortBy.value) {
    let result = [...products]

    // 关键词搜索
    if (filterParams.keyword) {
      const kw = filterParams.keyword.toLowerCase()
      result = result.filter(p => p.name.toLowerCase().includes(kw) || p.brand.toLowerCase().includes(kw))
    }

    // 品牌
    if (filterParams.brand) {
      result = result.filter(p => p.brand === filterParams.brand)
    }

    // 类型
    if (filterParams.types.length > 0) {
      result = result.filter(p => filterParams.types.includes(p.type))
    }

    // 价格区间
    if (filterParams.minPrice != null) {
      result = result.filter(p => p.pricePerSqm >= filterParams.minPrice!)
    }
    if (filterParams.maxPrice != null) {
      result = result.filter(p => p.pricePerSqm <= filterParams.maxPrice!)
    }

    // 排序
    switch (sort) {
      case 'priceAsc':
        result.sort((a, b) => a.pricePerSqm - b.pricePerSqm)
        break
      case 'priceDesc':
        result.sort((a, b) => b.pricePerSqm - a.pricePerSqm)
        break
      case 'newest':
        result.sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime())
        break
      case 'sales':
        result.sort((a, b) => (b.sales || 0) - (a.sales || 0))
        break
      default:
        result.sort((a, b) => a.id - b.id)
    }

    return result
  }

  // 加载数据（从 API 获取）
  async function fetchProducts(reset = false) {
    if (loading.value) return
    if (!reset && !hasMore.value) return

    loading.value = true
    try {
      // TODO: 调用 productApi 获取分页商品，参数包括筛选和排序
      // 以下为模拟，实际应替换为真实 API 调用
      // const res = await productApi.getProducts({ ...filter.value, page: page.value, size: pageSize })
      // products.value = reset ? res.content : [...products.value, ...res.content]
      // total = res.totalElements
      // hasMore.value = products.value.length < total
    } catch (error) {
      console.error('获取商品列表失败', error)
    } finally {
      loading.value = false
    }
  }

  // 应用筛选和排序（重置并重新加载）
  function applyFilter(newFilter?: Partial<typeof filter.value>, newSort?: typeof sortBy.value) {
    if (newFilter) {
      filter.value = { ...filter.value, ...newFilter }
    }
    if (newSort !== undefined) {
      sortBy.value = newSort
    }
    page.value = 1
    hasMore.value = true
    return fetchProducts(true)
  }

  // 重置所有筛选
  function resetFilter() {
    filter.value = { keyword: '', brand: '', types: [], minPrice: null, maxPrice: null }
    sortBy.value = 'default'
    applyFilter()
  }

  return {
    allProducts,
    products,
    filter,
    sortBy,
    page,
    hasMore,
    loading,
    filterCount,
    fetchProducts,
    applyFilter,
    resetFilter
  }
})