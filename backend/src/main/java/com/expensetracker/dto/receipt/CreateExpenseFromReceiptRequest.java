package com.expensetracker.dto.receipt;

import com.expensetracker.domain.ReceiptItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CreateExpenseFromReceiptRequest {
    private String receiptDataId;
    private BigDecimal amount;
    private String categoryId;
    private String storeName;
    private LocalDateTime date;
    private List<ReceiptItem> items;
    private String description;

    // Constructors
    public CreateExpenseFromReceiptRequest() {}

    // Getters and Setters
    public String getReceiptDataId() {
        return receiptDataId;
    }

    public void setReceiptDataId(String receiptDataId) {
        this.receiptDataId = receiptDataId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

    public void setItems(List<ReceiptItem> items) {
        this.items = items;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}