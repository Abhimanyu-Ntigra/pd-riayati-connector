package com.ntigra.riayati_middleware.dto.request.eligibility.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EligibilityResultDto {

    private String requestId;

    private String payerAuthorizationId;

    private String eligibilityStatus;

    private String denialCode;

    private String comments;

    private String startDate;

    private String endDate;

    private BigDecimal limitAmount;
}