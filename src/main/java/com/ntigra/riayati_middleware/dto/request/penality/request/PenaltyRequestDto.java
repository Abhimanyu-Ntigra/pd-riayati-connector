package com.ntigra.riayati_middleware.dto.request.penality.request;

import lombok.Data;

@Data
public class PenaltyRequestDto {
    // Header
    private String senderId;
    private String receiverId;
    private String payerId;
    private String dispositionFlag;
    private Integer recordCount;

    // Penalty
    private String penaltyId;
    private String claimId;
    private String remittedIdPayer;
    private String claimSubmittedDate;
    private String remittanceReceivedDate;
    private String remittedPaymentReference;
    private Double claimedNetAmount;
    private Double penaltyNetAmount;
    private String penaltyReasonCode;
    private String observationText;
}
