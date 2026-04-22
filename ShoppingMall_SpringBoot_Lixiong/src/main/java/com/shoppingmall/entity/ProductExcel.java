package com.shoppingmall.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductExcel {

    @ExcelProperty(index = 0) // 第1列：商品名
    private String productName;

    @ExcelProperty(index = 1) // 第2列：价格
    private BigDecimal price;

    @ExcelProperty(index = 2) // 第3列：剩余数量
    private Integer stock;

    @ExcelProperty(index = 3) // 第4列：图片URL（可选）
    private String pic;

    @ExcelProperty(index = 4) // 第5列：商品种类
    private String category;
}