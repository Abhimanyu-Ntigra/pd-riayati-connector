package com.ntigra.riayati_middleware.dto.request.Authorization.request;

import lombok.Data;

@Data
public class DiagnosisRequest {
    private String type;
    private String code;
    private DxInfoRequest dxInfo;
}
