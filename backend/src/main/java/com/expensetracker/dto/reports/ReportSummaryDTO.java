package com.expensetracker.dto.reports;

import java.util.List;

public class ReportSummaryDTO {
    private Long totalIncomeMinor;
    private Long totalExpensesMinor;
    private Long balanceMinor;
    private List<CategorySummaryDTO> perCategory;
    private List<MonthlySummaryDTO> perMonth;
    private List<TransactionSummaryDTO> recentTransactions;

    public ReportSummaryDTO() {}

    public static class CategorySummaryDTO {
        private String categoryId;
        private Long spentMinor;
        
        public CategorySummaryDTO() {}

        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

        public Long getSpentMinor() { return spentMinor; }
        public void setSpentMinor(Long spentMinor) { this.spentMinor = spentMinor; }
    }

    public static class MonthlySummaryDTO {
        private String month; // Format: "2025-10"
        private Long incomeMinor;
        private Long expenseMinor;
        
        public MonthlySummaryDTO() {}

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }

        public Long getIncomeMinor() { return incomeMinor; }
        public void setIncomeMinor(Long incomeMinor) { this.incomeMinor = incomeMinor; }

        public Long getExpenseMinor() { return expenseMinor; }
        public void setExpenseMinor(Long expenseMinor) { this.expenseMinor = expenseMinor; }
    }

    public static class TransactionSummaryDTO {
        private String id;
        private String categoryId;
        private Long amountMinor;
        
        public TransactionSummaryDTO() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

        public Long getAmountMinor() { return amountMinor; }
        public void setAmountMinor(Long amountMinor) { this.amountMinor = amountMinor; }
    }

    // Getters and setters
    public Long getTotalIncomeMinor() { return totalIncomeMinor; }
    public void setTotalIncomeMinor(Long totalIncomeMinor) { this.totalIncomeMinor = totalIncomeMinor; }

    public Long getTotalExpensesMinor() { return totalExpensesMinor; }
    public void setTotalExpensesMinor(Long totalExpensesMinor) { this.totalExpensesMinor = totalExpensesMinor; }

    public Long getBalanceMinor() { return balanceMinor; }
    public void setBalanceMinor(Long balanceMinor) { this.balanceMinor = balanceMinor; }

    public List<CategorySummaryDTO> getPerCategory() { return perCategory; }
    public void setPerCategory(List<CategorySummaryDTO> perCategory) { this.perCategory = perCategory; }

    public List<MonthlySummaryDTO> getPerMonth() { return perMonth; }
    public void setPerMonth(List<MonthlySummaryDTO> perMonth) { this.perMonth = perMonth; }

    public List<TransactionSummaryDTO> getRecentTransactions() { return recentTransactions; }
    public void setRecentTransactions(List<TransactionSummaryDTO> recentTransactions) { this.recentTransactions = recentTransactions; }
}