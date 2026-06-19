package com.ntigra.riayati_middleware.scheduler.authorization;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.util.Authorization.AuthorizationDownloadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthorizationDownloadScheduler {

    private final ThreadPoolTaskExecutor executor;
    private final RiayatiRestClient riayatiClient;
    private final AuthorizationDownloadBackgroundService downloadService;

    public AuthorizationDownloadScheduler(@Qualifier("authorizationDownloadTaskExecutor") ThreadPoolTaskExecutor executor,
                                          RiayatiRestClient riayatiClient,
                                          AuthorizationDownloadBackgroundService downloadService) {
        this.executor = executor;
        this.riayatiClient = riayatiClient;
        this.downloadService = downloadService;
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 7000)
    public void processPendingResponses() {
        ApiResponseDto response = riayatiClient.getNewAuthorization();
        if (response.getEntities() != null && !response.getEntities().isEmpty()) {
            for (TransactionEntityDto transaction : response.getEntities()) {
                executor.submit(new AuthorizationDownloadThread(transaction, downloadService));
            }
        }
    }
}
