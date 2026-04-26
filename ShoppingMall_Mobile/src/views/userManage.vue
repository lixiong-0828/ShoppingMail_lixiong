<template>
  <div class="manage">
    <div class="header">
      <button class="back-btn" @click="goBack">← 返回</button>
      <h2>用户管理</h2>
    </div>
    
    <div class="filter-bar">
      <select v-model="searchRole" @change="loadUsers">
        <option value="">全部</option>
        <option value="Admin">管理员</option>
        <option value="User">用户</option>
      </select>
      <button class="add-btn" @click="showAddModal = true">+ 添加</button>
    </div>
    
    <div class="user-list">
      <div v-for="item in users" :key="item.username" class="user-item">
        <div class="user-info">
          <h3>{{ item.userNameCh }}</h3>
          <p>{{ item.username }}</p>
          <span class="role-tag">{{ item.role }}</span>
        </div>
        <button class="delete-btn" @click="deleteUser(item.username)">删除</button>
      </div>
      <div v-if="users.length === 0" class="no-data">暂无用户</div>
    </div>
    
    <div v-if="showAddModal" class="modal-mask">
      <div class="modal-content">
        <h3>添加用户</h3>
        <div class="form">
          <div class="form-item">
            <label>用户名</label>
            <input v-model="form.username" type="text" />
          </div>
          <div class="form-item">
            <label>中文名</label>
            <input v-model="form.userNameCh" type="text" />
          </div>
          <div class="form-item">
            <label>密码</label>
            <input v-model="form.password" type="password" />
          </div>
          <div class="form-item">
            <label>角色</label>
            <select v-model="form.role">
              <option value="Admin">管理员</option>
              <option value="User">用户</option>
            </select>
          </div>
        </div>
        <div class="modal-buttons">
          <button class="save-btn" @click="saveUser">保存</button>
          <button class="cancel-btn" @click="showAddModal = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userManageAPI } from '../api/request'

export default {
  name: 'UserManage',
  setup() {
    const router = useRouter()
    const users = ref([])
    const searchRole = ref('')
    const showAddModal = ref(false)
    const form = ref({ username: '', userNameCh: '', password: '', role: 'User' })
    
    const loadUsers = async () => {
      try {
        const response = await userManageAPI.search('', searchRole.value)
        users.value = response.data || []
      } catch (err) {
        console.error('加载失败', err)
      }
    }
    
    const deleteUser = async (username) => {
      if (!confirm('确定删除？')) return
      alert('删除功能需要后端API支持')
    }
    
    const saveUser = async () => {
      try {
        await userManageAPI.create(form.value)
        showAddModal.value = false
        loadUsers()
      } catch (err) {
        alert('保存失败')
      }
    }
    
    const goBack = () => router.push('/home')
    
    onMounted(() => loadUsers())
    
    return { users, searchRole, showAddModal, form, loadUsers, deleteUser, saveUser, goBack }
  }
}
</script>

<style scoped>
.manage { min-height: 100vh; background: #f5f5f5; }
.header { display: flex; align-items: center; gap: 10px; padding: 15px; background: #fff; }
.back-btn { padding: 8px 12px; background: #667eea; color: #fff; border: none; border-radius: 4px; }
.header h2 { flex: 1; text-align: center; margin: 0; font-size: 18px; }
.filter-bar { display: flex; gap: 10px; padding: 15px; }
.filter-bar select { flex: 1; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
.add-btn { padding: 10px 16px; background: #27ae60; color: #fff; border: none; border-radius: 4px; }
.user-list { padding: 0 15px; }
.user-item { display: flex; align-items: center; background: #fff; border-radius: 8px; padding: 15px; margin-bottom: 15px; }
.user-info { flex: 1; }
.user-info h3 { font-size: 16px; margin-bottom: 5px; }
.user-info p { font-size: 14px; color: #666; margin-bottom: 5px; }
.role-tag { display: inline-block; padding: 4px 10px; background: #667eea; color: #fff; border-radius: 10px; font-size: 12px; }
.delete-btn { padding: 8px 12px; background: #e74c3c; color: #fff; border: none; border-radius: 4px; }
.no-data { text-align: center; padding: 50px; color: #999; }
.modal-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; }
.modal-content { background: #fff; border-radius: 12px; padding: 25px; width: 85%; max-width: 350px; }
.modal-content h3 { text-align: center; margin-bottom: 20px; }
.form-item { margin-bottom: 15px; }
.form-item label { display: block; margin-bottom: 5px; font-size: 14px; color: #555; }
.form-item input, .form-item select { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }
.modal-buttons { display: flex; gap: 10px; margin-top: 20px; }
.save-btn, .cancel-btn { flex: 1; padding: 12px; border: none; border-radius: 4px; font-size: 14px; }
.save-btn { background: #27ae60; color: #fff; }
.cancel-btn { background: #95a5a6; color: #fff; }
</style>