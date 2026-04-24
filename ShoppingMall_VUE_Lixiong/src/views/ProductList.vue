<template>
  <div class="product-list">
    <h2>商品一览</h2>
    <div class="filter-bar">
      <label>商品种类：</label>
      <select v-model="selectedCategory" @change="filterProducts">
        <option value="ALL">全部</option>
        <option value="电子商品">电子商品</option>
        <option value="生活用品">生活用品</option>
        <option value="体育用品">体育用品</option>
        <option value="其他">其他</option>
      </select>
    </div>
    <div class="products-grid">
      <div v-for="product in products" :key="product.id" class="product-card">
        <div class="product-image">
          <img v-if="product.imageUrl" :src="getImageUrl(product.imageUrl)" :alt="product.productName" />
          <div v-else class="no-image">No Image</div>
        </div>
        <div class="product-info">
          <h3>{{ product.productName }}</h3>
          <p class="price">¥{{ product.price }}</p>
          <p class="stock">剩余数量: {{ product.stockQuantity }}</p>
          <span class="category-tag">{{ product.category }}</span>
          <button class="buy-btn" @click="openBuyModal(product)">购买</button>
        </div>
      </div>
      <div v-if="products.length === 0" class="no-products">
        没有找到商品
      </div>
    </div>

    <!-- 购买确认弹窗 -->
    <div v-if="showConfirmModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <h3>确认购买</h3>
        <p>确认购买吗？</p>
        <p class="product-name">{{ selectedProduct?.productName }}</p>
        <p class="product-price">¥{{ selectedProduct?.price }}</p>
        <div class="quantity-selector">
          <label>数量：</label>
          <select v-model="selectedQuantity">
            <option v-for="n in 10" :key="n" :value="n">{{ n }}</option>
          </select>
        </div>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <div class="modal-buttons">
          <button class="confirm-btn" @click="confirmPurchase">确认</button>
          <button class="cancel-btn" @click="closeModal">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { productAPI, cartAPI } from '../services/api'

export default {
  name: 'ProductList',
  setup() {
    const products = ref([])
    const selectedCategory = ref('ALL')
    const showConfirmModal = ref(false)
    const selectedProduct = ref(null)
    const selectedQuantity = ref(1)
    const errorMessage = ref('')

    const getImageUrl = (url) => {
      if (!url) return ''
      if (url.startsWith('http')) return url
      if (url.startsWith('/api/upload')) return url
      return url
    }

    const filterProducts = async () => {
      try {
        const response = await productAPI.getAll(selectedCategory.value)
        products.value = response.data
      } catch (error) {
        console.error('Failed to load products:', error)
      }
    }

    const openBuyModal = (product) => {
      selectedProduct.value = product
      selectedQuantity.value = 1
      errorMessage.value = ''
      showConfirmModal.value = true
    }

    const closeModal = () => {
      showConfirmModal.value = false
      selectedProduct.value = null
      errorMessage.value = ''
    }

    const confirmPurchase = async () => {
      if (selectedQuantity.value > selectedProduct.value.stockQuantity) {
        errorMessage.value = '购买数量不能超过剩余数量：' + selectedProduct.value.stockQuantity
        return
      }

      try {
        const response = await cartAPI.buy(selectedProduct.value.id, selectedQuantity.value)
        if (response.data.success) {
          alert('购买成功！')
          closeModal()
          filterProducts()
        } else {
          errorMessage.value = response.data.message
        }
      } catch (error) {
        console.error('Purchase failed:', error)
        errorMessage.value = '购买失败，请重试'
      }
    }

    onMounted(() => {
      filterProducts()
    })

    return {
      products,
      selectedCategory,
      getImageUrl,
      filterProducts,
      showConfirmModal,
      selectedProduct,
      selectedQuantity,
      errorMessage,
      openBuyModal,
      closeModal,
      confirmPurchase
    }
  }
}
</script>

<style scoped>
.product-list {
  padding: 1rem;
}

.product-list h2 {
  margin-bottom: 1.5rem;
  color: #333;
}

.filter-bar {
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.filter-bar label {
  font-weight: 500;
  color: #555;
}

.filter-bar select {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  min-width: 150px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

.product-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 180px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  color: #999;
  font-size: 0.875rem;
}

.product-info {
  padding: 1rem;
}

.product-info h3 {
  margin-bottom: 0.5rem;
  color: #333;
  font-size: 1.1rem;
}

.price {
  color: #e74c3c;
  font-size: 1.25rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.stock {
  color: #666;
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
}

.category-tag {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  background: #667eea;
  color: white;
  border-radius: 12px;
  font-size: 0.75rem;
}

.no-products {
  grid-column: 1 / -1;
  text-align: center;
  padding: 3rem;
  color: #999;
}

.buy-btn {
  width: 100%;
  margin-top: 0.5rem;
  padding: 0.5rem;
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.buy-btn:hover {
  background: #219a52;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  min-width: 320px;
  max-width: 400px;
}

.modal-content h3 {
  margin-bottom: 1rem;
  color: #333;
}

.modal-content .product-name {
  font-weight: bold;
  color: #27ae60;
}

.modal-content .product-price {
  color: #e74c3c;
  font-size: 1.2rem;
  margin-bottom: 1rem;
}

.quantity-selector {
  margin: 1rem 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity-selector select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.error-message {
  color: #e74c3c;
  margin: 0.5rem 0;
}

.modal-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.confirm-btn {
  flex: 1;
  padding: 0.75rem;
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.confirm-btn:hover {
  background: #219a52;
}

.cancel-btn {
  flex: 1;
  padding: 0.75rem;
  background: #95a5a6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.cancel-btn:hover {
  background: #7f8c8d;
}
</style>