package com.ntigra.riayati_middleware.scheduler.eligibility;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.dto.request.EligibilityRequestDto;
import com.ntigra.riayati_middleware.respository.EligibilityRepository;
import com.ntigra.riayati_middleware.service.Eligibility.EligibilityService;
import com.ntigra.riayati_middleware.util.Eligiblity.EligibilityDownloadThread;
import com.ntigra.riayati_middleware.util.Eligiblity.EligibilityUploadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class EligibilityDownloadScheduler {

    private final ThreadPoolTaskExecutor eligibilityDownloadTaskExecutor;
    private final RiayatiRestClient riayatiClient;
    private final EligibilityService eligibilityService;

    public EligibilityDownloadScheduler(
            @Qualifier("eligibilityDownloadTaskExecutor") ThreadPoolTaskExecutor eligibilityDownloadTaskExecutor,
            RiayatiRestClient riayatiClient,
            EligibilityService eligibilityService) {
        this.eligibilityDownloadTaskExecutor = eligibilityDownloadTaskExecutor;
        this.riayatiClient = riayatiClient;
        this.eligibilityService = eligibilityService;
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 6000)
    public void processPendingResponses() {
        log.info("===== ELIGIBILITY DOWNLOAD SCHEDULER STARTED =====");

        try {
            // Step 1: Get new eligibility responses from Riayati
            ApiResponseDto response = riayatiClient.getNewEligibility();

            if (response == null) {
                log.warn("Received null response from getNewEligibility");
                return;
            }

            List<TransactionEntityDto> transactions = response.getEntities();

            if (transactions != null && !transactions.isEmpty()) {
                log.info("Found {} pending eligibility responses to download", transactions.size());

                // Step 2: Submit each transaction to thread pool for parallel processing
                for (TransactionEntityDto transaction : transactions) {
                    eligibilityDownloadTaskExecutor.submit(
                            new EligibilityDownloadThread(transaction, eligibilityService)
                    );
                }
            } else {
                log.debug("No pending eligibility responses found");
            }

        } catch (Exception e) {
            log.error("Eligibility download scheduler failed: ", e);
        }

        log.info("===== ELIGIBILITY DOWNLOAD SCHEDULER COMPLETED =====");
    }
}