import axios from 'axios'

const requestClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  headers: {
    'Content-Type': 'application/json'
  }
})

requestClient.interceptors.response.use(
  (response) => {
    const result = response.data
    if (result.code !== 200) {
      return Promise.reject(new Error(result.msg || '接口返回失败'))
    }
    return result.data
  },
  (error) => {
    const status = error.response?.status
    const message = error.response?.data?.msg

    return Promise.reject(
      new Error(message || (status
        ? `请求失败（HTTP ${status}）`
        : '网络请求失败，请检查网络连接'))
    )
  }
)

function request(path, userId, params = {}, options = {}) {
  return requestClient.request({
    ...options,
    url: path,
    method: options.method || 'GET',
    params,
    headers: {
      'X-User-Id': String(userId),
      ...(options.headers || {})
    }
  })
}

export const api = {
  me: (userId) => request('/api/user/me', userId),
  users: (userId, params) => request('/api/user/list', userId, params),
  contracts: (userId, params) => request('/api/contract/list', userId, params),
  departments: (userId) => request('/api/dept/tree', userId),
  menuTree: (userId) => request('/api/menu/tree', userId),
  menuManage: (userId) => request('/api/menu/manage', userId),
  roleMenus: (userId, roleId, menuIds) =>
      request(`/api/menu/role/${roleId}`, userId, {}, {
        method: 'PUT',
        data: menuIds
      })
}