package com.ntigra.riayati_middleware.scheduler.eligibility;

import com.ntigra.riayati_middleware.dto.request.EligibilityRequestDto;
import com.ntigra.riayati_middleware.respository.EligibilityRepository;
import com.ntigra.riayati_middleware.service.Eligibility.EligibilityService;
import com.ntigra.riayati_middleware.util.Eligiblity.EligibilityUploadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class EligibilityUploadScheduler {

    private final ThreadPoolTaskExecutor eligibilityUploadTaskExecutor;
    private final EligibilityRepository eligibilityRepository;
    private final EligibilityService eligibilityService;

    public EligibilityUploadScheduler(
            @Qualifier("eligibilityUploadTaskExecutor") ThreadPoolTaskExecutor eligibilityUploadTaskExecutor,
            EligibilityRepository eligibilityRepository,
            EligibilityService eligibilityService) {
        this.eligibilityUploadTaskExecutor = eligibilityUploadTaskExecutor;
        this.eligibilityRepository = eligibilityRepository;
        this.eligibilityService = eligibilityService;
    }

    @Scheduled(fixedDelay = 8000, initialDelay = 2000)
    public void processPendingEligibility() {
        log.info("===== ELIGIBILITY UPLOAD SCHEDULER STARTED =====");

        try {
            // Step 1: Fetch pending eligibility requests from database
            //List<EligibilityRequestDto> pendingRequests = eligibilityRepository.fetchPendingEligibilityRequests();
            List<EligibilityRequestDto> pendingRequests = eligibilityRepository.getClaims();

            if (pendingRequests != null && !pendingRequests.isEmpty()) {
                log.info("Found {} pending eligibility requests to upload", pendingRequests.size());

                // Step 2: Submit each request to thread pool for parallel processing
                for (EligibilityRequestDto request : pendingRequests) {
                    eligibilityUploadTaskExecutor.submit(
                            new EligibilityUploadThread(request, eligibilityService)
                    );
                }
            } else {
                log.debug("No pending eligibility requests found");
            }

        } catch (Exception e) {
            log.error("Eligibility upload scheduler failed: ", e);
        }

        log.info("===== ELIGIBILITY UPLOAD SCHEDULER COMPLETED =====");
    }
}