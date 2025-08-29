package com.expensetracker.service;

import com.expensetracker.domain.User;
import com.expensetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get the current user's family context for data operations.
     * Priority: 1) User's preferred family, 2) First family where user is ADMIN, 3) First family
     */
    public String getCurrentUserFamilyId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getFamilies().isEmpty()) {
            return null;
        }

        // 1. Check if user has a preferred family ID and it's valid
        if (user.getPreferredFamilyId() != null) {
            boolean hasAccessToPreferred = user.getFamilies().stream()
                    .anyMatch(fm -> fm.getFamilyId().equals(user.getPreferredFamilyId()));
            if (hasAccessToPreferred) {
                return user.getPreferredFamilyId();
            }
        }

        // 2. Find first family where user is ADMIN
        Optional<String> adminFamily = user.getFamilies().stream()
                .filter(fm -> fm.getRole() == User.Role.ADMIN)
                .map(User.FamilyMembership::getFamilyId)
                .findFirst();
        
        if (adminFamily.isPresent()) {
            return adminFamily.get();
        }

        // 3. Return first family as fallback
        return user.getFamilies().get(0).getFamilyId();
    }

    /**
     * Set the user's preferred family for future operations
     */
    public void setUserPreferredFamily(String userId, String familyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate that user has access to this family
        boolean hasAccess = user.getFamilies().stream()
                .anyMatch(fm -> fm.getFamilyId().equals(familyId));
        
        if (!hasAccess) {
            throw new IllegalArgumentException("User does not have access to family: " + familyId);
        }
        
        user.setPreferredFamilyId(familyId);
        userRepository.save(user);
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}