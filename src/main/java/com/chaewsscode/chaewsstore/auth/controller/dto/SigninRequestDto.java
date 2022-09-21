package com.chaewsscode.chaewsstore.auth.controller.dto;

import com.chaewsscode.chaewsstore.auth.service.dto.SigninServiceDto;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SigninRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public SigninServiceDto toServiceDto() {
        return SigninServiceDto.builder()
            .username(getUsername())
            .password(getPassword())
            .build();
    }
}
