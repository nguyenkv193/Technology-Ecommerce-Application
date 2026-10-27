<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <label class="block text-xs font-semibold uppercase tracking-wider text-slate-300">
        Thông số kỹ thuật (Technical Specs)
      </label>
      <button
        type="button"
        @click="addSpec"
        class="text-xs font-medium text-blue-400 hover:text-blue-300 flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-blue-500/10 border border-blue-500/20 hover:bg-blue-500/20 transition-all"
      >
        <Plus class="w-3.5 h-3.5" />
        <span>Thêm thông số</span>
      </button>
    </div>

    <div v-if="specs.length === 0" class="p-6 rounded-xl border border-dashed border-slate-800 text-center text-xs text-slate-500">
      Chưa có thông số kỹ thuật nào. Bấm "Thêm thông số" để thiết lập cấu hình sản phẩm.
    </div>

    <div v-else class="space-y-3">
      <div
        v-for="(spec, index) in specs"
        :key="index"
        class="flex items-center gap-3 p-3 rounded-xl bg-slate-950/60 border border-slate-800"
      >
        <div class="w-1/3">
          <input
            v-model="spec.specKey"
            type="text"
            placeholder="Tên thông số (e.g. CPU, RAM, Ổ cứng)"
            class="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
          />
        </div>
        <div class="flex-1">
          <input
            v-model="spec.specValue"
            type="text"
            placeholder="Giá trị (e.g. Apple M3 Max 14-core, 36GB Unified)"
            class="w-full bg-slate-900 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
          />
        </div>
        <button
          type="button"
          @click="removeSpec(index)"
          title="Xóa thông số này"
          class="p-2 rounded-lg text-slate-500 hover:text-rose-400 hover:bg-slate-800 transition-colors shrink-0"
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
