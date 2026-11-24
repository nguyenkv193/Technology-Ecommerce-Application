<template>
  <div ref="rootRef" class="relative" :class="block ? 'w-full' : 'inline-flex shrink-0'">
    <button
      type="button"
      :data-testid="triggerTestId"
      :disabled="disabled"
      @click="toggleMenu"
      class="bg-white hover:bg-zinc-50 border border-zinc-200 rounded-lg text-zinc-800 shadow-xs flex items-center justify-between gap-2 transition-all cursor-pointer disabled:opacity-50 disabled:bg-zinc-50 disabled:cursor-not-allowed"
      :class="[
        block ? 'w-full' : '',
        size === 'sm' ? 'h-7 px-2.5 text-[11px]' : 'h-9 px-3 text-xs font-medium'
      ]"
    >
      <span class="flex items-center gap-2 min-w-0">
        <component
          v-if="selectedOption?.icon"
          :is="selectedOption.icon"
          class="w-3.5 h-3.5 text-zinc-500 shrink-0"
        />
        <span class="truncate" :class="!selectedOption ? 'text-zinc-400 font-normal' : ''">
          {{ selectedOption?.label ?? placeholder }}
        </span>
      </span>
      <ChevronDown
        class="w-3.5 h-3.5 text-zinc-400 shrink-0 transition-transform duration-150"
        :class="{ 'rotate-180': isOpen }"
      />
    </button>

    <Transition
      enter-active-class="transition duration-150 ease-out"
      enter-from-class="opacity-0 scale-95 -translate-y-1"
      enter-to-class="opacity-100 scale-100 translate-y-0"
      leave-active-class="transition duration-100 ease-in"
      leave-from-class="opacity-100 scale-100 translate-y-0"
      leave-to-class="opacity-0 scale-95 -translate-y-1"
    >
      <div
        v-if="isOpen"
        class="absolute mt-1.5 bg-white border border-zinc-200 rounded-xl shadow-lg p-1.5 z-40 space-y-0.5 max-h-64 overflow-y-auto"
        :class="[
          align === 'right' ? 'right-0' : 'left-0',
          menuWidthClass || (block ? 'w-full' : 'min-w-full w-max')
        ]"
      >
        <button
          v-for="opt in options"
          :key="String(opt.value)"
          type="button"
          :data-testid="optionTestIdPrefix ? `${optionTestIdPrefix}-${opt.value}` : undefined"
          @click="selectOption(opt.value)"
          class="w-full flex items-start gap-2.5 px-2.5 py-2 rounded-lg text-left transition-colors cursor-pointer"
          :class="opt.value === modelValue ? 'bg-zinc-100 text-zinc-900' : 'hover:bg-zinc-50 text-zinc-700'"
        >
          <component
            v-if="opt.icon"
            :is="opt.icon"
            class="w-4 h-4 mt-0.5 shrink-0"
            :class="opt.value === modelValue ? 'text-zinc-900' : 'text-zinc-400'"
          />
          <div class="min-w-0 flex-1">
            <div class="text-xs font-medium leading-tight whitespace-nowrap">{{ opt.label }}</div>
            <div v-if="opt.description" class="text-[11px] text-zinc-400 mt-0.5 leading-tight">
              {{ opt.description }}
            </div>
          </div>
          <Check v-if="opt.value === modelValue" class="w-3.5 h-3.5 text-zinc-900 mt-0.5 shrink-0" />
        </button>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, type Component } from 'vue'
import { ChevronDown, Check } from 'lucide-vue-next'

export interface CustomSelectOption {
  value: string | number
  label: string
  description?: string
  icon?: Component
}

const props = withDefaults(
  defineProps<{
    modelValue: string | number | null | undefined
    options: CustomSelectOption[]
    placeholder?: string
    disabled?: boolean
    block?: boolean
    size?: 'sm' | 'md'
    align?: 'left' | 'right'
    menuWidthClass?: string
    triggerTestId?: string
    optionTestIdPrefix?: string
  }>(),
  {
    placeholder: '-- Chọn --',
    disabled: false,
    block: false,
    size: 'md',
    align: 'left',
    menuWidthClass: ''
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: any): void
  (e: 'change', value: any): void
}>()

const isOpen = ref(false)
const rootRef = ref<HTMLElement | null>(null)

const selectedOption = computed(() =>
  props.options.find(opt => opt.value === props.modelValue)
)

function toggleMenu() {
  if (props.disabled) return
  isOpen.value = !isOpen.value
}

function selectOption(value: string | number) {
  isOpen.value = false
  if (value !== props.modelValue) {
    emit('update:modelValue', value)
    emit('change', value)
  }
}

function handleClickOutside(event: MouseEvent) {
  if (rootRef.value && !rootRef.value.contains(event.target as Node)) {
    isOpen.value = false
  }
}

function handleEscape(event: KeyboardEvent) {
  if (event.key === 'Escape') {
    isOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  document.addEventListener('keydown', handleEscape)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
  document.removeEventListener('keydown', handleEscape)
})
</script>
