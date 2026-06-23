package com.ntigra.riayati_middleware.controller;

import com.ntigra.riayati_middleware.dto.request.*;
import com.ntigra.riayati_middleware.dto.response.*;
import com.ntigra.riayati_middleware.service.Authorization.AuthorizationService;
import com.ntigra.riayati_middleware.service.Claim.ClaimService;

import com.ntigra.riayati_middleware.service.Dispense.DispenseService;
import com.ntigra.riayati_middleware.service.ERX.ErxService;
import com.ntigra.riayati_middleware.service.Eligibility.EligibilityService;
import com.ntigra.riayati_middleware.service.Penalty.PenaltyService;

import com.ntigra.riayati_middleware.service.Eligibility.EligibilityServiceOld;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/riayati")
@RequiredArgsConstructor
public class RiayatiController {

    private final ClaimService claimService;
    private final EligibilityServiceOld eligibilityService;
    private final AuthorizationService authorizationService;
    private final ErxService erxService;
    private final DispenseService dispenseService;
    private final PenaltyService penaltyService;

    @PostMapping("/claim")
    public ClaimSubmissionResponseDto submitClaim(@RequestBody ClaimRequestDto request) {
        log.info("Received claim request for: {}", request.getClaimId());
        return claimService.submitClaim(request);
    }

//    @PostMapping("/eligibility")
//    public EligibilityResponseDto checkEligibility(@RequestBody EligibilityRequestDto request) {
//        log.info("Received eligibility check request for member: {}", request.getMemberId());
//        return eligibilityService.checkEligibility(request);
//    }

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

    // ==================== ERX ====================

    @PostMapping("/erx")
    public ErxResponseDto submitErx(@RequestBody ErxRequestDto request) {
        log.info("Received ERX request for: {}", request.getPrescriptionId());
        return erxService.sendErxRequest(request);
    }

    @PostMapping("/erx/cancel")
    public ErxResponseDto cancelErx(@RequestBody ErxRequestDto request) {
        log.info("Received ERX cancellation request for: {}", request.getPrescriptionId());
        request.setTransactionType("eRxCancellation");
        return erxService.sendErxRequest(request);
    }

    @GetMapping("/erx/status/{prescriptionId}")
    public ErxResponseDto getErxStatus(@PathVariable String prescriptionId) {
        log.info("Getting ERX status for: {}", prescriptionId);
        return ErxResponseDto.builder()
                .success(true)
                .message("Status retrieved")
                .build();
    }

    // ==================== DISPENSE ====================

    @PostMapping("/dispense")
    public DispenseResponseDto submitDispense(@RequestBody DispenseRequestDto request) {
        log.info("Received dispense request for: {}", request.getDispenseId());
        return dispenseService.sendDispense(request);
    }

    @PostMapping("/dispense/process-flow/{dispenseId}")
    public String processCompletePharmacyFlow(@PathVariable String dispenseId) {
        log.info("Processing complete pharmacy flow for: {}", dispenseId);
        dispenseService.processCompletePharmacyFlow(dispenseId);
        return "Pharmacy flow completed for dispense: " + dispenseId;
    }

    @GetMapping("/dispense/status/{dispenseId}")
    public DispenseResponseDto getDispenseStatus(@PathVariable String dispenseId) {
        log.info("Getting dispense status for: {}", dispenseId);
        return DispenseResponseDto.builder()
                .success(true)
                .message("Status retrieved")
                .build();
    }

    // ==================== PENALTY ====================

    @PostMapping("/penalty")
    public PenaltyResponseDto submitPenalty(@RequestBody PenaltyRequestDto request) {
        log.info("Received penalty request for claim: {}", request.getClaimId());
        return penaltyService.sendPenalty(request);
    }

    @GetMapping("/penalty/status/{claimId}")
    public PenaltyResponseDto getPenaltyStatus(@PathVariable String claimId) {
        log.info("Getting penalty status for claim: {}", claimId);
        return PenaltyResponseDto.builder()
                .success(true)
                .message("Status retrieved")
                .build();
    }

}