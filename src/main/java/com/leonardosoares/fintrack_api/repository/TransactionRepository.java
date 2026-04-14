package com.leonardosoares.fintrack_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leonardosoares.fintrack_api.model.Transaction;
import com.leonardosoares.fintrack_api.model.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByUser(User user);    
}
