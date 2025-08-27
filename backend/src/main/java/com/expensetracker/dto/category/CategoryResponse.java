package com.expensetracker.dto.category;

import com.expensetracker.domain.Category;

import java.time.Instant;

public class CategoryResponse {
    private String id;
    private String name;
    private Category.CategoryType type;
    private String icon;
    private String color;
    private boolean archived;
    private Instant createdAt;
    private Instant updatedAt;

    public CategoryResponse() {}

    public CategoryResponse(String id, String name, Category.CategoryType type, String icon, String color, boolean archived, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.color = color;
        this.archived = archived;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category.CategoryType getType() { return type; }
    public void setType(Category.CategoryType type) { this.type = type; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}