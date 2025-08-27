package com.expensetracker.controller;

import com.expensetracker.domain.LedgerEntry;
import com.expensetracker.dto.ledger.CreateLedgerEntryRequest;
import com.expensetracker.dto.ledger.LedgerEntryResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ledger")
@Tag(name = "Ledger", description = "Transaction ledger management APIs")
@Validated
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping
    @Operation(summary = "Create ledger entry", description = "Create a new transaction entry")
    public ResponseEntity<LedgerEntryResponse> createEntry(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateLedgerEntryRequest request) {
        LedgerEntryResponse entry = ledgerService.createEntry(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    @GetMapping
    @Operation(summary = "Get ledger entries", description = "Get paginated transaction entries with filters")
    public ResponseEntity<Page<LedgerEntryResponse>> getEntries(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) LedgerEntry.TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<LedgerEntryResponse> entries = ledgerService.getEntries(user, type, from, to, categoryId, memberId, page, size);
        return ResponseEntity.ok(entries);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update ledger entry", description = "Update an existing transaction entry")
    public ResponseEntity<LedgerEntryResponse> updateEntry(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id,
            @Valid @RequestBody CreateLedgerEntryRequest request) {
        LedgerEntryResponse entry = ledgerService.updateEntry(user, id, request);
        return ResponseEntity.ok(entry);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ledger entry", description = "Delete a transaction entry (soft delete)")
    public ResponseEntity<Void> deleteEntry(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id) {
        ledgerService.deleteEntry(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent entries", description = "Get the 5 most recent transaction entries")
    public ResponseEntity<List<LedgerEntryResponse>> getRecentEntries(
            @AuthenticationPrincipal UserPrincipal user) {
        List<LedgerEntryResponse> entries = ledgerService.getRecentEntries(user);
        return ResponseEntity.ok(entries);
    }
}