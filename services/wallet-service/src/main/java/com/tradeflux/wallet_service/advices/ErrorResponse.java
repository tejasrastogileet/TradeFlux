package com.tradeflux.wallet_service.advices;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private Integer status;

    private String error;

    private String message;

    private String path;
}
