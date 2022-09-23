package com.chaewsscode.chaewsstore.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chaewsscode.chaewsstore.auth.controller.AuthController;
import com.chaewsscode.chaewsstore.auth.controller.dto.AccountResponseDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.ResetPasswordRequestDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.SigninRequestDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.SignupRequestDto;
import com.chaewsscode.chaewsstore.auth.service.AuthService;
import com.chaewsscode.chaewsstore.config.HttpLogoutSuccessHandler;
import com.chaewsscode.chaewsstore.config.JwtAccessDeniedHandler;
import com.chaewsscode.chaewsstore.config.JwtAuthenticationEntryPoint;
import com.chaewsscode.chaewsstore.config.JwtFilter;
import com.chaewsscode.chaewsstore.config.TokenProvider;
import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.repository.AccountRepository;
import com.chaewsscode.chaewsstore.util.AccountUser;
import com.chaewsscode.chaewsstore.util.TokenDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    HttpLogoutSuccessHandler logoutSuccessHandler;

    @MockBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @MockBean
    JwtFilter jwtFilter;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    AuthService authService;

    @MockBean
    AccountRepository accountRepository;

    @Value("${test.server.http.scheme}")
    String scheme;
    @Value("${test.server.http.host}")
    String host;
    @Value("${test.server.http.port}")
    int port;

    String accessToken = "Bearer (accessToken)";
    String refreshToken = "refreshToken";

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
            Stream.of("ROLE_USER").map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("로그인 테스트")
    void signinTest() throws Exception {
        SigninRequestDto requestDto = SigninRequestDto.builder()
            .username("tiger")
            .password("testpassword")
            .build();

        AccountResponseDto responseDto = AccountResponseDto.builder()
            .id(1L)
            .username("tiger")
            .nickname("testnickname")
            .tokenDto(TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(1L)
                .build())
            .build();

        given(authService.signin(any())).willReturn(responseDto);

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        result.andDo(print()).andExpect(status().isOk())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("username").type(JsonFieldType.STRING)
                        .description("로그인할 사용자의 이메일"),
                    fieldWithPath("password").type(JsonFieldType.STRING)
                        .description("로그인할 사용자의 비밀번호")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                        .description("사용자의 고유한 아이디 값"),
                    fieldWithPath("data.username").type(JsonFieldType.STRING)
                        .description("사용자의 아이디"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                        .description("사용자의 닉네임"),
                    fieldWithPath("data.tokenDto.grantType").type(JsonFieldType.STRING)
                        .description("JWT Token 타입"),
                    fieldWithPath("data.tokenDto.accessToken").type(JsonFieldType.STRING)
                        .description("JWT Access Token 값"),
                    fieldWithPath("data.tokenDto.accessTokenExpiresIn").type(JsonFieldType.NUMBER)
                        .description("JWT Access Token 유효기간"),
                    fieldWithPath("data.tokenDto.refreshToken").type(JsonFieldType.STRING)
                        .description("JWT Refresh Token 값")
                ))
            );
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void signoutTest() throws Exception {
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/auth/signout")
                    .param("refresh-token", refreshToken))
            .andDo(print());

        result.andDo(print()).andExpect(status().isOk())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParameters(
                    parameterWithName("refresh-token").description("refreshToken 값")
                ))
            );
    }

    @Test
    @DisplayName("회원가입 테스트")
    void signupTest() throws Exception {
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .username("tiger")
            .password("abcdefg123!")
            .nickname("가나다라")
            .phone("01012345678")
            .email("abcd@gmail.com")
            .build();

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        result.andDo(print()).andExpect(status().isCreated())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("username").type(JsonFieldType.STRING)
                        .description("회원가입할 사용자의 아이디"),
                    fieldWithPath("password").type(JsonFieldType.STRING)
                        .description("회원가입할 사용자의 비밀번호"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING)
                        .description("회원가입할 사용자의 닉네임"),
                    fieldWithPath("phone").type(JsonFieldType.STRING)
                        .description("회원가입할 사용자의 핸드폰번호"),
                    fieldWithPath("email").type(JsonFieldType.STRING)
                        .description("회원가입할 사용자의 이메일")
                ))
            );
    }

    @Test
    @DisplayName("비밀번호 재설정 테스트")
    void resetPasswordTest() throws Exception {
        ResetPasswordRequestDto requestDto = ResetPasswordRequestDto.builder()
            .username("tiger")
            .password("abcdefg123!")
            .build();

        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/auth/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto))
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print());

        result.andDo(print()).andExpect(status().isOk())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("username").type(JsonFieldType.STRING)
                        .description("비밀번호 재설정할 사용자의 아이디"),
                    fieldWithPath("password").type(JsonFieldType.STRING)
                        .description("비밀번호 재설정할 사용자의 비밀번호")
                ))
            );
    }
}
