package com.expensetracker.repository;

import com.expensetracker.domain.Attachment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends MongoRepository<Attachment, String> {
    List<Attachment> findByLedgerEntryId(String ledgerEntryId);
    void deleteByLedgerEntryId(String ledgerEntryId);
}