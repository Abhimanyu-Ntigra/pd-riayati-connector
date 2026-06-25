package com.ntigra.riayati_middleware.scheduler.ERX;

import com.ntigra.riayati_middleware.service.polling.RiayatiPollingErxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RiayatiPollingErxScheduler {

    private final RiayatiPollingErxService pollingService;

    @Scheduled(
            fixedDelayString =
                    "${riayati.erx.polling.delay:10000}")
    public void poll() {
        pollingService.pollErxResponses();
    }
}