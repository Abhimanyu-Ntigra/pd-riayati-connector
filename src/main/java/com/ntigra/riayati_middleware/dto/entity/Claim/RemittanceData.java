package com.ntigra.riayati_middleware.dto.entity.Claim;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemittanceData {

    private String claimId;
    private String idPayer;
    private String providerId;
    private String denialCode;
    private String paymentReference;
    private String dateSettlement;
    private Double totalPaymentAmount;
    private Double totalGross;
    private Double totalPatientShare;
    private List<ActivityDetail> activityDetails;

}
