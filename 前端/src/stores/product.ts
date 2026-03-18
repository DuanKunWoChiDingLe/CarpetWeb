// src/stores/product.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Product } from '@/types/product'

// 模拟商品数据
const mockProducts: Product[] = [
  {
    id: 1,
    name: '道成·云栖客厅地毯',
    brand: '道成',
    type: '满铺毯',
    pricePerSqm: 199,
    images: '/images/product1.jpg',
    sales: 120,
    createdTime: '2025-01-10',
    stock: 50,
    status: 1
  },
  {
    id: 2,
    name: '飞湃·几何方块毯',
    brand: '飞湃',
    type: '方块毯',
    size: '50*50',
    pricePerSqm: 89,
    images: '/images/product2.jpg',
    sales: 80,
    createdTime: '2025-01-15',
    stock: 30,
    status: 1
  },
  {
    id: 3,
    name: '红塬·北欧简约满铺',
    brand: '红塬',
    type: '满铺毯',
    pricePerSqm: 159,
    images: '/images/product3.jpg',
    sales: 60,
    createdTime: '2025-01-20',
    stock: 20,
    status: 0
  },
  {
    id: 4,
    name: '道成·商务方块毯',
    brand: '道成',
    type: '方块毯',
    size: '100*25',
    pricePerSqm: 120,
    images: '/images/product4.jpg',
    sales: 45,
    createdTime: '2025-01-25',
    stock: 15,
    status: 1
  }
]

export const useProductStore = defineStore('product', () => {
  // 状态
  const allProducts = ref<Product[]>([...mockProducts])
  const products = ref<Product[]>([]) // 当前展示的商品列表（分页后）
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
        result.sort((a, b) => new Date(b.createdTime).getTime() - new Date(a.createdTime).getTime())
        break
      case 'sales':
        result.sort((a, b) => b.sales - a.sales)
        break
      default:
        result.sort((a, b) => a.id - b.id)
    }

    return result
  }

  // 加载数据（模拟 API）
  async function fetchProducts(reset = false) {
    if (loading.value) return
    if (!reset && !hasMore.value) return

    loading.value = true
    await new Promise(resolve => setTimeout(resolve, 600))

    const filtered = filterProducts(allProducts.value, filter.value, sortBy.value)
    const start = reset ? 0 : (page.value - 1) * pageSize
    const end = start + pageSize
    const pageItems = filtered.slice(start, end)

    if (reset) {
      products.value = pageItems
      page.value = 1
    } else {
      products.value = [...products.value, ...pageItems]
      page.value++
    }
    hasMore.value = end < filtered.length
    loading.value = false
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
    applyFilter,      // 确保这一行存在！
    resetFilter
  }
})