package com.ntigra.riayati_middleware.scheduler.Penalty;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.Penalty.PenaltyService;
import com.ntigra.riayati_middleware.util.Penalty.PenaltyDownloadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PenaltyDownloadScheduler {

    private final ThreadPoolTaskExecutor penaltyDownloadTaskExecutor;
    private final RiayatiRestClient riayatiClient;
    private final PenaltyService penaltyService;

    public PenaltyDownloadScheduler(
            @Qualifier("penaltyDownloadTaskExecutor") ThreadPoolTaskExecutor penaltyDownloadTaskExecutor,
            RiayatiRestClient riayatiClient,
            PenaltyService penaltyService) {
        this.penaltyDownloadTaskExecutor = penaltyDownloadTaskExecutor;
        this.riayatiClient = riayatiClient;
        this.penaltyService = penaltyService;
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void processPendingResponses() {
        log.info("===== PENALTY DOWNLOAD SCHEDULER STARTED =====");

        try {
            ApiResponseDto response = riayatiClient.getNewPenalty();

            if (response == null) {
                log.warn("Received null response from getNewPenalty");
                return;
            }

            List<TransactionEntityDto> transactions = response.getEntities();

            if (transactions != null && !transactions.isEmpty()) {
                log.info("Found {} pending penalty responses to download", transactions.size());

                for (TransactionEntityDto transaction : transactions) {
                    penaltyDownloadTaskExecutor.submit(
                            new PenaltyDownloadThread(transaction, penaltyService)
                    );
                }
            } else {
                log.debug("No pending penalty responses found");
            }

        } catch (Exception e) {
            log.error("Penalty download scheduler failed: ", e);
        }

        log.info("===== PENALTY DOWNLOAD SCHEDULER COMPLETED =====");
    }
}
