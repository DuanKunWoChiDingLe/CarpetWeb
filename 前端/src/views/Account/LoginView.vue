<!-- 登录组件 -->
<template>
  <div class="auth-view">
    <el-card class="auth-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>欢迎回来</h2>
          <p class="subtitle">请登录您的账户</p>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="0" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            :prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="密码"
            :prefix-icon="Lock"
            size="large"
            clearable
          >
            <template #suffix>
              <el-icon class="password-icon" @click="showPassword = !showPassword">
                <component :is="showPassword ? View : Hide" />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleLogin"
            size="large"
            class="submit-btn"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="form-footer">
          <span class="register-link" @click="$router.push('/register')">
            还没有账号？立即注册
          </span>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, View, Hide } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: ''
})
const loading = ref(false)
const formRef = ref()
const showPassword = ref(false)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  const success = await userStore.login(form.value.username, form.value.password)
  loading.value = false
  if (success) {
    ElMessage.success('登录成功')
    // 获取 redirect 参数，默认跳转首页
    const redirect = route.query.redirect as string || '/'
    router.replace(redirect)  // 使用 replace 避免登录页留在历史中
  }
}
</script>

<style scoped>
.auth-view {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;           /* 固定为视口高度，禁止页面滚动 */
  overflow: hidden;        /* 隐藏溢出，删除滚动条 */
  background: url('@/assets/login-bg.jpg') no-repeat center center fixed;
  background-size: cover;
  position: relative;
  padding: 20px;
}

/* 半透明遮罩层 */
.auth-view::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  max-height: calc(100vh - 40px);  /* 限制最大高度，留出上下内边距空间 */
  overflow-y: auto;                /* 当内容超出时，卡片内部滚动 */
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  border: none;
  position: relative;
  z-index: 2;
}

.card-header {
  text-align: center;
  padding: 20px 0 10px;
}

.card-header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  letter-spacing: 0.5px;
}

.card-header .subtitle {
  margin: 8px 0 0;
  color: #666;
  font-size: 14px;
}

.login-form {
  padding: 0 20px 30px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

.password-icon {
  cursor: pointer;
  color: #909399;
  transition: color 0.2s;
}

.password-icon:hover {
  color: #667eea;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: opacity 0.3s;
}

.submit-btn:hover {
  opacity: 0.9;
}

.submit-btn:active {
  transform: scale(0.98);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
}

.register-link {
  color: #667eea;
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.2s;
}

.register-link:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 小屏幕适配 */
@media screen and (max-width: 480px) {
  .auth-card {
    max-width: 100%;
  }
}
</style>