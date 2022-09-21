package com.chaewsscode.chaewsstore.auth.controller;

import com.chaewsscode.chaewsstore.auth.controller.dto.AccountResponseDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.SigninRequestDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.SignupRequestDto;
import com.chaewsscode.chaewsstore.auth.service.AuthService;
import com.chaewsscode.chaewsstore.util.ResponseCode;
import com.chaewsscode.chaewsstore.util.ResponseData;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping("signin")
    public ResponseEntity<ResponseData<AccountResponseDto>> signin(
        @RequestBody @Valid SigninRequestDto request) {
        AccountResponseDto data = authService.signin(request.toServiceDto());
        return ResponseData.toResponseEntity(ResponseCode.SIGNIN_SUCCESS, data);
    }

    // 회원가입
    @PostMapping("signup")
    public ResponseEntity<ResponseData> signup(@Valid @RequestBody SignupRequestDto request) {
        authService.signup(request.toServiceDto());
        return ResponseData.toResponseEntity(ResponseCode.SIGNUP_SUCCESS);
    }


}
