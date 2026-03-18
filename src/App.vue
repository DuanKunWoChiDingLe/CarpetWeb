<template>
  <div class="app">
    <NavBar />
    <main class="main-content">
      <router-view :key="$route.fullPath" />
    </main>
  </div>
</template>

<script setup lang="ts">
import NavBar from './components/layout/NavBar.vue'
import { onMounted, onUnmounted,watch  } from 'vue'
import { useUserStore } from './stores/user'
import { useCartStore } from '@/stores/cart'
import { connectWebSocket, subscribe, unsubscribe, disconnectWebSocket } from '@/api/websocket'
import { ElNotification } from 'element-plus'


const userStore = useUserStore()
const cartStore = useCartStore()

onMounted(() =>{
  userStore.fetchCurrentUser()
})

onMounted(async () => {
  await userStore.fetchCurrentUser()
  if (userStore.isLoggedIn) {
    await cartStore.fetchCart()
  }
})

// onMounted(() => {
//   console.log('App onMounted, isLoggedIn:', userStore.isLoggedIn)
//   if (userStore.isLoggedIn) {
//     console.log('用户ID:', userStore.userInfo?.id)
//     connectWebSocket(() => {
//       console.log('WebSocket 连接成功回调触发')
//       const userId = userStore.userInfo?.id
//       if (userId) {
//         console.log('准备订阅，userId:', userId)
//         subscribe(`/user/${userId}/queue/notifications`, (msg: any) => {
//           console.log('收到消息:', msg)
//           ElNotification({ title: msg.type, message: msg.content, type: 'info' })
//         })
//         console.log('订阅地址：', `/user/${userId}/queue/notifications`)
//       } else {
//         console.warn('userId 为空，无法订阅')
//       }
//     })
//   } else {
//     console.log('用户未登录，不建立 WebSocket 连接')
//   }
// })

watch(() => userStore.isLoggedIn, (isLoggedIn) => {
  if (isLoggedIn) {
    connectWebSocket(() => {
      console.log('WebSocket 连接成功')
      const userId = userStore.userInfo?.id
      const role = userStore.userInfo?.role

      // 所有登录用户订阅个人通知（可选）
      if (userId) {
        subscribe(`/user/${userId}/queue/notifications`, (msg: any) => {
          console.log('个人通知:', msg)
          ElNotification({
            title: msg.type === 'order' ? '订单通知' : '系统通知',
            message: msg.content,
            type: 'info'
          })
        })
      }

      // 商家额外订阅新订单广播
      if (role === 'merchant') {
        subscribe('/topic/newOrders', (count: number) => {
          console.log('新订单数量更新:', count)
          notificationStore.setNewOrderCount(count)
        })
      }
    })
  } else {
    disconnectWebSocket()
  }
}, { immediate: true })

onUnmounted(() => {
  disconnectWebSocket()
})

</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  margin: 0;
  padding: 0;
}
.app {
  height: 100vh;           /* 改为固定高度，避免 min-height 可能带来的不确定性 */
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;                 /* 自动填满剩余高度 */
  overflow-y: auto;        /* 如果内容太多，在此容器内滚动，页面整体无滚动条 */
}
</style>