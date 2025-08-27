package com.expensetracker.repository;

import com.expensetracker.domain.Family;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyRepository extends MongoRepository<Family, String> {
    Optional<Family> findByIdAndDeletedAtIsNull(String id);
}