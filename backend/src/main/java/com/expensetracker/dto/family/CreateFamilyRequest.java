package com.expensetracker.dto.family;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateFamilyRequest {
    @NotBlank(message = "Family name is required")
    private String name;
    
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code")
    private String currency;

    public CreateFamilyRequest() {}

    public CreateFamilyRequest(String name, String currency) {
        this.name = name;
        this.currency = currency;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}