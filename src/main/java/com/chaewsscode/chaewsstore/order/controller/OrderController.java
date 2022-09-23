package com.chaewsscode.chaewsstore.order.controller;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.order.controller.dto.OrderRequestDto;
import com.chaewsscode.chaewsstore.order.controller.dto.OrderResponseDto;
import com.chaewsscode.chaewsstore.order.service.OrderService;
import com.chaewsscode.chaewsstore.util.LoginAccount;
import com.chaewsscode.chaewsstore.util.ResponseCode;
import com.chaewsscode.chaewsstore.util.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    // 상품 주문
    @PostMapping()
    public ResponseEntity<ResponseData<OrderResponseDto>> createOrder(
        @LoginAccount Account account, @RequestBody OrderRequestDto request) {
        OrderResponseDto data = orderService.createOrder(account, request.toServiceDto());
        return ResponseData.toResponseEntity(ResponseCode.CREATE_ORDER_SUCCESS, data);
    }

    // 내 주문 목록 조회
    @GetMapping()
    public ResponseEntity<ResponseData<Page<OrderResponseDto>>> readOrders(
        @LoginAccount Account account, Pageable pageable) {
        Page<OrderResponseDto> data = orderService.readOrders(account, pageable);
        return ResponseData.toResponseEntity(ResponseCode.READ_ORDERS_SUCCESS, data);
    }

    // 내 주문 상세 조회
    @GetMapping("{orderId}")
    public ResponseEntity<ResponseData<OrderResponseDto>> readOrder(@LoginAccount Account account,
        @PathVariable Long orderId) {
        OrderResponseDto data = orderService.readOrder(account, orderId);
        return ResponseData.toResponseEntity(ResponseCode.READ_ORDER_SUCCESS, data);
    }

}
