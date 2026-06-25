package com.ntigra.riayati_middleware.util.Authorization;

import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.Authorization.AuthorizationServiceOld;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorizationDownloadThread implements Runnable {

    private final TransactionEntityDto transaction;
    private final AuthorizationServiceOld authorizationService;

    public AuthorizationDownloadThread(TransactionEntityDto transaction, AuthorizationServiceOld authorizationService) {
        this.transaction = transaction;
        this.authorizationService = authorizationService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing authorization download for transaction: {}", transaction.getId());
            authorizationService.processAuthorizationResponseInBackground(transaction);
            log.info("Authorization download completed: {}", transaction.getId());
        } catch (Exception e) {
            log.error("Failed to process authorization download: {}", transaction.getId(), e);
        }
    }
}