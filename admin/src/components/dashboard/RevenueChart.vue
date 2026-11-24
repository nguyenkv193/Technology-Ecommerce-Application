<template>
  <div class="h-64 w-full">
    <Line :data="chartData" :options="chartOptions" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Filler,
  Legend,
  type ChartOptions,
  type ScriptableContext
} from 'chart.js'
import { Line } from 'vue-chartjs'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Filler, Legend)

export interface RevenuePoint {
  label: string
  amount: number
  count: number
}

const props = defineProps<{
  points: RevenuePoint[]
}>()

function formatCompactVnd(val: number): string {
  if (val >= 1_000_000_000) return `${(val / 1_000_000_000).toFixed(1)}B ₫`
  if (val >= 1_000_000) return `${(val / 1_000_000).toFixed(val % 1_000_000 === 0 ? 0 : 1)}M ₫`
  if (val >= 1_000) return `${(val / 1_000).toFixed(0)}k ₫`
  return `${val} ₫`
}

const chartData = computed(() => ({
  labels: props.points.map((p) => p.label),
  datasets: [
    {
      label: 'Doanh thu',
      data: props.points.map((p) => p.amount),
      borderColor: '#2563eb',
      borderWidth: 2,
      tension: 0.35,
      fill: true,
      backgroundColor: (context: ScriptableContext<'line'>) => {
        const { ctx, chartArea } = context.chart
        if (!chartArea) return 'rgba(37, 99, 235, 0.12)'
        const gradient = ctx.createLinearGradient(0, chartArea.top, 0, chartArea.bottom)
        gradient.addColorStop(0, 'rgba(37, 99, 235, 0.20)')
        gradient.addColorStop(1, 'rgba(37, 99, 235, 0.0)')
        return gradient
      },
      pointRadius: 3,
      pointHoverRadius: 5.5,
      pointBackgroundColor: '#2563eb',
      pointBorderWidth: 0,
      pointHoverBackgroundColor: '#2563eb',
      pointHoverBorderColor: '#ffffff',
      pointHoverBorderWidth: 2
    }
  ]
}))

const chartOptions = computed<ChartOptions<'line'>>(() => ({
  responsive: true,
  maintainAspectRatio: false,
  interaction: {
    mode: 'index',
    intersect: false
  },
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      backgroundColor: '#18181b',
      titleColor: '#f4f4f5',
      bodyColor: '#e4e4e7',
      borderColor: '#27272a',
      borderWidth: 1,
      padding: 10,
      cornerRadius: 8,
      displayColors: false,
      callbacks: {
        label: (ctx) => {
          const idx = ctx.dataIndex
          const pt = props.points[idx]
          const formatted = new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
          }).format(pt?.amount || 0)
          return [`Doanh thu: ${formatted}`, `Số đơn: ${pt?.count || 0} đơn`]
        }
      }
    }
  },
  scales: {
    x: {
      grid: {
        display: false
      },
      border: {
        display: false
      },
      ticks: {
        color: '#71717a',
        font: {
          family: 'Inter, -apple-system, BlinkMacSystemFont, sans-serif',
          size: 11
        }
      }
    },
    y: {
      beginAtZero: true,
      border: {
        display: false,
        dash: [4, 4]
      },
      grid: {
        color: '#f4f4f5'
      },
      ticks: {
        color: '#a1a1aa',
        maxTicksLimit: 5,
        font: {
          family: 'Inter, -apple-system, BlinkMacSystemFont, sans-serif',
          size: 10
        },
        callback: (value) => formatCompactVnd(Number(value))
      }
    }
  }
}))
</script>
