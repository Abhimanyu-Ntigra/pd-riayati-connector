package com.ntigra.riayati_middleware.scheduler.claim;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.Claim.ClaimService;
import com.ntigra.riayati_middleware.util.Claim.ClaimDownloadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ClaimDownloadScheduler {

    private final ThreadPoolTaskExecutor claimDownloadTaskExecutor;
    private final RiayatiRestClient riayatiClient;
    private final ClaimService claimService;

    public ClaimDownloadScheduler(
            @Qualifier("claimDownloadTaskExecutor") ThreadPoolTaskExecutor claimDownloadTaskExecutor,
            RiayatiRestClient riayatiClient,
            ClaimService claimService) {
        this.claimDownloadTaskExecutor = claimDownloadTaskExecutor;
        this.riayatiClient = riayatiClient;
        this.claimService = claimService;
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 5000)
    public void processPendingRemittances() {
        log.info("===== CLAIM DOWNLOAD SCHEDULER STARTED =====");

        try {
            // Step 1: Get new remittances from Riayati
            ApiResponseDto response = riayatiClient.getNewClaim();

            if (response == null) {
                log.warn("Received null response from getNewClaim");
                return;
            }

            List<TransactionEntityDto> transactions = response.getEntities();

            if (transactions != null && !transactions.isEmpty()) {
                log.info("Found {} pending remittances to download", transactions.size());

                // Step 2: Submit each transaction to thread pool for parallel processing
                for (TransactionEntityDto transaction : transactions) {
                    claimDownloadTaskExecutor.submit(
                            new ClaimDownloadThread(transaction, claimService)
                    );
                }
            } else {
                log.debug("No pending remittances found");
            }

        } catch (Exception e) {
            log.error("Claim download scheduler failed: ", e);
        }

        log.info("===== CLAIM DOWNLOAD SCHEDULER COMPLETED =====");
    }
}