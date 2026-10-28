package com.tradeflux.user_service.repository;

import com.tradeflux.user_service.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {


    Optional<UserProfile> findByAuthUserId(Long authUserId);

    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByAuthUserId(Long authUserId);

    Page<UserProfile> findByUsernameContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );
}
