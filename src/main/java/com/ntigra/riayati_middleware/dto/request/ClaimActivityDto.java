package com.ntigra.riayati_middleware.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ClaimActivityDto {
    private String id;
    private String start;
    private String type;
    private String code;
    private Double quantity;
    private Double net;
    private Double patientShare;
    private String clinician;
    private Integer duration;
    private String priorAuthorizationId;
    private String dispensedActivityId;
    private List<ObservationDto> observations;
}