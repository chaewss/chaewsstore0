package com.chaewsscode.chaewsstore.product.controller.dto;

import com.chaewsscode.chaewsstore.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponseDto {

    private Long id;
    private String name;
    private Integer price;
    private Boolean isSold;
    private Long accountId;

    public static ProductResponseDto of(Product product) {
        return ProductResponseDto.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .isSold(product.getIsSold())
            .accountId(product.getAccount().getId())
            .build();
    }
}
