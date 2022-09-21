package com.chaewsscode.chaewsstore.auth.service;

import com.chaewsscode.chaewsstore.auth.controller.dto.AccountResponseDto;
import com.chaewsscode.chaewsstore.auth.service.dto.SigninServiceDto;
import com.chaewsscode.chaewsstore.auth.service.dto.SignupServiceDto;
import com.chaewsscode.chaewsstore.config.TokenProvider;
import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.domain.RefreshToken;
import com.chaewsscode.chaewsstore.exception.ResourceNotFoundException;
import com.chaewsscode.chaewsstore.repository.AccountRepository;
import com.chaewsscode.chaewsstore.repository.RefreshTokenRepository;
import com.chaewsscode.chaewsstore.util.AccountUser;
import com.chaewsscode.chaewsstore.util.ResponseCode;
import com.chaewsscode.chaewsstore.util.SecurityUtil;
import com.chaewsscode.chaewsstore.util.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // 로그인한 유저 정보 반환 to @CurrentUser
    @Transactional(readOnly = true)
    public Account getUserInfo() {
        return accountRepository.findByUsername(SecurityUtil.getCurrentUserName())
            .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));
    }

    // 로그인한 유저 정보 조회
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
        return new AccountUser(account);
    }

    // 로그인
    @Transactional(rollbackFor = Exception.class)
    public AccountResponseDto signin(SigninServiceDto serviceDto) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성 (미인증 토큰)
        UsernamePasswordAuthenticationToken authenticationToken = serviceDto.toAuthentication();

        // 2. 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken 저장
        Account account = accountRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));
        RefreshToken refreshToken = RefreshToken.builder()
            .account(account)
            .tokenValue(tokenDto.getRefreshToken())
            .build();

        refreshTokenRepository.save(refreshToken);

        //인증된 Authentication를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. 토큰 포함 현재 유저 정보 반환
        AccountResponseDto accountResponseDto = AccountResponseDto.of(getUserInfo());
        accountResponseDto.setTokenDto(tokenDto);
        return accountResponseDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void signup(SignupServiceDto serviceDto) {
        // 계정 생성
        Account account = serviceDto.toAccount(passwordEncoder);
        accountRepository.save(account);
    }

}
