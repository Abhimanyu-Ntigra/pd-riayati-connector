package com.ntigra.riayati_middleware.dto.request.eligibility.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EligibilityResponse {

    @JsonProperty("PriorAuthorization")
    private PriorAuthorization priorAuthorization;
}