package com.tradeflux.wallet_service.service;

import com.tradeflux.wallet_service.dtos.*;

public interface WalletService {

    CreateWalletResponse createWallet();

    WalletResponse getMyWallet();

    AddFundsResponse addFunds(AddFundsRequest request);

    WithdrawFundsResponse  withdrawFunds(WithdrawFundsRequest request);

    WalletTransactionPageResponse getTransactionHistory(
            int page, int size
    );

    ReserveFundsResponse reserveFunds(
            ReserveFundsRequest request
    );

    ReleaseFundsResponse releaseFunds(
            ReleaseFundsRequest request
    );

    ConsumeFundsResponse consumeFunds(
            ConsumeFundsRequest request
    );
}
