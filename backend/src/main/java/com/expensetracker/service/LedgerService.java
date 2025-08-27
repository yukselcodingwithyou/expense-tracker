package com.expensetracker.service;

import com.expensetracker.domain.Category;
import com.expensetracker.domain.LedgerEntry;
import com.expensetracker.dto.ledger.CreateLedgerEntryRequest;
import com.expensetracker.dto.ledger.LedgerEntryResponse;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.LedgerEntryRepository;
import com.expensetracker.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public LedgerService(LedgerEntryRepository ledgerEntryRepository, CategoryRepository categoryRepository, UserService userService) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public LedgerEntryResponse createEntry(UserPrincipal user, CreateLedgerEntryRequest request) {
        String familyId = userService.getCurrentUserFamilyId(user.getId());
        
        // Validate category exists and belongs to family
        Category category = categoryRepository.findByIdAndFamilyIdAndDeletedAtIsNull(request.getCategoryId(), familyId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        LedgerEntry.MoneyAmount amount = new LedgerEntry.MoneyAmount(request.getAmountMinor(), request.getCurrency());
        
        LedgerEntry entry = new LedgerEntry(
                familyId,
                request.getMemberId(),
                request.getType(),
                amount,
                request.getCategoryId(),
                request.getOccurredAt()
        );
        
        entry.setNotes(request.getNotes());
        entry.setAttachments(request.getAttachments());
        
        entry = ledgerEntryRepository.save(entry);
        return toResponse(entry, category.getName());
    }

    public Page<LedgerEntryResponse> getEntries(UserPrincipal user, LedgerEntry.TransactionType type, 
                                              Instant from, Instant to, String categoryId, 
                                              String memberId, int page, int size) {
        String familyId = userService.getCurrentUserFamilyId(user.getId());
        Pageable pageable = PageRequest.of(page, size);
        
        Page<LedgerEntry> entries;
        if (type != null) {
            entries = ledgerEntryRepository.findByFamilyIdAndTypeAndDeletedAtIsNullOrderByOccurredAtDesc(familyId, type, pageable);
        } else {
            entries = ledgerEntryRepository.findByFamilyIdAndDeletedAtIsNullOrderByOccurredAtDesc(familyId, pageable);
        }
        
        return entries.map(entry -> {
            String categoryName = getCategoryName(entry.getCategoryId());
            return toResponse(entry, categoryName);
        });
    }

    public LedgerEntryResponse updateEntry(UserPrincipal user, String entryId, CreateLedgerEntryRequest request) {
        String familyId = userService.getCurrentUserFamilyId(user.getId());
        
        LedgerEntry entry = ledgerEntryRepository.findByIdAndFamilyIdAndDeletedAtIsNull(entryId, familyId)
                .orElseThrow(() -> new RuntimeException("Ledger entry not found"));
        
        // Validate category exists and belongs to family
        Category category = categoryRepository.findByIdAndFamilyIdAndDeletedAtIsNull(request.getCategoryId(), familyId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        LedgerEntry.MoneyAmount amount = new LedgerEntry.MoneyAmount(request.getAmountMinor(), request.getCurrency());
        
        entry.setMemberId(request.getMemberId());
        entry.setType(request.getType());
        entry.setAmount(amount);
        entry.setCategoryId(request.getCategoryId());
        entry.setOccurredAt(request.getOccurredAt());
        entry.setNotes(request.getNotes());
        entry.setAttachments(request.getAttachments());
        entry.setUpdatedAt(Instant.now());
        
        entry = ledgerEntryRepository.save(entry);
        return toResponse(entry, category.getName());
    }

    public void deleteEntry(UserPrincipal user, String entryId) {
        String familyId = userService.getCurrentUserFamilyId(user.getId());
        
        LedgerEntry entry = ledgerEntryRepository.findByIdAndFamilyIdAndDeletedAtIsNull(entryId, familyId)
                .orElseThrow(() -> new RuntimeException("Ledger entry not found"));
        
        entry.setDeletedAt(Instant.now());
        ledgerEntryRepository.save(entry);
    }

    public List<LedgerEntryResponse> getRecentEntries(UserPrincipal user) {
        String familyId = userService.getCurrentUserFamilyId(user.getId());
        
        List<LedgerEntry> entries = ledgerEntryRepository.findTop5ByFamilyIdAndDeletedAtIsNullOrderByOccurredAtDesc(familyId);
        
        return entries.stream()
                .map(entry -> {
                    String categoryName = getCategoryName(entry.getCategoryId());
                    return toResponse(entry, categoryName);
                })
                .collect(Collectors.toList());
    }

    private String getCategoryName(String categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        return category.map(Category::getName).orElse("Unknown");
    }

    private LedgerEntryResponse toResponse(LedgerEntry entry, String categoryName) {
        LedgerEntryResponse response = new LedgerEntryResponse();
        response.setId(entry.getId());
        response.setMemberId(entry.getMemberId());
        response.setType(entry.getType());
        response.setAmountMinor(entry.getAmount().getMinor());
        response.setCurrency(entry.getAmount().getCurrency());
        response.setCategoryId(entry.getCategoryId());
        response.setCategoryName(categoryName);
        response.setOccurredAt(entry.getOccurredAt());
        response.setNotes(entry.getNotes());
        response.setAttachments(entry.getAttachments());
        response.setRecurringId(entry.getRecurringId());
        response.setCreatedAt(entry.getCreatedAt());
        response.setUpdatedAt(entry.getUpdatedAt());
        return response;
    }
}