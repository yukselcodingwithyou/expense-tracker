package com.expensetracker.service;

import com.expensetracker.domain.Category;
import com.expensetracker.dto.category.CategoryResponse;
import com.expensetracker.dto.category.CreateCategoryRequest;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public List<CategoryResponse> getCategories(UserPrincipal user, Category.CategoryType type) {
        String familyId = userService.getCurrentUserFamilyId(user.getId());
        
        List<Category> categories;
        if (type != null) {
            categories = categoryRepository.findByFamilyIdAndTypeAndDeletedAtIsNull(familyId, type);
        } else {
            categories = categoryRepository.findByFamilyIdAndDeletedAtIsNull(familyId);
        }
        
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse createCategory(UserPrincipal user, CreateCategoryRequest request) {
        String familyId = userService.getCurrentUserFamilyId(user.getId());
        
        if (categoryRepository.existsByFamilyIdAndNameAndTypeAndDeletedAtIsNull(familyId, request.getName(), request.getType())) {
            throw new RuntimeException("Category with this name and type already exists");
        }
        
        Category category = new Category(familyId, request.getName(), request.getType());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        
        category = categoryRepository.save(category);
        return toResponse(category);
    }

    public CategoryResponse updateCategory(UserPrincipal user, String categoryId, CreateCategoryRequest request) {
        String familyId = userService.getCurrentUserFamilyId(user.getId());
        
        Category category = categoryRepository.findByIdAndFamilyIdAndDeletedAtIsNull(categoryId, familyId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setUpdatedAt(Instant.now());
        
        category = categoryRepository.save(category);
        return toResponse(category);
    }

    public void deleteCategory(UserPrincipal user, String categoryId) {
        String familyId = userService.getCurrentUserFamilyId(user.getId());
        
        Category category = categoryRepository.findByIdAndFamilyIdAndDeletedAtIsNull(categoryId, familyId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setDeletedAt(Instant.now());
        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getIcon(),
                category.getColor(),
                category.isArchived(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}