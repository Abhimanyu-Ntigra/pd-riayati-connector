package com.ntigra.riayati_middleware.dto.entity.Penalty;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PenaltyResponse {

    private Long id;
    private String claimId;
    private String entityId;
    private String result;
    private String idPayer;
    private String denialCode;
    private String referenceNumber;
    private LocalDateTime penaltyDateSettlement;
    private Double penaltyPaymentAmount;
    private String comments;
    private String responseData;
    private String status;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
