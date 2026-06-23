package com.ntigra.riayati_middleware.scheduler.Dispense;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.Dispense.DispenseService;
import com.ntigra.riayati_middleware.util.Dispense.DispenseDownloadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DispenseDownloadScheduler {

    private final ThreadPoolTaskExecutor dispenseDownloadTaskExecutor;
    private final RiayatiRestClient riayatiClient;
    private final DispenseService dispenseService;

    public DispenseDownloadScheduler(
            @Qualifier("dispenseDownloadTaskExecutor") ThreadPoolTaskExecutor dispenseDownloadTaskExecutor,
            RiayatiRestClient riayatiClient,
            DispenseService dispenseService) {
        this.dispenseDownloadTaskExecutor = dispenseDownloadTaskExecutor;
        this.riayatiClient = riayatiClient;
        this.dispenseService = dispenseService;
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 9000)
    public void processPendingResponses() {
        log.info("===== DISPENSE DOWNLOAD SCHEDULER STARTED =====");

        try {
            ApiResponseDto response = riayatiClient.getNewDispense();

            if (response == null) {
                log.warn("Received null response from getNewDispense");
                return;
            }

            List<TransactionEntityDto> transactions = response.getEntities();

            if (transactions != null && !transactions.isEmpty()) {
                log.info("Found {} pending dispense responses to download", transactions.size());

                for (TransactionEntityDto transaction : transactions) {
                    dispenseDownloadTaskExecutor.submit(
                            new DispenseDownloadThread(transaction, dispenseService)
                    );
                }
            } else {
                log.debug("No pending dispense responses found");
            }

        } catch (Exception e) {
            log.error("Dispense download scheduler failed: ", e);
        }

        log.info("===== DISPENSE DOWNLOAD SCHEDULER COMPLETED =====");
    }
}
