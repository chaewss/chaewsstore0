package com.chaewsscode.chaewsstore.order.service.dto;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.domain.CustomerOrder;
import com.chaewsscode.chaewsstore.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderServiceDto {

    private Long productId;

    public CustomerOrder toCustomerOrder(Account account, Product product) {
        return CustomerOrder.builder()
            .account(account)
            .product(product)
            .build();
    }
}
