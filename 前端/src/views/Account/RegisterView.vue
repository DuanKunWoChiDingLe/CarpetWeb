<!-- 注册组件 -->
<template>
  <div class="auth-view">
    <el-card class="auth-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>创建账户</h2>
          <p class="subtitle">请填写注册信息</p>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="0" class="register-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名（4-20位字母/数字）"
            :prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="昵称（可选）"
            :prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="密码（8-20位，含字母和数字）"
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

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            :type="showConfirmPassword ? 'text' : 'password'"
            placeholder="确认密码"
            :prefix-icon="Lock"
            size="large"
            clearable
          >
            <template #suffix>
              <el-icon class="password-icon" @click="showConfirmPassword = !showConfirmPassword">
                <component :is="showConfirmPassword ? View : Hide" />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleRegister"
            size="large"
            class="submit-btn"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="form-footer">
          <span class="login-link" @click="$router.push('/login')">
            已有账号？立即登录
          </span>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, View, Hide } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})
const loading = ref(false)
const formRef = ref()
const showPassword = ref(false)
const showConfirmPassword = ref(false)

// 验证规则
const validatePass2 = (rule: any, value: string, callback: any) => {
  if (value !== form.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度必须在4-20之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '用户名只能包含字母和数字', trigger: 'blur' }
  ],
  nickname: [
    { max: 50, message: '昵称长度不能超过50', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度必须在8-20之间', trigger: 'blur' },
    { pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/, message: '密码必须同时包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ]
}

// 注册提交
const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true

  // 检查是否有临时商家注册标记（一次性）
  const isMerchantReg = sessionStorage.getItem('tempMerchantReg') === 'true'
  const role = isMerchantReg ? 'merchant' : 'customer'

  const success = await userStore.register(
    form.value.username,
    form.value.password,
    role,
    form.value.nickname // 传递昵称，可能为空字符串
  )

  // 无论成功失败，清除标记（避免重复使用）
  if (isMerchantReg) {
    sessionStorage.removeItem('tempMerchantReg')
  }

  loading.value = false
  if (success) {
    ElMessage.success('注册成功，已自动登录')
    router.replace('/')
  }
}
</script>

<style scoped>
.auth-view {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  overflow: hidden;
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
  max-height: calc(100vh - 40px);
  overflow-y: auto;
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

.register-form {
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

.login-link {
  color: #667eea;
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.2s;
}

.login-link:hover {
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