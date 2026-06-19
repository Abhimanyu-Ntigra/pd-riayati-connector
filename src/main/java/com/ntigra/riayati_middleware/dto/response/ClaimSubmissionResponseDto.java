package com.ntigra.riayati_middleware.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimSubmissionResponseDto {
    private Boolean success;
    private String entityId;
    private String referenceNumber;
    private Integer statusCode;
    private String message;
    private String errorMessage;
}