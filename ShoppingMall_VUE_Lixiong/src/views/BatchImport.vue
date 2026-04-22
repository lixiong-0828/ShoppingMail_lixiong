<template>
  <div class="batch-import">
    <h2>商品批量登录</h2>

    <div style="margin: 20px 0">
      <input type="file" accept=".xlsx" id="excelFile" />
    </div>

    <button 
      @click="uploadExcel" 
      style="padding: 8px 16px; background: #409eff; color: white; border: none; border-radius: 4px; cursor: pointer;">
      开始上传
    </button>
  </div>
</template>

<script>
export default {
  name: "BatchImport",
  methods: {
    async uploadExcel() {
      const fileInput = document.getElementById('excelFile');
      const file = fileInput.files[0];

      if (!file) {
        alert("请选择Excel文件");
        return;
      }

      const formData = new FormData();
      formData.append("file", file);

      try {
        const res = await fetch("http://localhost:8080/api/product/batchImport", {
          method: "POST",
          body: formData
        });

        const result = await res.json();
        alert(result.msg);
      } catch (e) {
        alert("上传失败，请确认后端已启动");
        console.error(e);
      }
    }
  }
};
</script>

<style scoped>
.batch-import {
  padding: 30px;
}
</style>