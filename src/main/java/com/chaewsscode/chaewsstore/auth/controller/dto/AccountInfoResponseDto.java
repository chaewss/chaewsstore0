package com.chaewsscode.chaewsstore.auth.controller.dto;

import com.chaewsscode.chaewsstore.domain.Account;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountInfoResponseDto {

    private String username;
    private String nickname;
    private String phone;
    private String email;

    public static AccountInfoResponseDto of(Account account) {
        return AccountInfoResponseDto.builder()
            .username(account.getUsername())
            .nickname(account.getNickname())
            .phone(account.getPhone())
            .email(account.getEmail())
            .build();
    }
}
