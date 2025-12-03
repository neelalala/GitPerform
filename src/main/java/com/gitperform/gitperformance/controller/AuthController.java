package com.gitperform.gitperformance.controller;

import com.gitperform.gitperformance.dto.auth.*;
import com.gitperform.gitperformance.dto.ApiResponse;
import com.gitperform.gitperformance.model.User;
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
    public ApiResponse<User> register(@RequestBody UserRegistrationDto registrationDto) {
        if (userService.userExists(registrationDto.getEmail())) {
            return new ApiResponse<>(false, "User already exists", null);
        }

        var user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(registrationDto.getPassword()); // encrypt this
        user.setDisplayName(registrationDto.getDisplayName());

        var savedUser = userService.createUser(user);
        return new ApiResponse<>(true, "Registration successful", savedUser);
    }

    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody UserLoginDto loginDto) {
        var user = userService.validateUser(loginDto.getEmail(), loginDto.getPassword());
        if (user != null) {
            return new ApiResponse<>(true, "Login successful", user);
        }
        return new ApiResponse<>(false, "Invalid credentials", null);
    }
}
