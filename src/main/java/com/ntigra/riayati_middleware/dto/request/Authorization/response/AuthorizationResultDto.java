package com.ntigra.riayati_middleware.dto.request.Authorization.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationResultDto {
    private String requestId;
    private String payerAuthorizationId;
    private String authorizationStatus;
    private String denialCode;
    private String comments;
    private String startDate;
    private String endDate;
    private Double limitAmount;
}
