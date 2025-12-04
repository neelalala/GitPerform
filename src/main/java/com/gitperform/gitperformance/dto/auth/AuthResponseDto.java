package com.gitperform.gitperformance.dto.auth;

import com.gitperform.gitperformance.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private UserDto user;
    private String message;
}