import { apiClient } from './client'
import type { ApiResponse, Banner } from '@/types'

export const bannerApi = {
  /**
   * Lấy danh sách banner đang hoạt động theo vị trí (mặc định HERO_SLIDER)
   */
  getActiveBanners: (position: string = 'HERO_SLIDER'): Promise<ApiResponse<Banner[]>> =>
    apiClient.get('/banners', { params: { position } }),

  /**
   * Lấy chi tiết banner theo ID
   */
  getBannerById: (id: number): Promise<ApiResponse<Banner>> =>
    apiClient.get(`/banners/${id}`),

  /**
   * Ghi nhận lượt click vào banner
   */
  recordClick: (id: number): Promise<ApiResponse<void>> =>
    apiClient.post(`/banners/${id}/click`),

  /**
   * (Admin) Lấy tất cả banner
   */
  getAllBanners: (position?: string): Promise<ApiResponse<Banner[]>> =>
    apiClient.get('/banners/admin', { params: position ? { position } : {} }),

  /**
   * (Admin) Tạo mới banner
   */
  createBanner: (data: Partial<Banner>): Promise<ApiResponse<Banner>> =>
    apiClient.post('/banners', data),

  /**
   * (Admin) Cập nhật banner
   */
  updateBanner: (id: number, data: Partial<Banner>): Promise<ApiResponse<Banner>> =>
    apiClient.put(`/banners/${id}`, data),

  /**
   * (Admin) Bật/Tắt trạng thái banner
   */
  toggleStatus: (id: number): Promise<ApiResponse<void>> =>
    apiClient.patch(`/banners/${id}/status`),

  /**
   * (Admin) Xoá banner
   */
  deleteBanner: (id: number): Promise<ApiResponse<void>> =>
    apiClient.delete(`/banners/${id}`)
}
