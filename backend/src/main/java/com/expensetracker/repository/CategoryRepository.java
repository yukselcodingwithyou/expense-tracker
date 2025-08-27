package com.expensetracker.repository;

import com.expensetracker.domain.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    List<Category> findByFamilyIdAndDeletedAtIsNull(String familyId);
    
    List<Category> findByFamilyIdAndTypeAndDeletedAtIsNull(String familyId, Category.CategoryType type);
    
    Optional<Category> findByIdAndFamilyIdAndDeletedAtIsNull(String id, String familyId);
    
    boolean existsByFamilyIdAndNameAndTypeAndDeletedAtIsNull(String familyId, String name, Category.CategoryType type);
}