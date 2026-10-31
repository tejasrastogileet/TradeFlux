package com.tradeflux.wallet_service.repository;

import com.tradeflux.wallet_service.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       SELECT w
       FROM Wallet w
       WHERE w.userId = :userId
       """)
    Optional<Wallet> findByUserIdForUpdate(
            @Param("userId") Long userId
    );


    Optional<Wallet> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
