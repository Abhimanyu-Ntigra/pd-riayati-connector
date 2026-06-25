package com.ntigra.riayati_middleware.dto.request.dispense.request;

import lombok.Data;

@Data
public class DispenseActivity {
    private String id;
    private String type;
    private String code;
    private String activityReference;
    private Double quantity;
    private Double dispensedQuantity;
    private String location;
    private String performerName;
    private String authorizationId;
    private String comments;
}