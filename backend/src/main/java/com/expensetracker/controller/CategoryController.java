package com.expensetracker.controller;

import com.expensetracker.domain.Category;
import com.expensetracker.dto.category.CategoryResponse;
import com.expensetracker.dto.category.CreateCategoryRequest;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Category management APIs")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get categories", description = "Get all categories for the current user's family")
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) Category.CategoryType type) {
        List<CategoryResponse> categories = categoryService.getCategories(user, type);
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new category")
    public ResponseEntity<CategoryResponse> createCategory(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing category")
    public ResponseEntity<CategoryResponse> updateCategory(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id,
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse category = categoryService.updateCategory(user, id, request);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category (soft delete)")
    public ResponseEntity<Void> deleteCategory(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id) {
        categoryService.deleteCategory(user, id);
        return ResponseEntity.noContent().build();
    }
}