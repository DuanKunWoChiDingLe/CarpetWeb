import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const proxyTarget = env.VITE_PROXY_TARGET || 'http://127.0.0.1:8080'
  const proxy = {
    '/api': { target: proxyTarget, changeOrigin: true },
    '/uploads': { target: proxyTarget, changeOrigin: true },
    '/ws': { target: proxyTarget, changeOrigin: true, ws: true },
  }

  return {
    plugins: [
      vue(),
      vueDevTools(),
    ],
    define: {
      global: 'globalThis',
      'process.env': {},
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    server: {
      host: '0.0.0.0',
      proxy,
    },
    preview: {
      host: '0.0.0.0',
      proxy,
    },
  }
})

