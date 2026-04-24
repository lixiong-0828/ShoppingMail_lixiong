<template>
  <div class="shopping-history">
    <h2>购物履历</h2>
    <div class="filter-bar">
      <label>商品种类：</label>
      <select v-model="selectedCategory">
        <option value="ALL">全部</option>
        <option value="电子商品">电子商品</option>
        <option value="生活用品">生活用品</option>
        <option value="体育用品">体育用品</option>
        <option value="其他">其他</option>
      </select>
      <button class="search-btn" @click="searchHistory">查询</button>
    </div>

    <div class="history-table" v-if="history.length > 0">
      <table>
        <thead>
          <tr>
            <th>商品名称</th>
            <th>单价</th>
            <th>购买数量</th>
            <th>小计</th>
            <th>商品种类</th>
            <th>购买时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in history" :key="item.id">
            <td>{{ item.productName || '商品已下架' }}</td>
            <td>¥{{ item.price }}</td>
            <td>{{ item.quantity }}</td>
            <td>¥{{ (item.price * item.quantity).toFixed(2) }}</td>
            <td>{{ item.category || '-' }}</td>
            <td>{{ formatDate(item.createdAt) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="no-data">
      暂无购物记录
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { cartAPI } from '../services/api'

export default {
  name: 'ShoppingHistory',
  setup() {
    const history = ref([])
    const selectedCategory = ref('ALL')

    const searchHistory = async () => {
      try {
        const response = await cartAPI.getHistory(selectedCategory.value)
        if (response.data.success) {
          history.value = response.data.data
        }
      } catch (error) {
        console.error('Failed to load history:', error)
      }
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    onMounted(() => {
      searchHistory()
    })

    return {
      history,
      selectedCategory,
      searchHistory,
      formatDate
    }
  }
}
</script>

<style scoped>
.shopping-history {
  padding: 1rem;
}

.shopping-history h2 {
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

.search-btn {
  padding: 0.5rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.search-btn:hover {
  background: #5a6fd6;
}

.history-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #f8f9fa;
}

th {
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #555;
  border-bottom: 2px solid #dee2e6;
}

td {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

tbody tr:hover {
  background: #f8f9fa;
}

.no-data {
  text-align: center;
  padding: 3rem;
  color: #999;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>