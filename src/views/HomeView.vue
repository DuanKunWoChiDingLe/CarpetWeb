<!-- 首页组件 -->
<template>
  <div class="home">
    <HomeCarousel />
    <el-row justify="center">
      <el-col :span="22">
        <HotRecommend />
        <NewArrivals />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import HomeCarousel from '@/components/home/HomeCarousel.vue'
import HotRecommend from '@/components/home/HotRecommend.vue'
import NewArrivals from '@/components/home/NewArrivals.vue'
</script>

<style scoped>
.home {
  padding: 20px 0;
  margin: 20px 0;
}
</style>