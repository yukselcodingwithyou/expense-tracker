package com.expensetracker.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "attachments")
public class Attachment {
    @Id
    private String id;
    private String ledgerEntryId;
    private String filename;
    private String originalFilename;
    private String contentType;
    private long size;
    private String storageKey; // MinIO object name or file path
    private Instant uploadedAt;

    // Constructors
    public Attachment() {
        this.uploadedAt = Instant.now();
    }

    public Attachment(String ledgerEntryId, String filename, String originalFilename, 
                     String contentType, long size, String storageKey) {
        this();
        this.ledgerEntryId = ledgerEntryId;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.size = size;
        this.storageKey = storageKey;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLedgerEntryId() { return ledgerEntryId; }
    public void setLedgerEntryId(String ledgerEntryId) { this.ledgerEntryId = ledgerEntryId; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }

    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
}