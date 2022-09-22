package com.chaewsscode.chaewsstore.product.service;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.domain.Product;
import com.chaewsscode.chaewsstore.product.controller.dto.ProductRequestDto;
import com.chaewsscode.chaewsstore.product.controller.dto.ProductResponseDto;
import com.chaewsscode.chaewsstore.product.service.dto.ProductServiceDto;
import com.chaewsscode.chaewsstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(Account account, ProductServiceDto serviceDto) {
        Product product = serviceDto.toProduct(account);
        productRepository.save(product);

        return ProductResponseDto.of(product);
    }

    public Page<ProductResponseDto> readMyProducts(Account account, Pageable pageable) {
        return productRepository.findAllByAccount(account, pageable).map(ProductResponseDto::of);
    }

}
