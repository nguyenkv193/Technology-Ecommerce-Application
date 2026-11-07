<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-2">
        <label class="block text-xs font-semibold uppercase tracking-wider text-zinc-700">
          Thông số kỹ thuật
        </label>
        <span class="text-[10px] font-mono px-1.5 py-0.5 rounded bg-zinc-100 text-zinc-600 border border-zinc-200">
          {{ specs.length }} thông số
        </span>
      </div>
      <button
        type="button"
        @click="addSpec"
        class="text-xs font-medium text-zinc-700 hover:text-zinc-900 flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-white hover:bg-zinc-50 border border-zinc-200 shadow-xs transition-all"
      >
        <Plus class="w-3.5 h-3.5" />
        <span>Thêm thông số</span>
      </button>
    </div>

    <div v-if="specs.length === 0" class="p-8 rounded-xl border border-dashed border-zinc-200 bg-zinc-50/50 text-center text-xs text-zinc-400">
      Chưa có thông số kỹ thuật nào. Bấm "Thêm thông số" để thiết lập cấu hình sản phẩm.
    </div>

    <div v-else class="space-y-2.5">
      <div
        v-for="(spec, index) in specs"
        :key="index"
        class="flex items-center gap-3 p-3 rounded-xl bg-zinc-50/80 border border-zinc-200 hover:border-zinc-300 transition-colors"
      >
        <div class="w-1/3">
          <input
            v-model="spec.specKey"
            type="text"
            placeholder="Tên thông số (e.g. CPU, RAM, GPU)"
            class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
          />
        </div>
        <div class="flex-1">
          <input
            v-model="spec.specValue"
            type="text"
            placeholder="Giá trị (e.g. Apple M3 Max 14-core, 36GB Unified)"
            class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs font-mono text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
          />
        </div>
        <button
          type="button"
          @click="removeSpec(index)"
          title="Xóa thông số này"
          class="p-2 rounded-lg text-zinc-400 hover:text-rose-600 hover:bg-zinc-200/60 transition-colors shrink-0"
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
