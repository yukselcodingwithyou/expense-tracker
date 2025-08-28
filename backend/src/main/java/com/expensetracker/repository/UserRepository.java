package com.expensetracker.repository;

import com.expensetracker.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    
    boolean existsByEmailAndDeletedAtIsNull(String email);
}