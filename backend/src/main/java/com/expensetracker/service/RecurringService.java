package com.expensetracker.service;

import com.expensetracker.domain.LedgerEntry;
import com.expensetracker.domain.RecurringRule;
import com.expensetracker.dto.ledger.CreateLedgerEntryRequest;
import com.expensetracker.dto.recurring.RecurringRuleDTO;
import com.expensetracker.repository.RecurringRuleRepository;
import com.expensetracker.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecurringService {

    private final RecurringRuleRepository recurringRuleRepository;
    private final LedgerService ledgerService;
    private final UserService userService;

    public RecurringService(RecurringRuleRepository recurringRuleRepository, 
                          LedgerService ledgerService,
                          UserService userService) {
        this.recurringRuleRepository = recurringRuleRepository;
        this.ledgerService = ledgerService;
        this.userService = userService;
    }

    public RecurringRuleDTO createRecurringRule(String userId, RecurringRuleDTO dto) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        if (familyId == null) {
            throw new RuntimeException("User must belong to a family to create recurring rules");
        }

        RecurringRule rule = new RecurringRule();
        rule.setFamilyId(familyId);
        rule.setName(dto.getName());
        rule.setType(LedgerEntry.TransactionType.valueOf(dto.getType()));
        rule.setAmountMinor(dto.getAmountMinor());
        rule.setCurrency(dto.getCurrency());
        rule.setCategoryId(dto.getCategoryId());
        rule.setMemberId(dto.getMemberId());
        rule.setStartDate(dto.getStartDate());
        rule.setEndDate(dto.getEndDate());
        rule.setTimezone(dto.getTimezone());
        
        // Convert frequency
        RecurringRule.Frequency frequency = new RecurringRule.Frequency();
        frequency.setUnit(RecurringRule.Frequency.FrequencyUnit.valueOf(dto.getFrequency().getUnit()));
        frequency.setInterval(dto.getFrequency().getInterval());
        frequency.setByMonthDay(dto.getFrequency().getByMonthDay());
        rule.setFrequency(frequency);
        
        // Calculate next run time
        rule.setNextRunAt(calculateNextRun(rule));
        
        RecurringRule saved = recurringRuleRepository.save(rule);
        return convertToDTO(saved);
    }

    public List<RecurringRuleDTO> getRecurringRules(String userId) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        if (familyId == null) {
            return List.of();
        }
        
        List<RecurringRule> rules = recurringRuleRepository.findByFamilyId(familyId);
        return rules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public RecurringRuleDTO updateRecurringRule(String userId, String ruleId, RecurringRuleDTO dto) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        
        RecurringRule rule = recurringRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Recurring rule not found"));
        
        if (!rule.getFamilyId().equals(familyId)) {
            throw new RuntimeException("Access denied to recurring rule");
        }
        
        rule.setName(dto.getName());
        rule.setType(LedgerEntry.TransactionType.valueOf(dto.getType()));
        rule.setAmountMinor(dto.getAmountMinor());
        rule.setCurrency(dto.getCurrency());
        rule.setCategoryId(dto.getCategoryId());
        rule.setMemberId(dto.getMemberId());
        rule.setStartDate(dto.getStartDate());
        rule.setEndDate(dto.getEndDate());
        rule.setTimezone(dto.getTimezone());
        rule.setIsPaused(dto.getIsPaused());
        
        // Update frequency
        RecurringRule.Frequency frequency = new RecurringRule.Frequency();
        frequency.setUnit(RecurringRule.Frequency.FrequencyUnit.valueOf(dto.getFrequency().getUnit()));
        frequency.setInterval(dto.getFrequency().getInterval());
        frequency.setByMonthDay(dto.getFrequency().getByMonthDay());
        rule.setFrequency(frequency);
        
        // Recalculate next run time if not paused
        if (!rule.getIsPaused()) {
            rule.setNextRunAt(calculateNextRun(rule));
        }
        
        rule.setUpdatedAt(Instant.now());
        
        RecurringRule saved = recurringRuleRepository.save(rule);
        return convertToDTO(saved);
    }

    public void deleteRecurringRule(String userId, String ruleId) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        
        RecurringRule rule = recurringRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Recurring rule not found"));
        
        if (!rule.getFamilyId().equals(familyId)) {
            throw new RuntimeException("Access denied to recurring rule");
        }
        
        recurringRuleRepository.deleteById(ruleId);
    }

    public void processRecurringRules() {
        List<RecurringRule> dueRules = recurringRuleRepository.findDueRules(Instant.now());
        
        for (RecurringRule rule : dueRules) {
            try {
                createTransactionFromRule(rule);
                rule.setNextRunAt(calculateNextRun(rule));
                recurringRuleRepository.save(rule);
            } catch (Exception e) {
                // Log error but continue processing other rules
                System.err.println("Failed to process recurring rule " + rule.getId() + ": " + e.getMessage());
            }
        }
    }

    private void createTransactionFromRule(RecurringRule rule) {
        // Create a request DTO and use the existing LedgerService method
        CreateLedgerEntryRequest request = new CreateLedgerEntryRequest();
        request.setMemberId(rule.getMemberId());
        request.setType(rule.getType());
        request.setAmountMinor(rule.getAmountMinor());
        request.setCurrency(rule.getCurrency());
        request.setCategoryId(rule.getCategoryId());
        request.setOccurredAt(Instant.now());
        request.setNotes("Recurring: " + rule.getName());
        
        // Create a user principal for the member (simplified)
        UserPrincipal userPrincipal = new UserPrincipal(rule.getMemberId(), "system");
        
        ledgerService.createEntry(userPrincipal, request);
    }

    private Instant calculateNextRun(RecurringRule rule) {
        LocalDate nextDate = rule.getStartDate();
        ZoneId zoneId = ZoneId.of(rule.getTimezone());
        LocalDate today = LocalDate.now(zoneId);
        
        if (nextDate.isBefore(today)) {
            nextDate = today;
        }
        
        RecurringRule.Frequency frequency = rule.getFrequency();
        
        switch (frequency.getUnit()) {
            case WEEKLY:
                nextDate = nextDate.plus(frequency.getInterval(), ChronoUnit.WEEKS);
                break;
            case MONTHLY:
                nextDate = nextDate.plus(frequency.getInterval(), ChronoUnit.MONTHS);
                break;
            case YEARLY:
                nextDate = nextDate.plus(frequency.getInterval(), ChronoUnit.YEARS);
                break;
        }
        
        // Convert to instant at start of day in the specified timezone
        return nextDate.atStartOfDay(zoneId).toInstant();
    }

    private RecurringRuleDTO convertToDTO(RecurringRule rule) {
        RecurringRuleDTO dto = new RecurringRuleDTO();
        dto.setId(rule.getId());
        dto.setFamilyId(rule.getFamilyId());
        dto.setName(rule.getName());
        dto.setType(rule.getType().toString());
        dto.setAmountMinor(rule.getAmountMinor());
        dto.setCurrency(rule.getCurrency());
        dto.setCategoryId(rule.getCategoryId());
        dto.setMemberId(rule.getMemberId());
        dto.setStartDate(rule.getStartDate());
        dto.setEndDate(rule.getEndDate());
        dto.setTimezone(rule.getTimezone());
        dto.setNextRunAt(rule.getNextRunAt());
        dto.setIsPaused(rule.getIsPaused());
        
        // Convert frequency
        RecurringRuleDTO.FrequencyDTO frequencyDTO = new RecurringRuleDTO.FrequencyDTO();
        frequencyDTO.setUnit(rule.getFrequency().getUnit().toString());
        frequencyDTO.setInterval(rule.getFrequency().getInterval());
        frequencyDTO.setByMonthDay(rule.getFrequency().getByMonthDay());
        dto.setFrequency(frequencyDTO);
        
        return dto;
    }
}