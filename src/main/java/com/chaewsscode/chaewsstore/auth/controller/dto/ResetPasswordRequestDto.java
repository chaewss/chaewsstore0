package com.chaewsscode.chaewsstore.auth.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$`~!@$!%#^?&\\(\\)-_=+])[A-Za-z\\d$`~!@$!%#^?&\\(\\)-_=+]{10,25}$")
    private String password;

}
