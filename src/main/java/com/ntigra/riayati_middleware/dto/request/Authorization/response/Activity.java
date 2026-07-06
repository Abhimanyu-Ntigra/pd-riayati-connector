package com.ntigra.riayati_middleware.dto.request.Authorization.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Activity {

    private String id;
    private String type;
    private String code;
    private Double quantity;
    private Double net;
    private String clinician;
    private Double patientShare;
    private Double paymentAmount;
    private String denialCode;
}
