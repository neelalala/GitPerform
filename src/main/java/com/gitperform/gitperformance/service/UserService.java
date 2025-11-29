package com.gitperform.gitperformance.service;

import com.gitperform.gitperformance.model.User;
import com.gitperform.gitperformance.repository.UserRepository;
import org.springframework.stereotype.Service;

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
        return userRepository.save(user);
    }

    public User validateUser(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
