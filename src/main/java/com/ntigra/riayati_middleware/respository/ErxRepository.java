package com.ntigra.riayati_middleware.respository;

import com.ntigra.riayati_middleware.dto.request.ErxRequestDto;

import java.util.List;

public interface ErxRepository {
    List<ErxRequestDto> fetchPendingErxRequests();
    void updateErxAsSent(Long id, String entityId, String referenceNumber);
    void updateErxAsSentWithResponse(Long id, String entityId, String referenceNumber, String responseJson);
    void updateErxAsFailed(Long id, String errorMessage);
    void updateRetryCount(Long id);
    void updateErxResponse(String prescriptionId, String result, String idPayer,
                           String denialCode, String startDate, String endDate,
                           Double limit, String responseData);
}