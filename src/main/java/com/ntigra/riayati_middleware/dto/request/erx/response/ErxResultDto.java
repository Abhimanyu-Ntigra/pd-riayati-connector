package com.ntigra.riayati_middleware.dto.request.erx.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErxResultDto {
    private String prescriptionId;
    private String referenceNumber;
    private String payerAuthorizationId;
    private String authorizationStatus;
    private String denialCode;
    private String startDate;
    private String endDate;
    private Double limitAmount;
}