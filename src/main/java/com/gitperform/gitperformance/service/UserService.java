package com.gitperform.gitperformance.service;

import com.gitperform.gitperformance.dto.auth.UserRegistrationDto;
import com.gitperform.gitperformance.dto.user.EmailChangeDto;
import com.gitperform.gitperformance.dto.user.PasswordChangeDto;
import com.gitperform.gitperformance.model.User;
import com.gitperform.gitperformance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User createUser(UserRegistrationDto dto) {
        var now = LocalDateTime.now();

        var user = new User();

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setDisplayName(dto.getDisplayName());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        var now = LocalDateTime.now();
        user.setUpdatedAt(now);
        return userRepository.save(user);
    }

    public User validateUser(String email, String password) {
        var user = userRepository.findByEmail(email).orElse(null);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean changePassword(Long userId, PasswordChangeDto passwordChangeDto) {
        var user = findById(userId);
        if (user == null) {
            return false;
        }
        if (!user.getPassword().equals(passwordChangeDto.getCurrentPassword())) {
            return false;
        }
        user.setPassword(passwordChangeDto.getNewPassword());
        updateUser(user);

        return true;
    }

    public boolean changeEmail(Long userId, EmailChangeDto emailChangeDto) {
        var user = findById(userId);
        if (user == null) {
            return false;
        }
        if (!user.getPassword().equals(emailChangeDto.getCurrentPassword())) {
            return false;
        }
        if (userExists(emailChangeDto.getNewEmail())) {
            return false;
        }

        user.setEmail(emailChangeDto.getNewEmail());
        updateUser(user);

        return true;
    }
}
