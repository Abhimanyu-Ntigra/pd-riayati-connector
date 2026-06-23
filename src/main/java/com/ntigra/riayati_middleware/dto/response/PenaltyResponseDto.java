package com.ntigra.riayati_middleware.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PenaltyResponseDto {
    private Boolean success;
    private String entityId;
    private String result;
    private String idPayer;
    private String denialCode;
    private String referenceNumber;
    private String penaltyDateSettlement;
    private Double penaltyPaymentAmount;
    private String message;
    private String errorMessage;
}
