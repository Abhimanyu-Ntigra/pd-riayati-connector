package com.ntigra.riayati_middleware.respository;

import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;

import java.util.List;

public interface AuthorizationRepository {

    // For UPLOAD: Fetch pending authorizations from DB
    List<AuthorizationRequestDto> fetchPendingAuthorizations();

    // Update after successful upload
    void updateAuthorizationAsSent(Long id, String entityId);

    /**
     * Update authorization as SENT with full response JSON
     */
    void updateAuthorizationAsSentWithResponse(Long id, String entityId, String responseJson);

    // Update as failed
    void updateAuthorizationAsFailed(Long id, String errorMessage);

    // Update retry count
    void updateRetryCount(Long id);

    // Update response from payer
    void updateAuthorizationResponse(String transactionId, String result, String denialCode,
                                     String responseComment, String idPayer,
                                     String startDate, String endDate,
                                     Double coverageLimit, String responseData);
}
