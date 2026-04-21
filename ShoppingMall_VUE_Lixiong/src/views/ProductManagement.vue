<template>
  <div class="product-management">
    <h2>商品管理</h2>
    <div class="form-section">
      <h3>{{ editMode ? '编辑商品' : '新商品登入' }}</h3>
      <form @submit.prevent="submitProduct">
        <div class="form-row">
          <div class="form-group">
            <label>商品名 *</label>
            <input v-model="product.productName" type="text" required />
          </div>
          <div class="form-group">
            <label>价格 *</label>
            <input v-model="product.price" type="number" step="0.01" min="0" required />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>剩余数量 *</label>
            <input v-model="product.stockQuantity" type="number" min="0" required />
          </div>
          <div class="form-group">
            <label>商品种类 *</label>
            <select v-model="product.category" required>
              <option value="">请选择种类</option>
              <option value="电子商品">电子商品</option>
              <option value="生活用品">生活用品</option>
              <option value="体育用品">体育用品</option>
              <option value="其他">其他</option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label>图片 (可选)</label>
          <div class="file-upload">
            <input type="file" id="productImage" accept="image/*" @change="handleFileChange" />
            <div v-if="product.imageUrl" class="preview-image">
              <img :src="getImageUrl(product.imageUrl)" alt="Preview" />
              <button type="button" class="remove-btn" @click="removeImage">×</button>
            </div>
            <div v-else class="upload-placeholder">
              点击选择图片
            </div>
          </div>
        </div>
        <div class="form-actions">
          <button type="submit" class="submit-btn">{{ editMode ? '更新' : '创建' }}</button>
          <button v-if="editMode" type="button" class="cancel-btn" @click="cancelEdit">取消</button>
        </div>
        <div v-if="message" :class="['message', message.type]">{{ message.text }}</div>
      </form>
    </div>
    <div class="products-table">
      <h3>商品列表</h3>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>商品名</th>
            <th>价格</th>
            <th>数量</th>
            <th>种类</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.id">
            <td>{{ p.id }}</td>
            <td>{{ p.productName }}</td>
            <td>¥{{ p.price }}</td>
            <td>{{ p.stockQuantity }}</td>
            <td>{{ p.category }}</td>
            <td>
              <button @click="editProduct(p)" class="edit-btn">编辑</button>
              <button @click="deleteProduct(p.id)" class="delete-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { productAPI, uploadAPI } from '../services/api'

export default {
  name: 'ProductManagement',
  setup() {
    const products = ref([])
    const editMode = ref(false)
    const editId = ref(null)
    const message = ref(null)
    const product = ref({
      productName: '',
      price: '',
      stockQuantity: '',
      category: '',
      imageUrl: ''
    })

    const getImageUrl = (url) => {
      if (!url) return ''
      if (url.startsWith('http')) return url
      return url
    }

    const handleFileChange = async (event) => {
      const file = event.target.files[0]
      if (!file) return
      try {
        const response = await uploadAPI.upload(file)
        const data = response.data
        if (data.success) {
          product.value.imageUrl = data.url
        } else {
          message.value = { type: 'error', text: data.message || '上传失败' }
        }
      } catch (error) {
        message.value = { type: 'error', text: '上传失败' }
      }
    }

    const removeImage = () => {
      product.value.imageUrl = ''
    }

    const loadProducts = async () => {
      try {
        const response = await productAPI.getAll('ALL')
        products.value = response.data
      } catch (error) {
        console.error('Failed to load products:', error)
      }
    }

    const submitProduct = async () => {
      try {
        const payload = {
          ...product.value,
          price: parseFloat(product.value.price),
          stockQuantity: parseInt(product.value.stockQuantity)
        }
        let response
        if (editMode.value) {
          response = await productAPI.update(editId.value, payload)
        } else {
          response = await productAPI.create(payload)
        }
        const data = response.data
        message.value = { type: data.success ? 'success' : 'error', text: data.message }
        if (data.success) {
          resetForm()
          loadProducts()
        }
      } catch (error) {
        message.value = { type: 'error', text: '操作失败' }
      }
    }

    const editProduct = (p) => {
      editMode.value = true
      editId.value = p.id
      product.value = { ...p }
    }

    const deleteProduct = async (id) => {
      if (!confirm('确定要删除这个商品吗？')) return
      try {
        const response = await productAPI.delete(id)
        const data = response.data
        message.value = { type: data.success ? 'success' : 'error', text: data.message }
        if (data.success) {
          loadProducts()
        }
      } catch (error) {
        message.value = { type: 'error', text: '删除失败' }
      }
    }

    const cancelEdit = () => {
      resetForm()
    }

    const resetForm = () => {
      editMode.value = false
      editId.value = null
      product.value = {
        productName: '',
        price: '',
        stockQuantity: '',
        category: '',
        imageUrl: ''
      }
    }

    onMounted(() => {
      loadProducts()
    })

    return {
      products,
      editMode,
      product,
      message,
      getImageUrl,
      handleFileChange,
      removeImage,
      submitProduct,
      editProduct,
      deleteProduct,
      cancelEdit
    }
  }
}
</script>

<style scoped>
.product-management {
  padding: 1rem;
}

.product-management h2 {
  margin-bottom: 1.5rem;
  color: #333;
}

.form-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.form-section h3 {
  margin-bottom: 1rem;
  color: #555;
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

.file-upload {
  border: 2px dashed #ddd;
  border-radius: 4px;
  padding: 1rem;
  text-align: center;
  cursor: pointer;
  position: relative;
}

.file-upload input[type="file"] {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  opacity: 0;
  cursor: pointer;
}

.upload-placeholder {
  color: #999;
  padding: 1rem;
}

.preview-image {
  position: relative;
  display: inline-block;
}

.preview-image img {
  max-width: 200px;
  max-height: 150px;
  border-radius: 4px;
}

.preview-image .remove-btn {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 24px;
  height: 24px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.submit-btn {
  padding: 0.75rem 2rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.cancel-btn {
  padding: 0.75rem 2rem;
  background: #95a5a6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
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

.products-table {
  background: white;
  border-radius: 8px;
}

.products-table h3 {
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

.edit-btn {
  padding: 0.25rem 0.75rem;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 0.5rem;
}

.delete-btn {
  padding: 0.25rem 0.75rem;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>