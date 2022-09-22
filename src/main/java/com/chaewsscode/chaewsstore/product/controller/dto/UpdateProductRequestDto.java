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
public class UpdateProductRequestDto {

    @NotBlank
    private Integer price;

    private Boolean isSold;

}
