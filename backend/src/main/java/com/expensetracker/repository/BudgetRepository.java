package com.expensetracker.repository;

import com.expensetracker.domain.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {
    
    /**
     * Find all budgets for a specific family
     */
    List<Budget> findByFamilyId(String familyId);
    
    /**
     * Find budget by ID and family ID (for security)
     */
    Optional<Budget> findByIdAndFamilyId(String id, String familyId);
    
    /**
     * Find budgets that overlap with a specific date range
     */
    List<Budget> findByFamilyIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
            String familyId, LocalDate endDate, LocalDate startDate);
    
    /**
     * Find active budget for a specific date
     */
    List<Budget> findByFamilyIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqualOrderByCreatedAtDesc(
            String familyId, LocalDate date, LocalDate sameDate);
    
    /**
     * Delete budget by ID and family ID (for security)
     */
    void deleteByIdAndFamilyId(String id, String familyId);
}