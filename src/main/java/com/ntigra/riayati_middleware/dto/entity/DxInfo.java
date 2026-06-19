package com.ntigra.riayati_middleware.dto.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DxInfo {
    private String type;
    private String code;
}