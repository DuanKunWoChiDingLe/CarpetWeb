<template>
  <div ref="chartRef" style="width: 100%; height: 100%;"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps<{
  data: {
    dates: string[]
    salesAmount: number[]
  }
  height?: string
}>()

const chartRef = ref<HTMLElement>()
let chart: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value)
  setOption()
}

const setOption = () => {
  if (!chart) return
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '5%', right: '5%', bottom: '10%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: props.data.dates,
      axisLabel: { fontSize: 10 }
    },
    yAxis: {
      type: 'value',
      name: '金额(元)',
      nameTextStyle: { fontSize: 10 },
      axisLabel: { fontSize: 10 }
    },
    series: [
      {
        name: '销售额',
        type: 'line',
        data: props.data.salesAmount,
        smooth: true,
        lineStyle: { color: '#409EFF', width: 2 },
        areaStyle: { color: 'rgba(64,158,255,0.1)' }
      }
    ]
  })
}

watch(() => props.data, () => {
  setOption()
}, { deep: true })

onMounted(() => {
  initChart()
  window.addEventListener('resize', () => chart?.resize())
})

onUnmounted(() => {
  window.removeEventListener('resize', () => chart?.resize())
  chart?.dispose()
})
</script>