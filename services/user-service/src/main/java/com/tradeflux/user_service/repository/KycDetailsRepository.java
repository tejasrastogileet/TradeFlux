package com.tradeflux.user_service.repository;

import com.tradeflux.user_service.entity.KycDetails;
import com.tradeflux.user_service.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KycDetailsRepository
        extends JpaRepository<KycDetails, Long> {

    Optional<KycDetails>
    findByAuthUserId(Long authUserId);

    boolean existsByPanNumber(String panNumber);

    boolean existsByAadhaarNumber(String aadhaarNumber);

    List<KycDetails>
    findByStatus(KycStatus status);
}
