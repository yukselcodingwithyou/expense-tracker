package com.expensetracker.dto.category;

import com.expensetracker.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    
    @NotNull(message = "Category type is required")
    private Category.CategoryType type;
    
    private String icon;
    private String color;

    public CreateCategoryRequest() {}

    public CreateCategoryRequest(String name, Category.CategoryType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category.CategoryType getType() { return type; }
    public void setType(Category.CategoryType type) { this.type = type; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}