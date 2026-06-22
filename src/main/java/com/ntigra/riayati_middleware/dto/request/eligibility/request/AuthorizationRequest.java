package com.ntigra.riayati_middleware.dto.request.eligibility.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AuthorizationRequest {

    @JsonProperty("Type")
    private String type;

    @JsonProperty("ID")
    private String id;

    @JsonProperty("RequestType")
    private String requestType;

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("MemberID")
    private String memberId;

    @JsonProperty("EmiratesIDNumber")
    private String emiratesIdNumber;

    @JsonProperty("DateOrdered")
    private String dateOrdered;

    @JsonProperty("DateOfBirth")
    private String dateOfBirth;

    @JsonProperty("Encounter")
    private EncounterRequest encounter;

    @JsonProperty("Diagnosis")
    private List<Object> diagnosis;

    @JsonProperty("Activity")
    private List<Object> activity;
}