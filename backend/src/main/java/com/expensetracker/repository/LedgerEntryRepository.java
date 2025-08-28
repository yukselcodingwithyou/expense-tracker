package com.expensetracker.repository;

import com.expensetracker.domain.LedgerEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LedgerEntryRepository extends MongoRepository<LedgerEntry, String> {
    Optional<LedgerEntry> findByIdAndFamilyIdAndDeletedAtIsNull(String id, String familyId);
    
    Page<LedgerEntry> findByFamilyIdAndDeletedAtIsNullOrderByOccurredAtDesc(String familyId, Pageable pageable);
    
    Page<LedgerEntry> findByFamilyIdAndTypeAndDeletedAtIsNullOrderByOccurredAtDesc(String familyId, LedgerEntry.TransactionType type, Pageable pageable);
    
    @Query("{'familyId': ?0, 'occurredAt': {'$gte': ?1, '$lte': ?2}, 'deletedAt': null}")
    List<LedgerEntry> findByFamilyIdAndOccurredAtBetweenAndDeletedAtIsNull(String familyId, Instant from, Instant to);
    
    @Query("{'familyId': ?0, 'categoryId': ?1, 'occurredAt': {'$gte': ?2, '$lte': ?3}, 'deletedAt': null}")
    List<LedgerEntry> findByFamilyIdAndCategoryIdAndOccurredAtBetweenAndDeletedAtIsNull(String familyId, String categoryId, Instant from, Instant to);
    
    List<LedgerEntry> findTop5ByFamilyIdAndDeletedAtIsNullOrderByOccurredAtDesc(String familyId);
}