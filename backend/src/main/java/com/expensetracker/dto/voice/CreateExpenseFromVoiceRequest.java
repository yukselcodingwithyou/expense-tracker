package com.expensetracker.dto.voice;

public class CreateExpenseFromVoiceRequest {
    private String voiceExpenseDataId;
    private Long amountMinor;
    private String currency;
    private String categoryId;
    private String description;
    private String merchant;

    // Constructors
    public CreateExpenseFromVoiceRequest() {}

    // Getters and Setters
    public String getVoiceExpenseDataId() {
        return voiceExpenseDataId;
    }

    public void setVoiceExpenseDataId(String voiceExpenseDataId) {
        this.voiceExpenseDataId = voiceExpenseDataId;
    }

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

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }
}