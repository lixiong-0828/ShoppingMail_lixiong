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
        <!-- Step 1: 订单确认 -->
        <div v-if="payStep === 1">
          <h3>确认订单</h3>
          <div class="order-info">
            <p class="product-name">{{ selectedProduct?.productName }}</p>
            <p class="product-price">单价: ¥{{ selectedProduct?.price }}</p>
            <div class="quantity-selector">
              <label>数量：</label>
              <select v-model="selectedQuantity">
                <option v-for="n in 10" :key="n" :value="n">{{ n }}</option>
              </select>
            </div>
            <p class="total-price">总金额: ¥{{ (selectedProduct?.price * selectedQuantity).toFixed(2) }}</p>
          </div>
          <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
          <div class="modal-buttons">
            <button class="confirm-btn" @click="toPayStep">去支付</button>
            <button class="cancel-btn" @click="closeModal">取消</button>
          </div>
        </div>

        <!-- Step 2: 模拟支付 -->
        <div v-if="payStep === 2" class="pay-section">
          <h3>模拟微信支付</h3>
          <div class="qr-code">
            <div class="qr-placeholder">
              <div class="qr-icon">💳</div>
              <p>模拟支付二维码</p>
            </div>
          </div>
          <div class="pay-info">
            <p>订单号: {{ orderNo }}</p>
            <p class="pay-amount">需支付: ¥{{ totalAmount }}</p>
          </div>
          <p class="pay-tip">请使用微信扫描上方二维码完成支付</p>
          <div class="modal-buttons">
            <button class="pay-btn" @click="completeMockPayment">
              <span class="btn-icon">✓</span> 模拟支付成功
            </button>
            <button class="cancel-btn" @click="payStep = 1">返回</button>
          </div>
        </div>

        <!-- Step 3: 支付成功 -->
        <div v-if="payStep === 3" class="success-section">
          <div class="success-icon">✓</div>
          <h3>支付成功！</h3>
          <p>您的订单已确认</p>
          <div class="order-detail">
            <p>订单号: {{ orderNo }}</p>
            <p>商品: {{ selectedProduct?.productName }}</p>
            <p>数量: {{ selectedQuantity }}</p>
            <p class="amount">¥{{ totalAmount }}</p>
          </div>
          <button class="confirm-btn" @click="closeModal">完成</button>
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
    const payStep = ref(1)
    const orderNo = ref('')
    const totalAmount = ref('0.00')

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
      payStep.value = 1
      showConfirmModal.value = true
    }

    const closeModal = () => {
      showConfirmModal.value = false
      selectedProduct.value = null
      errorMessage.value = ''
      payStep.value = 1
    }

    const toPayStep = () => {
      if (selectedQuantity.value > selectedProduct.value.stockQuantity) {
        errorMessage.value = '购买数量不能超过剩余数量：' + selectedProduct.value.stockQuantity
        return
      }
      orderNo.value = 'ORD' + Date.now()
      totalAmount.value = (selectedProduct.value.price * selectedQuantity.value).toFixed(2)
      payStep.value = 2
      errorMessage.value = ''
    }

    const completeMockPayment = async () => {
      try {
        const response = await cartAPI.buy(selectedProduct.value.id, selectedQuantity.value)
        if (response.data.success) {
          payStep.value = 3
        } else {
          errorMessage.value = response.data.message
        }
      } catch (error) {
        console.error('Payment failed:', error)
        errorMessage.value = '支付失败，请重试'
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
      payStep,
      orderNo,
      totalAmount,
      openBuyModal,
      closeModal,
      toPayStep,
      completeMockPayment
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

/* 模拟支付样式 */
.order-info {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.order-info .product-name {
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.order-info .product-price {
  color: #666;
  margin-bottom: 0.5rem;
}

.order-info .quantity-selector {
  margin: 0.75rem 0;
}

.order-info .total-price {
  font-size: 1.2rem;
  color: #e74c3c;
  font-weight: bold;
  margin-top: 0.5rem;
  padding-top: 0.5rem;
  border-top: 1px dashed #ddd;
}

.pay-section .qr-code {
  background: white;
  border: 2px dashed #27ae60;
  border-radius: 8px;
  padding: 1.5rem;
  margin: 1rem 0;
}

.qr-placeholder {
  text-align: center;
}

.qr-icon {
  font-size: 4rem;
  margin-bottom: 0.5rem;
}

.qr-placeholder p {
  color: #666;
  font-size: 0.9rem;
}

.pay-info {
  background: #fff9e6;
  padding: 0.75rem;
  border-radius: 8px;
  margin-bottom: 0.5rem;
}

.pay-info p {
  margin: 0.25rem 0;
  color: #666;
}

.pay-info .pay-amount {
  font-size: 1.3rem;
  color: #e74c3c;
  font-weight: bold;
}

.pay-tip {
  text-align: center;
  color: #999;
  font-size: 0.85rem;
  margin-bottom: 1rem;
}

.pay-btn {
  flex: 1;
  padding: 0.75rem;
  background: linear-gradient(135deg, #07c160 0%, #06ad56 100%);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.pay-btn:hover {
  background: linear-gradient(135deg, #06ad56 0%, #059a4d 100%);
}

.btn-icon {
  font-size: 1.2rem;
}

.success-section {
  text-align: center;
  padding: 1rem 0;
}

.success-icon {
  width: 60px;
  height: 60px;
  background: #27ae60;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin: 0 auto 1rem;
}

.success-section h3 {
  color: #27ae60;
  margin-bottom: 0.5rem;
}

.success-section > p {
  color: #666;
  margin-bottom: 1rem;
}

.order-detail {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  text-align: left;
  margin-bottom: 1rem;
}

.order-detail p {
  margin: 0.25rem 0;
  color: #666;
}

.order-detail .amount {
  font-size: 1.2rem;
  color: #e74c3c;
  font-weight: bold;
  margin-top: 0.5rem;
  padding-top: 0.5rem;
  border-top: 1px dashed #ddd;
}
</style>