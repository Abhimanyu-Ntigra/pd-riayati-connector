package com.ntigra.riayati_middleware.util.Eligiblity;

import com.ntigra.riayati_middleware.dto.TransactionEntityDto;
import com.ntigra.riayati_middleware.service.Eligibility.EligibilityServiceOld;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EligibilityDownloadThread implements Runnable {

    private final TransactionEntityDto transaction;
    private final EligibilityServiceOld eligibilityService;

    public EligibilityDownloadThread(TransactionEntityDto transaction, EligibilityServiceOld eligibilityService) {
        this.transaction = transaction;
        this.eligibilityService = eligibilityService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing eligibility download for transaction: {}", transaction.getId());
            eligibilityService.processEligibilityResponseInBackground(transaction);
            log.info("Eligibility download completed: {}", transaction.getId());
        } catch (Exception e) {
            log.error("Failed to process eligibility download: {}", transaction.getId(), e);
        }
    }
}