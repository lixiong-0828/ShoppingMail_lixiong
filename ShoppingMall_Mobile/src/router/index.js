import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/login.vue'
import Home from '../views/home.vue'
import ProductList from '../views/product.vue'
import ShoppingHistory from '../views/history.vue'
import ProductManage from '../views/productManage.vue'
import UserManage from '../views/userManage.vue'
import BatchImport from '../views/batchImport.vue'

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/login', component: Login },
  { path: '/home', component: Home },
  { path: '/product', component: ProductList },
  { path: '/history', component: ShoppingHistory },
  { path: '/product-manage', component: ProductManage },
  { path: '/user-manage', component: UserManage },
  { path: '/batch-import', component: BatchImport }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/home')
  } else if ((to.path === '/product-manage' || to.path === '/user-manage' || to.path === '/batch-import') && role !== 'Admin') {
    next('/home')
  } else {
    next()
  }
})

export default router