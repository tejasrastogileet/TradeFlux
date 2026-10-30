package com.tradeflux.user_service.dtos;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileRequest {

        @Size(max = 50)
        private String firstName;

        @Size(max = 50)
        private String lastName;

        @Size(max = 1000)
        private String bio;

        @Size(max = 100)
        private String tradingExperience;

        @Size(max = 100)
        private String riskProfile;
}