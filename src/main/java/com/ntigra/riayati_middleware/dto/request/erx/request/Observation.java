package com.ntigra.riayati_middleware.dto.request.erx.request;

import lombok.Data;

@Data
public class Observation {
    private String type;
    private String code;
    private String value;
    private String valueType;
}
