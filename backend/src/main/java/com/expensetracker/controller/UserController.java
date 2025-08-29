package com.expensetracker.controller;

import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "User context management APIs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current-family")
    @Operation(summary = "Get current family context", description = "Get the user's current family context")
    public ResponseEntity<CurrentFamilyResponse> getCurrentFamily(@AuthenticationPrincipal UserPrincipal user) {
        String familyId = userService.getCurrentUserFamilyId(user.getId());
        return ResponseEntity.ok(new CurrentFamilyResponse(familyId));
    }

    @PostMapping("/set-preferred-family")
    @Operation(summary = "Set preferred family", description = "Set the user's preferred family for operations")
    public ResponseEntity<Void> setPreferredFamily(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody SetPreferredFamilyRequest request) {
        userService.setUserPreferredFamily(user.getId(), request.getFamilyId());
        return ResponseEntity.ok().build();
    }

    // DTOs
    public static class CurrentFamilyResponse {
        private String familyId;

        public CurrentFamilyResponse(String familyId) {
            this.familyId = familyId;
        }

        public String getFamilyId() { return familyId; }
        public void setFamilyId(String familyId) { this.familyId = familyId; }
    }

    public static class SetPreferredFamilyRequest {
        private String familyId;

        public String getFamilyId() { return familyId; }
        public void setFamilyId(String familyId) { this.familyId = familyId; }
    }
}