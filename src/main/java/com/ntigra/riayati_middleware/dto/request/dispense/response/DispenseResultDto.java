package com.ntigra.riayati_middleware.dto.request.dispense.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DispenseResultDto {
    private String dispenseId;
    private String payerAuthorizationId;
    private String authorizationStatus;
    private String denialCode;
    private String startDate;
    private String endDate;
    private Double limitAmount;
}
