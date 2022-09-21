package com.chaewsscode.chaewsstore.auth.controller.dto;

import com.chaewsscode.chaewsstore.auth.service.dto.SignupServiceDto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$`~!@$!%#^?&\\(\\)-_=+])[A-Za-z\\d$`~!@$!%#^?&\\(\\)-_=+]{10,25}$")
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    private String phone;

    @NotBlank
    private String email;

    public SignupServiceDto toServiceDto() {
        return SignupServiceDto.builder()
            .username(getUsername())
            .password(getPassword())
            .nickname(getNickname())
            .phone(getPhone())
            .email(getEmail())
            .build();
    }
}
