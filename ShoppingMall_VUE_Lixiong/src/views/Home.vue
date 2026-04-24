<template>
  <div class="home">
    <div class="welcome">
      <h1>Welcome To Online-Shopping</h1>
      <p>欢迎来到在线购物系统</p>
      <div class="features">
        <div class="feature-card" @click="goTo('/product-list')">
          <div class="icon">🛒</div>
          <h3>商品一览</h3>
          <p>浏览所有商品</p>
        </div>
        <div class="feature-card" @click="goTo('/product-management')">
          <div class="icon">⚙️</div>
          <h3>商品管理</h3>
          <p>管理商品库存</p>
        </div>
        <div class="feature-card" @click="goTo('/user-management')">
          <div class="icon">👥</div>
          <h3>用户管理</h3>
          <p>管理系统用户</p>
        </div>
        <div class="feature-card" @click="goTo('/batchImport')">
          <div class="icon">📤</div>
          <h3>批量登录</h3>
          <p>Excel批量导入</p>
        </div>
        <div class="feature-card" @click="goTo('/shopping-history')">
          <div class="icon">📋</div>
          <h3>购物履历</h3>
          <p>查看购买记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'Home',
  setup() {
    const router = useRouter()
    
    const goTo = (path) => {
      router.push(path)
    }
    
    return { goTo }
  }
}
</script>

<style scoped>
.home {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 4rem);
}

.welcome {
  text-align: center;
}

.welcome h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.welcome > p {
  color: #7f8c8d;
  margin-bottom: 3rem;
  font-size: 1.25rem;
}

.features {
  display: flex;
  gap: 2rem;
  justify-content: center;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 180px;
  transition: transform 0.3s, cursor 0.3s;
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
  font-size: 1rem;
}

.feature-card p {
  color: #7f8c8d;
  font-size: 0.875rem;
}
</style>