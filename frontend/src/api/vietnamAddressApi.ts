export interface VietnamDivision {
  code: number
  name: string
  division_type?: string
  codename?: string
  phone_code?: number
}

export interface VietnamProvince extends VietnamDivision {
  districts?: VietnamDistrict[]
}

export interface VietnamDistrict extends VietnamDivision {
  province_code?: number
  wards?: VietnamWard[]
}

export interface VietnamWard extends VietnamDivision {
  district_code?: number
}

const BASE_URL = 'https://provinces.open-api.vn/api'

// Bộ nhớ đệm in-memory để chuyển đổi tỉnh/huyện tức thì không phải tải lại
let provincesCache: VietnamProvince[] | null = null
const districtsCache = new Map<number, VietnamDistrict[]>()
const wardsCache = new Map<number, VietnamWard[]>()

export const vietnamAddressApi = {
  getProvinces: async (): Promise<VietnamProvince[]> => {
    if (provincesCache && provincesCache.length > 0) {
      return provincesCache
    }
    const res = await fetch(`${BASE_URL}/p/`)
    if (!res.ok) throw new Error('Không thể tải danh sách tỉnh/thành phố')
    const data = await res.json()
    provincesCache = data
    return data
  },

  getDistrictsByProvince: async (provinceCode: number): Promise<VietnamDistrict[]> => {
    if (districtsCache.has(provinceCode)) {
      return districtsCache.get(provinceCode)!
    }
    const res = await fetch(`${BASE_URL}/p/${provinceCode}?depth=2`)
    if (!res.ok) throw new Error('Không thể tải danh sách quận/huyện')
    const data = await res.json()
    const districts = data.districts || []
    districtsCache.set(provinceCode, districts)
    return districts
  },

  getWardsByDistrict: async (districtCode: number): Promise<VietnamWard[]> => {
    if (wardsCache.has(districtCode)) {
      return wardsCache.get(districtCode)!
    }
    const res = await fetch(`${BASE_URL}/d/${districtCode}?depth=2`)
    if (!res.ok) throw new Error('Không thể tải danh sách phường/xã')
    const data = await res.json()
    const wards = data.wards || []
    wardsCache.set(districtCode, wards)
    return wards
  }
}
