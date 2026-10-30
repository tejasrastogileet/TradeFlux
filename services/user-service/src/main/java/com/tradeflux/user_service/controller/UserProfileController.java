package com.tradeflux.user_service.controller;

import com.tradeflux.user_service.dtos.UpdateUserProfileRequest;
import com.tradeflux.user_service.dtos.UserProfileResponse;
import com.tradeflux.user_service.dtos.UserProfileSummaryResponse;
import com.tradeflux.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(){
        log.info("Received request to fetch current user profile");
        return ResponseEntity.ok(userProfileService.getCurrentUserProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request){
        log.info("Received request to update current user profile");

        return ResponseEntity.ok(userProfileService.updateCurrentUserProfile(request));
    }


    @GetMapping("/{username}")
    public ResponseEntity<UserProfileSummaryResponse> getUserByUsername(@PathVariable String username){

        log.info("Received request to fetch by username: {}",username);
        return ResponseEntity.ok(userProfileService.getUserByUsername(username));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserProfileSummaryResponse>>
    searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "10")
            int size
    ) {
        log.info(
                "Received search request with keyword: {}",
                keyword
        );
        return ResponseEntity.ok(
                userProfileService.searchUsers(
                        keyword,
                        page,
                        size
                )
        );
    }
}
