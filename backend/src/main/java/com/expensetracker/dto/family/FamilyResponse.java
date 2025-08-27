package com.expensetracker.dto.family;

import java.time.Instant;

public class FamilyResponse {
    private String id;
    private String name;
    private String currency;
    private Instant createdAt;
    private Instant updatedAt;

    public FamilyResponse() {}

    public FamilyResponse(String id, String name, String currency, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}