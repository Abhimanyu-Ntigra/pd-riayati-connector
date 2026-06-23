package com.ntigra.riayati_middleware.dto.entity.ERX;

import lombok.Data;

@Data
public class ErxDiagnosis {
    private Long id;
    private String prescriptionId;
    private String code;                  // ICD-10 code
    private String type;
}
