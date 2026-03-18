import request from './request'

// 概览数据（今日/本月/累计）
export interface SalesOverview {
  today: {
    salesAmount: number
    orderCount: number
    salesVolume: number
  }
  month: {
    salesAmount: number
    orderCount: number
    salesVolume: number
  }
  total: {
    salesAmount: number
    orderCount: number
    salesVolume: number
  }
}

// 趋势数据
export interface SalesTrend {
  dates: string[]
  salesAmount: number[]
  orderCount: number[]
  salesVolume: number[]
}

// 系列销量
export interface SeriesSales {
  seriesName: string
  value: number
}

// 品牌销量
export interface BrandSales {
  brandName: string
  value: number
}

// 热销商品
export interface TopProduct {
  productId: number
  productName: string
  salesAmount: number
  salesVolume: number
}

export const statsApi = {
  async getOverview() {
    const response = await request.get<any, any>('/api/stats/sales/overview')
    return response.data || response  // 确保返回 data
  },
  async getTrend(params?: any) {
    const response = await request.get<any, any>('/api/stats/sales/trend', { params })
    return response.data || response
  },
  async getSalesBySeries(params?: any) {
    const response = await request.get<any, any>('/api/stats/sales/by-series', { params })
    return response.data || response
  },
  async getSalesByBrand(params?: any) {
    const response = await request.get<any, any>('/api/stats/sales/by-brand', { params })
    return response.data || response
  },
  async getTopProducts(params?: any) {
    const response = await request.get<any, any>('/api/stats/sales/top-products', { params })
    return response.data || response
  }
}