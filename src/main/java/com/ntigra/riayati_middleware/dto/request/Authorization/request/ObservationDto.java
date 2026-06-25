package com.ntigra.riayati_middleware.dto.request.Authorization.request;

import lombok.Data;

@Data
public class ObservationDto {
    private String type;
    private String code;
    private String value;
    private String valueType;
}
