package com.gitperform.gitperformance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;
    private String displayName;

    // constructors, getters, setters
}
