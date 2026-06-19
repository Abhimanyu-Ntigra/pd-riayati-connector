package com.ntigra.riayati_middleware.dto.request;

import lombok.Data;

@Data
public class ObservationDto {
    private String type;
    private String code;
    private String value;
    private String valueType;
}
