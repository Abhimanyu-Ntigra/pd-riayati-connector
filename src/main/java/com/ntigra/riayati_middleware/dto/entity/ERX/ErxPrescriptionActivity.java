package com.ntigra.riayati_middleware.dto.entity.ERX;

import com.ntigra.riayati_middleware.dto.entity.Observation;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ErxPrescriptionActivity {
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
    private Integer unitId;
    private Integer refills;
    private String routeOfAdmin;
    private String instructions;
    private List<Observation> observation;
}
