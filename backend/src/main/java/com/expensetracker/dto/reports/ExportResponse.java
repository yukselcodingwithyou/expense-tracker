package com.expensetracker.dto.reports;

public class ExportResponse {
    private String content;
    private String contentType;
    private String filename;

    public ExportResponse() {}

    public ExportResponse(String content, String contentType, String filename) {
        this.content = content;
        this.contentType = contentType;
        this.filename = filename;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
}