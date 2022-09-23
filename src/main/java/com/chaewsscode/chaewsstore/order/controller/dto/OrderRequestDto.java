package com.chaewsscode.chaewsstore.order.controller.dto;

import com.chaewsscode.chaewsstore.order.service.dto.OrderServiceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    private Long productId;

    public OrderServiceDto toServiceDto() {
        return OrderServiceDto.builder()
            .productId(productId)
            .build();
    }
}
