package com.ntigra.riayati_middleware.util.Dispense;

import com.ntigra.riayati_middleware.dto.request.DispenseRequestDto;
import com.ntigra.riayati_middleware.service.Dispense.DispenseService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DispenseUploadThread implements Runnable {

    private final DispenseRequestDto request;
    private final DispenseService dispenseService;

    public DispenseUploadThread(DispenseRequestDto request, DispenseService dispenseService) {
        this.request = request;
        this.dispenseService = dispenseService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing dispense upload: {}", request.getDispenseId());
            dispenseService.submitDispenseInBackground(request);
            log.info("Dispense upload completed: {}", request.getDispenseId());
        } catch (Exception e) {
            log.error("Failed to process dispense upload: {}", request.getDispenseId(), e);
        }
    }
}
