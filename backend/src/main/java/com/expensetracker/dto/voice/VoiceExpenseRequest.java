package com.expensetracker.dto.voice;

public class VoiceExpenseRequest {
    private String spokenText;
    private String preferredCurrency = "USD";

    // Constructors
    public VoiceExpenseRequest() {}

    public VoiceExpenseRequest(String spokenText, String preferredCurrency) {
        this.spokenText = spokenText;
        this.preferredCurrency = preferredCurrency;
    }

    // Getters and Setters
    public String getSpokenText() {
        return spokenText;
    }

    public void setSpokenText(String spokenText) {
        this.spokenText = spokenText;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }
}