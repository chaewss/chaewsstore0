package com.chaewsscode.chaewsstore.auth.controller.dto;

import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.util.TokenDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountResponseDto {

    private final Long id;
    private final String username;
    private final String nickname;
    private TokenDto tokenDto;

    public static AccountResponseDto of(Account account) {
        return AccountResponseDto.builder()
            .id(account.getId())
            .username(account.getUsername())
            .nickname(account.getNickname())
            .build();
    }

}
