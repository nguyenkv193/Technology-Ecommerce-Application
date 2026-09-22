import { aiClient } from './client'
import type { 
  AIRecommendationResponse, 
  AIEvaluationResponse, 
  AIClusterAnalysisResponse 
} from '@/types'

export const aiApi = {
  getPopular: (limit: number = 8): Promise<AIRecommendationResponse> => 
    aiClient.get('/recommend/popular', { params: { limit } }),
    
  getSimilar: (productId: number, limit: number = 4): Promise<AIRecommendationResponse> => 
    aiClient.get(`/recommend/similar/${productId}`, { params: { limit } }),
    
  getUserRecommendations: (userId: number, limit: number = 8): Promise<AIRecommendationResponse> => 
    aiClient.get(`/recommend/user/${userId}`, { params: { limit } }),
    
  getClusterAnalytics: (): Promise<AIClusterAnalysisResponse> => 
    aiClient.get('/analytics/clusters'),
    
  evaluateMetrics: (algorithm: string = 'hybrid', k: number = 5): Promise<AIEvaluationResponse> => 
    aiClient.post('/recommend/evaluate', null, { params: { algorithm, k } }),
    
  retrain: (): Promise<{ status: string; message: string }> => 
    aiClient.post('/recommend/retrain')
}
