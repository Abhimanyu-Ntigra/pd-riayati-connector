package com.ntigra.riayati_middleware.dto.request.Authorization.request;

import lombok.Data;

@Data
public class EncounterRequest {
    private String facilityId;
    private Integer type;
    private String patientId;
    private String start;
    private String end;
    private Integer startType;
    private Integer endType;
}