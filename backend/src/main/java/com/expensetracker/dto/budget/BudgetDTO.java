package com.expensetracker.dto.budget;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public class BudgetDTO {
    private String id;
    
    @NotNull(message = "Name is required")
    private String name;
    
    @NotNull(message = "Period is required")
    private PeriodDTO period;
    
    @Positive(message = "Overall limit must be positive")
    private Long overallLimitMinor;
    
    private boolean includeRecurring = true;
    
    private int alertThresholdPct = 80;
    
    private List<CategoryBudgetDTO> perCategory;

    public BudgetDTO() {}

    public static class PeriodDTO {
        @NotNull(message = "Period type is required")
        private String type; // "MONTH", "QUARTER", "YEAR", "CUSTOM"
        
        @NotNull(message = "Start date is required")
        private LocalDate start;
        
        @NotNull(message = "End date is required")
        private LocalDate end;
        
        public PeriodDTO() {}

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public LocalDate getStart() { return start; }
        public void setStart(LocalDate start) { this.start = start; }

        public LocalDate getEnd() { return end; }
        public void setEnd(LocalDate end) { this.end = end; }
    }

    public static class CategoryBudgetDTO {
        @NotNull(message = "Category ID is required")
        private String categoryId;
        
        @Positive(message = "Limit must be positive")
        private Long limitMinor;
        
        public CategoryBudgetDTO() {}

        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

        public Long getLimitMinor() { return limitMinor; }
        public void setLimitMinor(Long limitMinor) { this.limitMinor = limitMinor; }
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PeriodDTO getPeriod() { return period; }
    public void setPeriod(PeriodDTO period) { this.period = period; }

    public Long getOverallLimitMinor() { return overallLimitMinor; }
    public void setOverallLimitMinor(Long overallLimitMinor) { this.overallLimitMinor = overallLimitMinor; }

    public boolean getIncludeRecurring() { return includeRecurring; }
    public void setIncludeRecurring(boolean includeRecurring) { this.includeRecurring = includeRecurring; }

    public int getAlertThresholdPct() { return alertThresholdPct; }
    public void setAlertThresholdPct(int alertThresholdPct) { this.alertThresholdPct = alertThresholdPct; }

    public List<CategoryBudgetDTO> getPerCategory() { return perCategory; }
    public void setPerCategory(List<CategoryBudgetDTO> perCategory) { this.perCategory = perCategory; }
}