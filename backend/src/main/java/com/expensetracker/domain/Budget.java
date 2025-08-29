package com.expensetracker.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "budgets")
public class Budget {
    @Id
    private String id;
    
    @NotNull
    private String familyId;
    
    @NotNull
    private String name;
    
    @NotNull
    private Period period;
    
    @Positive
    private Long overallLimitMinor;
    
    private boolean includeRecurring = true;
    
    private int alertThresholdPct = 80; // Alert when spending reaches this % of limit
    
    private List<CategoryBudget> perCategory = new ArrayList<>();
    
    private Instant createdAt;
    private Instant updatedAt;

    // Constructors
    public Budget() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Budget(String familyId, String name, Period period, Long overallLimitMinor) {
        this();
        this.familyId = familyId;
        this.name = name;
        this.period = period;
        this.overallLimitMinor = overallLimitMinor;
    }

    // Inner classes
    public static class Period {
        @NotNull
        private PeriodType type;
        
        @NotNull
        private LocalDate start;
        
        @NotNull
        private LocalDate end;

        public Period() {}

        public Period(PeriodType type, LocalDate start, LocalDate end) {
            this.type = type;
            this.start = start;
            this.end = end;
        }

        // Getters and setters
        public PeriodType getType() { return type; }
        public void setType(PeriodType type) { this.type = type; }
        
        public LocalDate getStart() { return start; }
        public void setStart(LocalDate start) { this.start = start; }
        
        public LocalDate getEnd() { return end; }
        public void setEnd(LocalDate end) { this.end = end; }
    }

    public static class CategoryBudget {
        @NotNull
        private String categoryId;
        
        @Positive
        private Long limitMinor;

        public CategoryBudget() {}

        public CategoryBudget(String categoryId, Long limitMinor) {
            this.categoryId = categoryId;
            this.limitMinor = limitMinor;
        }

        // Getters and setters
        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
        
        public Long getLimitMinor() { return limitMinor; }
        public void setLimitMinor(Long limitMinor) { this.limitMinor = limitMinor; }
    }

    public enum PeriodType {
        MONTH, QUARTER, YEAR, CUSTOM
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFamilyId() { return familyId; }
    public void setFamilyId(String familyId) { this.familyId = familyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Period getPeriod() { return period; }
    public void setPeriod(Period period) { this.period = period; }

    public Long getOverallLimitMinor() { return overallLimitMinor; }
    public void setOverallLimitMinor(Long overallLimitMinor) { this.overallLimitMinor = overallLimitMinor; }

    public boolean isIncludeRecurring() { return includeRecurring; }
    public void setIncludeRecurring(boolean includeRecurring) { this.includeRecurring = includeRecurring; }

    public int getAlertThresholdPct() { return alertThresholdPct; }
    public void setAlertThresholdPct(int alertThresholdPct) { this.alertThresholdPct = alertThresholdPct; }

    public List<CategoryBudget> getPerCategory() { return perCategory; }
    public void setPerCategory(List<CategoryBudget> perCategory) { this.perCategory = perCategory; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}