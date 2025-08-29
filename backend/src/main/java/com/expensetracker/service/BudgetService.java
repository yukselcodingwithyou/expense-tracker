package com.expensetracker.service;

import com.expensetracker.domain.Budget;
import com.expensetracker.domain.LedgerEntry;
import com.expensetracker.dto.budget.BudgetDTO;
import com.expensetracker.dto.budget.BudgetSpendDTO;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final UserService userService;

    public BudgetService(BudgetRepository budgetRepository, LedgerEntryRepository ledgerEntryRepository, UserService userService) {
        this.budgetRepository = budgetRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.userService = userService;
    }

    /**
     * Create a new budget
     */
    public Budget createBudget(String userId, BudgetDTO budgetDTO) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        if (familyId == null) {
            throw new IllegalStateException("User is not associated with any family");
        }

        Budget budget = convertDtoToBudget(budgetDTO);
        budget.setFamilyId(familyId);

        return budgetRepository.save(budget);
    }

    /**
     * Get all budgets for user's current family
     */
    public List<Budget> getBudgetsForUser(String userId) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        if (familyId == null) {
            return List.of();
        }
        
        return budgetRepository.findByFamilyId(familyId);
    }

    /**
     * Get a specific budget by ID (with family security check)
     */
    public Optional<Budget> getBudgetById(String userId, String budgetId) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        if (familyId == null) {
            return Optional.empty();
        }
        
        return budgetRepository.findByIdAndFamilyId(budgetId, familyId);
    }

    /**
     * Update an existing budget
     */
    public Budget updateBudget(String userId, String budgetId, BudgetDTO budgetDTO) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        if (familyId == null) {
            throw new IllegalStateException("User is not associated with any family");
        }

        Budget existingBudget = budgetRepository.findByIdAndFamilyId(budgetId, familyId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        // Update fields
        existingBudget.setName(budgetDTO.getName());
        existingBudget.setPeriod(convertPeriodDtoToPeriod(budgetDTO.getPeriod()));
        existingBudget.setOverallLimitMinor(budgetDTO.getOverallLimitMinor());
        existingBudget.setIncludeRecurring(budgetDTO.getIncludeRecurring());
        existingBudget.setAlertThresholdPct(budgetDTO.getAlertThresholdPct());
        existingBudget.setPerCategory(convertCategoryBudgets(budgetDTO.getPerCategory()));

        return budgetRepository.save(existingBudget);
    }

    /**
     * Delete a budget
     */
    public void deleteBudget(String userId, String budgetId) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        if (familyId == null) {
            throw new IllegalStateException("User is not associated with any family");
        }

        budgetRepository.deleteByIdAndFamilyId(budgetId, familyId);
    }

    /**
     * Get budget spending status
     */
    public BudgetSpendDTO getBudgetSpendingStatus(String userId, String budgetId) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        if (familyId == null) {
            throw new IllegalStateException("User is not associated with any family");
        }

        Budget budget = budgetRepository.findByIdAndFamilyId(budgetId, familyId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        // Get spending data for the budget period
        List<LedgerEntry> entries = ledgerEntryRepository.findByFamilyIdAndTypeAndOccurredAtBetweenAndDeletedAtIsNull(
                familyId,
                LedgerEntry.TransactionType.EXPENSE,
                budget.getPeriod().getStart().atStartOfDay().atZone(java.time.ZoneOffset.UTC).toInstant(),
                budget.getPeriod().getEnd().atTime(23, 59, 59).atZone(java.time.ZoneOffset.UTC).toInstant()
        );

        // Calculate overall spending
        long totalSpent = entries.stream()
                .mapToLong(entry -> entry.getAmount().getMinor())
                .sum();

        // Calculate spending by category
        Map<String, Long> spendingByCategory = entries.stream()
                .collect(Collectors.groupingBy(
                        LedgerEntry::getCategoryId,
                        Collectors.summingLong(entry -> entry.getAmount().getMinor())
                ));

        return buildBudgetSpendDTO(budget, totalSpent, spendingByCategory);
    }

    // Helper methods
    private Budget convertDtoToBudget(BudgetDTO dto) {
        Budget budget = new Budget();
        budget.setName(dto.getName());
        budget.setPeriod(convertPeriodDtoToPeriod(dto.getPeriod()));
        budget.setOverallLimitMinor(dto.getOverallLimitMinor());
        budget.setIncludeRecurring(dto.getIncludeRecurring());
        budget.setAlertThresholdPct(dto.getAlertThresholdPct());
        budget.setPerCategory(convertCategoryBudgets(dto.getPerCategory()));
        return budget;
    }

    private Budget.Period convertPeriodDtoToPeriod(BudgetDTO.PeriodDTO dto) {
        Budget.PeriodType type = Budget.PeriodType.valueOf(dto.getType());
        return new Budget.Period(type, dto.getStart(), dto.getEnd());
    }

    private List<Budget.CategoryBudget> convertCategoryBudgets(List<BudgetDTO.CategoryBudgetDTO> dtos) {
        if (dtos == null) return List.of();
        
        return dtos.stream()
                .map(dto -> new Budget.CategoryBudget(dto.getCategoryId(), dto.getLimitMinor()))
                .collect(Collectors.toList());
    }

    private BudgetSpendDTO buildBudgetSpendDTO(Budget budget, long totalSpent, Map<String, Long> spendingByCategory) {
        BudgetSpendDTO dto = new BudgetSpendDTO();
        
        // Set period
        BudgetSpendDTO.PeriodDTO periodDto = new BudgetSpendDTO.PeriodDTO();
        periodDto.setStart(budget.getPeriod().getStart());
        periodDto.setEnd(budget.getPeriod().getEnd());
        dto.setPeriod(periodDto);

        // Set overall spending
        BudgetSpendDTO.SpendingSummaryDTO overallDto = new BudgetSpendDTO.SpendingSummaryDTO();
        overallDto.setLimitMinor(budget.getOverallLimitMinor());
        overallDto.setSpentMinor(totalSpent);
        dto.setOverall(overallDto);

        // Set category spending
        List<BudgetSpendDTO.CategorySpendingDTO> categorySpending = budget.getPerCategory().stream()
                .map(categoryBudget -> {
                    BudgetSpendDTO.CategorySpendingDTO categoryDto = new BudgetSpendDTO.CategorySpendingDTO();
                    categoryDto.setCategoryId(categoryBudget.getCategoryId());
                    categoryDto.setLimitMinor(categoryBudget.getLimitMinor());
                    categoryDto.setSpentMinor(spendingByCategory.getOrDefault(categoryBudget.getCategoryId(), 0L));
                    return categoryDto;
                })
                .collect(Collectors.toList());
        
        dto.setByCategory(categorySpending);
        return dto;
    }
}