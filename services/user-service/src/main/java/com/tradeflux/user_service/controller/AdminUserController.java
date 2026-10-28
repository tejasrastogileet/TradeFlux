package com.tradeflux.user_service.controller;


import com.tradeflux.user_service.auth.RoleAllowed;
import com.tradeflux.user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
@RoleAllowed({"ADMIN"})
public class AdminUserController {

    private final UserProfileService userProfileService;

    @PutMapping("/{authUserId}/deactivate")
    public ResponseEntity<String>
    deactivateUser(
            @PathVariable Long authUserId
    ) {

        log.warn(
                "Received admin request to deactivate user: {}",
                authUserId
        );
        userProfileService.deactivateUser(authUserId);
        return ResponseEntity.ok(
                "User deactivated successfully"
        );
    }

    @PutMapping("/{authUserId}/activate")
    public ResponseEntity<String>
    activateUser(
            @PathVariable Long authUserId
    ) {
        log.warn(
                "Received admin request to activate user: {}",
                authUserId
        );
        userProfileService.activateUser(authUserId);
        return ResponseEntity.ok(
                "User activated successfully"
        );
    }
}
