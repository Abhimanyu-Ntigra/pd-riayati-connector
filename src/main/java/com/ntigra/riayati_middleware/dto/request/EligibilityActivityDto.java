package com.ntigra.riayati_middleware.dto.request;

import com.ntigra.riayati_middleware.dto.request.Authorization.request.ObservationDto;
import lombok.Data;
import java.util.List;

@Data
public class EligibilityActivityDto {
    private String id;
    private String code;
    private String type;           // Activity type
    private String location;       // Service location
    private Double quantity;
    private Double net;
    private String clinician;
    private Double duration;
    private List<ObservationDto> observations;
}