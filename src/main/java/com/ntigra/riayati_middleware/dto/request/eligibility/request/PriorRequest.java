package com.ntigra.riayati_middleware.dto.request.eligibility.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class PriorRequest {

    @JsonProperty("Header")
    private RequestHeader header;

    @JsonProperty("Authorization")
    private AuthorizationRequest authorization;
}