<template>
  <div class="relative w-full" ref="containerRef">
    <!-- Trigger Button -->
    <button
      type="button"
      :disabled="disabled || loading"
      @click="toggleDropdown"
      :class="[
        'w-full rounded-xl border text-xs text-left px-3 py-2.5 flex items-center justify-between gap-2 transition-all cursor-pointer select-none',
        isOpen
          ? 'border-zinc-900 ring-2 ring-zinc-900/10 bg-white shadow-xs'
          : 'border-zinc-200 hover:border-zinc-300 bg-white hover:bg-zinc-50/50',
        disabled ? 'opacity-50 cursor-not-allowed bg-zinc-100 hover:border-zinc-200' : ''
      ]"
    >
      <span 
        class="truncate flex-1" 
        :class="selectedItem ? 'font-semibold text-zinc-900' : 'text-zinc-400 font-normal'"
      >
        {{ selectedItem ? selectedItem.name : placeholder }}
      </span>

      <!-- Right Action / Spinner / Chevron -->
      <div class="flex items-center gap-1.5 shrink-0">
        <!-- Loading spinner -->
        <svg 
          v-if="loading" 
          class="w-3.5 h-3.5 animate-spin text-zinc-400" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2.5"
        >
          <circle cx="12" cy="12" r="10" stroke-opacity="0.25" />
          <path d="M12 2a10 10 0 0 1 10 10" />
        </svg>

        <!-- Clear button (if selected and not disabled) -->
        <span
          v-else-if="selectedItem && clearable && !disabled"
          @click.stop="handleClear"
          class="p-0.5 rounded-full hover:bg-zinc-200/80 text-zinc-400 hover:text-zinc-700 transition"
          title="Xóa lựa chọn"
        >
          <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <line x1="18" y1="6" x2="6" y2="18" />
            <line x1="6" y1="6" x2="18" y2="18" />
          </svg>
        </span>

        <!-- Chevron Icon -->
        <svg 
          v-else
          :class="[
            'w-3.5 h-3.5 text-zinc-400 transition-transform duration-200',
            isOpen ? 'rotate-180 text-zinc-800' : ''
          ]" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2"
        >
          <polyline points="6 9 12 15 18 9" />
        </svg>
      </div>
    </button>

    <!-- Dropdown Menu Popover -->
    <div
      v-if="isOpen"
      class="absolute left-0 right-0 z-50 mt-1.5 bg-white border border-zinc-200 rounded-xl shadow-xl overflow-hidden animate-fade-in"
    >
      <!-- Search Input Box -->
      <div class="p-2 border-b border-zinc-100 bg-zinc-50/70">
        <div class="relative flex items-center">
          <svg class="w-3.5 h-3.5 text-zinc-400 absolute left-2.5 pointer-events-none" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8" />
            <line x1="21" y1="21" x2="16.65" y2="16.65" />
          </svg>
          <input
            ref="searchInputRef"
            v-model="searchQuery"
            type="text"
            :placeholder="searchPlaceholder || 'Gõ để tìm nhanh...'"
            class="w-full pl-8 pr-7 py-1.5 bg-white border border-zinc-200 rounded-lg text-xs text-zinc-900 focus:outline-none focus:border-zinc-800 transition"
            @keydown.esc="closeDropdown"
            @keydown.down.prevent="navigateDown"
            @keydown.up.prevent="navigateUp"
            @keydown.enter.prevent="selectHighlighted"
          />
          <button
            v-if="searchQuery"
            @click="searchQuery = ''"
            type="button"
            class="absolute right-2 text-zinc-400 hover:text-zinc-600 text-xs p-0.5"
          >
            ✕
          </button>
        </div>
      </div>

      <!-- Options List -->
      <div 
        ref="listRef"
        class="max-h-56 overflow-y-auto p-1.5 space-y-0.5 scrollbar-thin"
      >
        <div
          v-for="(option, idx) in filteredOptions"
          :key="option.code"
          @click="selectOption(option)"
          :class="[
            'px-3 py-2 rounded-lg text-xs flex items-center justify-between cursor-pointer transition select-none',
            isSelected(option)
              ? 'bg-zinc-900 text-white font-semibold'
              : highlightedIndex === idx
                ? 'bg-zinc-100 text-zinc-900 font-medium'
                : 'text-zinc-700 hover:bg-zinc-100 hover:text-zinc-900'
          ]"
        >
          <span class="truncate">{{ option.name }}</span>
          <!-- Checkmark icon -->
          <svg
            v-if="isSelected(option)"
            class="w-3.5 h-3.5 shrink-0 ml-2 text-white"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.5"
          >
            <polyline points="20 6 9 17 4 12" />
          </svg>
        </div>

        <!-- Empty state when no matches -->
        <div 
          v-if="filteredOptions.length === 0" 
          class="py-6 px-3 text-center text-xs text-zinc-400 space-y-1"
        >
          <p class="font-medium text-zinc-600">Không tìm thấy kết quả</p>
          <p class="text-[11px] text-zinc-400">Thử gõ từ khóa khác</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'

