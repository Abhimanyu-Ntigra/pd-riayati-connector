package com.ntigra.riayati_middleware.dto.entity;

import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class Diagnosis {
    private String type;
    private String code;
    private DxInfo dxInfo;
}
