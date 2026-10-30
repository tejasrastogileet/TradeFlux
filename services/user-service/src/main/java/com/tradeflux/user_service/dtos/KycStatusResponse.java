package com.tradeflux.user_service.dtos;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KycStatusResponse {


    private String status;
    private String rejectionReason;
}