export interface SelectOption {
  code: number | string
  name: string
  [key: string]: any
}

const props = withDefaults(
  defineProps<{
    modelValue: number | string | null
    options: SelectOption[]
    placeholder?: string
    searchPlaceholder?: string
    disabled?: boolean
    loading?: boolean
    clearable?: boolean
  }>(),
  {
    modelValue: null,
    placeholder: '-- Chọn một mục --',
    searchPlaceholder: 'Tìm kiếm nhanh...',
    disabled: false,
    loading: false,
    clearable: true
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: number | string | null): void
  (e: 'change', option: SelectOption | null): void
}>()

const isOpen = ref(false)
const searchQuery = ref('')
const highlightedIndex = ref(-1)

const containerRef = ref<HTMLElement | null>(null)
const searchInputRef = ref<HTMLInputElement | null>(null)
const listRef = ref<HTMLElement | null>(null)

// Normalize Vietnamese string for robust searching (accent-insensitive)
const removeVietnameseTones = (str: string): string => {
  return str
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'D')
    .toLowerCase()
    .trim()
}

const selectedItem = computed(() => {
  if (props.modelValue === null || props.modelValue === undefined || props.modelValue === '') {
    return null
  }
  return props.options.find(opt => String(opt.code) === String(props.modelValue)) || null
})

const filteredOptions = computed(() => {
  if (!searchQuery.value.trim()) {
    return props.options
  }
  const query = removeVietnameseTones(searchQuery.value)
  return props.options.filter(opt => {
    const optName = removeVietnameseTones(opt.name)
    return optName.includes(query)
  })
})

const isSelected = (option: SelectOption): boolean => {
  if (props.modelValue === null || props.modelValue === undefined) return false
  return String(option.code) === String(props.modelValue)
}

const toggleDropdown = () => {
  if (props.disabled || props.loading) return
  if (isOpen.value) {
    closeDropdown()
  } else {
    openDropdown()
  }
}

const openDropdown = () => {
  isOpen.value = true
  searchQuery.value = ''
  highlightedIndex.value = -1
  nextTick(() => {
    searchInputRef.value?.focus()
  })
}

const closeDropdown = () => {
  isOpen.value = false
  searchQuery.value = ''
  highlightedIndex.value = -1
}

const selectOption = (option: SelectOption) => {
  emit('update:modelValue', option.code)
  emit('change', option)
  closeDropdown()
}

const handleClear = () => {
  emit('update:modelValue', null)
  emit('change', null)
  searchQuery.value = ''
}

const navigateDown = () => {
  if (filteredOptions.value.length === 0) return
  if (highlightedIndex.value < filteredOptions.value.length - 1) {
    highlightedIndex.value++
  } else {
    highlightedIndex.value = 0
  }
}

const navigateUp = () => {
  if (filteredOptions.value.length === 0) return
  if (highlightedIndex.value > 0) {
    highlightedIndex.value--
  } else {
    highlightedIndex.value = filteredOptions.value.length - 1
  }
}

const selectHighlighted = () => {
  if (
    highlightedIndex.value >= 0 &&
    highlightedIndex.value < filteredOptions.value.length
  ) {
    selectOption(filteredOptions.value[highlightedIndex.value])
  } else if (filteredOptions.value.length === 1) {
    selectOption(filteredOptions.value[0])
  }
}

// Click outside handling
const handleClickOutside = (event: MouseEvent) => {
  if (containerRef.value && !containerRef.value.contains(event.target as Node)) {
    closeDropdown()
  }
}

watch(() => props.disabled, (disabled) => {
  if (disabled && isOpen.value) {
    closeDropdown()
  }
})

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.scrollbar-thin::-webkit-scrollbar {
  width: 4px;
}
.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
}
.scrollbar-thin::-webkit-scrollbar-thumb {
  background: #d4d4d8;
  border-radius: 9999px;
}
.scrollbar-thin::-webkit-scrollbar-thumb:hover {
  background: #a1a1aa;
}
</style>
