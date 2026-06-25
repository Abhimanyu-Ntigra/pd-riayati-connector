package com.ntigra.riayati_middleware.scheduler.authorization;

import com.ntigra.riayati_middleware.service.polling.RiayatiPollingAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RiayatiPollingAuthorizationScheduler {

    private final RiayatiPollingAuthorizationService pollingService;

    @Scheduled(
            fixedDelayString =
                    "${riayati.authorization.polling.delay:10000}")
    public void poll() {
        pollingService.pollAuthorizationResponses();
    }
}