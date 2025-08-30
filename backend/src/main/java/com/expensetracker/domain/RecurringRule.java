package com.expensetracker.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "recurring_rules")
public class RecurringRule {
    @Id
    private String id;
    private String familyId;
    private String name;
    private LedgerEntry.TransactionType type; // EXPENSE or INCOME
    private Long amountMinor;
    private String currency;
    private String categoryId;
    private String memberId;
    private Frequency frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String timezone;
    private Instant nextRunAt;
    private boolean isPaused = false;
    private Instant createdAt;
    private Instant updatedAt;

    public RecurringRule() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static class Frequency {
        private FrequencyUnit unit; // WEEKLY, MONTHLY, YEARLY
        private int interval = 1;
        private List<Integer> byMonthDay; // for monthly recurrence

        public enum FrequencyUnit {
            WEEKLY, MONTHLY, YEARLY
        }

        public Frequency() {}

        public FrequencyUnit getUnit() { return unit; }
        public void setUnit(FrequencyUnit unit) { this.unit = unit; }

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

    public LedgerEntry.TransactionType getType() { return type; }
    public void setType(LedgerEntry.TransactionType type) { this.type = type; }

    public Long getAmountMinor() { return amountMinor; }
    public void setAmountMinor(Long amountMinor) { this.amountMinor = amountMinor; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }

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

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}