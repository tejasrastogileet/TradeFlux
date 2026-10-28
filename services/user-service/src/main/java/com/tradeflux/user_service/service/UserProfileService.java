package com.tradeflux.user_service.service;

import com.tradeflux.user_service.dtos.UpdateUserProfileRequest;
import com.tradeflux.user_service.dtos.UserProfileResponse;
import com.tradeflux.user_service.dtos.UserProfileSummaryResponse;
import org.springframework.data.domain.Page;


public interface UserProfileService {


    UserProfileResponse getCurrentUserProfile();

    UserProfileResponse updateCurrentUserProfile(UpdateUserProfileRequest request);

    UserProfileSummaryResponse getUserByUsername(String username);

    Page<UserProfileSummaryResponse> searchUsers(
            String keyword,
            int page,
            int size
    );

    void deactivateUser(Long authUserId);;

    void activateUser(Long authUserId);


}
