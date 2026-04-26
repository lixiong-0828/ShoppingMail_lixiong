<template>
  <div class="home">
    <div class="welcome-card">
      <h2>欢迎，{{ userName }}</h2>
      <p>在线购物系统</p>
    </div>
    <div class="menu-grid">
      <div class="menu-item" @click="goTo('/product')">
        <span class="icon">🛒</span>
        <span class="label">商品一览</span>
      </div>
      <div class="menu-item" @click="goTo('/history')">
        <span class="icon">📋</span>
        <span class="label">购物履历</span>
      </div>
    </div>
    
    <div v-if="isAdmin" class="admin-section">
      <h3>管理功能</h3>
      <div class="menu-grid">
        <div class="menu-item" @click="goTo('/product-manage')">
          <span class="icon">⚙️</span>
          <span class="label">商品管理</span>
        </div>
        <div class="menu-item" @click="goTo('/user-manage')">
          <span class="icon">👥</span>
          <span class="label">用户管理</span>
        </div>
      </div>
      <div class="menu-grid" style="margin-top:15px">
        <div class="menu-item" @click="goTo('/batch-import')">
          <span class="icon">📤</span>
          <span class="label">批量导入</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'Home',
  setup() {
    const router = useRouter()
    const userName = ref('')
    const isAdmin = ref(false)

    onMounted(() => {
      userName.value = localStorage.getItem('userNameCh') || '用户'
      isAdmin.value = localStorage.getItem('role') === 'Admin'
      if (!localStorage.getItem('token')) {
        router.push('/login')
      }
    })

    const goTo = (path) => {
      router.push(path)
    }

    return { userName, isAdmin, goTo }
  }
}
</script>

<style scoped>
.home { padding: 15px; }
.welcome-card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 16px; padding: 30px 20px; margin-bottom: 20px; color: white; }
.welcome-card h2 { font-size: 20px; margin-bottom: 5px; }
.welcome-card p { font-size: 14px; opacity: 0.8; }
.menu-grid { display: flex; gap: 15px; }
.menu-item { flex: 1; background: white; border-radius: 12px; padding: 25px; display: flex; flex-direction: column; align-items: center; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.menu-item:active { background: #f0f0f0; }
.icon { font-size: 30px; margin-bottom: 10px; }
.label { font-size: 14px; color: #333; }
.admin-section { margin-top: 30px; }
.admin-section h3 { font-size: 16px; color: #333; margin-bottom: 15px; }
</style>