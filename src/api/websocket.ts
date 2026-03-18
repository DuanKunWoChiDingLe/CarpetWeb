import SockJS from 'sockjs-client'
import { Client, Message } from '@stomp/stompjs'

// 单例 WebSocket 连接
let stompClient: Client | null = null
let isConnected = false

// 消息回调映射
const subscriptions: Map<string, (msg: any) => void> = new Map()

// 获取基础 URL（用于 WebSocket 端点）
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export const connectWebSocket = (onConnect?: () => void, onError?: (error: any) => void) => {
  if (stompClient && isConnected) {
    onConnect?.()
    return
  }

  const socket = new SockJS(`${baseURL}/ws`)
  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    debug: (str) => console.log('[STOMP]', str),
    onConnect: () => {
      isConnected = true
      console.log('WebSocket 连接成功')
      // 重新订阅所有已注册的订阅
      subscriptions.forEach((callback, destination) => {
        stompClient?.subscribe(destination, (message: Message) => {
          const body = JSON.parse(message.body)
          callback(body)
        })
      })
      onConnect?.()
    },
    onStompError: (frame) => {
      console.error('STOMP 错误', frame)
      onError?.(frame)
    },
    onWebSocketError: (event) => {
      console.error('WebSocket 错误', event)
      onError?.(event)
    },
    onDisconnect: () => {
      isConnected = false
      console.log('WebSocket 断开')
    }
  })

  stompClient.activate()
}

export const disconnectWebSocket = () => {
  stompClient?.deactivate()
  stompClient = null
  isConnected = false
}

export const subscribe = (destination: string, callback: (msg: any) => void) => {
  console.log('subscribe called with destination:', destination)
  subscriptions.set(destination, callback)
  if (isConnected && stompClient) {
    console.log('立即订阅', destination)
    stompClient.subscribe(destination, (message: Message) => {
      const body = JSON.parse(message.body)
      callback(body)
    })
  } else {
    console.log('WebSocket未连接，订阅已缓存')
  }
}

export const unsubscribe = (destination: string) => {
  subscriptions.delete(destination)
}

export const sendMessage = (destination: string, body: Record<string, unknown>) => {
  if (stompClient && isConnected) {
    stompClient.publish({
      destination: `/app${destination}`,
      body: JSON.stringify(body)
    })
  } else {
    console.warn('WebSocket 未连接，消息未发送')
  }
}