package com.ntigra.riayati_middleware.respository;

import com.ntigra.riayati_middleware.dto.entity.Penalty.PenaltyResponse;
import com.ntigra.riayati_middleware.dto.request.PenaltyRequestDto;

import java.util.List;
import java.util.Optional;

public interface PenaltyRepository {

    List<PenaltyRequestDto> fetchPendingPenalties();

    Optional<PenaltyResponse> findPenaltyResponseByClaimId(String claimId);

    // ==================== INSERT METHODS ====================

    void savePenaltyResponse(PenaltyResponse response);

    // ==================== UPDATE METHODS ====================

    void updatePenaltyAsSent(String claimId, String entityId);

    void updatePenaltyAsSentWithResponse(String claimId, String entityId, String responseJson);

    void updatePenaltyAsFailed(String claimId, String errorMessage);

    void updateRetryCount(String claimId);

    void updatePenaltyResponse(String claimId, String result, String idPayer,
                               String denialCode, String referenceNumber,
                               String penaltyDateSettlement, Double penaltyPaymentAmount,
                               String comments, String responseData);
}