package com.ntigra.riayati_middleware.dto.polling;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetNewResponse {

    @JsonProperty("Entities")
    private List<GetNewEntity> entities;

    @JsonProperty("StatusCode")
    private Integer statusCode;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Success")
    private Boolean success;
}