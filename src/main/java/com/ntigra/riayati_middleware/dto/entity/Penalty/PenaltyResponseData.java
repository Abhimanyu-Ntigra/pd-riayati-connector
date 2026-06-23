package com.ntigra.riayati_middleware.dto.entity.Penalty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenaltyResponseData {

    private String claimId;
    private String result;
    private String idPayer;
    private String denialCode;
    private String referenceNumber;
    private String penaltyDateSettlement;
    private Double penaltyPaymentAmount;
    private String comments;

}
