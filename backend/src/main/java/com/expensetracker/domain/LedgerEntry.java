package com.expensetracker.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.List;

@Document(collection = "ledger")
@CompoundIndex(def = "{'familyId': 1, 'occurredAt': -1}")
@CompoundIndex(def = "{'familyId': 1, 'categoryId': 1, 'occurredAt': -1}")
public class LedgerEntry {
    @Id
    private String id;
    
    @NotNull
    private String familyId;
    
    @NotNull
    private String memberId;
    
    @NotNull
    private TransactionType type;
    
    @NotNull
    private MoneyAmount amount;
    
    @NotNull
    private String categoryId;
    
    @NotNull
    private Instant occurredAt;
    
    private String notes;
    private List<String> attachments;
    private String recurringId; // Reference to recurring rule if auto-generated
    
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public enum TransactionType {
        EXPENSE, INCOME
    }

    public static class MoneyAmount {
        @Positive
        private Long minor; // Amount in minor units (cents)
        
        @NotNull
        private String currency;

        public MoneyAmount() {}

        public MoneyAmount(Long minor, String currency) {
            this.minor = minor;
            this.currency = currency;
        }

        public Long getMinor() { return minor; }
        public void setMinor(Long minor) { this.minor = minor; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

    // Constructors
    public LedgerEntry() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public LedgerEntry(String familyId, String memberId, TransactionType type, MoneyAmount amount, String categoryId, Instant occurredAt) {
        this();
        this.familyId = familyId;
        this.memberId = memberId;
        this.type = type;
        this.amount = amount;
        this.categoryId = categoryId;
        this.occurredAt = occurredAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFamilyId() { return familyId; }
    public void setFamilyId(String familyId) { this.familyId = familyId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public MoneyAmount getAmount() { return amount; }
    public void setAmount(MoneyAmount amount) { this.amount = amount; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

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

    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}