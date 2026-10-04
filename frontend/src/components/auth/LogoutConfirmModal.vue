<template>
  <Teleport to="body">
    <Transition name="logout-modal">
      <div 
        v-if="authStore.isLogoutModalOpen"
        class="fixed inset-0 z-[100] flex items-center justify-center p-4 overflow-y-auto"
        @keydown.esc="handleClose"
        tabindex="-1"
      >
        <!-- Backdrop Blur -->
        <div 
          class="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity" 
          @click="handleClose"
        ></div>

        <!-- Modal Dialog Card -->
        <div 
          class="modal-card relative w-full max-w-[380px] my-auto bg-white rounded-3xl shadow-2xl border border-zinc-200/80 p-6 sm:p-7 text-center z-10 text-zinc-900 select-none will-change-transform"
          @click.stop
        >
          <!-- Icon -->
          <div class="mx-auto w-12 h-12 rounded-2xl bg-red-50 border border-red-100 text-red-600 flex items-center justify-center mb-4 shadow-sm">
            <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
              <polyline points="16 17 21 12 16 7"></polyline>
              <line x1="21" y1="12" x2="9" y2="12"></line>
            </svg>
          </div>

          <!-- Title -->
          <h3 class="text-lg font-bold text-zinc-900">
            Xác nhận đăng xuất
          </h3>

          <!-- Message -->
          <p class="text-xs text-zinc-500 mt-2 leading-relaxed">
            Bạn có chắc chắn muốn đăng xuất khỏi tài khoản 
            <strong class="text-zinc-800 font-semibold">{{ authStore.user?.fullName || 'TechStore User' }}</strong>
            <span v-if="authStore.user?.email" class="block text-[11px] text-zinc-400 mt-0.5">({{ authStore.user.email }})</span>?
          </p>
          <p class="text-[11px] text-zinc-400 mt-1">
            Giỏ hàng và danh sách yêu thích của bạn sẽ được lưu an toàn trên hệ thống.
          </p>

          <!-- Buttons -->
          <div class="mt-6 flex items-center gap-2.5">
            <button
              type="button"
              @click="handleClose"
              class="flex-1 py-2.5 px-4 rounded-xl border border-zinc-200/80 bg-zinc-50 hover:bg-zinc-100 text-zinc-700 font-medium text-xs transition cursor-pointer"
            >
              Ở lại
            </button>
            <button
              type="button"
              @click="confirmLogout"
              class="flex-1 py-2.5 px-4 rounded-xl bg-red-600 hover:bg-red-700 text-white font-medium text-xs shadow-md shadow-red-600/20 transition cursor-pointer flex items-center justify-center gap-1.5"
            >
              <span>Đăng xuất</span>
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'
import { toast } from 'vue-sonner'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()

const handleClose = () => {
  authStore.closeLogoutModal()
}

const confirmLogout = () => {
  const userName = authStore.user?.fullName || 'bạn'
  authStore.logout()
  cartStore.resetCart()
  wishlistStore.items = []
  authStore.closeLogoutModal()

  toast.info('Đã đăng xuất', {
    description: `Tạm biệt ${userName}, hẹn gặp lại bạn tại TechStore.`
  })

  // If user is on a protected route like /orders, redirect to home
  if (route.path === '/orders') {
    router.push('/')
  }
}

// Lock scroll when modal is active
watch(() => authStore.isLogoutModalOpen, (isOpen) => {
  if (typeof document !== 'undefined') {
    if (isOpen) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = ''
    }
  }
})
</script>

<style scoped>
.logout-modal-enter-active,
.logout-modal-leave-active {
  transition: opacity 0.22s ease;
}

.logout-modal-enter-active .modal-card,
.logout-modal-leave-active .modal-card {
  transition: transform 0.25s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.22s ease;
}

.logout-modal-enter-from,
.logout-modal-leave-to {
  opacity: 0;
}

.logout-modal-enter-from .modal-card,
.logout-modal-leave-to .modal-card {
  opacity: 0;
  transform: scale(0.95) translateY(12px);
}

.logout-modal-enter-to .modal-card,
.logout-modal-leave-from .modal-card {
  opacity: 1;
  transform: scale(1) translateY(0);
}
</style>
