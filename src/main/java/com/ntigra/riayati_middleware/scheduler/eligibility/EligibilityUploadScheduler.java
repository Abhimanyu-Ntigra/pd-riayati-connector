package com.ntigra.riayati_middleware.scheduler.eligibility;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.polling.GetNewEntity;
import com.ntigra.riayati_middleware.dto.polling.GetNewResponse;
import com.ntigra.riayati_middleware.dto.request.EligibilityRequestDto;
import com.ntigra.riayati_middleware.respository.EligibilityRepository;
import com.ntigra.riayati_middleware.service.Eligibility.EligibilityServiceOld;
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
    private final RiayatiRestClient riayatiClient;
    private final EligibilityRepository eligibilityRepository;
    private final EligibilityServiceOld eligibilityService;

    public EligibilityUploadScheduler(
            @Qualifier("eligibilityUploadTaskExecutor") ThreadPoolTaskExecutor eligibilityUploadTaskExecutor,
            RiayatiRestClient riayatiClient,
            EligibilityRepository eligibilityRepository,
            EligibilityServiceOld eligibilityService) {
        this.eligibilityUploadTaskExecutor = eligibilityUploadTaskExecutor;
        this.riayatiClient = riayatiClient;
        this.eligibilityRepository = eligibilityRepository;
        this.eligibilityService = eligibilityService;
    }

    @Scheduled(fixedDelay = 8000, initialDelay = 2000)
    public void processPendingEligibility() {
        log.info("===== ELIGIBILITY UPLOAD SCHEDULER STARTED =====");

        try {
            // Step 1: Fetch pending eligibility requests from database
            GetNewResponse getNewResponse = riayatiClient.getNewEligibility();

            if (getNewResponse != null && getNewResponse.getEntities() != null) {
                log.info("Found {} transactions in GetNew response", getNewResponse.getEntities().size());
                // Log the transaction IDs for reference
                for (GetNewEntity entity : getNewResponse.getEntities()) {
                    log.info("GetNew transaction: ID={}, SenderID={}, TransactionDate={}",
                            entity.getId(), entity.getSenderId(), entity.getTransactionDate());
                }
            }

            List<EligibilityRequestDto> pendingRequests = eligibilityRepository.fetchPendingEligibilityRequests();

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