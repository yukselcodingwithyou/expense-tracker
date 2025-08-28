package com.expensetracker.dto.ledger;

import com.expensetracker.domain.LedgerEntry;

import java.time.Instant;
import java.util.List;

public class LedgerEntryResponse {
    private String id;
    private String memberId;
    private LedgerEntry.TransactionType type;
    private Long amountMinor;
    private String currency;
    private String categoryId;
    private String categoryName;
    private Instant occurredAt;
    private String notes;
    private List<String> attachments;
    private String recurringId;
    private Instant createdAt;
    private Instant updatedAt;

    public LedgerEntryResponse() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }

    public String getRecurringId() { return recurringId; }
    public void setRecurringId(String recurringId) { this.recurringId = recurringId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}