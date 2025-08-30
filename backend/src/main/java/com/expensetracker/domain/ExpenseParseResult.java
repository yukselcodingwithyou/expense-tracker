package com.expensetracker.domain;

import java.time.Instant;

public class ExpenseParseResult {
    private Long amountMinor; // Amount in smallest currency unit (e.g., cents)
    private String currency;
    private String categoryId;
    private String description;
    private Instant occurredAt;
    private String merchant;
    private double confidence;

    // Constructors
    public ExpenseParseResult() {}

    public ExpenseParseResult(Long amountMinor, String currency, String categoryId, 
                             String description, Instant occurredAt, String merchant, double confidence) {
        this.amountMinor = amountMinor;
        this.currency = currency;
        this.categoryId = categoryId;
        this.description = description;
        this.occurredAt = occurredAt;
        this.merchant = merchant;
        this.confidence = confidence;
    }

    // Getters and Setters
    public Long getAmountMinor() {
        return amountMinor;
    }

    public void setAmountMinor(Long amountMinor) {
        this.amountMinor = amountMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}