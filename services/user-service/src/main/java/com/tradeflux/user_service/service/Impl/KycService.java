package com.tradeflux.user_service.service.Impl;

import com.tradeflux.user_service.dtos.AdminKycReviewRequest;
import com.tradeflux.user_service.dtos.KycDetailsResponse;
import com.tradeflux.user_service.dtos.KycStatusResponse;
import com.tradeflux.user_service.dtos.SubmitKycRequest;

import java.util.List;

public interface KycService {


    void submitKyc(SubmitKycRequest request);
    KycStatusResponse getKycStatus();
    KycDetailsResponse getKycDetails();

    void reviewKyc(Long authUserId, AdminKycReviewRequest request);

    List<KycDetailsResponse> getKycByStatus(String status);

}
