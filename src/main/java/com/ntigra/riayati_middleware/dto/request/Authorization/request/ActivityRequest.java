package com.ntigra.riayati_middleware.dto.request.Authorization.request;

import lombok.Data;
import java.util.List;

@Data
public class ActivityRequest {
    private String id;
    private String activityReference;
    private String start;
    private String type;
    private String location;
    private String code;
    private Double quantity;
    private String unit;
    private Double net;
    private String clinician;
    private Double duration;
    private List<ObservationRequest> observation;
}
