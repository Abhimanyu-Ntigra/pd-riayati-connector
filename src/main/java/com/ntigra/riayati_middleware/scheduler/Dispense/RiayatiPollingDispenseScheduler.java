package com.ntigra.riayati_middleware.scheduler.Dispense;

import com.ntigra.riayati_middleware.service.polling.RiayatiPollingDispenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RiayatiPollingDispenseScheduler {

    private final RiayatiPollingDispenseService pollingService;

    @Scheduled(
            fixedDelayString =
                    "${riayati.dispense.polling.delay:10000}")
    public void poll() {
        pollingService.pollDispenseResponses();
    }
}
