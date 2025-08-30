package com.expensetracker.repository;

import com.expensetracker.domain.VoiceExpenseData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceExpenseDataRepository extends MongoRepository<VoiceExpenseData, String> {
    List<VoiceExpenseData> findByUserId(String userId);
    List<VoiceExpenseData> findByUserIdOrderByCreatedAtDesc(String userId);
}