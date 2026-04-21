<template>
  <div class="user-management">
    <h2>用户管理</h2>
    
    <div class="search-section">
      <h3>检索用户一览</h3>
      <div class="search-form">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="searchUserName" type="text" placeholder="输入用户名搜索" />
        </div>
        <div class="form-group">
          <label>权限</label>
          <select v-model="searchRole">
            <option value="">全部</option>
            <option value="Admin">Admin</option>
            <option value="Customer">Customer</option>
          </select>
        </div>
        <button @click="searchUsers" class="search-btn">搜索</button>
      </div>
    </div>

    <div class="users-table">
      <h3>用户列表</h3>
      <table>
        <thead>
          <tr>
            <th>用户名(EN)</th>
            <th>用户名(CH)</th>
            <th>权限</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.userNameEn">
            <td>{{ user.userNameEn }}</td>
            <td>{{ user.userNameCh }}</td>
            <td>
              <span :class="['role-tag', user.role.toLowerCase()]">{{ user.role }}</span>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="users.length === 0" class="no-users">没有找到用户</div>
    </div>

    <div class="create-section">
      <h3>登录新用户</h3>
      <form @submit.prevent="createUser">
        <div class="form-row">
          <div class="form-group">
            <label>UserName(EN) * (Primary Key)</label>
            <input v-model="newUser.userNameEn" type="text" required />
          </div>
          <div class="form-group">
            <label>UserName(CH) *</label>
            <input v-model="newUser.userNameCh" type="text" required />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>密码 *</label>
            <input v-model="newUser.password" type="password" required />
          </div>
          <div class="form-group">
            <label>权限 *</label>
            <select v-model="newUser.role" required>
              <option value="Customer">Customer</option>
              <option value="Admin">Admin</option>
            </select>
          </div>
        </div>
        <button type="submit" class="submit-btn">创建用户</button>
        <div v-if="message" :class="['message', message.type]">{{ message.text }}</div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { userAPI } from '../services/api'

export default {
  name: 'UserManagement',
  setup() {
    const users = ref([])
    const searchUserName = ref('')
    const searchRole = ref('')
    const message = ref(null)
    const newUser = ref({
      userNameEn: '',
      userNameCh: '',
      password: '',
      role: 'Customer'
    })

    const searchUsers = async () => {
      try {
        const response = await userAPI.search(searchUserName.value, searchRole.value)
        users.value = response.data
      } catch (error) {
        console.error('Failed to search users:', error)
      }
    }

    const createUser = async () => {
      try {
        const response = await userAPI.create(newUser.value)
        const data = response.data
        message.value = { type: data.success ? 'success' : 'error', text: data.message }
        if (data.success) {
          newUser.value = { userNameEn: '', userNameCh: '', password: '', role: 'Customer' }
          searchUsers()
        }
      } catch (error) {
        message.value = { type: 'error', text: '创建用户失败' }
      }
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleString('zh-CN')
    }

    onMounted(() => {
      searchUsers()
    })

    return {
      users,
      searchUserName,
      searchRole,
      newUser,
      message,
      searchUsers,
      createUser,
      formatDate
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 1rem;
}

.user-management h2 {
  margin-bottom: 1.5rem;
  color: #333;
}

.search-section,
.create-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.search-section h3,
.create-section h3 {
  margin-bottom: 1rem;
  color: #555;
}

.search-form {
  display: flex;
  gap: 1rem;
  align-items: flex-end;
}

.search-form .form-group {
  flex: 1;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
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

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.search-btn,
.submit-btn {
  padding: 0.75rem 2rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.search-btn:hover,
.submit-btn:hover {
  background: #5568d3;
}

.message {
  margin-top: 1rem;
  padding: 0.75rem;
  border-radius: 4px;
  text-align: center;
}

.message.success {
  background: #d4edda;
  color: #155724;
}

.message.error {
  background: #f8d7da;
  color: #721c24;
}

.users-table {
  margin-bottom: 2rem;
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.users-table h3 {
  margin-bottom: 1rem;
  color: #555;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
}

th {
  background: #f8f9fa;
  font-weight: 600;
}

.role-tag {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
}

.role-tag.admin {
  background: #e74c3c;
  color: white;
}

.role-tag.customer {
  background: #3498db;
  color: white;
}

.no-users {
  padding: 2rem;
  text-align: center;
  color: #999;
}
</style>