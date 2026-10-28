<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-2">
        <label class="block text-xs font-semibold uppercase tracking-wider text-zinc-300">
          Thông số kỹ thuật
        </label>
        <span class="text-[10px] font-mono px-1.5 py-0.5 rounded bg-zinc-800 text-zinc-400 border border-zinc-700/50">
          {{ specs.length }} thông số
        </span>
      </div>
      <button
        type="button"
        @click="addSpec"
        class="text-xs font-medium text-zinc-200 hover:text-white flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-zinc-800 hover:bg-zinc-750 border border-zinc-700/60 shadow-sm transition-all"
      >
        <Plus class="w-3.5 h-3.5" />
        <span>Thêm thông số</span>
      </button>
    </div>

    <div v-if="specs.length === 0" class="p-8 rounded-xl border border-dashed border-zinc-800/80 bg-zinc-950/40 text-center text-xs text-zinc-500">
      Chưa có thông số kỹ thuật nào. Bấm "Thêm thông số" để thiết lập cấu hình sản phẩm.
    </div>

    <div v-else class="space-y-2.5">
      <div
        v-for="(spec, index) in specs"
        :key="index"
        class="flex items-center gap-3 p-3 rounded-xl bg-zinc-950/60 border border-zinc-800/70 hover:border-zinc-700/80 transition-colors"
      >
        <div class="w-1/3">
          <input
            v-model="spec.specKey"
            type="text"
            placeholder="Tên thông số (e.g. CPU, RAM, GPU)"
            class="w-full bg-zinc-900 border border-zinc-800 rounded-lg px-3 py-2 text-xs text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
          />
        </div>
        <div class="flex-1">
          <input
            v-model="spec.specValue"
            type="text"
            placeholder="Giá trị (e.g. Apple M3 Max 14-core, 36GB Unified)"
            class="w-full bg-zinc-900 border border-zinc-800 rounded-lg px-3 py-2 text-xs font-mono text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
          />
        </div>
        <button
          type="button"
          @click="removeSpec(index)"
          title="Xóa thông số này"
          class="p-2 rounded-lg text-zinc-500 hover:text-rose-400 hover:bg-zinc-800/80 transition-colors shrink-0"
        >
          <Trash2 class="w-4 h-4" />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Plus, Trash2 } from 'lucide-vue-next'

interface SpecItem {
  specKey: string
  specValue: string
  displayOrder?: number
}

const props = defineProps<{
  modelValue: SpecItem[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: SpecItem[]): void
}>()

const specs = props.modelValue

function addSpec() {
  specs.push({
    specKey: '',
    specValue: '',
    displayOrder: specs.length + 1
  })
  emit('update:modelValue', specs)
}

function removeSpec(index: number) {
  specs.splice(index, 1)
  emit('update:modelValue', specs)
}
</script>
