package com.ntigra.riayati_middleware.scheduler.authorization;

import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.respository.AuthorizationRepository;
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

    private final ThreadPoolTaskExecutor executor;
    private final AuthorizationRepository repository;
    private final AuthorizationUploadBackgroundService uploadService;

    public AuthorizationUploadScheduler(@Qualifier("authorizationUploadTaskExecutor") ThreadPoolTaskExecutor executor,
                                        AuthorizationRepository repository,
                                        AuthorizationUploadBackgroundService uploadService) {
        this.executor = executor;
        this.repository = repository;
        this.uploadService = uploadService;
    }

    @Scheduled(fixedDelay = 8000, initialDelay = 3000)
    public void processPendingAuthorizations() {
        List<AuthorizationRequestDto> pending = repository.fetchPendingAuthorizations();
        if (pending != null && !pending.isEmpty()) {
            for (AuthorizationRequestDto request : pending) {
                executor.submit(new AuthorizationUploadThread(request, uploadService));
            }
        }
    }
}
