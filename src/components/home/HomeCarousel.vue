<template>
  <div class="carousel-card">
    <el-carousel
      :interval="2000"
      type="card"
      height="300px"
      class="home-carousel"
      v-loading="loading"
      loop
      :pause-on-hover="true"
    >
      <el-carousel-item v-for="item in carouselList" :key="item.id">
        <div class="carousel-item" @click="handleItemClick(item)">
          <el-image
            :src="getImageUrl(item.imageUrl)"
            fit="cover"
            class="carousel-image"
          >
            <template #error>
              <div class="image-error">图片加载失败</div>
            </template>
          </el-image>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { bannerApi, type Banner } from '@/api/banner'

const router = useRouter()
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const loading = ref(false)
const carouselList = ref<Banner[]>([])

const getImageUrl = (path: string) => {
  if (!path) return ''
  const fullUrl = path.startsWith('http') ? path : baseURL + path
  return fullUrl
}

const fetchBanners = async () => {
  loading.value = true
  try {
    const res = await bannerApi.getEnabled()
    carouselList.value = res
  } catch (error) {
    ElMessage.error('获取轮播图失败')
  } finally {
    loading.value = false
  }
}

const handleItemClick = (item: Banner) => {
  if (item.linkType === 'product') {
    router.push(`/product/${item.linkValue}`)
  } else if (item.linkType === 'url') {
    window.open(item.linkValue, '_blank')
  }
}

onMounted(() => {
  fetchBanners()
})
</script>

<style scoped>
.carousel-card {
  max-width: 1200px;
  margin: 0 auto;
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}
.home-carousel {
  width: 100%;
}
.carousel-item {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  cursor: pointer;
}
.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  color: #999;
  font-size: 14px;
}
</style>