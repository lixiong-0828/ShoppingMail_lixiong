<template>
  <div class="history">
    <div class="filter-bar">
      <select v-model="selectedCategory" @change="loadHistory">
        <option value="">全部</option>
        <option value="电子商品">电子商品</option>
        <option value="生活用品">生活用品</option>
        <option value="体育用品">体育用品</option>
        <option value="其他">其他</option>
      </select>
      <button class="refresh-btn" @click="loadHistory">检索</button>
    </div>
    
    <div class="history-list">
      <div v-for="item in historyList" :key="item.id" class="history-card">
        <div class="history-header">
          <span class="order-no">订单ID: {{ item.id }}</span>
          <span class="order-date">{{ item.createdAt }}</span>
        </div>
        <div class="history-content">
          <h3>{{ item.productName }}</h3>
          <span class="category-tag">{{ item.category }}</span>
        </div>
        <div class="history-footer">
          <span>数量: {{ item.quantity }}</span>
          <span class="total-price">¥{{ item.price }}</span>
        </div>
      </div>
      <div v-if="historyList.length === 0" class="no-history">暂无购物记录</div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { cartAPI } from '../api/request'
import { useRouter } from 'vue-router'

export default {
  name: 'ShoppingHistory',
  setup() {
    const router = useRouter()
    const historyList = ref([])
    const selectedCategory = ref('')
    const role = ref('')
    
    const loadHistory = async () => {
      try {
        let response
        // Admin with category filter, regular users get all
        if (role.value === 'Admin' && selectedCategory.value) {
          // Admin can filter by category - fetch all and filter in frontend
          response = await cartAPI.getHistoryAll()
          const allData = response.data?.data || response.data || []
          historyList.value = allData.filter(item => item.category === selectedCategory.value)
        } else if (role.value === 'Admin') {
          response = await cartAPI.getHistoryAll()
          historyList.value = response.data?.data || response.data || []
        } else {
          response = await cartAPI.getHistory(selectedCategory.value)
          historyList.value = response.data?.data || response.data || []
        }
      } catch (err) {
        console.error('加载失败', err)
      }
    }
    
    onMounted(() => {
      role.value = localStorage.getItem('role') || ''
      loadHistory()
    })
    
    return { historyList, selectedCategory, loadHistory, role }
  }
}
</script>

<style scoped>
.history {
  min-height: 100vh;
  background: #f5f5f5;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 10px;
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

.refresh-btn {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.history-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.history-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 12px;
  color: #333;
}

.order-date {
  font-size: 12px;
  color: #999;
}

.history-content h3 {
  font-size: 15px;
  color: #333;
  margin-bottom: 10px;
}

.category-tag {
  display: inline-block;
  padding: 4px 10px;
  background: #667eea;
  color: white;
  border-radius: 10px;
  font-size: 11px;
}

.history-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}

.history-footer span {
  font-size: 14px;
  color: #666;
}

.history-footer .total-price {
  font-size: 16px;
  color: #e74c3c;
  font-weight: bold;
}

.no-history {
  padding: 50px;
  text-align: center;
  color: #999;
}
</style>