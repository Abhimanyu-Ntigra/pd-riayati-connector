package com.ntigra.riayati_middleware.util.ERX;

import com.ntigra.riayati_middleware.dto.request.ErxRequestDto;
import com.ntigra.riayati_middleware.service.ERX.ErxServiceOld;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErxUploadThread implements Runnable {

    private final ErxRequestDto request;
    private final ErxServiceOld erxService;

    public ErxUploadThread(ErxRequestDto request, ErxServiceOld erxService) {
        this.request = request;
        this.erxService = erxService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing ERX upload: {}", request.getPrescriptionId());
            erxService.submitErxInBackground(request);
            log.info("ERX upload completed: {}", request.getPrescriptionId());
        } catch (Exception e) {
            log.error("Failed to process ERX upload: {}", request.getPrescriptionId(), e);
        }
    }
}