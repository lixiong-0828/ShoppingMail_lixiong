<template>
  <div id="app">
    <div v-if="isLoggedIn" class="layout">
      <header class="header">
        <h1>在线购物系统</h1>
        <button @click="logout" class="logout-btn">退出</button>
      </header>
      <nav class="tab-bar">
        <router-link to="/home" class="tab-item">首页</router-link>
        <router-link to="/product" class="tab-item">商品</router-link>
        <router-link to="/history" class="tab-item">订单</router-link>
        <router-link v-if="isAdmin" to="/product-manage" class="tab-item">管理</router-link>
      </nav>
      <main class="main-content">
        <router-view />
      </main>
    </div>
    <div v-else class="login-page">
      <router-view />
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const isLoggedIn = ref(false)
    const isAdmin = ref(false)

    const checkLogin = () => {
      isLoggedIn.value = !!localStorage.getItem('token')
      isAdmin.value = localStorage.getItem('role') === 'Admin'
    }

    const logout = () => {
      if (confirm('确定要退出登录吗？')) {
        localStorage.clear()
        isLoggedIn.value = false
        isAdmin.value = false
        router.push('/login')
      }
    }

    onMounted(() => {
      checkLogin()
    })

    return { isLoggedIn, isAdmin, logout }
  }
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
#app { min-height: 100vh; }
.layout { min-height: 100vh; background: #f5f5f5; }
.header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 15px; display: flex; justify-content: space-between; align-items: center; }
.header h1 { color: white; font-size: 16px; }
.logout-btn { padding: 6px 12px; background: rgba(255,255,255,0.2); color: #fff; border: none; border-radius: 4px; font-size: 12px; }
.tab-bar { display: flex; background: white; border-bottom: 1px solid #eee; flex-wrap: wrap; }
.tab-item { flex: 1; min-width: 25%; padding: 12px 8px; text-align: center; color: #666; text-decoration: none; font-size: 12px; }
.tab-item.router-link-active { color: #667eea; border-bottom: 2px solid #667eea; }
.main-content { padding: 10px; }
.login-page { min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
</style>