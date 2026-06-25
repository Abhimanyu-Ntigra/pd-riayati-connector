package com.ntigra.riayati_middleware.respository;

import com.ntigra.riayati_middleware.dto.request.Authorization.response.Activity;
import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;

import java.util.List;

public interface AuthorizationRepository {

    // For UPLOAD: Fetch pending authorizations from DB
    List<AuthorizationRequestDto> fetchPendingAuthorizations();

    void updateAuthorizationAsSent(Long id, String entityId);
    void updateAuthorizationAsSentWithResponse(Long id, String entityId, String responseJson);
    void updateAuthorizationAsFailed(Long id, String errorMessage);

    // Update retry count
    void updateRetryCount(Long id);

    // Update response from payer
    void updatePreAuthOrder(String preAuthRef, Activity activity);

     // Update PreAuthHead status
    void updatePreAuthStatus(String preAuthRef, Integer status, Integer isProceed);

     //Find PreAuthHead ID by PreAuthRef
    String findPreAuthHeadIdByPreAuthRef(String preAuthRef);


    void updateBillStatus(String preAuthId);
}
