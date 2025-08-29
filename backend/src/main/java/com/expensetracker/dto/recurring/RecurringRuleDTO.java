package com.expensetracker.dto.recurring;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class RecurringRuleDTO {
    private String id;
    
    @NotNull(message = "Family ID is required")
    private String familyId;
    
    @NotNull(message = "Name is required")
    private String name;
    
    @NotNull(message = "Type is required")
    private String type; // "EXPENSE" or "INCOME"
    
    @Positive(message = "Amount must be positive")
    private Long amountMinor;
    
    @NotNull(message = "Currency is required")
    private String currency;
    
    @NotNull(message = "Category ID is required")
    private String categoryId;
    
    @NotNull(message = "Member ID is required")
    private String memberId;
    
    @NotNull(message = "Frequency is required")
    private FrequencyDTO frequency;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @NotNull(message = "Timezone is required")
    private String timezone;
    
    private Instant nextRunAt;
    
    private boolean isPaused = false;

    public RecurringRuleDTO() {}

    public static class FrequencyDTO {
        @NotNull(message = "Frequency unit is required")
        private String unit; // "WEEKLY", "MONTHLY", "YEARLY"
        
        private int interval = 1;
        
        private List<Integer> byMonthDay;
        
        public FrequencyDTO() {}

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public int getInterval() { return interval; }
        public void setInterval(int interval) { this.interval = interval; }

        public List<Integer> getByMonthDay() { return byMonthDay; }
        public void setByMonthDay(List<Integer> byMonthDay) { this.byMonthDay = byMonthDay; }
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFamilyId() { return familyId; }
    public void setFamilyId(String familyId) { this.familyId = familyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getAmountMinor() { return amountMinor; }
    public void setAmountMinor(Long amountMinor) { this.amountMinor = amountMinor; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public FrequencyDTO getFrequency() { return frequency; }
    public void setFrequency(FrequencyDTO frequency) { this.frequency = frequency; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public Instant getNextRunAt() { return nextRunAt; }
    public void setNextRunAt(Instant nextRunAt) { this.nextRunAt = nextRunAt; }

    public boolean getIsPaused() { return isPaused; }
    public void setIsPaused(boolean isPaused) { this.isPaused = isPaused; }
}