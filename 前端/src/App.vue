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
import { onMounted, onUnmounted, watch } from 'vue'
import { useUserStore } from './stores/user'
import { useCartStore } from '@/stores/cart'
import { connectWebSocket, subscribe, disconnectWebSocket } from '@/api/websocket'
import { ElNotification } from 'element-plus'
import { useNotificationStore } from '@/stores/notification'

const notificationStore = useNotificationStore()
const userStore = useUserStore()
const cartStore = useCartStore()

const setupNotifications = () => {
  connectWebSocket(() => {
    const userId = userStore.userInfo?.id
    const role = userStore.userInfo?.role

    if (userId) {
      subscribe(`/user/${userId}/queue/notifications`, (msg: any) => {
        ElNotification({
          title: msg.type === 'order' ? '订单通知' : '系统通知',
          message: msg.content,
          type: 'info'
        })
      })
    }

    if (role === 'merchant') {
      subscribe('/topic/newOrders', (msg: unknown) => {
        if (typeof msg === 'number') notificationStore.setNewOrderCount(msg)
      })
    }
  })
}

onMounted(async () => {
  if (userStore.token && !userStore.sessionRestored) {
    await userStore.fetchCurrentUser()
  }
  if (userStore.isLoggedIn) {
    await cartStore.fetchCart()
    setupNotifications()
  }
})

watch(() => userStore.isLoggedIn, async (isLoggedIn) => {
  if (isLoggedIn) {
    await cartStore.fetchCart()
    setupNotifications()
  } else {
    disconnectWebSocket()
  }
})

onUnmounted(() => {
  disconnectWebSocket()
})
</script>

<style>
:root {
  color-scheme: light;
  font-synthesis: none;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  width: 100%;
  min-height: 100%;
}

body {
  min-height: 100dvh;
  overflow-x: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  color: #303133;
  background: #f5f7fa;
}

button,
input,
textarea,
select {
  font: inherit;
}

img,
video {
  max-width: 100%;
}

.app {
  min-height: 100dvh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

@media (max-width: 768px) {
  .app {
    min-height: 100svh;
  }

  .main-content {
    overflow-x: hidden;
  }
}
</style>



