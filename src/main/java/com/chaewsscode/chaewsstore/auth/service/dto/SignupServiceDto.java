package com.chaewsscode.chaewsstore.auth.service.dto;

import com.chaewsscode.chaewsstore.domain.Account;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
public class SignupServiceDto {

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    public Account toAccount(PasswordEncoder passwordEncoder) {
        return Account.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .nickname(nickname)
            .phone(phone)
            .email(email)
            .build();
    }
}
