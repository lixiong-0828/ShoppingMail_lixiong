<template>
  <div class="login-container">
    <div class="login-box">
      <h2>登录 - 在线购物系统</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名 (Username)</label>
          <input v-model="username" type="text" required />
        </div>
        <div class="form-group">
          <label>密码 (Password)</label>
          <input v-model="password" type="password" required />
        </div>
        <div v-if="error" class="error">{{ error }}</div>
        <button type="submit" class="login-btn">登录</button>
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
import { authAPI } from '../services/api'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const username = ref('')
    const password = ref('')
    const error = ref('')

    const handleLogin = async () => {
      try {
        error.value = ''
        const response = await authAPI.login(username.value, password.value)
        const data = response.data
        if (data.success) {
          localStorage.setItem('token', data.token)
          localStorage.setItem('username', data.username)
          localStorage.setItem('role', data.role)
          localStorage.setItem('userNameCh', data.userNameCh)
          localStorage.setItem('loginTime', Date.now().toString())
          router.push('/home')
        } else {
          error.value = data.message || '登录失败'
        }
      } catch (err) {
        error.value = '登录失败，请检查网络'
      }
    }

    return {
      username,
      password,
      error,
      handleLogin
    }
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
}

.login-box {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #555;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.error {
  color: #e74c3c;
  margin-bottom: 1rem;
  text-align: center;
}

.login-btn {
  width: 100%;
  padding: 0.75rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.3s;
}

.login-btn:hover {
  background: #5568d3;
}

.hints {
  margin-top: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  font-size: 0.875rem;
  color: #666;
}

.hints p {
  margin: 0.25rem 0;
}
</style>