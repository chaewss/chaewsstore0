package com.chaewsscode.chaewsstore.order.controller.dto;

import com.chaewsscode.chaewsstore.domain.CustomerOrder;
import com.chaewsscode.chaewsstore.product.controller.dto.ProductResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponseDto {

    private Long id;
    private Long accountId;
    private ProductResponseDto product;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy.MM.dd - HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static OrderResponseDto of(CustomerOrder order) {
        return OrderResponseDto.builder()
            .id(order.getId())
            .accountId(order.getAccount().getId())
            .product(ProductResponseDto.of(order.getProduct()))
            .createdAt(order.getCreatedAt())
            .build();
    }
}
