import apiClient from './client'
import type { ApiResponse, PageResponse, Role, User, UserStatus } from '@/types'

export interface FetchAdminUsersParams {
  keyword?: string
  role?: Role | ''
  status?: UserStatus | ''
  page?: number
  size?: number
}

export interface UpdateAdminUserPayload {
  role?: Role
  status?: UserStatus
}

export async function fetchAdminUsers(params: FetchAdminUsersParams = {}): Promise<PageResponse<User>> {
  const res = await apiClient.get<ApiResponse<PageResponse<User>>>('/users/admin', {
    params: {
      keyword: params.keyword?.trim() || undefined,
      role: params.role || undefined,
      status: params.status || undefined,
      page: params.page ?? 0,
      size: params.size ?? 10
    }
  })
  return res.data.data
}

export async function updateAdminUser(id: number, payload: UpdateAdminUserPayload): Promise<User> {
  const res = await apiClient.patch<ApiResponse<User>>(`/users/admin/${id}`, payload)
  return res.data.data
}
