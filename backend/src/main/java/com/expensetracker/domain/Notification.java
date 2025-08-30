package com.expensetracker.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String userId;
    private String familyId;
    private NotificationType type;
    private String title;
    private String message;
    private Map<String, Object> data;
    private boolean isRead = false;
    private boolean emailSent = false;
    private Instant createdAt;

    public enum NotificationType {
        BUDGET_ALERT, BUDGET_EXCEEDED, WEEKLY_SUMMARY, MONTHLY_REPORT
    }

    // Constructors
    public Notification() {
        this.createdAt = Instant.now();
    }

    public Notification(String userId, String familyId, NotificationType type, String title, String message) {
        this();
        this.userId = userId;
        this.familyId = familyId;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFamilyId() { return familyId; }
    public void setFamilyId(String familyId) { this.familyId = familyId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}