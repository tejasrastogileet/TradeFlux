package com.tradeflux.user_service.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;

    private Long authUserId;

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private String bio;

    private String tradingExperience;

    private String riskProfile;

    private Boolean kycVerified;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}