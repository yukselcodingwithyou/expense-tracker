package com.expensetracker.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Document(collection = "categories")
@CompoundIndex(def = "{'familyId': 1, 'name': 1, 'type': 1}", unique = true)
public class Category {
    @Id
    private String id;
    
    @NotNull
    private String familyId;
    
    @NotBlank
    private String name;
    
    @NotNull
    private CategoryType type;
    
    private String icon;
    private String color;
    private boolean isArchived = false;
    
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public enum CategoryType {
        EXPENSE, INCOME
    }

    // Constructors
    public Category() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Category(String familyId, String name, CategoryType type) {
        this();
        this.familyId = familyId;
        this.name = name;
        this.type = type;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFamilyId() { return familyId; }
    public void setFamilyId(String familyId) { this.familyId = familyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}