package com.ntigra.riayati_middleware.dto.request;

import lombok.Data;

@Data
public class PenaltyRequestDto {

    // ==================== DATABASE TRACKING ====================
    private Long id;
    private String claimId;
    private String penaltyId;
    private Integer retryCount;

    // ==================== HEADER INFORMATION ====================
    private String senderId;
    private String receiverId;
    private String payerId;
    private String facilityId;
    private String dispositionFlag;
    private Integer recordCount;

    // ==================== PENALTY INFORMATION ====================
    private String remittedIdPayer;
    private String claimSubmittedDate;
    private String remittanceReceivedDate;
    private String remittedPaymentReference;
    private Double claimedNetAmount;
    private Double penaltyNetAmount;
    private String penaltyReasonCode;
    private String observationText;
}