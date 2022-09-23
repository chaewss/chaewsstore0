package com.chaewsscode.chaewsstore.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.domain.CustomerOrder;
import com.chaewsscode.chaewsstore.domain.Product;
import com.chaewsscode.chaewsstore.exception.ForbiddenException;
import com.chaewsscode.chaewsstore.exception.ResourceNotFoundException;
import com.chaewsscode.chaewsstore.order.controller.dto.OrderResponseDto;
import com.chaewsscode.chaewsstore.order.service.OrderService;
import com.chaewsscode.chaewsstore.order.service.dto.OrderServiceDto;
import com.chaewsscode.chaewsstore.repository.CustomerOrderRepository;
import com.chaewsscode.chaewsstore.repository.ProductRepository;
import com.chaewsscode.chaewsstore.util.ResponseCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private Pageable pageable;

    @Spy
    @InjectMocks
    private OrderService orderService;

    Account account1 = Account.builder()
        .id(1L)
        .username("hello")
        .build();

    Account account2 = Account.builder()
        .id(2L)
        .username("bye")
        .build();

    Account account3 = Account.builder()
        .id(3L)
        .username("rabbit")
        .build();

    Product product1 = Product.builder()
        .id(1L)
        .name("식탁")
        .price(40000)
        .isSold(false)
        .account(account1)
        .build();

    Product product2 = Product.builder()
        .id(2L)
        .name("의자")
        .price(10000)
        .isSold(false)
        .account(account2)
        .build();

    CustomerOrder order1 = CustomerOrder.builder()
        .product(product1)
        .account(account2)
        .build();

    @Test
    @DisplayName("상품 등록 성공")
    void createOrderSuccess() {
        // given
        CustomerOrder newOrder = CustomerOrder.builder()
            .product(product2)
            .account(account1)
            .build();

        OrderServiceDto serviceDto = OrderServiceDto.builder()
            .productId(2L)
            .build();

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.of(product2));
        given(orderRepository.save(any())).willReturn(newOrder);

        // when
        orderService.createOrder(account1, serviceDto);

        // then
        verify(productRepository, times(1)).findById(any());
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("상품 등록 실패 - 상품 Not Found")
    void createOrderFailProductNotFound() {
        // given
        OrderServiceDto serviceDto = OrderServiceDto.builder()
            .productId(2L)
            .build();

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.empty());

        // when
        ResourceNotFoundException result = assertThrows(ResourceNotFoundException.class,
            () -> orderService.createOrder(account1, serviceDto));

        // then
        verify(productRepository, times(1)).findById(any());
        assertThat(result.getResponseCode()).isEqualTo(ResponseCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("상품 등록 실패 - 상품 주인")
    void createOrderFailProductOwner() {
        // given
        OrderServiceDto serviceDto = OrderServiceDto.builder()
            .productId(2L)
            .build();

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.of(product2));

        // when
        ForbiddenException result = assertThrows(ForbiddenException.class,
            () -> orderService.createOrder(account2, serviceDto));

        // then
        verify(productRepository, times(1)).findById(any());
        assertThat(result.getResponseCode()).isEqualTo(ResponseCode.CREATE_ORDER_FAIL_OWNER);
    }

    @Test
    @DisplayName("내 주문 목록 조회 성공")
    void readProductsSuccess() {
        // given
        Page<CustomerOrder> orders = new PageImpl<>(List.of(order1));

        // mocking
        given(orderRepository.findAllByAccount(any(), any())).willReturn(orders);

        // when
        Page<OrderResponseDto> result = orderService.readOrders(account2, pageable);

        // then
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findAllByAccount(any(), any());
    }

    @Test
    @DisplayName("내 주문 상세 조회 성공")
    void readProductSuccess() {
        // given
        Long orderId = 1L;

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(order1));

        // when
        orderService.readOrder(account2, orderId);

        // then
        verify(orderRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("내 주문 상세 조회 실패 - 주문 Not Found")
    void readProductFailOrderNotFound() {
        // given
        Long orderId = 100L;

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        // when
        ResourceNotFoundException result = assertThrows(ResourceNotFoundException.class,
            () -> orderService.readOrder(account2, orderId));

        // then
        verify(orderRepository, times(1)).findById(any());
        assertThat(result.getResponseCode()).isEqualTo(ResponseCode.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("내 주문 상세 조회 실패 - 주문자 or 판매자 아님")
    void readProductFailOrderBadRequest() {
        // given
        Long orderId = 1L;

        // mocking
        given(orderRepository.findById(any())).willReturn(Optional.of(order1));

        // when
        ForbiddenException result = assertThrows(ForbiddenException.class,
            () -> orderService.readOrder(account3, orderId));

        // then
        verify(orderRepository, times(1)).findById(any());
        assertThat(result.getResponseCode()).isEqualTo(ResponseCode.READ_ORDER_FAIL_NOT_OWNER);
    }

}
