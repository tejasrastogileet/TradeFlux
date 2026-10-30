package com.tradeflux.user_service.service.Impl;

import com.tradeflux.user_service.auth.AuthenticatedUser;
import com.tradeflux.user_service.auth.UserContextHolder;
import com.tradeflux.user_service.dtos.AdminKycReviewRequest;
import com.tradeflux.user_service.dtos.KycDetailsResponse;
import com.tradeflux.user_service.dtos.KycStatusResponse;
import com.tradeflux.user_service.dtos.SubmitKycRequest;
import com.tradeflux.user_service.entity.KycDetails;
import com.tradeflux.user_service.enums.KycStatus;
import com.tradeflux.user_service.exceptions.BadRequestException;
import com.tradeflux.user_service.exceptions.ResourceNotFoundException;
import com.tradeflux.user_service.repository.KycDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class KycServiceImpl implements KycService{

    private final KycDetailsRepository kycDetailsRepository;
    private final ModelMapper modelMapper;


    @Override
    public void submitKyc(SubmitKycRequest request) {

        AuthenticatedUser currentUser = getAuthenticatedUser();
        log.info("Submitting KYC for userId : {}",currentUser.getUserId());

        if(kycDetailsRepository.findByAuthUserId(currentUser.getUserId())
                .isPresent()) {
            throw new BadRequestException("KYC already exists");
        }
            if(kycDetailsRepository.existsByPanNumber(request.getPanNumber())){
                throw new BadRequestException("PAN number already exists");
        }

            if(kycDetailsRepository.existsByAadhaarNumber(request.getAadhaarNumber())){
                throw new BadRequestException("AADHAR number already exists");
            }


         KycDetails kycDetails = modelMapper.map(request, KycDetails.class);
         kycDetails.setAuthUserId(currentUser.getUserId());

         kycDetailsRepository.save(kycDetails);
         log.info("KYC submitted successfully for userId: {}",currentUser.getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public KycStatusResponse getKycStatus() {


        AuthenticatedUser currentUser = getAuthenticatedUser();

        log.info("Fetching KYC status for userId: {}",currentUser.getUserId());

        KycDetails kycDetails = kycDetailsRepository.findByAuthUserId(currentUser.getUserId())
                .orElse(null);

        if(kycDetails == null){
            return KycStatusResponse.builder().status(KycStatus.NOT_SUBMITTED.name())
                    .build();
        }

        return KycStatusResponse.builder()
                .status(kycDetails.getStatus().name())
                .rejectionReason(kycDetails.getRejectionReason())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public KycDetailsResponse getKycDetails() {

        AuthenticatedUser currentUser = getAuthenticatedUser();
        log.info("Fetching KYC details for userId: {}",currentUser.getUserId());

        KycDetails kycDetails = kycDetailsRepository.findByAuthUserId(currentUser.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("KYC details not found"));

        return modelMapper.map(kycDetails, KycDetailsResponse.class);
    }

    @Override
    public void reviewKyc(Long authUserId, AdminKycReviewRequest request) {

        log.info("Reviewing KYC for userId: {}",authUserId);

        KycDetails kycDetails = kycDetailsRepository.findByAuthUserId(authUserId)
                .orElseThrow(()-> new ResourceNotFoundException("KYC details not found"));

        KycStatus status = KycStatus.valueOf(request.getStatus().toUpperCase());
        kycDetails.setStatus(status);

        if(status == KycStatus.REJECTED){
            kycDetails.setRejectionReason(request.getRejectionReason());
        }
        kycDetailsRepository.save(kycDetails);
        log.info("KYC reviewed successfully for userId: {}",authUserId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<KycDetailsResponse> getKycByStatus(

            String status
    ) {

        log.info("Fetching KYC records with status: {}",status);
        KycStatus kycStatus =KycStatus.valueOf(status.toUpperCase());

        List<KycDetails> kycDetails =kycDetailsRepository.findByStatus(kycStatus);
        return kycDetails.stream()
                .map(kyc ->

                        modelMapper.map(
                                kyc,
                                KycDetailsResponse.class
                        )
                )
                .toList();
    }


    private AuthenticatedUser getAuthenticatedUser() {

        AuthenticatedUser currentUser =  UserContextHolder.getCurrentUser();
        if (currentUser == null) {
            throw new BadRequestException(
                    "Authenticated user not found"
            );
        }
        return currentUser;
    }
}
