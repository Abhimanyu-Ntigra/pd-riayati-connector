package com.ntigra.riayati_middleware.util.Claim;


import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.Claim.ClaimService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClaimDownloadThread implements Runnable {

    private final TransactionEntityDto transaction;
    private final ClaimService claimService;

    public ClaimDownloadThread(TransactionEntityDto transaction, ClaimService claimService) {
        this.transaction = transaction;
        this.claimService = claimService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing claim download for transaction: {}", transaction.getId());
            claimService.processRemittanceInBackground(transaction);
            log.info("Claim download completed: {}", transaction.getId());
        } catch (Exception e) {
            log.error("Failed to process claim download: {}", transaction.getId(), e);
        }
    }
}