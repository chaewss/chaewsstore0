package com.chaewsscode.chaewsstore.product.controller.dto;

import com.chaewsscode.chaewsstore.domain.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy.MM.dd - HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static ProductResponseDto of(Product product) {
        return ProductResponseDto.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .isSold(product.getIsSold())
            .accountId(product.getAccount().getId())
            .createdAt(product.getCreatedAt())
            .build();
    }
}
