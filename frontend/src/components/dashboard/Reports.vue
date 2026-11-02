<template>
  <div class="reports">
    <el-tabs v-model="reportType">
      <el-tab-pane label="日报表" name="daily">
        <el-table :data="dailyReports" border stripe style="width: 100%">
          <el-table-column prop="date" label="日期" />
          <el-table-column prop="totalSales" label="总销量 (m²)" />
          <el-table-column prop="totalAmount" label="总销售额 (元)">
            <template #default="{ row }">¥{{ row.totalAmount.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="fullSales" label="满铺销量 (m²)" />
          <el-table-column prop="blockSales" label="方块销量 (m²)" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="月报表" name="monthly">
        <el-table :data="monthlyReports" border stripe style="width: 100%">
          <el-table-column prop="month" label="月份" />
          <el-table-column prop="totalSales" label="总销量 (m²)" />
          <el-table-column prop="totalAmount" label="总销售额 (元)">
            <template #default="{ row }">¥{{ row.totalAmount.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="品牌明细">
            <template #default="{ row }">
              <div v-for="b in row.brands" :key="b.brand">
                {{ b.brand }}: {{ b.sales }} m², ¥{{ b.amount.toFixed(2) }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const reportType = ref('daily')

// 模拟日报表数据
const dailyReports = ref([
  { date: '2025-03-05', totalSales: 123, totalAmount: 12345, fullSales: 80, blockSales: 43 },
  { date: '2025-03-06', totalSales: 134, totalAmount: 13456, fullSales: 90, blockSales: 44 },
  { date: '2025-03-07', totalSales: 145, totalAmount: 14567, fullSales: 95, blockSales: 50 },
])

// 模拟月报表数据
const monthlyReports = ref([
  {
    month: '2025-03',
    totalSales: 3456,
    totalAmount: 345678,
    brands: [
      { brand: '道成', sales: 1500, amount: 150000 },
      { brand: '飞湃', sales: 1000, amount: 100000 },
      { brand: '红塬', sales: 956, amount: 95678 },
    ]
  },
  {
    month: '2025-02',
    totalSales: 2987,
    totalAmount: 298765,
    brands: [
      { brand: '道成', sales: 1200, amount: 120000 },
      { brand: '飞湃', sales: 900, amount: 90000 },
      { brand: '红塬', sales: 887, amount: 88765 },
    ]
  },
])
</script>

<style scoped>
.reports {
  padding: 10px;
}
</style>