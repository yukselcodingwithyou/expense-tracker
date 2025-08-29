package com.expensetracker.dto.ledger;

import com.expensetracker.domain.LedgerEntry;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public class LedgerCreateDTO {
    @NotNull(message = "Transaction type is required")
    private LedgerEntry.TransactionType type;
    
    @Positive(message = "Amount must be positive")
    private Long amountMinor;
    
    @NotNull(message = "Currency is required")
    private String currency;
    
    @NotNull(message = "Category ID is required")
    private String categoryId;
    
    @NotNull(message = "Member ID is required")
    private String memberId;
    
    @NotNull(message = "Occurred date is required")
    private Instant occurredAt;
    
    private String notes;

    public LedgerCreateDTO() {}

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

    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}