package com.leonardosoares.fintrack_api.spec;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.leonardosoares.fintrack_api.model.Transaction;
import com.leonardosoares.fintrack_api.model.User;
import com.leonardosoares.fintrack_api.model.enums.TransactionType;

public class TransactionSpecification {

    public static Specification<Transaction> hasUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }

    public static Specification<Transaction> hasType(TransactionType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<Transaction> hasCategoryId(UUID categoryId) {
        return (root, query, cb) -> 
        categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }
    
}
