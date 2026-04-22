import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import ProductList from '../views/ProductList.vue'
import ProductManagement from '../views/ProductManagement.vue'
import UserManagement from '../views/UserManagement.vue'
import BatchImport from '../views/BatchImport.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/product-list',
    name: 'ProductList',
    component: ProductList
  },
  {
    path: '/product-management',
    name: 'ProductManagement',
    component: ProductManagement
  },
  {
    path: '/user-management',
    name: 'UserManagement',
    component: UserManagement
  },
  {
    path: '/batchImport',
    name: 'BatchImport',
    component: BatchImport   // 统一风格
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const getToken = () => {
  try {
    return localStorage.getItem('token')
  } catch {
    return null
  }
}

router.beforeEach((to, from, next) => {
  const token = getToken()
  
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/home')
  } else {
    next()
  }
})

export default router