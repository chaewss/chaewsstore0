package com.chaewsscode.chaewsstore.product.controller.dto;

import com.chaewsscode.chaewsstore.product.service.dto.ProductServiceDto;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ProductRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private Integer price;

    public ProductServiceDto toServiceDto() {
        return ProductServiceDto.builder()
            .name(getName())
            .price(getPrice())
            .build();
    }
}
