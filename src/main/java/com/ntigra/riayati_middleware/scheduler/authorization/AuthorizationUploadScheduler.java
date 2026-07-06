package com.ntigra.riayati_middleware.scheduler.authorization;

import com.ntigra.riayati_middleware.client.RiayatiRestClient;
import com.ntigra.riayati_middleware.dto.ApiResponseDto;
import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.dto.polling.GetNewEntity;
import com.ntigra.riayati_middleware.dto.polling.GetNewResponse;
import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.respository.AuthorizationRepository;
import com.ntigra.riayati_middleware.service.Authorization.AuthorizationServiceOld;
import com.ntigra.riayati_middleware.util.Authorization.AuthorizationUploadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AuthorizationUploadScheduler {

    private final ThreadPoolTaskExecutor authorizationUploadTaskExecutor;
    private final RiayatiRestClient riayatiClient;
    private final AuthorizationRepository authorizationRepository;
    private final AuthorizationServiceOld authorizationService;

    public AuthorizationUploadScheduler(
            @Qualifier("authorizationUploadTaskExecutor") ThreadPoolTaskExecutor authorizationUploadTaskExecutor,
            RiayatiRestClient riayatiClient,
            AuthorizationRepository authorizationRepository,
            AuthorizationServiceOld authorizationService) {
        this.authorizationUploadTaskExecutor = authorizationUploadTaskExecutor;
        this.riayatiClient = riayatiClient;
        this.authorizationRepository = authorizationRepository;
        this.authorizationService = authorizationService;
    }


    @Scheduled(fixedDelay = 8000, initialDelay = 3000)
    public void processPendingAuthorizations() {
        log.info("===== AUTHORIZATION UPLOAD SCHEDULER STARTED =====");

        try {
            // STEP 1: First call GetNew to get pending transactions
            log.info("Calling GetNew to fetch pending authorization transactions...");
            GetNewResponse getNewResponse = riayatiClient.getNewAuthorization();

            if (getNewResponse != null && getNewResponse.getEntities() != null) {
                log.info("Found {} transactions in GetNew response", getNewResponse.getEntities().size());
                // Log the transaction IDs for reference
                for (GetNewEntity entity : getNewResponse.getEntities()) {
                    log.debug("GetNew transaction: ID={}, SenderID={}, TransactionDate={}",
                            entity.getId(), entity.getSenderId(), entity.getTransactionDate());
                }
            }

            // STEP 2: Fetch pending authorization requests from database
            List<AuthorizationRequestDto> pendingRequests = authorizationRepository.fetchPendingAuthorizations();

            if (pendingRequests != null && !pendingRequests.isEmpty()) {
                log.info("Found {} pending authorization requests to upload", pendingRequests.size());

                // STEP 3: Submit each request to thread pool for parallel processing
                for (AuthorizationRequestDto request : pendingRequests) {
                    authorizationUploadTaskExecutor.submit(
                            new AuthorizationUploadThread(request, authorizationService)
                    );
                }
            } else {
                log.debug("No pending authorization requests found");
            }

        } catch (Exception e) {
            log.error("Authorization upload scheduler failed: ", e);
        }

        log.info("===== AUTHORIZATION UPLOAD SCHEDULER COMPLETED =====");
    }
}