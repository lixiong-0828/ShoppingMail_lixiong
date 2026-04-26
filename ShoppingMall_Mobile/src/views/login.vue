<template>
  <div class="login-container">
    <div class="login-box">
      <h2>登录</h2>
      <p class="subtitle">在线购物系统</p>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="username" type="text" required />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="password" type="password" required />
        </div>
        <div v-if="error" class="error">{{ error }}</div>
        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <div class="hints">
        <p>提示: admin/admin123 (管理员), customer/customer123 (顾客)</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../api/request'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const username = ref('')
    const password = ref('')
    const error = ref('')
    const loading = ref(false)

    const handleLogin = async () => {
      if (!username.value || !password.value) {
        error.value = '请输入用户名和密码'
        return
      }
      loading.value = true
      error.value = ''
      try {
        const response = await authAPI.login(username.value, password.value)
        const res = response.data
        if (res.success) {
          localStorage.setItem('token', res.token)
          localStorage.setItem('username', res.username)
          localStorage.setItem('role', res.role)
          localStorage.setItem('userNameCh', res.userNameCh)
          localStorage.setItem('loginTime', Date.now().toString())
          router.push('/home')
        } else {
          error.value = res.message || '登录失败'
        }
      } catch (err) {
        error.value = '登录失败，请检查网络'
      } finally {
        loading.value = false
      }
    }
    return { username, password, error, loading, handleLogin }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  background: white;
  padding: 40px 30px;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 10px;
  color: #333;
}

.subtitle {
  text-align: center;
  color: #999;
  margin-bottom: 30px;
  font-size: 14px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.error {
  color: #e74c3c;
  margin-bottom: 15px;
  text-align: center;
  font-size: 14px;
}

.login-btn {
  width: 100%;
  padding: 14px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}

.login-btn:hover {
  background: #5568d3;
}

.login-btn:disabled {
  background: #ccc;
}

.hints {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 12px;
  color: #666;
}
</style>