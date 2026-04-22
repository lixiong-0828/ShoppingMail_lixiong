<template>
  <div id="app">
    <div v-if="isLoggedIn" class="layout">
      <aside class="sidebar">
        <div class="sidebar-header">Shopping Mall</div>
        <nav class="menu">
          <router-link to="/home" class="menu-item">主画面</router-link>
          <router-link to="/product-list" class="menu-item">商品一览</router-link>
          <router-link to="/product-management" class="menu-item">商品管理</router-link>
          <router-link to="/user-management" class="menu-item">用户管理</router-link>
          <router-link to="/batchImport" class="menu-item">批量登录</router-link>
        </nav>
        <div class="sidebar-footer">
          <div class="user-info">
            <div>{{ userNameCh }}</div>
            <div class="system-time">系统时间: {{ currentTime }}</div>
            <div class="login-time">登录后: {{ loginDuration }}</div>
          </div>
          <button @click="logout" class="logout-btn">退出</button>
        </div>
      </aside>
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
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const role = ref('')
    const userNameCh = ref('')
    const loginTime = ref(0)
    const isLoggedIn = ref(false)
    
    const currentTime = ref('')
    const loginDuration = ref('')
    let timer = null

    const checkLogin = () => {
      isLoggedIn.value = !!localStorage.getItem('token')
      role.value = localStorage.getItem('role') || ''
      userNameCh.value = localStorage.getItem('userNameCh') || ''
      loginTime.value = parseInt(localStorage.getItem('loginTime')) || 0
    }

    const updateTime = () => {
      const now = new Date()
      currentTime.value = now.toLocaleString('zh-CN', { 
        year: 'numeric', 
        month: '2-digit', 
        day: '2-digit',
        hour: '2-digit', 
        minute: '2-digit',
        second: '2-digit'
      })
      
      if (loginTime.value > 0) {
        const diff = Math.floor((now.getTime() - loginTime.value) / 1000)
        const hours = Math.floor(diff / 3600)
        const minutes = Math.floor((diff % 3600) / 60)
        const seconds = diff % 60
        loginDuration.value = `${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
      }
    }

    const logout = () => {
      localStorage.clear()
      role.value = ''
      userNameCh.value = ''
      loginTime.value = 0
      isLoggedIn.value = false
      if (timer) clearInterval(timer)
      router.push('/login')
    }

    watch(() => route.path, () => {
      checkLogin()
    }, { immediate: true })

    onMounted(() => {
      checkLogin()
      if (!loginTime.value && isLoggedIn.value) {
        loginTime.value = Date.now()
        localStorage.setItem('loginTime', loginTime.value)
      }
      updateTime()
      timer = setInterval(() => {
        updateTime()
        checkLogin()
      }, 1000)
    })

    onUnmounted(() => {
      if (timer) clearInterval(timer)
    })

    return {
      isLoggedIn,
      role,
      userNameCh,
      currentTime,
      loginDuration,
      logout
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 220px;
  background: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
}

.sidebar-header {
  padding: 1.5rem;
  font-size: 1.25rem;
  font-weight: bold;
  border-bottom: 1px solid #34495e;
}

.menu {
  flex: 1;
  padding: 1rem 0;
}

.menu-item {
  display: block;
  padding: 0.875rem 1.5rem;
  color: #ecf0f1;
  text-decoration: none;
  transition: background 0.2s;
}

.menu-item:hover {
  background: #34495e;
}

.menu-item.router-link-active {
  background: #34495e;
  border-left: 3px solid #667eea;
}

.sidebar-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #34495e;
}

.user-info {
  margin-bottom: 0.75rem;
  font-size: 0.875rem;
  color: #bdc3c7;
}

.user-info > div {
  margin-bottom: 0.25rem;
}

.system-time, .login-time {
  font-size: 0.75rem;
  color: #95a5a6;
}

.logout-btn {
  width: 100%;
  padding: 0.5rem;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.logout-btn:hover {
  background: #c0392b;
}

.main-content {
  flex: 1;
  margin-left: 220px;
  padding: 2rem;
  background: #f5f5f5;
  min-height: 100vh;
}

.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
</style>