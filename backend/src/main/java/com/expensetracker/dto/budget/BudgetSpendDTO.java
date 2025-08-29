package com.expensetracker.dto.budget;

import java.time.LocalDate;
import java.util.List;

public class BudgetSpendDTO {
    private PeriodDTO period;
    private SpendingSummaryDTO overall;
    private List<CategorySpendingDTO> byCategory;

    public BudgetSpendDTO() {}

    public static class PeriodDTO {
        private LocalDate start;
        private LocalDate end;
        
        public PeriodDTO() {}

        public LocalDate getStart() { return start; }
        public void setStart(LocalDate start) { this.start = start; }

        public LocalDate getEnd() { return end; }
        public void setEnd(LocalDate end) { this.end = end; }
    }

    public static class SpendingSummaryDTO {
        private Long limitMinor;
        private Long spentMinor;
        
        public SpendingSummaryDTO() {}

        public Long getLimitMinor() { return limitMinor; }
        public void setLimitMinor(Long limitMinor) { this.limitMinor = limitMinor; }

        public Long getSpentMinor() { return spentMinor; }
        public void setSpentMinor(Long spentMinor) { this.spentMinor = spentMinor; }
    }

    public static class CategorySpendingDTO {
        private String categoryId;
        private Long limitMinor;
        private Long spentMinor;
        
        public CategorySpendingDTO() {}

        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

        public Long getLimitMinor() { return limitMinor; }
        public void setLimitMinor(Long limitMinor) { this.limitMinor = limitMinor; }

        public Long getSpentMinor() { return spentMinor; }
        public void setSpentMinor(Long spentMinor) { this.spentMinor = spentMinor; }
    }

    // Getters and setters
    public PeriodDTO getPeriod() { return period; }
    public void setPeriod(PeriodDTO period) { this.period = period; }

    public SpendingSummaryDTO getOverall() { return overall; }
    public void setOverall(SpendingSummaryDTO overall) { this.overall = overall; }

    public List<CategorySpendingDTO> getByCategory() { return byCategory; }
    public void setByCategory(List<CategorySpendingDTO> byCategory) { this.byCategory = byCategory; }
}