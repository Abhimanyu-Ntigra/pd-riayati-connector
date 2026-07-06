package com.ntigra.riayati_middleware.dto.request.eligibility.request;

import com.ntigra.riayati_middleware.dto.entity.DxInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EligibilityDiagnosis {
    private String type;
    private String code;
    private DxInfo dxInfo;
}