<template>
  <div class="profile-view">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>个人资料</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card class="profile-card" v-loading="loading">
      <template #header>
        <span>个人资料</span>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <!-- 头像 -->
        <el-form-item label="头像">
          <div class="avatar-uploader">
            <el-upload
              class="avatar-uploader"
              :action="`${baseURL}/upload/image`"
              :headers="{ Authorization: `Bearer ${userStore.token}` }"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="form.avatar" :src="getImageUrl(form.avatar)" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="avatar-tip">点击上传头像，支持 jpg/png，不超过 2MB</div>
          </div>
        </el-form-item>

        <!-- 昵称 -->
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" maxlength="50" />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { useRouter } from 'vue-router'
import { type UploadResponse } from '@/api/upload'

const userStore = useUserStore()
const router = useRouter()
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const loading = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance | null>(null)
const form = reactive({
  nickname: '',
  avatar: ''
})

// 表单校验规则
const rules = {
  nickname: [{ max: 50, message: '昵称长度不能超过50', trigger: 'blur' }]
}

// 获取图片完整 URL
const getImageUrl = (path: string) => {
  console.log('getImageUrl 接收到 path:', path)  // 打印原始路径
  if (!path) return ''
  if (path.startsWith('http')) return path
  if (path.startsWith('/uploads')) {
    console.log('path 以 /uploads 开头，直接返回:', path)  // 打印返回路径
    return path
  }
  const fullUrl = baseURL + path
  console.log('拼接后 URL:', fullUrl)
  return fullUrl
}


// 加载当前用户信息
const loadProfile = async () => {
  loading.value = true
  try {
    const res = await userApi.getProfile()
    // 只更新可能变化的字段，保留原有 role 等
    if (userStore.userInfo) {
      userStore.userInfo.nickname = res.nickname
      userStore.userInfo.avatar = res.avatar
    } else {
      // 如果 userStore 中没有，则直接赋值（登录时已存在，理论上不会进入这里）
      userStore.userInfo = res
    }
    form.nickname = res.nickname || ''
    form.avatar = res.avatar || ''
  } catch (error) {
    console.log(error);
    
    ElMessage.error('获取个人信息失败')
  } finally {
    loading.value = false
  }
}

// 头像上传成功
const handleAvatarSuccess = (response: UploadResponse, file: File) => {
  const url = response.data?.url || response.url
  if (url) {
    form.avatar = url
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败：返回数据格式异常')
  }
}

const handleAvatarError = () => {
  ElMessage.error('头像上传失败')
}

// 上传前校验
const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

// 提交修改
const submitForm = async () => {
  await formRef.value?.validate()
  submitting.value = true
  try {
    const updated = await userApi.updateProfile({
      nickname: form.nickname,
      avatar: form.avatar
    })
    userStore.userInfo = updated
    ElMessage.success('资料更新成功')
    router.back()
  } catch (error: unknown) {
    let errorMsg = '更新失败'
    if (error && typeof error === 'object' && 'response' in error) {
      const err = error as { response?: { data?: { message?: string } } }
      errorMsg = err.response?.data?.message || errorMsg
    }
    ElMessage.error(errorMsg)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-view {
  max-width: 600px;
  margin: 30px auto;
  padding: 0 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.profile-card {
  width: 100%;
}
.avatar-uploader {
  text-align: center;
}
.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}
.avatar-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
</style>