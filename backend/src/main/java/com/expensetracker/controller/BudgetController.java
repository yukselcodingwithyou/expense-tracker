package com.expensetracker.controller;

import com.expensetracker.domain.Budget;
import com.expensetracker.dto.budget.BudgetDTO;
import com.expensetracker.dto.budget.BudgetSpendDTO;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.BudgetService;
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
@RequestMapping("/api/v1/budgets")
@Tag(name = "Budget", description = "Budget management APIs")
@Validated
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    @Operation(summary = "Create budget", description = "Create a new budget for the user's family")
    public ResponseEntity<Budget> createBudget(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody BudgetDTO budgetDTO) {
        Budget budget = budgetService.createBudget(user.getId(), budgetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }

    @GetMapping
    @Operation(summary = "List budgets", description = "Get all budgets for the user's current family")
    public ResponseEntity<List<Budget>> getBudgets(@AuthenticationPrincipal UserPrincipal user) {
        List<Budget> budgets = budgetService.getBudgetsForUser(user.getId());
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget details", description = "Get a specific budget by ID")
    public ResponseEntity<Budget> getBudgetById(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id) {
        return budgetService.getBudgetById(user.getId(), id)
                .map(budget -> ResponseEntity.ok(budget))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update budget", description = "Update an existing budget")
    public ResponseEntity<Budget> updateBudget(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id,
            @Valid @RequestBody BudgetDTO budgetDTO) {
        try {
            Budget budget = budgetService.updateBudget(user.getId(), id, budgetDTO);
            return ResponseEntity.ok(budget);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget", description = "Delete a budget")
    public ResponseEntity<Void> deleteBudget(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id) {
        budgetService.deleteBudget(user.getId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Get budget status", description = "Get current spending status against budget")
    public ResponseEntity<BudgetSpendDTO> getBudgetStatus(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id) {
        try {
            BudgetSpendDTO status = budgetService.getBudgetSpendingStatus(user.getId(), id);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}