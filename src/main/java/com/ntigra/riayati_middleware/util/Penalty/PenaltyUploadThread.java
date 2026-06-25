package com.ntigra.riayati_middleware.util.Penalty;

import com.ntigra.riayati_middleware.dto.request.PenaltyRequestDto;
import com.ntigra.riayati_middleware.service.Penalty.PenaltyServiceOld;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PenaltyUploadThread implements Runnable {

    private final PenaltyRequestDto request;
    private final PenaltyServiceOld penaltyService;

    public PenaltyUploadThread(PenaltyRequestDto request, PenaltyServiceOld penaltyService) {
        this.request = request;
        this.penaltyService = penaltyService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing penalty upload for claim: {}", request.getClaimId());
            penaltyService.submitPenaltyInBackground(request);
            log.info("Penalty upload completed for claim: {}", request.getClaimId());
        } catch (Exception e) {
            log.error("Failed to process penalty upload for claim: {}", request.getClaimId(), e);
        }
    }
}