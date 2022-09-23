package com.chaewsscode.chaewsstore.product.controller;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.product.controller.dto.ProductRequestDto;
import com.chaewsscode.chaewsstore.product.controller.dto.ProductResponseDto;
import com.chaewsscode.chaewsstore.product.controller.dto.UpdateProductRequestDto;
import com.chaewsscode.chaewsstore.product.service.ProductService;
import com.chaewsscode.chaewsstore.util.LoginAccount;
import com.chaewsscode.chaewsstore.util.ResponseCode;
import com.chaewsscode.chaewsstore.util.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    // 전체 상품 조회
    @GetMapping()
    public ResponseEntity<ResponseData<Page<ProductResponseDto>>> readProducts(Pageable pageable) {
        Page<ProductResponseDto> data = productService.readProducts(pageable);
        return ResponseData.toResponseEntity(ResponseCode.READ_PRODUCTS_SUCCESS, data);
    }

    // 본인 상품 조회
    @GetMapping("my")
    public ResponseEntity<ResponseData<Page<ProductResponseDto>>> readMyProducts(
        @LoginAccount Account account, Pageable pageable) {
        Page<ProductResponseDto> data = productService.readMyProducts(account, pageable);
        return ResponseData.toResponseEntity(ResponseCode.READ_MY_PRODUCTS_SUCCESS, data);
    }

    // 상품 상세 조회
    @GetMapping("{productId}")
    public ResponseEntity<ResponseData<ProductResponseDto>> readProduct(@PathVariable Long productId) {
        ProductResponseDto data = productService.readProduct(productId);
        return ResponseData.toResponseEntity(ResponseCode.READ_PRODUCT_SUCCESS, data);
    }

    // 상품 등록
    @PostMapping()
    public ResponseEntity<ResponseData<ProductResponseDto>> createProduct(
        @LoginAccount Account account, @RequestBody ProductRequestDto request) {
        ProductResponseDto data = productService.createProduct(account, request.toServiceDto());
        return ResponseData.toResponseEntity(ResponseCode.CREATE_PRODUCT_SUCCESS, data);
    }

    // 상품 정보 수정
    @PatchMapping("{productId}")
    public ResponseEntity<ResponseData<ProductResponseDto>> updateProduct(
        @LoginAccount Account account, @PathVariable Long productId,
        @RequestBody UpdateProductRequestDto request) {
        ProductResponseDto data = productService.updateProduct(account, productId, request);
        return ResponseData.toResponseEntity(ResponseCode.UPDATE_PRODUCT_SUCCESS, data);
    }

    // 상품 삭제
    @DeleteMapping("{productId}")
    public ResponseEntity<ResponseData> deleteProduct(@LoginAccount Account account,
        @PathVariable Long productId) {
        productService.deleteProduct(account, productId);
        return ResponseData.toResponseEntity(ResponseCode.DELETE_PRODUCT_SUCCESS);
    }
}
