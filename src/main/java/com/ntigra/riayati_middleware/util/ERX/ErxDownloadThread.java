package com.ntigra.riayati_middleware.util.ERX;

import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.ERX.ErxServiceOld;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErxDownloadThread implements Runnable {

    private final TransactionEntityDto transaction;
    private final ErxServiceOld erxService;

    public ErxDownloadThread(TransactionEntityDto transaction, ErxServiceOld erxService) {
        this.transaction = transaction;
        this.erxService = erxService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing ERX download for transaction: {}", transaction.getId());
            erxService.processErxResponseInBackground(transaction);
            log.info("ERX download completed: {}", transaction.getId());
        } catch (Exception e) {
            log.error("Failed to process ERX download: {}", transaction.getId(), e);
        }
    }
}