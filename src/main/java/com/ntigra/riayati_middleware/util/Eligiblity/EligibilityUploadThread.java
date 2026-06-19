package com.ntigra.riayati_middleware.util.Eligiblity;

import com.ntigra.riayati_middleware.dto.request.EligibilityRequestDto;
import com.ntigra.riayati_middleware.service.Eligibility.EligibilityService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EligibilityUploadThread implements Runnable {

    private final EligibilityRequestDto request;
    private final EligibilityService eligibilityService;

    public EligibilityUploadThread(EligibilityRequestDto request, EligibilityService eligibilityService) {
        this.request = request;
        this.eligibilityService = eligibilityService;
    }

    @Override
    public void run() {
        try {
            log.info("Processing eligibility upload: {}", request.getTransactionId());
            eligibilityService.submitEligibilityInBackground(request);
            log.info("Eligibility upload completed: {}", request.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to process eligibility upload: {}", request.getTransactionId(), e);
        }
    }
}