package com.tradeflux.wallet_service.controller;


import com.tradeflux.wallet_service.dtos.*;
import com.tradeflux.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;


    @PostMapping("/create")
    public CreateWalletResponse createWallet() {

        log.info("Request received to create wallet");
        return walletService.createWallet();
    }

    @GetMapping("/my")
    public WalletResponse getMyWallet() {

        log.info("Request received to fetch wallet");
        return walletService.getMyWallet();
    }


    @PostMapping("/add-funds")
    public AddFundsResponse addFunds(

            @Valid
            @RequestBody
            AddFundsRequest request
    ) {
        log.info(
                "Request received to add funds. Amount: {}",
                request.getAmount()
        );

        return walletService.addFunds(request);
    }

    @PostMapping("/withdraw")
    public WithdrawFundsResponse withdrawFunds(

            @Valid
            @RequestBody
            WithdrawFundsRequest request
    ) {

        log.info(
                "Request received to withdraw funds. Amount: {}",
                request.getAmount()
        );

        return walletService.withdrawFunds(request);
    }


    @GetMapping("/transactions")
    public WalletTransactionPageResponse getTransactionHistory(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        log.info(
                "Request received to fetch wallet transactions. Page: {}, Size: {}",
                page,
                size
        );
        return walletService.getTransactionHistory(
                page,
                size
        );
    }


    @PostMapping("/reserve")
    public ReserveFundsResponse reserveFunds(

            @Valid
            @RequestBody
            ReserveFundsRequest request
    ) {

        log.info(
                "Request received to reserve funds. UserId: {}, Amount: {}",
                request.getUserId(),
                request.getAmount()
        );

        return walletService.reserveFunds(request);
    }

    @PostMapping("/release")
    public ReleaseFundsResponse releaseFunds(

            @Valid
            @RequestBody
            ReleaseFundsRequest request
    ) {

        log.info(
                "Request received to release funds. UserId: {}, Amount: {}",
                request.getUserId(),
                request.getAmount()
        );

        return walletService.releaseFunds(request);
    }

    @PostMapping("/consume")
    public ConsumeFundsResponse consumeFunds(

            @Valid
            @RequestBody
            ConsumeFundsRequest request
    ) {

        log.info(
                "Request received to consume funds. UserId: {}, Amount: {}",
                request.getUserId(),
                request.getAmount()
        );

        return walletService.consumeFunds(request);
    }
}