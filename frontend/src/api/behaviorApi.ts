import { apiClient } from './client'
import type { ApiResponse } from '@/types'

// Sinh và duy trì sessionId duy nhất cho phiên duyệt web của trình duyệt
const getSessionId = (): string => {
  let sid = sessionStorage.getItem('techstore_session_id')
  if (!sid) {
    sid = 'sess_' + Math.random().toString(36).substring(2, 12) + '_' + Date.now()
    sessionStorage.setItem('techstore_session_id', sid)
  }
  return sid
}

export type ActionType = 'VIEW' | 'SEARCH' | 'ADD_TO_CART' | 'WISHLIST' | 'PURCHASE' | 'RATING'

export const behaviorApi = {
  track: (actionType: ActionType, productId: number | null = null, actionValue: string | null = null): Promise<any> => {
    return apiClient.post('/behaviors/track', {
      actionType,
      productId,
      actionValue,
      sessionId: getSessionId()
    }).catch(err => {
      // Tracking ngầm, không làm gián đoạn trải nghiệm của người dùng nếu có lỗi mạng
      console.debug('Tracking skipped:', err.message)
    })
  },
  getMyBehaviors: (): Promise<ApiResponse<any[]>> => 
    apiClient.get('/behaviors/my'),

  /**
   * Lấy top các từ khóa tìm kiếm thịnh hành từ hành vi người dùng thực tế
   */
  getTrendingKeywords: (limit: number = 8, days: number = 7): Promise<ApiResponse<{ keyword: string; link: string; searchCount: number }[]>> =>
    apiClient.get('/behaviors/trending-keywords', { params: { limit, days } })
}
