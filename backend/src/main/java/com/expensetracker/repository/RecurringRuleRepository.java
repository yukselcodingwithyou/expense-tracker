package com.expensetracker.repository;

import com.expensetracker.domain.RecurringRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface RecurringRuleRepository extends MongoRepository<RecurringRule, String> {
    
    List<RecurringRule> findByFamilyIdAndIsPausedFalse(String familyId);
    
    List<RecurringRule> findByFamilyId(String familyId);
    
    @Query("{ 'nextRunAt' : { $lte : ?0 }, 'isPaused' : false }")
    List<RecurringRule> findDueRules(Instant currentTime);
    
    @Query("{ 'familyId' : ?0, 'isPaused' : false, 'nextRunAt' : { $lte : ?1 } }")
    List<RecurringRule> findDueRulesByFamily(String familyId, Instant currentTime);
}