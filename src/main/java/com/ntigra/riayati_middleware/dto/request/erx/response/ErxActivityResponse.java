package com.ntigra.riayati_middleware.dto.request.erx.response;

import lombok.Data;

@Data
public class ErxActivityResponse {
    private String id;
    private String type;
    private String code;
    private Double quantity;
    private Double net;
    private Double patientShare;
    private Double paymentAmount;
    private String denialCode;
}
