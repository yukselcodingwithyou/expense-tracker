package com.expensetracker.controller;

import com.expensetracker.domain.LedgerEntry;
import com.expensetracker.domain.VoiceExpenseData;
import com.expensetracker.dto.ledger.LedgerEntryResponse;
import com.expensetracker.dto.voice.CreateExpenseFromVoiceRequest;
import com.expensetracker.dto.voice.VoiceExpenseRequest;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.VoiceExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/voice")
@Tag(name = "Voice Expense", description = "Voice expense processing and creation APIs")
public class VoiceExpenseController {

    private final VoiceExpenseService voiceExpenseService;

    public VoiceExpenseController(VoiceExpenseService voiceExpenseService) {
        this.voiceExpenseService = voiceExpenseService;
    }

    @PostMapping("/process")
    @Operation(summary = "Process voice expense", description = "Process spoken text to extract expense information")
    public ResponseEntity<VoiceExpenseData> processVoiceExpense(
            @RequestBody VoiceExpenseRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        
        VoiceExpenseData data = voiceExpenseService.processVoiceExpense(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    @PostMapping("/create-expense")
    @Operation(summary = "Create expense from voice", description = "Create a ledger entry from processed voice data")
    public ResponseEntity<LedgerEntryResponse> createExpenseFromVoice(
            @RequestBody CreateExpenseFromVoiceRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        
        LedgerEntryResponse entry = voiceExpenseService.createExpenseFromVoice(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    @GetMapping
    @Operation(summary = "Get user voice expenses", description = "Get all processed voice expenses for the current user")
    public ResponseEntity<List<VoiceExpenseData>> getUserVoiceExpenses(
            @AuthenticationPrincipal UserPrincipal user) {
        
        List<VoiceExpenseData> voiceExpenses = voiceExpenseService.getUserVoiceExpenseData(user.getId());
        return ResponseEntity.ok(voiceExpenses);
    }

    @GetMapping("/{voiceExpenseDataId}")
    @Operation(summary = "Get voice expense data", description = "Get specific voice expense data by ID")
    public ResponseEntity<VoiceExpenseData> getVoiceExpenseData(
            @PathVariable String voiceExpenseDataId,
            @AuthenticationPrincipal UserPrincipal user) {
        
        VoiceExpenseData data = voiceExpenseService.getVoiceExpenseData(voiceExpenseDataId, user.getId());
        return ResponseEntity.ok(data);
    }
}