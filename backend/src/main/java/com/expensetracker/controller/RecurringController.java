package com.expensetracker.controller;

import com.expensetracker.dto.recurring.RecurringRuleDTO;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.RecurringService;
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
@RequestMapping("/api/v1/recurring")
@Tag(name = "Recurring Transactions", description = "Recurring transaction management APIs")
@Validated
public class RecurringController {

    private final RecurringService recurringService;

    public RecurringController(RecurringService recurringService) {
        this.recurringService = recurringService;
    }

    @PostMapping
    @Operation(summary = "Create recurring rule", description = "Create a new recurring transaction rule")
    public ResponseEntity<RecurringRuleDTO> createRecurringRule(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody RecurringRuleDTO dto) {
        
        RecurringRuleDTO created = recurringService.createRecurringRule(user.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Get recurring rules", description = "Get all recurring rules for the user's family")
    public ResponseEntity<List<RecurringRuleDTO>> getRecurringRules(
            @AuthenticationPrincipal UserPrincipal user) {
        
        List<RecurringRuleDTO> rules = recurringService.getRecurringRules(user.getId());
        return ResponseEntity.ok(rules);
    }

    @PutMapping("/{ruleId}")
    @Operation(summary = "Update recurring rule", description = "Update an existing recurring rule")
    public ResponseEntity<RecurringRuleDTO> updateRecurringRule(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String ruleId,
            @Valid @RequestBody RecurringRuleDTO dto) {
        
        RecurringRuleDTO updated = recurringService.updateRecurringRule(user.getId(), ruleId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{ruleId}")
    @Operation(summary = "Delete recurring rule", description = "Delete a recurring rule")
    public ResponseEntity<Void> deleteRecurringRule(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String ruleId) {
        
        recurringService.deleteRecurringRule(user.getId(), ruleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/process")
    @Operation(summary = "Process recurring rules", description = "Process all due recurring rules (admin only)")
    public ResponseEntity<Void> processRecurringRules() {
        recurringService.processRecurringRules();
        return ResponseEntity.ok().build();
    }
}