<template>
  <div class="manage">
    <div class="header">
      <button class="back-btn" @click="goBack">← 返回</button>
      <h2>商品管理</h2>
    </div>
    
    <div class="add-bar">
      <button class="add-btn" @click="showAddModal = true">+ 添加商品</button>
    </div>
    
    <div class="product-list">
      <div v-for="item in products" :key="item.id" class="product-item">
        <div class="product-main">
          <h3>{{ item.productName }}</h3>
          <p class="price">¥{{ item.price }}</p>
          <p class="stock">库存: {{ item.stockQuantity }}</p>
          <span class="category-tag">{{ item.category }}</span>
        </div>
        <div class="product-actions">
          <button class="edit-btn" @click="editProduct(item)">编辑</button>
          <button class="delete-btn" @click="deleteProduct(item.id)">删除</button>
        </div>
      </div>
      <div v-if="products.length === 0" class="no-data">暂无商品</div>
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <div v-if="showAddModal" class="modal-mask">
      <div class="modal-content">
        <h3>{{ editingProduct ? '编辑商品' : '添加商品' }}</h3>
        <div class="form">
          <div class="form-item">
            <label>商品名</label>
            <input v-model="form.productName" type="text" />
          </div>
          <div class="form-item">
            <label>价格</label>
            <input v-model.number="form.price" type="number" />
          </div>
          <div class="form-item">
            <label>库存</label>
            <input v-model.number="form.stockQuantity" type="number" />
          </div>
          <div class="form-item">
            <label>分类</label>
            <select v-model="form.category">
              <option value="电子商品">电子商品</option>
              <option value="生活用品">生活用品</option>
              <option value="体育用品">体育用品</option>
              <option value="其他">其他</option>
            </select>
          </div>
        </div>
        <div class="modal-buttons">
          <button class="save-btn" @click="saveProduct">保存</button>
          <button class="cancel-btn" @click="closeModal">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { productManageAPI } from '../api/request'

export default {
  name: 'ProductManage',
  setup() {
    const router = useRouter()
    const products = ref([])
    const showAddModal = ref(false)
    const editingProduct = ref(null)
    const form = ref({
      productName: '',
      price: 0,
      stockQuantity: 0,
      category: '电子商品'
    })
    
    const loadProducts = async () => {
      try {
        const response = await productManageAPI.getAll()
        products.value = response.data || []
      } catch (err) {
        console.error('加载失败', err)
      }
    }
    
    const editProduct = (item) => {
      editingProduct.value = item
      form.value = { ...item }
      showAddModal.value = true
    }
    
    const deleteProduct = async (id) => {
      if (!confirm('确定删除？')) return
      try {
        await productManageAPI.delete(id)
        loadProducts()
      } catch (err) {
        alert('删除失败')
      }
    }
    
    const saveProduct = async () => {
      try {
        if (editingProduct.value) {
          await productManageAPI.update(editingProduct.value.id, form.value)
        } else {
          await productManageAPI.create(form.value)
        }
        closeModal()
        loadProducts()
      } catch (err) {
        alert('保存失败')
      }
    }
    
    const closeModal = () => {
      showAddModal.value = false
      editingProduct.value = null
      form.value = { productName: '', price: 0, stockQuantity: 0, category: '电子商品' }
    }
    
    const goBack = () => router.push('/home')
    
    onMounted(() => loadProducts())
    
    return { products, showAddModal, editingProduct, form, editProduct, deleteProduct, saveProduct, closeModal, goBack }
  }
}
</script>

<style scoped>
.manage { min-height: 100vh; background: #f5f5f5; }
.header { display: flex; align-items: center; gap: 10px; padding: 15px; background: #fff; }
.back-btn { padding: 8px 12px; background: #667eea; color: #fff; border: none; border-radius: 4px; }
.header h2 { flex: 1; text-align: center; margin: 0; font-size: 18px; }
.add-bar { padding: 15px; }
.add-btn { width: 100%; padding: 12px; background: #27ae60; color: #fff; border: none; border-radius: 8px; font-size: 16px; }
.product-list { padding: 0 15px; }
.product-item { background: #fff; border-radius: 8px; padding: 15px; margin-bottom: 15px; }
.product-main h3 { font-size: 16px; margin-bottom: 8px; }
.price { font-size: 18px; color: #e74c3c; font-weight: bold; }
.stock { font-size: 14px; color: #666; margin: 5px 0; }
.category-tag { display: inline-block; padding: 4px 10px; background: #667eea; color: #fff; border-radius: 10px; font-size: 12px; }
.product-actions { display: flex; gap: 10px; margin-top: 10px; }
.edit-btn, .delete-btn { flex: 1; padding: 10px; border: none; border-radius: 4px; font-size: 14px; }
.edit-btn { background: #667eea; color: #fff; }
.delete-btn { background: #e74c3c; color: #fff; }
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