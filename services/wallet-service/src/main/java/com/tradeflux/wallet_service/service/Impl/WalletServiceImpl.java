package com.tradeflux.wallet_service.service.Impl;


import com.tradeflux.wallet_service.auth.AuthenticatedUser;
import com.tradeflux.wallet_service.auth.UserContextHolder;
import com.tradeflux.wallet_service.dtos.*;
import com.tradeflux.wallet_service.entity.Wallet;
import com.tradeflux.wallet_service.entity.WalletTransaction;
import com.tradeflux.wallet_service.enums.Currency;
import com.tradeflux.wallet_service.enums.TransactionSource;
import com.tradeflux.wallet_service.enums.TransactionStatus;
import com.tradeflux.wallet_service.enums.TransactionType;
import com.tradeflux.wallet_service.exceptions.*;
import com.tradeflux.wallet_service.repository.WalletRepository;
import com.tradeflux.wallet_service.repository.WalletTransactionRepository;
import com.tradeflux.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public CreateWalletResponse createWallet() {

        AuthenticatedUser currentUser = getAuthenticatedUser();

        log.info("Creating wallet for userId: {}",currentUser.getUserId());

        if(walletRepository.existsByUserId(currentUser.getUserId())) {
            throw new WalletAlreadyExistsException("Wallet already exists");
        }

        Wallet wallet = Wallet.builder()
                .userId(currentUser.getUserId())
                .balance(BigDecimal.ZERO)
                .reservedBalance(BigDecimal.ZERO)
                .currency(Currency.INR)
                .build();

        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet created successfully, walletId: {}",savedWallet.getId());

        return CreateWalletResponse.builder()
                .walletId(savedWallet.getId())
                .userId(savedWallet.getUserId())
                .message("Wallet created successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getMyWallet() {
        AuthenticatedUser currentUser = getAuthenticatedUser();

        log.info("Fetching wallet for userId: {}",currentUser.getUserId());

        Wallet wallet = walletRepository.findByUserId(currentUser.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Wallet not found"
                                ));

        return WalletResponse.builder()
                .walletId(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .reservedBalance(wallet.getReservedBalance())
                .currency(wallet.getCurrency())
                .build();

}

    @Override
    @Transactional
    public AddFundsResponse addFunds(AddFundsRequest request) {
        AuthenticatedUser currentUser = getAuthenticatedUser();

        Wallet wallet = walletRepository.findByUserIdForUpdate(currentUser.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Wallet not found"
                                ));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        walletRepository.save(wallet);

        WalletTransaction transaction =
                WalletTransaction.builder()
                        .wallet(wallet)
                        .userId(currentUser.getUserId())
                        .amount(request.getAmount())
                        .type(TransactionType.CREDIT)
                        .status(TransactionStatus.SUCCESS)
                        .source(TransactionSource.ADD_FUNDS)
                        .description("Funds added to wallet")
                        .build();

        transaction = transactionRepository.save(transaction);

        log.info(
                "Added funds. UserId: {}, Amount: {}",
                currentUser.getUserId(),
                request.getAmount()
        );

        return AddFundsResponse.builder()
                .transactionId(transaction.getId())
                .amountAdded(request.getAmount())
                .updatedBalance(wallet.getBalance())
                .message("Funds added successfully")
                .build();
    }

    @Override
    @Transactional
    public WithdrawFundsResponse withdrawFunds(WithdrawFundsRequest request) {
        AuthenticatedUser currentUser = getAuthenticatedUser();


        Wallet wallet = walletRepository.findByUserIdForUpdate(currentUser.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Wallet not found"
                                ));

        if (wallet.getBalance()
                .compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient wallet balance"
            );
        }
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));

        walletRepository.save(wallet);

        WalletTransaction transaction =
                WalletTransaction.builder()
                        .wallet(wallet)
                        .userId(currentUser.getUserId())
                        .amount(request.getAmount())
                        .type(TransactionType.DEBIT)
                        .status(TransactionStatus.SUCCESS)
                        .source(TransactionSource.WITHDRAWAL)
                        .description("Funds withdrawn")
                        .build();

        transaction = transactionRepository.save(transaction);
        log.info(
                "Funds withdrawn. UserId: {}, Amount: {}",
                currentUser.getUserId(),
                request.getAmount()
        );

        return WithdrawFundsResponse.builder()
                .transactionId(transaction.getId())
                .withdrawnAmount(request.getAmount())
                .updatedBalance(wallet.getBalance())
                .message("Funds withdrawn successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public WalletTransactionPageResponse getTransactionHistory(int page, int size) {
        AuthenticatedUser currentUser = getAuthenticatedUser();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending()
                );

        Page<WalletTransaction> transactions =
                transactionRepository.findByUserId(
                        currentUser.getUserId(),
                        pageable
                );

        List<TransactionResponse> response =
                transactions.getContent()
                        .stream()
                        .map(tx -> modelMapper.map(
                                tx,
                                TransactionResponse.class
                        ))
                        .toList();

        return WalletTransactionPageResponse.builder()
                .transactions(response)
                .currentPage(transactions.getNumber())
                .totalPages(transactions.getTotalPages())
                .totalElements(transactions.getTotalElements())
                .last(transactions.isLast())
                .build();
    }

    @Override
    @Transactional
    public ReserveFundsResponse reserveFunds(ReserveFundsRequest request) {


        log.info("Reserving funds, UserId: {}, Amount: {}",request.getUserId(), request.getAmount());

        Wallet wallet=  walletRepository.findByUserIdForUpdate(request.getUserId())
                .orElseThrow(()->
                        new ResourceNotFoundException("Wallet not found"));

        BigDecimal availableBalance = wallet.getBalance()
                .subtract(wallet.getReservedBalance());

        if(availableBalance.compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient available balance");
        }

        wallet.setReservedBalance(wallet.getReservedBalance().add(request.getAmount()));

        walletRepository.save(wallet);
        log.info("Funds reserved successfully, UserId: {}",request.getUserId());

        return ReserveFundsResponse.builder()
                .userId(wallet.getUserId())
                .reservedAmount(request.getAmount())
                .availableBalance(
                        wallet.getBalance()
                                .subtract(wallet.getReservedBalance())
                )
                .reservedBalance(
                        wallet.getReservedBalance()
                )
                .message("Funds reserved successfully")
                .build();
    }

    @Override
    @Transactional
    public ReleaseFundsResponse releaseFunds(
            ReleaseFundsRequest request
    ) {

        log.info(
                "Releasing funds. UserId: {}, Amount: {}",
                request.getUserId(),
                request.getAmount()
        );

        Wallet wallet =
                walletRepository.findByUserIdForUpdate(
                                request.getUserId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Wallet not found"
                                ));

        if (wallet.getReservedBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new BadRequestException(
                    "Reserved balance is less than requested release amount"
            );
        }

        wallet.setReservedBalance(
                wallet.getReservedBalance()
                        .subtract(request.getAmount())
        );

        walletRepository.save(wallet);

        log.info(
                "Funds released successfully. UserId: {}",
                request.getUserId()
        );

        return ReleaseFundsResponse.builder()
                .userId(wallet.getUserId())
                .releasedAmount(request.getAmount())
                .availableBalance(
                        wallet.getBalance()
                                .subtract(wallet.getReservedBalance())
                )
                .reservedBalance(
                        wallet.getReservedBalance()
                )
                .message("Funds released successfully")
                .build();
    }

    @Override
    @Transactional
    public ConsumeFundsResponse consumeFunds(
            ConsumeFundsRequest request
    ) {

        log.info(
                "Consuming reserved funds. UserId: {}, Amount: {}",
                request.getUserId(),
                request.getAmount()
        );

        Wallet wallet =
                walletRepository.findByUserIdForUpdate(
                                request.getUserId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Wallet not found"
                                ));

        if (wallet.getReservedBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new BadRequestException(
                    "Insufficient reserved balance"
            );
        }

        wallet.setReservedBalance(
                wallet.getReservedBalance()
                        .subtract(request.getAmount())
        );

        wallet.setBalance(
                wallet.getBalance()
                        .subtract(request.getAmount())
        );

        walletRepository.save(wallet);

        log.info(
                "Reserved funds consumed successfully. UserId: {}",
                request.getUserId()
        );

        return ConsumeFundsResponse.builder()
                .userId(wallet.getUserId())
                .consumedAmount(request.getAmount())
                .availableBalance(
                        wallet.getBalance()
                                .subtract(wallet.getReservedBalance())
                )
                .reservedBalance(
                        wallet.getReservedBalance()
                )
                .message("Funds consumed successfully")
                .build();
    }

    private AuthenticatedUser getAuthenticatedUser() {

        AuthenticatedUser authenticatedUser =
                UserContextHolder
                        .getCurrentUser();

        if (authenticatedUser == null) {

            throw new ForbiddenException(
                    "Access denied"
            );
        }
        return authenticatedUser;
    }
}
