package com.chaewsscode.chaewsstore.order.service;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.domain.CustomerOrder;
import com.chaewsscode.chaewsstore.domain.Product;
import com.chaewsscode.chaewsstore.exception.ForbiddenException;
import com.chaewsscode.chaewsstore.exception.ResourceNotFoundException;
import com.chaewsscode.chaewsstore.order.controller.dto.OrderResponseDto;
import com.chaewsscode.chaewsstore.order.service.dto.OrderServiceDto;
import com.chaewsscode.chaewsstore.repository.CustomerOrderRepository;
import com.chaewsscode.chaewsstore.repository.ProductRepository;
import com.chaewsscode.chaewsstore.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDto createOrder(Account account, OrderServiceDto serviceDto) {
        Product product = productRepository.findById(serviceDto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.PRODUCT_NOT_FOUND));

        // 상품 주인 확인
        if (product.getAccount().equals(account)) {
            throw new ForbiddenException(ResponseCode.CREATE_ORDER_FAIL_OWNER);
        }

        CustomerOrder order = serviceDto.toCustomerOrder(account, product);
        orderRepository.save(order);
        product.setIsSoldTrue();

        return OrderResponseDto.of(order);
    }

}
