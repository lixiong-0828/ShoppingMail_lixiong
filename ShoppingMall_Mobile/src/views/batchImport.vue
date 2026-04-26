<template>
  <div class="batch">
    <div class="header">
      <button class="back-btn" @click="goBack">← 返回</button>
      <h2>批量导入</h2>
    </div>
    
    <div class="content">
      <div class="tips">
        <h3>Excel批量导入说明</h3>
        <p>1. 准备Excel文件(.xlsx)</p>
        <p>2. 表头必须包含: 商品名称, 价格, 库存, 分类</p>
        <p>3. 示例:</p>
        <table class="example-table">
          <tr><th>商品名称</th><th>价格</th><th>库存</th><th>分类</th></tr>
          <tr><td>iPhone15</td><td>5999</td><td>100</td><td>电子商品</td></tr>
          <tr><td>羽毛球拍</td><td>168</td><td>50</td><td>体育用品</td></tr>
        </table>
      </div>
      
      <div class="upload-area">
        <input type="file" accept=".xlsx,.xls" @change="onFileChange" ref="fileInput" />
        <button class="upload-btn" @click="uploadFile" :disabled="!file || uploading">
          {{ uploading ? '上传中...' : '选择文件上传' }}
        </button>
      </div>
      
      <div v-if="result" class="result" :class="result.success ? 'success' : 'error'">
        <p>{{ result.message }}</p>
        <p v-if="result.count">成功导入: {{ result.count }} 条</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'BatchImport',
  setup() {
    const router = useRouter()
    const file = ref(null)
    const uploading = ref(false)
    const result = ref(null)
    const fileInput = ref(null)
    
    const onFileChange = (e) => {
      file.value = e.target.files[0]
    }
    
    const uploadFile = async () => {
      if (!file.value) {
        alert('请先选择文件')
        return
      }
      uploading.value = true
      result.value = null
      
      const formData = new FormData()
      formData.append('file', file.value)
      
      try {
        const token = localStorage.getItem('token')
        const response = await axios.post('http://localhost:8080/api/products/batch', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${token}`
          }
        })
        result.value = response.data
      } catch (err) {
        result.value = { success: false, message: '上传失败: ' + (err.response?.data?.message || err.message) }
      } finally {
        uploading.value = false
      }
    }
    
    const goBack = () => router.push('/home')
    
    return { file, uploading, result, onFileChange, uploadFile, goBack }
  }
}
</script>

<style scoped>
.batch { min-height: 100vh; background: #f5f5f5; }
.header { display: flex; align-items: center; gap: 10px; padding: 15px; background: #fff; }
.back-btn { padding: 8px 12px; background: #667eea; color: #fff; border: none; border-radius: 4px; }
.header h2 { flex: 1; text-align: center; margin: 0; font-size: 18px; }
.content { padding: 15px; }
.tips { background: #fff; border-radius: 8px; padding: 20px; margin-bottom: 20px; }
.tips h3 { font-size: 16px; margin-bottom: 15px; }
.tips p { font-size: 14px; color: #666; margin-bottom: 8px; }
.example-table { width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 12px; }
.example-table th, .example-table td { border: 1px solid #ddd; padding: 8px; text-align: center; }
.example-table th { background: #f5f5f5; }
.upload-area { background: #fff; border-radius: 8px; padding: 20px; }
.upload-area input { width: 100%; margin-bottom: 15px; }
.upload-btn { width: 100%; padding: 14px; background: #667eea; color: #fff; border: none; border-radius: 8px; font-size: 16px; }
.upload-btn:disabled { background: #ccc; }
.result { margin-top: 20px; padding: 15px; border-radius: 8px; text-align: center; }
.result.success { background: #d4edda; color: #155724; }
.result.error { background: #f8d7da; color: #721c24; }
</style>