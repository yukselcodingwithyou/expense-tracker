package com.expensetracker.controller;

import com.expensetracker.dto.reports.ReportSummaryDTO;
import com.expensetracker.dto.reports.ExportResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Financial reports and analytics APIs")
@Validated
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Generate report summary", description = "Generate financial summary report for a date range")
    public ResponseEntity<ReportSummaryDTO> generateReport(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ReportSummaryDTO report = reportService.generateReport(user.getId(), startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/export")
    @Operation(summary = "Export data", description = "Export transaction data in specified format")
    public ResponseEntity<ExportResponse> exportData(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "CSV") String format) {
        
        ExportResponse export = reportService.exportData(user.getId(), startDate, endDate, format);
        return ResponseEntity.ok(export);
    }
}