package com.ntigra.riayati_middleware.controller;

import com.ntigra.riayati_middleware.dto.request.Authorization.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.dto.request.ClaimRequestDto;
import com.ntigra.riayati_middleware.dto.request.dispense.request.DispenseRequestDto;
import com.ntigra.riayati_middleware.dto.request.erx.request.ErxRequestDto;
import com.ntigra.riayati_middleware.dto.request.penality.request.PenaltyRequestDto;
import com.ntigra.riayati_middleware.dto.response.*;
import com.ntigra.riayati_middleware.service.Authorization.AuthorizationService;
import com.ntigra.riayati_middleware.service.Claim.ClaimService;

import com.ntigra.riayati_middleware.service.Dispense.DispenseService;
import com.ntigra.riayati_middleware.service.ERX.ErxService;
import com.ntigra.riayati_middleware.service.Penalty.PenaltyService;
import com.ntigra.riayati_middleware.service.Penalty.PenaltyServiceOld;

import com.ntigra.riayati_middleware.service.Eligibility.EligibilityServiceOld;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> sendAuthorization(@RequestBody AuthorizationRequestDto request) {
        log.info("POST /authorization - Sending authorization for member: {}", request.getMemberId());
        return ResponseEntity.ok(authorizationService.submit(request));
    }

    @GetMapping("/authorization/status/{transactionId}")
    public ResponseEntity<?> getAuthorizationStatus(@PathVariable String transactionId) {
        log.info("GET /authorization/status - Getting status for: {}", transactionId);
        return ResponseEntity.ok(authorizationService.getStatus(transactionId));
    }


    // ==================== ERX ====================

    @PostMapping("/erx")
    public ResponseEntity<?> submitErx(@RequestBody ErxRequestDto request) {
        log.info("POST /erx - Submitting ERX: {}", request.getPrescriptionId());
        return ResponseEntity.ok(erxService.submit(request));
    }

    @GetMapping("/erx/status/{prescriptionId}")
    public ResponseEntity<?> getErxStatus(@PathVariable String prescriptionId) {
        log.info("GET /erx/status - Getting status for: {}", prescriptionId);
        return ResponseEntity.ok(erxService.getStatus(prescriptionId));
    }

    // ==================== DISPENSE ====================

    @PostMapping("/dispense")
    public ResponseEntity<?> submitDispense(@RequestBody DispenseRequestDto request) {
        log.info("POST /dispense - Submitting dispense: {}", request.getDispenseId());
        return ResponseEntity.ok(dispenseService.submit(request));
    }

    @GetMapping("/dispense/status/{dispenseId}")
    public ResponseEntity<?> getDispenseStatus(@PathVariable String dispenseId) {
        log.info("GET /dispense/status - Getting status for: {}", dispenseId);
        return ResponseEntity.ok(dispenseService.getStatus(dispenseId));
    }

    // ==================== PENALTY ====================

    @PostMapping("/penalty")
    public ResponseEntity<?> submitPenalty(@RequestBody PenaltyRequestDto request) {
        log.info("POST /penalty - Submitting penalty for claim: {}", request.getClaimId());
        return ResponseEntity.ok(penaltyService.submit(request));
    }

    @GetMapping("/penalty/status/{claimId}")
    public ResponseEntity<?> getPenaltyStatus(@PathVariable String claimId) {
        log.info("GET /penalty/status - Getting status for claim: {}", claimId);
        return ResponseEntity.ok(penaltyService.getStatus(claimId));
    }

}