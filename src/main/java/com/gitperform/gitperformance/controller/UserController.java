package com.gitperform.gitperformance.controller;

import com.gitperform.gitperformance.dto.ApiResponse;
import com.gitperform.gitperformance.dto.user.EmailChangeDto;
import com.gitperform.gitperformance.dto.user.PasswordChangeDto;
import com.gitperform.gitperformance.dto.user.UserDto;
import com.gitperform.gitperformance.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserDto> getUser(@PathVariable Long userId) {
        var user = userService.findById(userId);
        if (user == null) {
            return new ApiResponse<>(false, "User not found", null);
        }
        return new ApiResponse<>(true, "User retrieved", new UserDto(user));
    }

    @PutMapping("/{userId}/password")
    public ApiResponse<Boolean> changePassword(
            @PathVariable Long userId,
            @RequestBody PasswordChangeDto passwordChangeDto) {

        boolean success = userService.changePassword(userId, passwordChangeDto);
        String message = success ? "Password changed successfully" : "Failed to change password";

        return new ApiResponse<>(success, message, success);
    }

    @PutMapping("/{userId}/email")
    public ApiResponse<Boolean> changeEmail(
            @PathVariable Long userId,
            @RequestBody EmailChangeDto emailChangeDto) {

        boolean success = userService.changeEmail(userId, emailChangeDto);
        String message = success ? "Email changed successfully" : "Failed to change email";

        return new ApiResponse<>(success, message, success);
    }
}