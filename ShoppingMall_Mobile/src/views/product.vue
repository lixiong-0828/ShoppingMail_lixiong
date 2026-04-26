<template>
  <div class="product-list">
    <div class="filter-bar">
      <span>商品种类：</span>
      <select v-model="selectedCategory" @change="loadProducts">
        <option value="">全部</option>
        <option value="电子商品">电子商品</option>
        <option value="生活用品">生活用品</option>
        <option value="体育用品">体育用品</option>
        <option value="其他">其他</option>
      </select>
    </div>
    
    <div class="products-grid">
      <div v-for="item in products" :key="item.id" class="product-card" @click="openBuyModal(item)">
        <div class="product-image">
          <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" :alt="item.productName" />
          <div v-else class="no-image">暂无图片</div>
        </div>
        <div class="product-info">
          <h3>{{ item.productName }}</h3>
          <p class="price">¥{{ item.price }}</p>
          <p class="stock">库存: {{ item.stockQuantity }}</p>
          <span class="category-tag">{{ item.category }}</span>
        </div>
      </div>
      <div v-if="products.length === 0" class="no-products">暂无商品</div>
    </div>
    
    <!-- 购买弹窗 -->
    <div v-if="showModal" class="modal-mask" @click="closeModal">
      <div class="modal-content" @click.stop>
        <!-- Step 1: 确认订单 -->
        <div v-if="step === 1">
          <h3>确认订单</h3>
          <div class="order-info">
            <p class="product-name">{{ selectedProduct?.productName }}</p>
            <p class="product-price">单价: ¥{{ selectedProduct?.price }}</p>
            <div class="quantity-row">
              <span>数量：</span>
              <select v-model="selectedQuantity">
                <option v-for="n in 10" :key="n" :value="n">{{ n }}</option>
              </select>
            </div>
            <p class="total-price">总金额: ¥{{ totalAmount }}</p>
          </div>
          <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
          <div class="modal-buttons">
            <button class="confirm-btn" @click="toPayStep">去支付</button>
            <button class="cancel-btn" @click="closeModal">取消</button>
          </div>
        </div>
        
        <!-- Step 2: 模拟支付 -->
        <div v-if="step === 2" class="pay-section">
          <h3>模拟微信支付</h3>
          <div class="qr-area">💳</div>
          <p>模拟支付二维码</p>
          <div class="pay-info">
            <p>订单号: {{ orderNo }}</p>
            <p class="pay-amount">需支付: ¥{{ totalAmount }}</p>
          </div>
          <div class="modal-buttons">
            <button class="pay-btn" @click="completePayment">模拟支付成功</button>
            <button class="cancel-btn" @click="step = 1">返回</button>
          </div>
        </div>
        
        <!-- Step 3: 支付成功 -->
        <div v-if="step === 3" class="success-section">
          <div class="success-icon">✓</div>
          <h3>支付成功！</h3>
          <p>您的订单已确认</p>
          <div class="order-detail">
            <p>订单号: {{ orderNo }}</p>
            <p>商品: {{ selectedProduct?.productName }}</p>
            <p>数量: {{ selectedQuantity }}</p>
            <p class="detail-amount">¥{{ totalAmount }}</p>
          </div>
          <button class="confirm-btn" @click="closeModal">完成</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { productAPI, cartAPI } from '../api/request'

