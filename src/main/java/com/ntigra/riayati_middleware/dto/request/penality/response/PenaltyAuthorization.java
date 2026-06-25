package com.ntigra.riayati_middleware.dto.request.penality.response;

import lombok.Data;

@Data
public class PenaltyAuthorization {
    private String result;
    private String id;
    private String idPayer;
    private String denialCode;
    private String referenceNumber;
    private String penaltyDateSettlement;
    private Double penaltyPaymentAmount;
    private String comments;
}
