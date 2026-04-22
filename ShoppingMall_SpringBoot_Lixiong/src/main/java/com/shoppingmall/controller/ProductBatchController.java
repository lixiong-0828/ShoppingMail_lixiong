package com.shoppingmall.controller;

import com.alibaba.excel.EasyExcel;
import com.shoppingmall.entity.ProductExcel;
import com.shoppingmall.listener.ProductExcelListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductBatchController {

    @Autowired
    private ProductExcelListener listener;

    @PostMapping("/batchImport")
    public Map<String, Object> batchImport(@RequestParam("file") MultipartFile file) throws Exception {
        // 跳过第一行标题，从第二行开始读取
        EasyExcel.read(file.getInputStream(), ProductExcel.class, listener)
                .sheet()
                .headRowNumber(1)
                .doRead();

        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg", "批量导入成功");
        return map;
    }
}