export default {
  name: 'ProductList',
  setup() {
    const products = ref([])
    const selectedCategory = ref('')
    const showModal = ref(false)
    const step = ref(1)
    const selectedProduct = ref(null)
    const selectedQuantity = ref(1)
    const errorMsg = ref('')
    const orderNo = ref('')
    
    const totalAmount = computed(() => {
      if (!selectedProduct.value) return '0.00'
      return (selectedProduct.value.price * selectedQuantity.value).toFixed(2)
    })
    
    const loadProducts = async () => {
      try {
        const response = await productAPI.getAll(selectedCategory.value)
        products.value = response.data
      } catch (err) {
        console.error('加载失败', err)
      }
    }
    
    const getImageUrl = (url) => {
      if (!url) return ''
      if (url.startsWith('http')) return url
      if (url.startsWith('/api')) return url
      return url
    }
    
    const openBuyModal = (product) => {
      selectedProduct.value = product
      selectedQuantity.value = 1
      step.value = 1
      errorMsg.value = ''
      showModal.value = true
    }
    
    const closeModal = () => {
      showModal.value = false
      selectedProduct.value = null
      step.value = 1
      loadProducts()
    }
    
    const toPayStep = () => {
      if (selectedQuantity.value > selectedProduct.value.stockQuantity) {
        errorMsg.value = '购买数量不能超过库存：' + selectedProduct.value.stockQuantity
        return
      }
      orderNo.value = 'ORD' + Date.now()
      step.value = 2
      errorMsg.value = ''
    }
    
    const completePayment = async () => {
      try {
        const response = await cartAPI.buy(selectedProduct.value.id, selectedQuantity.value)
        const res = response.data
        if (res.success) {
          step.value = 3
        } else {
          errorMsg.value = res.message
        }
      } catch (err) {
        errorMsg.value = '支付失败'
      }
    }
    
    onMounted(() => {
      loadProducts()
    })
    
    return {
      products, selectedCategory, showModal, step, selectedProduct,
      selectedQuantity, errorMsg, orderNo, totalAmount,
      loadProducts, getImageUrl, openBuyModal, closeModal, toPayStep, completePayment
    }
  }
}
</script>

<style scoped>
.product-list {
  min-height: 100vh;
  background: #f5f5f5;
}

.filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  background: white;
  padding: 15px;
  border-radius: 8px;
}

.filter-bar select {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.products-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.product-card {
  width: calc(50% - 7.5px);
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.product-image {
  height: 120px;
  background: #f0f0f0;
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
  font-size: 12px;
}

.product-info {
  padding: 12px;
}

.product-info h3 {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.price {
  font-size: 16px;
  color: #e74c3c;
  font-weight: bold;
}

.stock {
  font-size: 12px;
  color: #666;
  margin: 5px 0;
}

.category-tag {
  display: inline-block;
  padding: 4px 10px;
  background: #667eea;
  color: white;
  border-radius: 10px;
  font-size: 11px;
}

.no-products {
  width: 100%;
  padding: 50px;
  text-align: center;
  color: #999;
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 25px;
  width: 85%;
  max-width: 350px;
}

.modal-content h3 {
  margin-bottom: 15px;
  text-align: center;
}

.order-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
}

.order-info .product-name {
  font-weight: bold;
  color: #333;
}

.order-info .product-price {
  color: #666;
  margin: 8px 0;
}

.quantity-row {
  display: flex;
  align-items: center;
  margin: 10px 0;
}

.quantity-row select {
  margin-left: 10px;
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.total-price {
  font-size: 18px;
  color: #e74c3c;
  font-weight: bold;
  padding-top: 10px;
  border-top: 1px dashed #ddd;
}

.error-msg {
  color: #e74c3c;
  text-align: center;
  margin-bottom: 10px;
  font-size: 13px;
}

.modal-buttons {
  display: flex;
  gap: 10px;
}

.confirm-btn, .pay-btn {
  flex: 1;
  padding: 12px;
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
}

.cancel-btn {
  flex: 1;
  padding: 12px;
  background: #95a5a6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
}

.pay-section {
  text-align: center;
}

.qr-area {
  font-size: 60px;
  padding: 30px;
  border: 2px dashed #27ae60;
  border-radius: 8px;
  margin: 15px 0;
}

.pay-info {
  background: #fff9e6;
  padding: 12px;
  border-radius: 8px;
  margin: 15px 0;
}

.pay-amount {
  font-size: 18px;
  color: #e74c3c;
  font-weight: bold;
}

.success-section {
  text-align: center;
}

.success-icon {
  width: 50px;
  height: 50px;
  line-height: 50px;
  background: #27ae60;
  color: white;
  border-radius: 50%;
  font-size: 24px;
  margin: 0 auto 15px;
}

.order-detail {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin: 15px 0;
  text-align: left;
}

.order-detail p {
  margin: 5px 0;
  color: #666;
}

.detail-amount {
  font-size: 18px;
  color: #e74c3c;
  font-weight: bold;
}
</style>