import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi, type UserInfo } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { useCartStore } from './cart'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const isLoggedIn = ref(!!token.value)
  const sessionRestored = ref(false)
  const cartStore = useCartStore()

  const saveSession = (nextToken: string, nextUser: UserInfo) => {
    token.value = nextToken
    userInfo.value = nextUser
    isLoggedIn.value = true
    sessionRestored.value = true
    localStorage.setItem('token', nextToken)
  }

  const clearSession = (showMessage = false) => {
    token.value = ''
    userInfo.value = null
    isLoggedIn.value = false
    sessionRestored.value = false
    localStorage.removeItem('token')
    cartStore.resetCart()
    if (showMessage) ElMessage.success('已退出登录')
  }

  const login = async (username: string, password: string) => {
    try {
      const res = await authApi.login({ username, password })
      saveSession(res.token, {
        id: res.id,
        username: res.username,
        nickname: res.nickname,
        role: res.role,
        avatar: res.avatar
      })
      ElMessage.success('登录成功')
      return true
    } catch (error: any) {
      const payload = error.response?.data
      const message = payload?.message || payload?.error
      if (message) {
        ElMessage.error(message)
      } else if (error.request) {
        ElMessage.error('无法连接到服务器，请检查网络或后端是否启动')
      } else {
        ElMessage.error('登录失败：' + error.message)
      }
      return false
    }
  }

  // 公开注册只创建普通用户，商家账号必须通过后台授权创建
  const register = async (username: string, password: string) => {
    try {
      const res = await authApi.register({ username, password })
      saveSession(res.token, {
        id: res.id,
        username: res.username,
        nickname: res.nickname,
        role: res.role,
        avatar: res.avatar
      })
      ElMessage.success('注册成功，已自动登录')
      return true
    } catch (error: any) {
      const payload = error.response?.data
      const fieldErrors = payload?.data
      if (fieldErrors && typeof fieldErrors === 'object' && !Array.isArray(fieldErrors)) {
        Object.values(fieldErrors).forEach(message => ElMessage.error(String(message)))
      } else {
        ElMessage.error(payload?.message || '注册失败，请稍后重试')
      }
      return false
    }
  }

  const logout = () => clearSession(true)

  // 页面刷新后恢复当前用户；失败时静默清理过期会话
  const fetchCurrentUser = async () => {
    if (!token.value) {
      clearSession(false)
      sessionRestored.value = true
      return
    }
    try {
      const res = await authApi.getCurrentUser()
      userInfo.value = res
      isLoggedIn.value = true
    } catch {
      clearSession(false)
    } finally {
      sessionRestored.value = true
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    sessionRestored,
    login,
    register,
    logout,
    fetchCurrentUser
  }
}, {
  persist: {
    key: 'daocheng-user',
    pick: ['token', 'userInfo']
  }
})





