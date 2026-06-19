package com.ntigra.riayati_middleware.dto.entity.Eligibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EligibilityResponseData {

    private String transactionId;
    private String result;
    private String idPayer;
    private String denialCode;
    private String startDate;
    private String endDate;
    private Double limit;
}
