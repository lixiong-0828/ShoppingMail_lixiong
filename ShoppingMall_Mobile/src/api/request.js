import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.clear()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const authAPI = {
  login: (username, password) => api.post('/auth/login', { username, password })
}

export const productAPI = {
  getAll: (category) => api.get('/products', { params: { category } })
}

export const cartAPI = {
  getHistory: (category) => api.get('/cart/history', { params: { category } }),
  getHistoryAll: () => api.get('/cart/history-all'),
  buy: (productId, quantity) => api.post('/cart/buy', { productId, quantity })
}

export const productManageAPI = {
  getAll: () => api.get('/products'),
  create: (product) => api.post('/products', product),
  update: (id, product) => api.put(`/products/${id}`, product),
  delete: (id) => api.delete(`/products/${id}`)
}

export const userManageAPI = {
  search: (userName, role) => api.get('/users', { params: { userName, role } }),
  create: (user) => api.post('/users', user),
  getByUsername: (username) => api.get(`/users/${username}`)
}

export default api