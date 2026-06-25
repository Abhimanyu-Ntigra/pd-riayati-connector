package com.ntigra.riayati_middleware.dto.request.Authorization.response;

import lombok.Data;

import java.util.List;

@Data
public class Authorization {

    private String result;
    private String id;
    private String idPayer;
    private String denialCode;
    private String start;
    private String end;
    private Double limit;
    private String comments;
    private List<Activity> activity;
}
