package com.ntigra.riayati_middleware.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class AuthorizationActivityDto {
    private String id;
    private String activityReference;  // Reference to previous activity
    private String start;
    private String type;               // 3=Service, 5=Drug, etc.
    private String location;           // 1=Inpatient, 2=Outpatient, 3=Emergency
    private String code;               // CPT/HCPCS code
    private Double quantity;
    private String unit;
    private Double net;
    private String clinician;
    private Double duration;           // For inpatient stays
    private List<ObservationDto> observations;
}
