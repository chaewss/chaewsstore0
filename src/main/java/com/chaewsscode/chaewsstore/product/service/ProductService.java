package com.chaewsscode.chaewsstore.product.service;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.domain.CustomerOrder;
import com.chaewsscode.chaewsstore.domain.Product;
import com.chaewsscode.chaewsstore.exception.BadRequestException;
import com.chaewsscode.chaewsstore.exception.ForbiddenException;
import com.chaewsscode.chaewsstore.exception.ResourceNotFoundException;
import com.chaewsscode.chaewsstore.product.controller.dto.ProductResponseDto;
import com.chaewsscode.chaewsstore.product.controller.dto.UpdateProductRequestDto;
import com.chaewsscode.chaewsstore.product.service.dto.ProductServiceDto;
import com.chaewsscode.chaewsstore.repository.CustomerOrderRepository;
import com.chaewsscode.chaewsstore.repository.ProductRepository;
import com.chaewsscode.chaewsstore.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> readProducts(Pageable pageable) {
        return productRepository.findAllByIsSoldIsFalse(pageable).map(ProductResponseDto::of);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> readMyProducts(Account account, Pageable pageable) {
        return productRepository.findAllByAccount(account, pageable).map(ProductResponseDto::of);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto readProduct(Long productId) {
        return productRepository.findById(productId)
            .map(ProductResponseDto::of)
            .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.PRODUCT_NOT_FOUND));
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductResponseDto createProduct(Account account, ProductServiceDto serviceDto) {
        Product product = serviceDto.toProduct(account);
        productRepository.save(product);

        return ProductResponseDto.of(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductResponseDto updateProduct(Account account, Long productId,
        UpdateProductRequestDto request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.PRODUCT_NOT_FOUND));

        // 상품 주인 확인
        if (!product.getAccount().equals(account)) {
            throw new ForbiddenException(ResponseCode.UPDATE_PRODUCT_FAIL_NOT_OWNER);
        }
        // 해당 상품의 주문 내역이 있는지 확인
        if (orderRepository.existsByProduct(product)) {
            throw new BadRequestException(ResponseCode.UPDATE_PRODUCT_FAIL_ALREADY_SOLDOUT);
        }
        product.setInfo(request.getPrice(), request.getIsSold());

        return ProductResponseDto.of(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Account account, Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.PRODUCT_NOT_FOUND));

        // 상품 주인 확인
        if (!product.getAccount().equals(account)) {
            throw new ForbiddenException(ResponseCode.DELETE_PRODUCT_FAIL_NOT_OWNER);
        }
        // 해당 상품의 주문 내역이 있는지 확인
        if (orderRepository.existsByProduct(product)) {
            throw new BadRequestException(ResponseCode.DELETE_PRODUCT_FAIL_ALREADY_SOLDOUT);
        }
        productRepository.delete(product);
    }
}
