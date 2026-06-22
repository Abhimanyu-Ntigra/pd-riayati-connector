package com.ntigra.riayati_middleware.dto.request.eligibility.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AuthorizationResponse {

    @JsonProperty("Result")
    private String result;

    @JsonProperty("ID")
    private String id;

    @JsonProperty("IDPayer")
    private String idPayer;

    @JsonProperty("DenialCode")
    private String denialCode;

    @JsonProperty("Start")
    private String start;

    @JsonProperty("End")
    private String end;

    @JsonProperty("Limit")
    private BigDecimal limit;

    @JsonProperty("Comments")
    private String comments;
}