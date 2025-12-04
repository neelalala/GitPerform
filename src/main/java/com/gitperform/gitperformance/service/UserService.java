package com.gitperform.gitperformance.service;

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

    public User createUser(User user) {
        var now = LocalDateTime.now();
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
}
