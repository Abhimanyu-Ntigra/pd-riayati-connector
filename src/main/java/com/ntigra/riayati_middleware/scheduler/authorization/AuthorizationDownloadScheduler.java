package com.ntigra.riayati_middleware.scheduler.authorization;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.Authorization.AuthorizationService;
import com.ntigra.riayati_middleware.util.Authorization.AuthorizationDownloadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AuthorizationDownloadScheduler {

    private final ThreadPoolTaskExecutor authorizationDownloadTaskExecutor;
    private final RiayatiRestClient riayatiClient;
    private final AuthorizationService authorizationService;

    public AuthorizationDownloadScheduler(
            @Qualifier("authorizationDownloadTaskExecutor") ThreadPoolTaskExecutor authorizationDownloadTaskExecutor,
            RiayatiRestClient riayatiClient,
            AuthorizationService authorizationService) {
        this.authorizationDownloadTaskExecutor = authorizationDownloadTaskExecutor;
        this.riayatiClient = riayatiClient;
        this.authorizationService = authorizationService;
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 7000)
    public void processPendingResponses() {
        log.info("===== AUTHORIZATION DOWNLOAD SCHEDULER STARTED =====");

        try {
            ApiResponseDto response = riayatiClient.getNewAuthorization();

            if (response == null) {
                log.warn("Received null response from getNewAuthorization");
                return;
            }

            List<TransactionEntityDto> transactions = response.getEntities();

            if (transactions != null && !transactions.isEmpty()) {
                log.info("Found {} pending authorization responses to download", transactions.size());

                for (TransactionEntityDto transaction : transactions) {
                    authorizationDownloadTaskExecutor.submit(
                            new AuthorizationDownloadThread(transaction, authorizationService)
                    );
                }
            } else {
                log.debug("No pending authorization responses found");
            }

        } catch (Exception e) {
            log.error("Authorization download scheduler failed: ", e);
        }

        log.info("===== AUTHORIZATION DOWNLOAD SCHEDULER COMPLETED =====");
    }
}

