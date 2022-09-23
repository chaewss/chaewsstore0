package com.chaewsscode.chaewsstore.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chaewsscode.chaewsstore.config.JwtAccessDeniedHandler;
import com.chaewsscode.chaewsstore.config.JwtAuthenticationEntryPoint;
import com.chaewsscode.chaewsstore.config.JwtFilter;
import com.chaewsscode.chaewsstore.config.TokenProvider;
import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.order.controller.OrderController;
import com.chaewsscode.chaewsstore.order.controller.dto.OrderRequestDto;
import com.chaewsscode.chaewsstore.order.controller.dto.OrderResponseDto;
import com.chaewsscode.chaewsstore.order.service.OrderService;
import com.chaewsscode.chaewsstore.product.controller.dto.ProductResponseDto;
import com.chaewsscode.chaewsstore.repository.CustomerOrderRepository;
import com.chaewsscode.chaewsstore.util.AccountUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @MockBean
    JwtFilter jwtFilter;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    OrderService orderService;

    @MockBean
    CustomerOrderRepository orderRepository;

    @Value("${test.server.http.scheme}")
    String scheme;
    @Value("${test.server.http.host}")
    String host;
    @Value("${test.server.http.port}")
    int port;

    String accessToken = "Bearer (accessToken)";

    @BeforeEach
    public void setup(WebApplicationContext ctx,
        RestDocumentationContextProvider restDocumentationContextProvider) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
            .apply(documentationConfiguration(restDocumentationContextProvider))
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .alwaysDo(print())
            .build();

        Account account = Account.builder()
            .id(1L)
            .username("test")
            .password("1234")
            .nickname("testNickname")
            .build();

        UserDetails accountUser = new AccountUser(account);

        Authentication authentication = new UsernamePasswordAuthenticationToken(accountUser, null,
            Stream.of("ROLE_USER")
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

//        given(tokenProvider.getAuthentication(anyString())).willReturn(authentication);
    }

    @Test
    @DisplayName("상품 등록 테스트")
    void createProductTest() throws Exception {
        OrderRequestDto requestDto = OrderRequestDto.builder()
            .productId(1L)
            .build();

        ProductResponseDto productResponseDto = ProductResponseDto.builder()
            .id(1L)
            .name("자전거")
            .price(70000)
            .isSold(false)
            .accountId(2L)
            .build();

        OrderResponseDto orderResponseDto = OrderResponseDto.builder()
            .id(1L)
            .accountId(1L)
            .product(productResponseDto)
            .createdAt(LocalDateTime.now())
            .build();

        given(orderService.createOrder(any(), any())).willReturn(orderResponseDto);

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/orders")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        result.andDo(print()).andExpect(status().isCreated())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type의 AccessToken 값")
                ),
                requestFields(
                    fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 고유번호")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("주문 고유번호"),
                    fieldWithPath("data.accountId").type(JsonFieldType.NUMBER)
                        .description("주문자 고유번호"),
                    fieldWithPath("data.product.id").type(JsonFieldType.NUMBER).description("상품 고유번호"),
                    fieldWithPath("data.product.name").type(JsonFieldType.STRING)
                        .description("상품명"),
                    fieldWithPath("data.product.price").type(JsonFieldType.NUMBER)
                        .description("상품 가격"),
                    fieldWithPath("data.product.isSold").type(JsonFieldType.BOOLEAN)
                        .description("상품 품절 여부"),
                    fieldWithPath("data.product.accountId").type(JsonFieldType.NUMBER)
                        .description("상품 주인 고유번호")
                ))
            );
    }

    @Test
    @DisplayName("내 주문 목록 조회 테스트")
    void readOrdersTest() throws Exception {
        ProductResponseDto productResponseDto1 = ProductResponseDto.builder()
            .id(1L)
            .name("자전거")
            .price(70000)
            .isSold(false)
            .accountId(2L)
            .build();

        ProductResponseDto productResponseDto2 = ProductResponseDto.builder()
            .id(3L)
            .name("자전거 바구니")
            .price(2000)
            .isSold(false)
            .accountId(2L)
            .build();

        OrderResponseDto orderResponseDto1 = OrderResponseDto.builder()
            .id(1L)
            .accountId(1L)
            .product(productResponseDto1)
            .createdAt(LocalDateTime.now())
            .build();

        OrderResponseDto orderResponseDto2 = OrderResponseDto.builder()
            .id(2L)
            .accountId(1L)
            .product(productResponseDto2)
            .createdAt(LocalDateTime.now())
            .build();

        List<OrderResponseDto> responseDtos = List.of(orderResponseDto1, orderResponseDto2);
        Page<OrderResponseDto> responseDto = new PageImpl<>(responseDtos);
        given(orderService.readOrders(any(), any())).willReturn(responseDto);

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/orders")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .queryParam("page", "0")
                    .queryParam("sort", "createdAt")
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        result.andDo(print()).andExpect(status().isOk())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type의 AccessToken 값")
                ),
                requestParameters(
                    parameterWithName("page").description("페이지 번호"),
                    parameterWithName("sort").description("정렬 기준")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.content.[].id").type(JsonFieldType.NUMBER).description("주문 고유번호"),
                    fieldWithPath("data.content.[].accountId").type(JsonFieldType.NUMBER)
                        .description("주문자 고유번호"),
                    fieldWithPath("data.content.[].product.id").type(JsonFieldType.NUMBER).description("상품 고유번호"),
                    fieldWithPath("data.content.[].product.name").type(JsonFieldType.STRING)
                        .description("상품명"),
                    fieldWithPath("data.content.[].product.price").type(JsonFieldType.NUMBER)
                        .description("상품 가격"),
                    fieldWithPath("data.content.[].product.isSold").type(JsonFieldType.BOOLEAN)
                        .description("상품 품절 여부"),
                    fieldWithPath("data.content.[].product.accountId").type(JsonFieldType.NUMBER)
                        .description("상품 주인 고유번호")
                ))
            );
    }

    @Test
    @DisplayName("내 주문 상세 조회 테스트")
    void readOrderTest() throws Exception {
        ProductResponseDto productResponseDto = ProductResponseDto.builder()
            .id(1L)
            .name("자전거")
            .price(70000)
            .isSold(false)
            .accountId(2L)
            .build();

        OrderResponseDto orderResponseDto = OrderResponseDto.builder()
            .id(1L)
            .accountId(1L)
            .product(productResponseDto)
            .createdAt(LocalDateTime.now())
            .build();

        given(orderService.readOrder(any(), any())).willReturn(orderResponseDto);

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/orders/{orderId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        result.andDo(print()).andExpect(status().isOk())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type의 AccessToken 값")
                ),
                pathParameters(
                    parameterWithName("orderId").description("주문 고유번호")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("주문 고유번호"),
                    fieldWithPath("data.accountId").type(JsonFieldType.NUMBER)
                        .description("주문자 고유번호"),
                    fieldWithPath("data.product.id").type(JsonFieldType.NUMBER).description("상품 고유번호"),
                    fieldWithPath("data.product.name").type(JsonFieldType.STRING)
                        .description("상품명"),
                    fieldWithPath("data.product.price").type(JsonFieldType.NUMBER)
                        .description("상품 가격"),
                    fieldWithPath("data.product.isSold").type(JsonFieldType.BOOLEAN)
                        .description("상품 품절 여부"),
                    fieldWithPath("data.product.accountId").type(JsonFieldType.NUMBER)
                        .description("상품 주인 고유번호")
                ))
            );
    }

}
