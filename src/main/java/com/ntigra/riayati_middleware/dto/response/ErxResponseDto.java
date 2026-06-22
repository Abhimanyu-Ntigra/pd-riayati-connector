package com.ntigra.riayati_middleware.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErxResponseDto {
    private Boolean success;
    private String entityId;
    private String referenceNumber;
    private String idPayer;
    private String result;
    private String denialCode;
    private String startDate;
    private String endDate;
    private Double limit;
    private String message;
    private String errorMessage;
}
