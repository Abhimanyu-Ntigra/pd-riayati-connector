package com.ntigra.riayati_middleware.scheduler.Penalty;

import com.ntigra.riayati_middleware.service.polling.RiayatiPollingPenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RiayatiPollingPenaltyScheduler {

    private final RiayatiPollingPenaltyService pollingService;

    @Scheduled(
            fixedDelayString =
                    "${riayati.penalty.polling.delay:15000}")
    public void poll() {
        pollingService.pollPenaltyResponses();
    }
}
