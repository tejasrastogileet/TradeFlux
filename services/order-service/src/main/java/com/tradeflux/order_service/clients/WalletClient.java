package com.tradeflux.order_service.clients;


import com.tradeflux.order_service.clients.dto.*;
import com.tradeflux.order_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "wallet-service",
        configuration = FeignConfig.class
)
public interface WalletClient {

    @PostMapping("/api/v1/wallet/reserve")
    ReserveFundsResponse reserveFunds(
            @RequestBody ReserveFundsRequest request
    );

    @PostMapping("/api/v1/wallet/release")
    ReleaseFundsResponse releaseFunds(
            @RequestBody ReleaseFundsRequest request
    );

    @PostMapping("/api/v1/wallet/consume")
    ConsumeFundsResponse consumeFunds(
            @RequestBody ConsumeFundsRequest request
    );
}