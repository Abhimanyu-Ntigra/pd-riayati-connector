package com.ntigra.riayati_middleware.dto.request.erx.request;

import lombok.Data;

@Data
public class ErxFrequencyDto {
    private Integer unitPerFrequency;
    private Integer frequencyValue;
    private String frequencyType;         // Day, Week, Month
}
