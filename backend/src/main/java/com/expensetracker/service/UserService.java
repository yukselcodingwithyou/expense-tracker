package com.expensetracker.service;

import com.expensetracker.domain.User;
import com.expensetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getCurrentUserFamilyId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // For now, return the first family ID if available
        // TODO: Implement proper family context/selection
        return user.getFamilies().isEmpty() ? null : user.getFamilies().get(0).getFamilyId();
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}