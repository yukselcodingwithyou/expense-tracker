package com.expensetracker.controller;

import com.expensetracker.dto.family.CreateFamilyRequest;
import com.expensetracker.dto.family.FamilyResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/families")
@Tag(name = "Families", description = "Family management APIs")
@Validated
public class FamilyController {

    private final FamilyService familyService;

    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @PostMapping
    @Operation(summary = "Create family", description = "Create a new family and add current user as admin")
    public ResponseEntity<FamilyResponse> createFamily(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateFamilyRequest request) {
        FamilyResponse family = familyService.createFamily(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(family);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get family", description = "Get family details by ID")
    public ResponseEntity<FamilyResponse> getFamily(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id) {
        FamilyResponse family = familyService.getFamily(user, id);
        return ResponseEntity.ok(family);
    }
}