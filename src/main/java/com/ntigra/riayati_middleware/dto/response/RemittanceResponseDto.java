package com.ntigra.riayati_middleware.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RemittanceResponseDto {
    private Boolean success;
    private String claimId;
    private String idPayer;
    private Double paymentAmount;
    private String paymentReference;
    private String denialCode;
    private String message;
}