package com.ntigra.riayati_middleware.respository;

import com.ntigra.riayati_middleware.dto.request.ErxRequestDto;
import com.ntigra.riayati_middleware.dto.request.erx.response.ErxActivityResponse;

import java.util.List;

public interface ErxRepository {
    List<ErxRequestDto> fetchPendingErxRequests();
    void updateErxAsSent(Long id, String entityId, String referenceNumber);
    void updateErxAsSentWithResponse(Long id, String entityId, String referenceNumber, String responseJson);
    void updateErxAsFailed(Long id, String errorMessage);
    void updateRetryCount(Long id);
    void updateErxOrders(String preAuthRef, ErxActivityResponse item);
    void updateErxStatus(String preAuthRef, Integer status, Integer isProceed);
    String findPreAuthHeadIdByPreAuthRef(String preAuthRef);
}