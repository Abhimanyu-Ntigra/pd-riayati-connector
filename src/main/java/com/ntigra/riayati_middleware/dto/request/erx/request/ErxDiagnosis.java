package com.ntigra.riayati_middleware.dto.request.erx.request;

import lombok.Data;

@Data
public class ErxDiagnosis {
    private String type;                  // Principal or Secondary
    private String code;                  // ICD-10 code
}
