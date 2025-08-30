package com.expensetracker.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "voice_expense_data")
public class VoiceExpenseData {
    @Id
    private String id;
    private String userId;
    private String originalText;
    private ExpenseParseResult parseResult;
    private double overallConfidence;
    private List<String> suggestions;
    private Map<String, Double> fieldConfidences;
    private LocalDateTime createdAt;

    // Constructors
    public VoiceExpenseData() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public ExpenseParseResult getParseResult() {
        return parseResult;
    }

    public void setParseResult(ExpenseParseResult parseResult) {
        this.parseResult = parseResult;
    }

    public double getOverallConfidence() {
        return overallConfidence;
    }

    public void setOverallConfidence(double overallConfidence) {
        this.overallConfidence = overallConfidence;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public Map<String, Double> getFieldConfidences() {
        return fieldConfidences;
    }

    public void setFieldConfidences(Map<String, Double> fieldConfidences) {
        this.fieldConfidences = fieldConfidences;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}