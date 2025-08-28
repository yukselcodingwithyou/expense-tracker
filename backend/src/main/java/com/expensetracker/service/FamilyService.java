package com.expensetracker.service;

import com.expensetracker.domain.Family;
import com.expensetracker.domain.User;
import com.expensetracker.dto.family.CreateFamilyRequest;
import com.expensetracker.dto.family.FamilyResponse;
import com.expensetracker.repository.FamilyRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    public FamilyService(FamilyRepository familyRepository, UserRepository userRepository) {
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
    }

    public FamilyResponse createFamily(UserPrincipal user, CreateFamilyRequest request) {
        Family family = new Family(request.getName(), request.getCurrency());
        family = familyRepository.save(family);
        
        // Add user as admin to the family
        User userData = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User.FamilyMembership membership = new User.FamilyMembership(family.getId(), User.Role.ADMIN);
        userData.getFamilies().add(membership);
        userData.setUpdatedAt(Instant.now());
        
        userRepository.save(userData);
        
        return toResponse(family);
    }

    public FamilyResponse getFamily(UserPrincipal user, String familyId) {
        // Verify user has access to this family
        User userData = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean hasAccess = userData.getFamilies().stream()
                .anyMatch(membership -> membership.getFamilyId().equals(familyId));
        
        if (!hasAccess) {
            throw new RuntimeException("Access denied to family");
        }
        
        Family family = familyRepository.findByIdAndDeletedAtIsNull(familyId)
                .orElseThrow(() -> new RuntimeException("Family not found"));
        
        return toResponse(family);
    }

    private FamilyResponse toResponse(Family family) {
        return new FamilyResponse(
                family.getId(),
                family.getName(),
                family.getCurrency(),
                family.getCreatedAt(),
                family.getUpdatedAt()
        );
    }
}