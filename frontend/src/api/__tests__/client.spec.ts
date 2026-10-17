import { describe, it, expect, beforeEach } from 'vitest'
import { apiClient } from '../client'

describe('apiClient', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('has base URL and default headers configured', () => {
    expect(apiClient.defaults.baseURL).toBe('/api/v1')
    expect(apiClient.defaults.headers['Content-Type']).toBe('application/json')
  })

  it('adds Authorization header in request interceptor if token exists in localStorage', async () => {
    localStorage.setItem('techstore_access_token', 'my_secret_token')

    // Find the request interceptor
    const requestInterceptor = (apiClient.interceptors.request as any).handlers[0]
    const config = { headers: {} as any }
    const modifiedConfig = await requestInterceptor.fulfilled(config)

    expect(modifiedConfig.headers.Authorization).toBe('Bearer my_secret_token')
  })

  it('does not add Authorization header if no token in localStorage', async () => {
    const requestInterceptor = (apiClient.interceptors.request as any).handlers[0]
    const config = { headers: {} as any }
    const modifiedConfig = await requestInterceptor.fulfilled(config)

    expect(modifiedConfig.headers.Authorization).toBeUndefined()
  })

  it('unwraps response.data in response interceptor', () => {
    const responseInterceptor = (apiClient.interceptors.response as any).handlers[0]
    const mockResponse = {
      data: { success: true, message: 'Success', data: [1, 2, 3] }
    }
    const result = responseInterceptor.fulfilled(mockResponse)
    expect(result).toEqual({ success: true, message: 'Success', data: [1, 2, 3] })
  })

  it('clears token from localStorage on 401 response and rejects with error message', async () => {
    localStorage.setItem('techstore_access_token', 'expired_token')
    localStorage.setItem('techstore_user', JSON.stringify({ id: 1 }))

    const responseInterceptor = (apiClient.interceptors.response as any).handlers[0]
    const mock401Error = {
      response: {
        status: 401,
        data: { message: 'Phiên đăng nhập hết hạn' }
      }
    }

    await expect(responseInterceptor.rejected(mock401Error)).rejects.toThrow('Phiên đăng nhập hết hạn')
    expect(localStorage.getItem('techstore_access_token')).toBeNull()
    expect(localStorage.getItem('techstore_user')).toBeNull()
  })

  it('rejects with fallback message if response data has no custom message', async () => {
    const responseInterceptor = (apiClient.interceptors.response as any).handlers[0]
    const mockNetworkError = {
      response: {
        status: 500
      }
    }

    await expect(responseInterceptor.rejected(mockNetworkError)).rejects.toThrow('Có lỗi xảy ra, vui lòng thử lại sau.')
  })
})
