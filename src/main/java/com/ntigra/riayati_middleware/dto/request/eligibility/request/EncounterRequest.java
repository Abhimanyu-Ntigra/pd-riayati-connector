package com.ntigra.riayati_middleware.dto.request.eligibility.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EncounterRequest {

    @JsonProperty("FacilityID")
    private String facilityId;

    @JsonProperty("Type")
    private Integer type;
}