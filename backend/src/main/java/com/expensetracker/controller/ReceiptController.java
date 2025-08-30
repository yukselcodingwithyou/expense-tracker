package com.expensetracker.controller;

import com.expensetracker.domain.LedgerEntry;
import com.expensetracker.domain.ReceiptData;
import com.expensetracker.dto.ledger.LedgerEntryResponse;
import com.expensetracker.dto.receipt.CreateExpenseFromReceiptRequest;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.ReceiptOCRService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/receipts")
@Tag(name = "Receipt OCR", description = "Receipt OCR processing and expense creation APIs")
public class ReceiptController {

    private final ReceiptOCRService receiptOCRService;

    public ReceiptController(ReceiptOCRService receiptOCRService) {
        this.receiptOCRService = receiptOCRService;
    }

    @PostMapping("/process")
    @Operation(summary = "Process receipt", description = "Upload and process a receipt image using OCR")
    public ResponseEntity<ReceiptData> processReceipt(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal user) {
        
        ReceiptData data = receiptOCRService.processReceipt(file, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    @PostMapping("/create-expense")
    @Operation(summary = "Create expense from receipt", description = "Create a ledger entry from processed receipt data")
    public ResponseEntity<LedgerEntryResponse> createExpenseFromReceipt(
            @RequestBody CreateExpenseFromReceiptRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        
        LedgerEntryResponse entry = receiptOCRService.createExpenseFromReceipt(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    @GetMapping
    @Operation(summary = "Get user receipts", description = "Get all processed receipts for the current user")
    public ResponseEntity<List<ReceiptData>> getUserReceipts(
            @AuthenticationPrincipal UserPrincipal user) {
        
        List<ReceiptData> receipts = receiptOCRService.getUserReceiptData(user.getId());
        return ResponseEntity.ok(receipts);
    }

    @GetMapping("/{receiptDataId}")
    @Operation(summary = "Get receipt data", description = "Get specific receipt data by ID")
    public ResponseEntity<ReceiptData> getReceiptData(
            @PathVariable String receiptDataId,
            @AuthenticationPrincipal UserPrincipal user) {
        
        ReceiptData data = receiptOCRService.getReceiptData(receiptDataId, user.getId());
        return ResponseEntity.ok(data);
    }
}