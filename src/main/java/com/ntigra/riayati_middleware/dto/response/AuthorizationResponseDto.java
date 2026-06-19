package com.ntigra.riayati_middleware.dto.response;

import com.ntigra.riayati_middleware.dto.entity.Authorization.ActivityResponse;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AuthorizationResponseDto {
    private Boolean success;
    private String entityId;
    private String idPayer;
    private String result;          // "Yes" or "No"
    private String denialCode;      // If result is "No"
    private String startDate;
    private String endDate;
    private Double limit;
    private List<ActivityResponse> activities;
    private String message;
    private String errorMessage;
}