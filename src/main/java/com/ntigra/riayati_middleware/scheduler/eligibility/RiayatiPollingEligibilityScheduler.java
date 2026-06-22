package com.ntigra.riayati_middleware.scheduler.eligibility;

import com.ntigra.riayati_middleware.service.polling.RiayatiPollingEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RiayatiPollingEligibilityScheduler {

    private final RiayatiPollingEligibilityService pollingService;

    @Scheduled(
            fixedDelayString =
                    "${riayati.eligibility.polling.delay}")
    public void poll() {

        pollingService
                .pollEligibilityResponses();
    }
}