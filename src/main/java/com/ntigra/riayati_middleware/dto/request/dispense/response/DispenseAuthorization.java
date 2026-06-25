package com.ntigra.riayati_middleware.dto.request.dispense.response;

import lombok.Data;

@Data
public class DispenseAuthorization {
    private String result;
    private String id;
    private String idPayer;
    private String denialCode;
    private String start;
    private String end;
    private Double limit;
    private String comments;
}
