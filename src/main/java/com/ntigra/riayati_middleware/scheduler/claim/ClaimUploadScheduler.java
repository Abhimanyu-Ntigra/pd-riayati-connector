package com.ntigra.riayati_middleware.scheduler.claim;

import com.ntigra.riayati_middleware.dto.request.ClaimRequestDto;
import com.ntigra.riayati_middleware.respository.ClaimRepository;
import com.ntigra.riayati_middleware.service.Claim.ClaimService;
import com.ntigra.riayati_middleware.util.Claim.ClaimUploadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ClaimUploadScheduler {

    private final ThreadPoolTaskExecutor claimUploadTaskExecutor;
    private final ClaimRepository claimRepository;
    private final ClaimService claimService;

    public ClaimUploadScheduler(
            @Qualifier("claimUploadTaskExecutor") ThreadPoolTaskExecutor claimUploadTaskExecutor,
            ClaimRepository claimRepository,
            ClaimService claimService) {
        this.claimUploadTaskExecutor = claimUploadTaskExecutor;
        this.claimRepository = claimRepository;
        this.claimService = claimService;
    }

    @Scheduled(fixedDelay = 8000, initialDelay = 1000)
    public void processPendingClaims() {
        log.info("===== CLAIM UPLOAD SCHEDULER STARTED =====");

        try {
            // Step 1: Fetch pending claims from database
            List<ClaimRequestDto> pendingClaims = claimRepository.fetchPendingClaimsForUpload();

            if (pendingClaims != null && !pendingClaims.isEmpty()) {
                log.info("Found {} pending claims to upload", pendingClaims.size());

                // Step 2: Submit each claim to thread pool for parallel processing
                for (ClaimRequestDto claimRequest : pendingClaims) {
                    claimUploadTaskExecutor.submit(
                            new ClaimUploadThread(claimRequest, claimService)
                    );
                }
            } else {
                log.debug("No pending claims found");
            }

        } catch (Exception e) {
            log.error("Claim upload scheduler failed: ", e);
        }

        log.info("===== CLAIM UPLOAD SCHEDULER COMPLETED =====");
    }
}