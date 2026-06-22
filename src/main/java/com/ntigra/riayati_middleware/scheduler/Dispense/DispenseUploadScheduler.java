package com.ntigra.riayati_middleware.scheduler.Dispense;

import com.ntigra.riayati_middleware.dto.request.DispenseRequestDto;
import com.ntigra.riayati_middleware.respository.DispenseRepository;
import com.ntigra.riayati_middleware.service.Dispense.DispenseService;
import com.ntigra.riayati_middleware.util.Dispense.DispenseUploadThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DispenseUploadScheduler {

    private final ThreadPoolTaskExecutor dispenseUploadTaskExecutor;
    private final DispenseRepository dispenseRepository;
    private final DispenseService dispenseService;

    public DispenseUploadScheduler(
            @Qualifier("dispenseUploadTaskExecutor") ThreadPoolTaskExecutor dispenseUploadTaskExecutor,
            DispenseRepository dispenseRepository,
            DispenseService dispenseService) {
        this.dispenseUploadTaskExecutor = dispenseUploadTaskExecutor;
        this.dispenseRepository = dispenseRepository;
        this.dispenseService = dispenseService;
    }

    @Scheduled(fixedDelay = 8000, initialDelay = 5000)
    public void processPendingDispenses() {
        log.info("===== DISPENSE UPLOAD SCHEDULER STARTED =====");

        try {
            List<DispenseRequestDto> pendingDispenses = dispenseRepository.fetchPendingDispenses();

            if (pendingDispenses != null && !pendingDispenses.isEmpty()) {
                log.info("Found {} pending dispenses to upload", pendingDispenses.size());

                for (DispenseRequestDto request : pendingDispenses) {
                    dispenseUploadTaskExecutor.submit(
                            new DispenseUploadThread(request, dispenseService)
                    );
                }
            } else {
                log.debug("No pending dispenses found");
            }

        } catch (Exception e) {
            log.error("Dispense upload scheduler failed: ", e);
        }

        log.info("===== DISPENSE UPLOAD SCHEDULER COMPLETED =====");
    }
}
