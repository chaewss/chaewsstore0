package com.chaewsscode.chaewsstore.product.service.dto;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductServiceDto {

    private String name;

    private Integer price;

    public Product toProduct(Account account) {
        return Product.builder()
            .name(name)
            .price(price)
            .isSold(false)
            .account(account)
            .build();
    }
}
