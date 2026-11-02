import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface Order {
  id: number
  orderNo: string
  userId: number
  userName: string
  totalAmount: number
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
  createdAt: string
  items: {
    productId: number
    productName: string
    quantity: number
    price: number
    image: string
  }[]
}

// 模拟订单数据
const mockOrders: Order[] = [
  {
    id: 1,
    orderNo: '20250304001',
    userId: 101,
    userName: '张**',
    totalAmount: 599,
    status: 'pending',
    createdAt: '2025-03-04 10:23',
    items: [
      { productId: 1, productName: '道成·云栖客厅地毯', quantity: 1, price: 599, image: '/images/product1.jpg' }
    ]
  },
  {
    id: 2,
    orderNo: '20250304002',
    userId: 102,
    userName: '李**',
    totalAmount: 1299,
    status: 'paid',
    createdAt: '2025-03-04 11:45',
    items: [
      { productId: 2, productName: '飞湃·几何方块毯', quantity: 2, price: 399, image: '/images/product2.jpg' },
      { productId: 3, productName: '红塬·北欧简约满铺', quantity: 1, price: 501, image: '/images/product3.jpg' }
    ]
  },
  {
    id: 3,
    orderNo: '20250304003',
    userId: 103,
    userName: '王**',
    totalAmount: 399,
    status: 'shipped',
    createdAt: '2025-03-04 14:12',
    items: [
      { productId: 4, productName: '道成·商务方块毯', quantity: 1, price: 399, image: '/images/product4.jpg' }
    ]
  },
  {
    id: 4,
    orderNo: '20250304004',
    userId: 104,
    userName: '赵**',
    totalAmount: 899,
    status: 'completed',
    createdAt: '2025-03-04 15:30',
    items: [
      { productId: 5, productName: '飞湃·现代简约满铺', quantity: 1, price: 899, image: '/images/product5.jpg' }
    ]
  },
  {
    id: 5,
    orderNo: '20250304005',
    userId: 105,
    userName: '孙**',
    totalAmount: 199,
    status: 'cancelled',
    createdAt: '2025-03-04 16:20',
    items: [
      { productId: 6, productName: '红塬·防滑门垫', quantity: 1, price: 199, image: '/images/product6.jpg' }
    ]
  }
]

export const useOrderStore = defineStore('order', () => {
  const orders = ref<Order[]>([...mockOrders])

  // 获取所有订单
  const fetchOrders = () => {
    return orders.value
  }

  // 更新订单状态
  const updateOrderStatus = (orderId: number, newStatus: Order['status']) => {
    const order = orders.value.find(o => o.id === orderId)
    if (order) {
      order.status = newStatus
    }
  }

  // 获取订单详情
  const getOrderById = (orderId: number) => {
    return orders.value.find(o => o.id === orderId)
  }

  return {
    orders,
    fetchOrders,
    updateOrderStatus,
    getOrderById
  }
})