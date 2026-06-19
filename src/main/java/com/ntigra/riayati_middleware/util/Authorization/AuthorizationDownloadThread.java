package com.ntigra.riayati_middleware.util.Authorization;

import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.scheduler.authorization.AuthorizationDownloadBackgroundService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorizationDownloadThread implements Runnable {
    private final TransactionEntityDto transaction;
    private final AuthorizationDownloadBackgroundService downloadService;

    public AuthorizationDownloadThread(TransactionEntityDto transaction,
                                       AuthorizationDownloadBackgroundService downloadService) {
        this.transaction = transaction;
        this.downloadService = downloadService;
    }

    @Override
    public void run() {
        try {
            downloadService.processAuthorizationResponse(transaction);
        } catch (Exception e) {
            log.error("Failed to process authorization download: {}", transaction.getId(), e);
        }
    }
}