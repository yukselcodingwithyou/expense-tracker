package com.expensetracker.service;

import com.expensetracker.domain.LedgerEntry;
import com.expensetracker.dto.reports.ReportSummaryDTO;
import com.expensetracker.dto.reports.ExportResponse;
import com.expensetracker.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final UserService userService;

    public ReportService(LedgerEntryRepository ledgerEntryRepository, UserService userService) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.userService = userService;
    }

    public ReportSummaryDTO generateReport(String userId, LocalDate startDate, LocalDate endDate) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        if (familyId == null) {
            throw new RuntimeException("User must belong to a family to generate reports");
        }

        List<LedgerEntry> entries = ledgerEntryRepository.findByFamilyIdAndOccurredAtBetweenAndDeletedAtIsNull(
                familyId, 
                startDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC),
                endDate.atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC));

        ReportSummaryDTO report = new ReportSummaryDTO();
        
        // Calculate totals
        long totalIncome = 0;
        long totalExpenses = 0;
        
        for (LedgerEntry entry : entries) {
            if (entry.getType() == LedgerEntry.TransactionType.INCOME) {
                totalIncome += entry.getAmount().getMinor();
            } else {
                totalExpenses += entry.getAmount().getMinor();
            }
        }
        
        report.setTotalIncomeMinor(totalIncome);
        report.setTotalExpensesMinor(totalExpenses);
        report.setBalanceMinor(totalIncome - totalExpenses);
        
        // Per category summary
        Map<String, Long> categorySpending = new HashMap<>();
        for (LedgerEntry entry : entries) {
            if (entry.getType() == LedgerEntry.TransactionType.EXPENSE) {
                categorySpending.merge(entry.getCategoryId(), entry.getAmount().getMinor(), Long::sum);
            }
        }
        
        List<ReportSummaryDTO.CategorySummaryDTO> categoryData = categorySpending.entrySet().stream()
                .map(e -> {
                    ReportSummaryDTO.CategorySummaryDTO cat = new ReportSummaryDTO.CategorySummaryDTO();
                    cat.setCategoryId(e.getKey());
                    cat.setSpentMinor(e.getValue());
                    return cat;
                })
                .collect(Collectors.toList());
        report.setPerCategory(categoryData);
        
        // Monthly summary
        Map<String, MonthlyData> monthlyData = new HashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (LedgerEntry entry : entries) {
            String monthKey = entry.getOccurredAt().atZone(java.time.ZoneOffset.UTC).format(monthFormatter);
            MonthlyData data = monthlyData.computeIfAbsent(monthKey, k -> new MonthlyData());
            
            if (entry.getType() == LedgerEntry.TransactionType.INCOME) {
                data.income += entry.getAmount().getMinor();
            } else {
                data.expense += entry.getAmount().getMinor();
            }
        }
        
        List<ReportSummaryDTO.MonthlySummaryDTO> monthlyList = monthlyData.entrySet().stream()
                .map(e -> {
                    ReportSummaryDTO.MonthlySummaryDTO month = new ReportSummaryDTO.MonthlySummaryDTO();
                    month.setMonth(e.getKey());
                    month.setIncomeMinor(e.getValue().income);
                    month.setExpenseMinor(e.getValue().expense);
                    return month;
                })
                .collect(Collectors.toList());
        report.setPerMonth(monthlyList);
        
        // Recent transactions (last 10)
        List<LedgerEntry> recentEntries = entries.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .collect(Collectors.toList());
        
        List<ReportSummaryDTO.TransactionSummaryDTO> recentTransactions = recentEntries.stream()
                .map(e -> {
                    ReportSummaryDTO.TransactionSummaryDTO tx = new ReportSummaryDTO.TransactionSummaryDTO();
                    tx.setId(e.getId());
                    tx.setCategoryId(e.getCategoryId());
                    tx.setAmountMinor(e.getAmount().getMinor());
                    return tx;
                })
                .collect(Collectors.toList());
        report.setRecentTransactions(recentTransactions);
        
        return report;
    }

    public ExportResponse exportData(String userId, LocalDate startDate, LocalDate endDate, String format) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        if (familyId == null) {
            throw new RuntimeException("User must belong to a family to export data");
        }

        List<LedgerEntry> entries = ledgerEntryRepository.findByFamilyIdAndOccurredAtBetweenAndDeletedAtIsNull(
                familyId, 
                startDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC),
                endDate.atTime(23, 59, 59).toInstant(java.time.ZoneOffset.UTC));

        ExportResponse response = new ExportResponse();
        
        if ("CSV".equalsIgnoreCase(format)) {
            response.setContent(generateCSV(entries));
            response.setContentType("text/csv");
            response.setFilename("expense_report_" + startDate + "_to_" + endDate + ".csv");
        } else {
            throw new IllegalArgumentException("Unsupported export format: " + format);
        }
        
        return response;
    }

    private String generateCSV(List<LedgerEntry> entries) {
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Type,Amount,Currency,Category,Member,Description\n");
        
        for (LedgerEntry entry : entries) {
            csv.append(entry.getOccurredAt().atZone(java.time.ZoneOffset.UTC).toLocalDate()).append(",")
               .append(entry.getType()).append(",")
               .append(entry.getAmount().getMinor() / 100.0).append(",")
               .append(entry.getAmount().getCurrency()).append(",")
               .append(entry.getCategoryId()).append(",")
               .append(entry.getMemberId()).append(",")
               .append("\"").append((entry.getNotes() != null ? entry.getNotes() : "").replace("\"", "\"\"")).append("\"")
               .append("\n");
        }
        
        return csv.toString();
    }

    private static class MonthlyData {
        long income = 0;
        long expense = 0;
    }
}