package com.shoppingmall.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.shoppingmall.entity.Products;
import com.shoppingmall.entity.ProductExcel;
import com.shoppingmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductExcelListener extends AnalysisEventListener<ProductExcel> {

    @Autowired
    private ProductService productService;

    @Override
    public void invoke(ProductExcel excel, AnalysisContext analysisContext) {
        Products product = new Products();

        // 正确字段名（严格匹配你的 Products 实体）
        product.setProductName(excel.getProductName());
        product.setPrice(excel.getPrice());
        product.setStockQuantity(excel.getStock());  // 你用的是 stockQuantity
        product.setImageUrl(excel.getPic());         // 你用的是 imageUrl
        product.setCategory(excel.getCategory());

        // 正确方法名（你 Service 里唯一的新增方法）
        productService.createProduct(product);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}