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
  type ChartOptions
} from 'chart.js'
import { Line } from 'vue-chartjs'
import type { DailyInteractionPoint } from '@/types'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Filler, Legend)

const props = defineProps<{
  series: DailyInteractionPoint[]
}>()

function formatShortDate(isoDate: string): string {
  const parts = isoDate.split('-')
  if (parts.length !== 3) return isoDate
  return `${parts[2]}/${parts[1]}`
}

const chartData = computed(() => ({
  labels: props.series.map((d) => formatShortDate(d.date)),
  datasets: [
    {
      label: 'Lượt xem (Click)',
      data: props.series.map((d) => d.views),
      borderColor: '#2563eb',
      backgroundColor: 'rgba(37, 99, 235, 0.08)',
      borderWidth: 2,
      tension: 0.35,
      fill: true,
      pointRadius: 2.5,
      pointHoverRadius: 5
    },
    {
      label: 'Lượt tym (Wishlist)',
      data: props.series.map((d) => d.wishlists),
      borderColor: '#e11d48',
      backgroundColor: 'rgba(225, 29, 72, 0.06)',
      borderWidth: 2,
      tension: 0.35,
      fill: false,
      pointRadius: 2.5,
      pointHoverRadius: 5
    },
    {
      label: 'Thêm giỏ hàng',
      data: props.series.map((d) => d.addToCarts),
      borderColor: '#d97706',
      backgroundColor: 'rgba(217, 119, 6, 0.06)',
      borderWidth: 2,
      tension: 0.35,
      fill: false,
      pointRadius: 2.5,
      pointHoverRadius: 5
    },
    {
      label: 'Mua hàng',
      data: props.series.map((d) => d.purchases),
      borderColor: '#059669',
      backgroundColor: 'rgba(5, 150, 105, 0.06)',
      borderWidth: 2,
      tension: 0.35,
      fill: false,
      pointRadius: 2.5,
      pointHoverRadius: 5
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
      position: 'top',
      align: 'end',
      labels: {
        boxWidth: 10,
        boxHeight: 10,
        usePointStyle: true,
        pointStyle: 'circle',
        color: '#52525b',
        font: {
          size: 11
        }
      }
    },
    tooltip: {
      backgroundColor: '#18181b',
      titleColor: '#f4f4f5',
      bodyColor: '#e4e4e7',
      borderColor: '#27272a',
      borderWidth: 1,
      padding: 10,
      cornerRadius: 8
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
        maxTicksLimit: 10,
        font: {
          family: 'ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace',
          size: 10
        }
      }
    },
    y: {
      beginAtZero: true,
      border: {
        display: false
      },
      grid: {
        color: '#f4f4f5'
      },
      ticks: {
        color: '#a1a1aa',
        precision: 0,
        maxTicksLimit: 5,
        font: {
          family: 'ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace',
          size: 10
        }
      }
    }
  }
}))
</script>
