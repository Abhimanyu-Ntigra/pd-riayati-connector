package com.ntigra.riayati_middleware.dto.entity.Claim;


import com.ntigra.riayati_middleware.dto.entity.Observation;
import lombok.Builder;
import lombok.Data;

import java.util.*;
@Data
@Builder
public  class Activity {
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
    private List<Observation> observation;
}
