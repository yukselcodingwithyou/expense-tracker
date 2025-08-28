package com.expensetracker.dto.ledger;

import com.expensetracker.domain.LedgerEntry;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.List;

public class CreateLedgerEntryRequest {
    @NotNull(message = "Member ID is required")
    private String memberId;
    
    @NotNull(message = "Transaction type is required")
    private LedgerEntry.TransactionType type;
    
    @Positive(message = "Amount must be positive")
    private Long amountMinor;
    
    @NotNull(message = "Currency is required")
    private String currency;
    
    @NotNull(message = "Category ID is required")
    private String categoryId;
    
    @NotNull(message = "Occurred date is required")
    private Instant occurredAt;
    
    private String notes;
    private List<String> attachments;

    public CreateLedgerEntryRequest() {}

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public LedgerEntry.TransactionType getType() { return type; }
    public void setType(LedgerEntry.TransactionType type) { this.type = type; }

    public Long getAmountMinor() { return amountMinor; }
    public void setAmountMinor(Long amountMinor) { this.amountMinor = amountMinor; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
}