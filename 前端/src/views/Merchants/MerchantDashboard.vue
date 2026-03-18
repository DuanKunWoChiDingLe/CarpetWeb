<template>
  <div class="merchant-dashboard">
    <!-- 二级导航（面包屑样式） -->
    <el-breadcrumb separator="/" class="dashboard-nav">
      <el-breadcrumb-item
        :to="{ path: '/merchant/dashboard' }"
        :class="{ 'is-active': isActive('dashboard') }"
      >
        控制台
      </el-breadcrumb-item>
      <el-breadcrumb-item
        :to="{ path: '/merchant/dashboard/carousel' }"
        :class="{ 'is-active': isActive('carousel') }"
      >
        轮播图管理
      </el-breadcrumb-item>
      <el-breadcrumb-item
        :to="{ path: '/merchant/dashboard/sales' }"
        :class="{ 'is-active': isActive('sales') }"
      >
        销量统计
      </el-breadcrumb-item>
      <el-breadcrumb-item
        :to="{ path: '/merchant/dashboard/reports' }"
        :class="{ 'is-active': isActive('reports') }"
      >
        报表
      </el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 路由出口：显示当前子页面 -->
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()

const isActive = (page: string) => {
  // 判断当前路由路径是否匹配对应子模块
  return route.path.includes(`/merchant/dashboard/${page}`) ||
         (page === 'dashboard' && route.path === '/merchant/dashboard')
}
</script>

<style scoped>
.merchant-dashboard {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}
.dashboard-nav {
  margin-bottom: 20px;
  font-size: 16px;
  background-color: #fff;
  padding: 12px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}
.dashboard-nav .el-breadcrumb__item :deep(.el-breadcrumb__inner) {
  font-weight: normal;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}
.dashboard-nav .el-breadcrumb__item :deep(.el-breadcrumb__inner:hover) {
  color: #409EFF;
}
.dashboard-nav .el-breadcrumb__item.is-active :deep(.el-breadcrumb__inner) {
  font-weight: bold;
  color: #409EFF;
}
</style>