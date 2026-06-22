package com.ntigra.riayati_middleware.respository;

import com.ntigra.riayati_middleware.dto.request.DispenseRequestDto;

import java.util.List;

public interface DispenseRepository {


    /**
     * Fetch pending dispense requests from database
     * For Pharmacy to dispense medications
     */
    List<DispenseRequestDto> fetchPendingDispenses();

    void updateDispenseAsSent(Long id, String entityId);

    void updateDispenseAsSentWithResponse(Long id, String entityId, String responseJson);

    void updateDispenseAsFailed(Long id, String errorMessage);

    void updateRetryCount(Long id);

    void updateDispenseResponse(String dispenseId, String result, String idPayer,
                                String denialCode, String startDate, String endDate,
                                Double limit, String responseData);
}