package com.tradeflux.wallet_service.repository;

import com.tradeflux.wallet_service.entity.WalletTransaction;
import com.tradeflux.wallet_service.enums.TransactionStatus;
import com.tradeflux.wallet_service.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {

    Page<WalletTransaction> findByUserId(
            Long userId,
            Pageable pageable
    );

    Page<WalletTransaction> findByUserIdAndType(
            Long userId,
            TransactionType type,
            Pageable pageable
    );

    Page<WalletTransaction> findByUserIdAndStatus(
            Long userId,
            TransactionStatus status,
            Pageable pageable
    );

    Page<WalletTransaction> findByWalletId(
            Long walletId,
            Pageable pageable
    );
}