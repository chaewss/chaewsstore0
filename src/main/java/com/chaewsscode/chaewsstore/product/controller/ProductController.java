package com.chaewsscode.chaewsstore.product.controller;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.product.controller.dto.ProductRequestDto;
import com.chaewsscode.chaewsstore.product.controller.dto.ProductResponseDto;
import com.chaewsscode.chaewsstore.product.service.ProductService;
import com.chaewsscode.chaewsstore.util.LoginAccount;
import com.chaewsscode.chaewsstore.util.ResponseCode;
import com.chaewsscode.chaewsstore.util.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("product")
public class ProductController {

    private final ProductService productService;

    // 상품 등록
    @PostMapping()
    public ResponseEntity<ResponseData<ProductResponseDto>> createProduct(
        @LoginAccount Account account, @RequestBody ProductRequestDto request) {
        ProductResponseDto data = productService.createProduct(account, request.toServiceDto());
        return ResponseData.toResponseEntity(ResponseCode.CREATE_PRODUCT_SUCCESS, data);
    }


}
