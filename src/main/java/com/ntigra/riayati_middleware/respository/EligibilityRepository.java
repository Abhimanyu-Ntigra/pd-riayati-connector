package com.ntigra.riayati_middleware.respository;

import com.ntigra.riayati_middleware.dto.request.EligibilityRequestDto;

import java.util.List;

public interface EligibilityRepository {

    // For UPLOAD: Fetch pending eligibility requests from your DB
    List<EligibilityRequestDto> fetchPendingEligibilityRequests();

    // Update after successful upload
    void updateEligibilityAsSent(Long id, String entityId);

    /**
     * Update eligibility as SENT with full response JSON
     */
    void updateEligibilityAsSentWithResponse(Long id, String entityId, String responseJson);

    /**
     * Update eligibility as FAILED
     */
    void updateEligibilityAsFailed(Long id, String errorMessage);

    /**
     * Increment retry count
     */
    void updateRetryCount(Long id);

    /**
     * Update eligibility response after receiving from payer
     */
    void updateEligibilityResponse(String transactionId, String result, String idPayer,
                                   String denialCode, String startDate, String endDate,
                                   Double limit, String responseData);
}