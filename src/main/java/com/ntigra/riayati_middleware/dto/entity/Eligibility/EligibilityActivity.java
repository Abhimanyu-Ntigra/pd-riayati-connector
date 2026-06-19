package com.ntigra.riayati_middleware.dto.entity.Eligibility;

import com.ntigra.riayati_middleware.dto.entity.Observation;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class EligibilityActivity {
    private String id;
    private String start;
    private String type;
    private String location;
    private String code;
    private Double quantity;
    private String unit;
    private Double net;
    private String clinician;
    private Double duration;
    private List<Observation> observation;
}
