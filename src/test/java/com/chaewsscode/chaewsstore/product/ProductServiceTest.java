package com.chaewsscode.chaewsstore.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.domain.Product;
import com.chaewsscode.chaewsstore.exception.BadRequestException;
import com.chaewsscode.chaewsstore.exception.ForbiddenException;
import com.chaewsscode.chaewsstore.exception.ResourceNotFoundException;
import com.chaewsscode.chaewsstore.product.controller.dto.ProductResponseDto;
import com.chaewsscode.chaewsstore.product.controller.dto.UpdateProductRequestDto;
import com.chaewsscode.chaewsstore.product.service.ProductService;
import com.chaewsscode.chaewsstore.product.service.dto.ProductServiceDto;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private Pageable pageable;

    @Spy
    @InjectMocks
    private ProductService productService;

    Account account1 = Account.builder()
        .id(1L)
        .username("hello")
        .build();

    Account account2 = Account.builder()
        .id(2L)
        .username("bye")
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

    @Test
    @DisplayName("전체 상품 조회 성공")
    void readProductsSuccess() {
        // given
        Page<Product> products = new PageImpl<>(List.of(product1, product2));

        // mocking
        given(productRepository.findAllByIsSoldIsFalse(any())).willReturn(products);

        // when
        Page<ProductResponseDto> result = productService.readProducts(pageable);

        // then
        assertEquals(2, result.getTotalElements());
        verify(productRepository, times(1)).findAllByIsSoldIsFalse(any());
    }

    @Test
    @DisplayName("본인 상품 조회 성공")
    void readMyProductsSuccess() {
        // given
        Page<Product> products = new PageImpl<>(List.of(product1));

        // mocking
        given(productRepository.findAllByAccount(any(), any())).willReturn(products);

        // when
        Page<ProductResponseDto> result = productService.readMyProducts(account1, pageable);

        // then
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAllByAccount(any(), any());
    }

    @Test
    @DisplayName("상품 등록 성공")
    void createProductSuccess() {
        // given
        Product newProduct = Product.builder()
            .id(3L)
            .name("식탁보")
            .price(5000)
            .isSold(false)
            .account(account1)
            .build();

        ProductServiceDto serviceDto = ProductServiceDto.builder()
            .name("식탁보")
            .price(5000)
            .build();

        // mocking
        given(productRepository.save(any())).willReturn(newProduct);

        // when
        productService.createProduct(account1, serviceDto);

        // then
        verify(productRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("상품 정보 수정 성공")
    void updateProductSuccess() {
        // given
        Long productId = 3L;

        Product product = Product.builder()
            .id(3L)
            .name("식탁보")
            .price(5000)
            .isSold(true)
            .account(account1)
            .build();

        UpdateProductRequestDto requestDto = UpdateProductRequestDto.builder()
            .price(5000)
            .isSold(true)
            .build();

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.of(product));
        given(orderRepository.existsByProduct(any())).willReturn(false);

        // when
        productService.updateProduct(account1, productId, requestDto);

        // then
        verify(productRepository, times(1)).findById(any());
        verify(orderRepository, times(1)).existsByProduct(any());
    }

    @Test
    @DisplayName("상품 정보 수정 실패 - 상품 Not Found")
    void updateProductFailProductNotFound() {
        // given
        Long productId = 100L;

        UpdateProductRequestDto requestDto = UpdateProductRequestDto.builder()
            .price(5000)
            .isSold(true)
            .build();

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.empty());

        // when
        ResourceNotFoundException result = assertThrows(ResourceNotFoundException.class,
            () -> productService.updateProduct(account1, productId, requestDto));

        // then
        verify(productRepository, times(1)).findById(any());
        assertThat(result.getResponseCode()).isEqualTo(ResponseCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("상품 정보 수정 실패 - 상품 Not Owner")
    void updateProductFailProductNotOwner() {
        // given
        Long productId = 2L;

        UpdateProductRequestDto requestDto = UpdateProductRequestDto.builder()
            .price(5000)
            .isSold(true)
            .build();

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.of(product2));

        // when
        ForbiddenException result = assertThrows(ForbiddenException.class,
            () -> productService.updateProduct(account1, productId, requestDto));

        // then
        verify(productRepository, times(1)).findById(any());
        assertThat(result.getResponseCode()).isEqualTo(ResponseCode.UPDATE_PRODUCT_FAIL_NOT_OWNER);
    }

    @Test
    @DisplayName("상품 정보 수정 실패 - 상품 Sold out")
    void updateProductFailProductSoldOut() {
        // given
        Long productId = 1L;

        UpdateProductRequestDto requestDto = UpdateProductRequestDto.builder()
            .price(5000)
            .isSold(true)
            .build();

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.of(product1));
        given(orderRepository.existsByProduct(any())).willReturn(true);

        // when
        BadRequestException result = assertThrows(BadRequestException.class,
            () -> productService.updateProduct(account1, productId, requestDto));

        // then
        verify(productRepository, times(1)).findById(any());
        verify(orderRepository, times(1)).existsByProduct(any());
        assertThat(result.getResponseCode()).isEqualTo(
            ResponseCode.UPDATE_PRODUCT_FAIL_ALREADY_SOLDOUT);
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteProductSuccess() {
        // given
        Long productId = 1L;

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.of(product1));
        given(orderRepository.existsByProduct(any())).willReturn(false);
        doNothing().when(productRepository).delete(product1);

        // when
        productService.deleteProduct(account1, productId);

        // then
        verify(productRepository, times(1)).findById(any());
        verify(orderRepository, times(1)).existsByProduct(any());
        verify(productRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("상품 삭제 실패 - 상품 Not Found")
    void deleteProductFailProductNotFound() {
        // given
        Long productId = 100L;

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.empty());

        // when
        ResourceNotFoundException result = assertThrows(ResourceNotFoundException.class,
            () -> productService.deleteProduct(account1, productId));

        // then
        verify(productRepository, times(1)).findById(any());
        assertThat(result.getResponseCode()).isEqualTo(ResponseCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("상품 삭제 실패 - 상품 Not Owner")
    void deleteProductFailProductNotOwner() {
        // given
        Long productId = 2L;

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.of(product2));

        // when
        ForbiddenException result = assertThrows(ForbiddenException.class,
            () -> productService.deleteProduct(account1, productId));

        // then
        verify(productRepository, times(1)).findById(any());
        assertThat(result.getResponseCode()).isEqualTo(ResponseCode.DELETE_PRODUCT_FAIL_NOT_OWNER);
    }

    @Test
    @DisplayName("상품 삭제 실패 - 상품 Sold out")
    void deleteProductFailProductSoldOut() {
        // given
        Long productId = 1L;

        // mocking
        given(productRepository.findById(any())).willReturn(Optional.of(product1));
        given(orderRepository.existsByProduct(any())).willReturn(true);

        // when
        BadRequestException result = assertThrows(BadRequestException.class,
            () -> productService.deleteProduct(account1, productId));

        // then
        verify(productRepository, times(1)).findById(any());
        verify(orderRepository, times(1)).existsByProduct(any());
        assertThat(result.getResponseCode()).isEqualTo(
            ResponseCode.DELETE_PRODUCT_FAIL_ALREADY_SOLDOUT);
    }
}
