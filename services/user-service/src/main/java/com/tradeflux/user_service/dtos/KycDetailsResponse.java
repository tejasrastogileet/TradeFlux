package com.tradeflux.user_service.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDetailsResponse {

    private Long id;
    private Long authUserId;
    private String fullName;
    private String panNumber;
    private String aadhaarNumber;
    private LocalDate dateOfBirth;
    private String address;
    private String status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}