package com.ntigra.riayati_middleware.respository;

import com.ntigra.riayati_middleware.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository
        extends JpaRepository<TransactionHistory, Long> {
}