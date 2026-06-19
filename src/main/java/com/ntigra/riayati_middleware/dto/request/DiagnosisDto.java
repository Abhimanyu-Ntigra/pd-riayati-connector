package com.ntigra.riayati_middleware.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisDto {
    private String code;
    private String type;
    private String dxInfoType;
    private String dxInfoCode;
}
