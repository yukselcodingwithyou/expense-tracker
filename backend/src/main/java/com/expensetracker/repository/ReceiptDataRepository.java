package com.expensetracker.repository;

import com.expensetracker.domain.ReceiptData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptDataRepository extends MongoRepository<ReceiptData, String> {
    List<ReceiptData> findByUserId(String userId);
    List<ReceiptData> findByUserIdOrderByCreatedAtDesc(String userId);
}