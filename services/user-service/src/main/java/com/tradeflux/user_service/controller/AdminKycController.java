package com.tradeflux.user_service.controller;


import com.tradeflux.user_service.auth.RoleAllowed;
import com.tradeflux.user_service.dtos.AdminKycReviewRequest;
import com.tradeflux.user_service.dtos.KycDetailsResponse;
import com.tradeflux.user_service.service.Impl.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/admin/kyc")
@RequiredArgsConstructor
@Slf4j
@RoleAllowed({"ADMIN"})
public class AdminKycController {


    private final KycService kycService;

    @PutMapping("/{authUserId}/review")
    public ResponseEntity<String> reviewKyc(@PathVariable Long authUserId,
            @Valid @RequestBody AdminKycReviewRequest request  ) {

        log.warn( "Received admin KYC review request for userId: {}",authUserId);
        kycService.reviewKyc(authUserId, request);
        return ResponseEntity.ok(
                "KYC reviewed successfully"
        );
    }

    @GetMapping
    public ResponseEntity<List<KycDetailsResponse>> getKycByStatus(@RequestParam String status){

        log.info("Received request to fetch KYC records with status: {}",status);
        return ResponseEntity.ok(kycService.getKycByStatus(status));
    }
}
