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
        </div>
      </div>
      <div v-if="products.length === 0" class="no-products">
        没有找到商品
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { productAPI } from '../services/api'

export default {
  name: 'ProductList',
  setup() {
    const products = ref([])
    const selectedCategory = ref('ALL')

    const getImageUrl = (url) => {
      if (!url) return ''
      if (url.startsWith('http')) return url
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

    onMounted(() => {
      filterProducts()
    })

    return {
      products,
      selectedCategory,
      getImageUrl,
      filterProducts
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
</style>