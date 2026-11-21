import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import ProfileView from '../ProfileView.vue'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/authApi'

vi.mock('@/api/authApi', () => ({
  authApi: {
    updateProfile: vi.fn(),
    changePassword: vi.fn(),
    getAddresses: vi.fn(),
    addAddress: vi.fn(),
    updateAddress: vi.fn(),
    deleteAddress: vi.fn(),
    setDefaultAddress: vi.fn()
  }
}))
vi.mock('@/api/vietnamAddressApi', () => ({
  vietnamAddressApi: {
    getProvinces: vi.fn().mockResolvedValue([]),
    getDistrictsByProvince: vi.fn().mockResolvedValue([]),
    getWardsByDistrict: vi.fn().mockResolvedValue([])
  }
}))
vi.mock('vue-sonner', () => ({ toast: { success: vi.fn(), error: vi.fn() } }))

const user = { id: 1, email: 'a@b.com', fullName: 'Nguyen Van A', phone: '0912345678', role: 'USER', status: 'ACTIVE' }
const address = { id: 9, recipientName: 'A', phone: '0912345678', province: 'HN', district: 'CG', ward: 'DV', addressDetail: '1', fullAddress: '1, DV, CG, HN', isDefault: false }

const setup = async () => {
  setActivePinia(createPinia())
  const store = useAuthStore()
  store.token = 'tok'
  store.user = user as any
  vi.mocked(authApi.getAddresses).mockResolvedValue({ success: true, data: [address] } as any)
  const wrapper = mount(ProfileView)
  await flushPromises()
  return { wrapper, store }
}

describe('ProfileView.vue', () => {
  beforeEach(() => vi.clearAllMocks())

  it('shows login prompt when unauthenticated', () => {
    setActivePinia(createPinia())
    const wrapper = mount(ProfileView)
    expect(wrapper.find('[data-testid="profile-login"]').exists()).toBe(true)
  })

  it('updates profile with valid data', async () => {
    const { wrapper, store } = await setup()
    vi.mocked(authApi.updateProfile).mockResolvedValue({ success: true, data: { ...user, fullName: 'Moi' } } as any)
    await wrapper.find('#fullName').setValue('Moi')
    await wrapper.find('[data-testid="profile-form"]').trigger('submit')
    await flushPromises()
    expect(authApi.updateProfile).toHaveBeenCalledWith({ fullName: 'Moi', phone: '0912345678' })
    expect(store.user?.fullName).toBe('Moi')
  })

  it('rejects invalid phone', async () => {
    const { wrapper } = await setup()
    await wrapper.find('#phone').setValue('123')
    await wrapper.find('[data-testid="profile-form"]').trigger('submit')
    expect(authApi.updateProfile).not.toHaveBeenCalled()
    expect(wrapper.text()).toContain('không đúng định dạng')
  })

  it('validates password confirmation then submits', async () => {
    const { wrapper } = await setup()
    await wrapper.find('[data-testid="tab-password"]').trigger('click')
    await wrapper.find('#currentPassword').setValue('old123')
    await wrapper.find('#newPassword').setValue('new123')
    await wrapper.find('#confirmPassword').setValue('other')
    await wrapper.find('[data-testid="password-form"]').trigger('submit')
    expect(wrapper.find('[data-testid="password-error"]').text()).toContain('không khớp')
    expect(authApi.changePassword).not.toHaveBeenCalled()

    vi.mocked(authApi.changePassword).mockResolvedValue({ success: true } as any)
    await wrapper.find('#confirmPassword').setValue('new123')
    await wrapper.find('[data-testid="password-form"]').trigger('submit')
    await flushPromises()
    expect(authApi.changePassword).toHaveBeenCalledWith({ currentPassword: 'old123', newPassword: 'new123' })
  })

  it('lists addresses, sets default and deletes', async () => {
    const { wrapper } = await setup()
    await wrapper.find('[data-testid="tab-addresses"]').trigger('click')
    expect(wrapper.findAll('[data-testid="address-item"]')).toHaveLength(1)

    vi.mocked(authApi.setDefaultAddress).mockResolvedValue({ success: true } as any)
    await wrapper.find('[data-testid="set-default"]').trigger('click')
    await flushPromises()
    expect(authApi.setDefaultAddress).toHaveBeenCalledWith(9)

    vi.spyOn(window, 'confirm').mockReturnValue(true)
    vi.mocked(authApi.deleteAddress).mockResolvedValue({ success: true } as any)
    await wrapper.find('[data-testid="delete-address"]').trigger('click')
    await flushPromises()
    expect(authApi.deleteAddress).toHaveBeenCalledWith(9)
  })

  it('renders sidebar with initial avatar and switches right content by tab', async () => {
    const { wrapper } = await setup()
    expect(wrapper.find('[data-testid="profile-avatar-initial"]').text()).toBe('N')
    expect(wrapper.find('[data-testid="active-tab-title"]').text()).toBe('Hồ sơ cá nhân')
    expect(wrapper.find('[data-testid="profile-form"]').exists()).toBe(true)

    await wrapper.find('[data-testid="tab-password"]').trigger('click')
    expect(wrapper.find('[data-testid="active-tab-title"]').text()).toBe('Đổi mật khẩu')
    expect(wrapper.find('[data-testid="password-form"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="profile-form"]').exists()).toBe(false)

    await wrapper.find('[data-testid="tab-addresses"]').trigger('click')
    expect(wrapper.find('[data-testid="active-tab-title"]').text()).toBe('Sổ địa chỉ')
  })

  it('shows avatar image and removes it', async () => {
    const { wrapper, store } = await setup()
    store.user = { ...user, avatarUrl: 'data:image/jpeg;base64,abc' } as any
    await flushPromises()
    expect(wrapper.find('[data-testid="profile-avatar-img"]').attributes('src')).toBe('data:image/jpeg;base64,abc')

    vi.mocked(authApi.updateProfile).mockResolvedValue({ success: true, data: { ...user, avatarUrl: null } } as any)
    await wrapper.find('[data-testid="avatar-remove"]').trigger('click')
    await flushPromises()
    expect(authApi.updateProfile).toHaveBeenCalledWith({ fullName: 'Nguyen Van A', phone: '0912345678', avatarUrl: '' })
    expect(wrapper.find('[data-testid="profile-avatar-initial"]').exists()).toBe(true)
  })
})
