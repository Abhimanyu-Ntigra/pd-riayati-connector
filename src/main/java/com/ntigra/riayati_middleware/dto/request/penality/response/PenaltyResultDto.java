package com.ntigra.riayati_middleware.dto.request.penality.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PenaltyResultDto {
    private String claimId;
    private String payerPenaltyId;
    private String penaltyStatus;
    private String denialCode;
    private String settlementDate;
    private Double paymentAmount;
    private String comments;
}