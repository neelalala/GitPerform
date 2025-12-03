package com.gitperform.gitperformance.dto.auth;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;
    private String displayName;
}
