package com.ntigra.riayati_middleware.dto.request.erx.response;

import lombok.Data;

import java.util.List;

@Data
public class ErxAuthorization {
    private String result;
    private String id;
    private String idPayer;
    private String denialCode;
    private String start;
    private String end;
    private Double limit;
    private String comments;
    private List<ErxActivityResponse> activity;
}
