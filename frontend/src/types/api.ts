// src/types/api.ts
export interface Page<T> {
  content: T[]          // 当前页数据
  totalElements: number // 总记录数
  totalPages: number    // 总页数
  number: number        // 当前页码（从0开始）
  size: number          // 每页大小
  first: boolean
  last: boolean
  empty: boolean
  // 其他字段可按需添加
}