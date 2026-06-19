package com.ntigra.riayati_middleware.dto.entity.Eligibility;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EligibilityDiagnosis {
    private String type;      // Principal or Secondary
    private String code;      // ICD-10 code
}
