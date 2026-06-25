package com.ntigra.riayati_middleware.dto.request.erx.request;

import lombok.Data;
import java.util.List;

@Data
public class ErxActivity {
    private String id;
    private String type;
    private String code;
    private Double quantity;
    private Double duration;
    private Integer unitId;
    private Integer refills;
    private String routeOfAdmin;
    private String instructions;
    private String start;
    private ErxFrequency frequency;
    private List<Observation> observation;
}
