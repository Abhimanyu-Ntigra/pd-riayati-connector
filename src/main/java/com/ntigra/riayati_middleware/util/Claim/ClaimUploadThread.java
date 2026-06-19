package com.ntigra.riayati_middleware.util.Claim;

import com.ntigra.riayati_middleware.dto.request.ClaimRequestDto;
import com.ntigra.riayati_middleware.service.Claim.ClaimService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClaimUploadThread implements Runnable {

    private final ClaimRequestDto claimRequest;
    private final ClaimService claimService;

    public ClaimUploadThread(ClaimRequestDto claimRequest, ClaimService claimService) {
        this.claimRequest = claimRequest;
        this.claimService = claimService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing claim upload: {}", claimRequest.getClaimId());
            claimService.submitClaimInBackground(claimRequest);
            log.info("Claim upload completed: {}", claimRequest.getClaimId());
        } catch (Exception e) {
            log.error("Failed to process claim upload: {}", claimRequest.getClaimId(), e);
        }
    }
}