package com.ntigra.riayati_middleware.dto.request;

import lombok.Data;

@Data
public class DispenseActivityDto {

    private String id;
    private String type;                     // "5" for Drug
    private String code;                     // Medication code
    private String activityReference;        // Activity ID from ERX
    private Double quantity;
    private Double dispensedQuantity;
    private String location;                 // 1=Inpatient, 2=Outpatient, 3=Emergency, 4=Home, 5=Ambulance
    private String performerName;
    private String authorizationId;
    private String comments;
}
