package com.ntigra.riayati_middleware.controller;

import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.dto.request.ClaimRequestDto;
import com.ntigra.riayati_middleware.dto.request.EligibilityRequestDto;
import com.ntigra.riayati_middleware.dto.response.AuthorizationResponseDto;
import com.ntigra.riayati_middleware.dto.response.ClaimSubmissionResponseDto;
import com.ntigra.riayati_middleware.dto.response.EligibilityResponseDto;
import com.ntigra.riayati_middleware.service.Authorization.AuthorizationService;
import com.ntigra.riayati_middleware.service.Claim.ClaimService;
import com.ntigra.riayati_middleware.service.Eligibility.EligibilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/riayati")
@RequiredArgsConstructor
public class RiayatiController {

    private final ClaimService claimService;
    private final EligibilityService eligibilityService;
    private final AuthorizationService authorizationService;

    @PostMapping("/claim")
    public ClaimSubmissionResponseDto submitClaim(@RequestBody ClaimRequestDto request) {
        log.info("Received claim request for: {}", request.getClaimId());
        return claimService.submitClaim(request);
    }

    @PostMapping("/eligibility")
    public EligibilityResponseDto checkEligibility(@RequestBody EligibilityRequestDto request) {
        log.info("Received eligibility check request for member: {}", request.getMemberId());
        return eligibilityService.checkEligibility(request);
    }

    @GetMapping("/eligibility/status/{transactionId}")
    public EligibilityResponseDto getEligibilityStatus(@PathVariable String transactionId) {
        log.info("Getting eligibility status for: {}", transactionId);
        // Implement status check
        return EligibilityResponseDto.builder()
                .success(true)
                .message("Status retrieved")
                .build();
    }

    @PostMapping("/authorization")
    public AuthorizationResponseDto sendAuthorization(@RequestBody AuthorizationRequestDto request) {
        return authorizationService.sendAuthorization(request);
    }

    @GetMapping("/authorization/status/{transactionId}")
    public AuthorizationResponseDto getAuthorizationStatus(@PathVariable String transactionId) {
        // Implementation
        return AuthorizationResponseDto.builder().success(true).build();
    }

}