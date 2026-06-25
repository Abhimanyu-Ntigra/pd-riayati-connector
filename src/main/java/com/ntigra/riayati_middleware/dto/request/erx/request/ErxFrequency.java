package com.ntigra.riayati_middleware.dto.request.erx.request;

import lombok.Data;

@Data
public class ErxFrequency {
    private Integer unitPerFrequency;
    private Integer frequencyValue;
    private String frequencyType;
}
