package com.ntigra.riayati_middleware.util.Penalty;

import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.Penalty.PenaltyService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PenaltyDownloadThread implements Runnable {

    private final TransactionEntityDto transaction;
    private final PenaltyService penaltyService;

    public PenaltyDownloadThread(TransactionEntityDto transaction, PenaltyService penaltyService) {
        this.transaction = transaction;
        this.penaltyService = penaltyService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing penalty download for transaction: {}", transaction.getId());
            penaltyService.processPenaltyResponseInBackground(transaction);
            log.info("Penalty download completed for transaction: {}", transaction.getId());
        } catch (Exception e) {
            log.error("Failed to process penalty download for transaction: {}", transaction.getId(), e);
        }
    }
}