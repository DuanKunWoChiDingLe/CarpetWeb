export interface Product {
  id: number
  name: string
  brand: '道成' | '飞湃' | '红塬'
  type: '满铺毯' | '方块毯'
  size?: string
  pricePerSqm: number
  images: string
  sales: number
  stock: number
  status: 0 | 1
  description?: string  // 新增（后端可能返回）
  // 移除 createdTime，因为列表接口不返回
}