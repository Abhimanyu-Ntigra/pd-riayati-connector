package com.ntigra.riayati_middleware.util.Dispense;

import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.Dispense.DispenseServiceOld;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DispenseDownloadThread implements Runnable {

    private final TransactionEntityDto transaction;
    private final DispenseServiceOld dispenseService;

    public DispenseDownloadThread(TransactionEntityDto transaction, DispenseServiceOld dispenseService) {
        this.transaction = transaction;
        this.dispenseService = dispenseService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing dispense download for transaction: {}", transaction.getId());
            dispenseService.processDispenseResponseInBackground(transaction);
            log.info("Dispense download completed: {}", transaction.getId());
        } catch (Exception e) {
            log.error("Failed to process dispense download: {}", transaction.getId(), e);
        }
    }
}
