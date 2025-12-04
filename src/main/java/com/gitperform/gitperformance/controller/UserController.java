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

        var user = userService.findById(userId);
        if (user == null) {
            return new ApiResponse<>(false, "User not found", false);
        }

        if (!user.getPassword().equals(passwordChangeDto.getCurrentPassword())) {
            return new ApiResponse<>(false, "Current password is incorrect", false);
        }

        user.setPassword(passwordChangeDto.getNewPassword());
        userService.updateUser(user);

        return new ApiResponse<>(true, "Password changed successfully", true);
    }

    @PutMapping("/{userId}/email")
    public ApiResponse<Boolean> changeEmail(
            @PathVariable Long userId,
            @RequestBody EmailChangeDto emailChangeDto) {

        var user = userService.findById(userId);

        if (user == null) {
            return new ApiResponse<>(false, "User not found", false);
        }

        if (!user.getPassword().equals(emailChangeDto.getCurrentPassword())) {
            return new ApiResponse<>(false, "Current password is incorrect", false);
        }

        if (userService.userExists(emailChangeDto.getNewEmail())) {
            return new ApiResponse<>(false, "Email already exists", false);
        }

        user.setEmail(emailChangeDto.getNewEmail());
        userService.updateUser(user);

        return new ApiResponse<>(true, "Email changed successfully", true);
    }
}