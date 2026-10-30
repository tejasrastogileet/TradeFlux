package com.tradeflux.user_service.controller;


import com.tradeflux.user_service.dtos.KycDetailsResponse;
import com.tradeflux.user_service.dtos.KycStatusResponse;
import com.tradeflux.user_service.dtos.SubmitKycRequest;
import com.tradeflux.user_service.service.Impl.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/kyc")
@RequiredArgsConstructor
@Slf4j
public class KycController {

    private final KycService kycService;


    @PostMapping("/submit")
    public ResponseEntity<String> submitKyc(@Valid @RequestBody SubmitKycRequest request){
        log.info("Received KYC submission request");
        kycService.submitKyc(request);

        return ResponseEntity.ok("KYC submitted successfully");
    }

    @GetMapping("/status")
    public ResponseEntity<KycStatusResponse> getKycStatus(){
        log.info("Received request to fetch KYC status");
        return ResponseEntity.ok(kycService.getKycStatus());
    }

    @GetMapping("/details")
    public ResponseEntity<KycDetailsResponse> getKycDetails(){
        log.info("Received request to fetch KYC details");
        return ResponseEntity.ok(kycService.getKycDetails());
    }
}
