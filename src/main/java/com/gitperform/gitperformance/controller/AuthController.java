package com.gitperform.gitperformance.controller;

import com.gitperform.gitperformance.dto.ApiResponse;
import com.gitperform.gitperformance.dto.auth.AuthResponseDto;
import com.gitperform.gitperformance.dto.auth.UserRegistrationDto;
import com.gitperform.gitperformance.dto.auth.UserLoginDto;
import com.gitperform.gitperformance.dto.user.UserDto;
import com.gitperform.gitperformance.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponseDto> register(@RequestBody UserRegistrationDto registrationDto) {
        if (userService.userExists(registrationDto.getEmail())) {
            return new ApiResponse<>(false, "User already exists", null);
        }

        var user = userService.createUser(registrationDto);

        if (user == null) {
            return new ApiResponse<>(false, "Registration failed", null);
        }

        AuthResponseDto response = new AuthResponseDto(
                new UserDto(user),
                "Registration successful"
        );

        return new ApiResponse<>(true, "Registration successful", response);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponseDto> login(@RequestBody UserLoginDto loginDto) {
        var user = userService.validateUser(loginDto.getEmail(), loginDto.getPassword());

        if (user == null) {
            return new ApiResponse<>(false, "Invalid credentials", null);
        }

        AuthResponseDto response = new AuthResponseDto(
                new UserDto(user),
                "Login successful"
        );

        return new ApiResponse<>(true, "Login successful", response);
    }
}