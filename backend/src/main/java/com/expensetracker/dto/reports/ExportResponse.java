package com.expensetracker.dto.reports;

public class ExportResponse {
    private String exportId;

    public ExportResponse() {}

    public ExportResponse(String exportId) {
        this.exportId = exportId;
    }

    public String getExportId() { return exportId; }
    public void setExportId(String exportId) { this.exportId = exportId; }
}