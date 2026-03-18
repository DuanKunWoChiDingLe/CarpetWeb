import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'


const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.mount('#app')


// 添加开发者调试函数（仅开发环境）
if (import.meta.env.DEV) {
  // @ts-ignore
  window.enableMerchantReg = () => {
    sessionStorage.setItem('tempMerchantReg', 'true')
    console.log('%c✅ 商家注册模式已激活（一次有效）', 'color: #67c23a; font-weight: bold')
  }
}