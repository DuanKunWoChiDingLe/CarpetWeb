// import request from './request'

// // 概览数据（今日/本月/累计）
// export interface SalesOverview {
//   today: {
//     salesAmount: number
//     orderCount: number
//     salesVolume: number
//   }
//   month: {
//     salesAmount: number
//     orderCount: number
//     salesVolume: number
//   }
//   total: {
//     salesAmount: number
//     orderCount: number
//     salesVolume: number
//   }
// }

// // 趋势数据
// export interface SalesTrend {
//   dates: string[]
//   salesAmount: number[]
//   orderCount: number[]
//   salesVolume: number[]
// }

// // 系列销量
// export interface SeriesSales {
//   seriesName: string
//   value: number
// }

// // 品牌销量
// export interface BrandSales {
//   brandName: string
//   value: number
// }

// // 热销商品
// export interface TopProduct {
//   productId: number
//   productName: string
//   salesAmount: number
//   salesVolume: number
// }

// export const statsApi = {
//   async getOverview() {
//     const response = await request.get<any, any>('/stats/sales/overview')
//     return response.data || response  // 确保返回 data
//   },
//   async getTrend(params?: any) {
//     const response = await request.get<any, any>('/stats/sales/trend', { params })
//     return response.data || response
//   },
//   async getSalesBySeries(params?: any) {
//     const response = await request.get<any, any>('/stats/sales/by-series', { params })
//     return response.data || response
//   },
//   async getSalesByBrand(params?: any) {
//     const response = await request.get<any, any>('/stats/sales/by-brand', { params })
//     return response.data || response
//   },
//   async getTopProducts(params?: any) {
//     const response = await request.get<any, any>('/stats/sales/top-products', { params })
//     return response.data || response
//   }
// }

import request from './request'

// 统一响应格式（如果项目中有公共定义，可以导入；这里临时定义）
interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

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

// 查询参数接口
export interface TrendQueryParams {
  startDate?: string
  endDate?: string
  interval?: 'day' | 'week' | 'month'
}

export interface SeriesSalesQueryParams {
  startDate?: string
  endDate?: string
  type?: 'amount' | 'volume'
}

export interface BrandSalesQueryParams {
  startDate?: string
  endDate?: string
  type?: 'amount' | 'volume'
}

export interface TopProductsQueryParams {
  limit?: number
  startDate?: string
  endDate?: string
}

export const statsApi = {
  async getOverview(): Promise<SalesOverview> {
    const response = await request.get<ApiResponse<SalesOverview>>('/stats/sales/overview')
    return response.data
  },

  async getTrend(params?: TrendQueryParams): Promise<SalesTrend> {
    const response = await request.get<ApiResponse<SalesTrend>>('/stats/sales/trend', { params })
    return response.data
  },

  async getSalesBySeries(params?: SeriesSalesQueryParams): Promise<SeriesSales[]> {
    const response = await request.get<ApiResponse<SeriesSales[]>>('/stats/sales/by-series', { params })
    return response.data
  },

  async getSalesByBrand(params?: BrandSalesQueryParams): Promise<BrandSales[]> {
    const response = await request.get<ApiResponse<BrandSales[]>>('/stats/sales/by-brand', { params })
    return response.data
  },

  async getTopProducts(params?: TopProductsQueryParams): Promise<TopProduct[]> {
    const response = await request.get<ApiResponse<TopProduct[]>>('/stats/sales/top-products', { params })
    return response.data
  }
}