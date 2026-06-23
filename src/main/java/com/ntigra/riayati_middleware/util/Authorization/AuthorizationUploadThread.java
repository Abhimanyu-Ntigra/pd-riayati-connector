package com.ntigra.riayati_middleware.util.Authorization;

import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.service.Authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorizationUploadThread implements Runnable {

    private final AuthorizationRequestDto request;
    private final AuthorizationService authorizationService;

    public AuthorizationUploadThread(AuthorizationRequestDto request, AuthorizationService authorizationService) {
        this.request = request;
        this.authorizationService = authorizationService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing authorization upload: {}", request.getTransactionId());
            authorizationService.submitAuthorizationInBackground(request);
            log.info("Authorization upload completed: {}", request.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to process authorization upload: {}", request.getTransactionId(), e);
        }
    }
}