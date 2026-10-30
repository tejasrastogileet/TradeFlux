package com.tradeflux.user_service.entity;


import com.tradeflux.user_service.enums.KycStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "kyc_details",
        uniqueConstraints = {

                @UniqueConstraint(
                        name = "uk_kyc_auth_user_id",
                        columnNames = "auth_user_id"
                ),

                @UniqueConstraint(
                        name = "uk_kyc_pan_number",
                        columnNames = "pan_number"
                ),

                @UniqueConstraint(
                        name = "uk_kyc_aadhaar_number",
                        columnNames = "aadhaar_number"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long authUserId;


    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String panNumber;

    @Column(nullable = false, unique = true)
    private String aadhaarNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 1000)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus status;

    @Column(length = 1000)
    private String rejectionReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = KycStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
