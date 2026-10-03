import { apiClient } from './client'
import type { ApiResponse, RecommendationResponse } from '@/types'

export const recommendationApi = {
  getPersonalized: (limit: number = 8): Promise<ApiResponse<RecommendationResponse>> =>
    apiClient.get('/recommendations/me', { params: { limit } }),

  getSimilar: (productId: number, limit: number = 4): Promise<ApiResponse<RecommendationResponse>> =>
    apiClient.get(`/recommendations/similar/${productId}`, { params: { limit } })
}
