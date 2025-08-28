package com.expensetracker.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    @Email
    @Indexed(unique = true)
    private String email;
    
    @NotBlank
    private String passwordHash;
    
    private Set<String> providers; // OAuth providers like "google"
    
    private List<FamilyMembership> families = new ArrayList<>();
    
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    // Constructors
    public User() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public User(String email, String passwordHash) {
        this();
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Inner class for family membership
    public static class FamilyMembership {
        private String familyId;
        private Role role;

        public FamilyMembership() {}

        public FamilyMembership(String familyId, Role role) {
            this.familyId = familyId;
            this.role = role;
        }

        public String getFamilyId() { return familyId; }
        public void setFamilyId(String familyId) { this.familyId = familyId; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
    }

    public enum Role {
        ADMIN, MEMBER
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Set<String> getProviders() { return providers; }
    public void setProviders(Set<String> providers) { this.providers = providers; }

    public List<FamilyMembership> getFamilies() { return families; }
    public void setFamilies(List<FamilyMembership> families) { this.families = families; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}