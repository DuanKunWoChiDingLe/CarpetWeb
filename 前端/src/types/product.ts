import type { Product as ApiProduct } from '@/api/product'

export interface Product extends ApiProduct {
  brand?: string
  type?: string
  sales?: number
}
