package com.tradeflux.user_service.service.Impl;

import com.tradeflux.user_service.auth.AuthenticatedUser;
import com.tradeflux.user_service.auth.UserContextHolder;
import com.tradeflux.user_service.dtos.UpdateUserProfileRequest;
import com.tradeflux.user_service.dtos.UserProfileResponse;
import com.tradeflux.user_service.dtos.UserProfileSummaryResponse;
import com.tradeflux.user_service.entity.UserProfile;
import com.tradeflux.user_service.exceptions.BadRequestException;
import com.tradeflux.user_service.exceptions.ResourceNotFoundException;
import com.tradeflux.user_service.repository.UserProfileRepository;
import com.tradeflux.user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {

        AuthenticatedUser currentUser = getAuthenticatedUser();
        log.info("Fetching user profile for userId: {}",currentUser.getUserId());;

        UserProfile profile = userProfileRepository.findByAuthUserId(currentUser.getUserId())
                .orElseThrow(()->{
                    log.error("Profile not found for userId: {}",currentUser.getUserId());

                    return new ResourceNotFoundException("User profile not found");
                });

        return modelMapper.map(profile, UserProfileResponse.class);
    }

    @Override
    public UserProfileResponse updateCurrentUserProfile(UpdateUserProfileRequest request) {

        AuthenticatedUser currentUser = getAuthenticatedUser();
        log.info("Updating profile for userId: {}",currentUser.getUserId());

        UserProfile profile = userProfileRepository.findByAuthUserId(currentUser.getUserId())
                .orElseThrow(()-> {
                    log.error("Profile not found for userId: {}",currentUser.getUserId());

                    return new ResourceNotFoundException("Profile not found");
                });

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setBio(request.getBio());
        profile.setTradingExperience(request.getTradingExperience());
        profile.setRiskProfile(request.getRiskProfile());

        UserProfile updatedProfile = userProfileRepository.save(profile);

        log.info("Profile updated successfully for userId: {}",currentUser.getUserId());
        return modelMapper.map(updatedProfile, UserProfileResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileSummaryResponse getUserByUsername(String username) {


        log.info("Fetching user Profile by username: {}",username);

        UserProfile profile = userProfileRepository.findByUsername(username)
                .orElseThrow(()->{
                    log.error("User Profile not found for username: {}",username);
                    return new ResourceNotFoundException("User profile not found");
                });

        return modelMapper.map(profile, UserProfileSummaryResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileSummaryResponse> searchUsers(String keyword, int page, int size) {


        log.info("Searching user with keyword: {}, page: {}, size: {}",keyword, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());

        Page<UserProfile> users = userProfileRepository.findByUsernameContainingIgnoreCase(keyword, pageable);
        return users.map(user->
                modelMapper.map(user, UserProfileSummaryResponse.class));
    }

    @Override
    public void deactivateUser(Long authUserId) {

        log.warn("deactivating user with authUserId: {}",authUserId);
        UserProfile profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(()->
                        new ResourceNotFoundException("User profile not found"));

        if(!profile.getActive()){
            throw new BadRequestException("User already deactivated");
        }
        profile.setActive(false);
        userProfileRepository.save(profile);

        log.warn("User deactivated successfully: {}",authUserId);
    }

    @Override
    public void activateUser(Long authUserId) {

        log.warn("Activating user with authUserId: {}",authUserId);
        UserProfile profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(()->
                        new ResourceNotFoundException("User profile not found"));

        if(profile.getActive()){
            throw new BadRequestException("User already active");
        }
        profile.setActive(true);
        userProfileRepository.save(profile);

        log.info("User activated successfully: {}",authUserId);
    }

    private AuthenticatedUser getAuthenticatedUser(){
        AuthenticatedUser currentUser = UserContextHolder.getCurrentUser();

        if (currentUser == null){
            log.error("No authenticated user found in context");
            throw new BadRequestException("Authenticated user not found");
        }

        return currentUser;
    }

}
