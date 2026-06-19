package com.ntigra.riayati_middleware.util.Authorization;

import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.scheduler.authorization.AuthorizationUploadBackgroundService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorizationUploadThread implements Runnable {
    private final AuthorizationRequestDto request;
    private final AuthorizationUploadBackgroundService uploadService;

    public AuthorizationUploadThread(AuthorizationRequestDto request,
                                     AuthorizationUploadBackgroundService uploadService) {
        this.request = request;
        this.uploadService = uploadService;
    }

    @Override
    public void run() {
        try {
            uploadService.submitAuthorizationInBackground(request);
        } catch (Exception e) {
            log.error("Failed to process authorization: {}", request.getTransactionId(), e);
        }
    }
}
