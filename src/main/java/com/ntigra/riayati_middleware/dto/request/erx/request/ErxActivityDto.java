package com.ntigra.riayati_middleware.dto.request.erx.request;

import com.ntigra.riayati_middleware.dto.request.Authorization.request.ObservationDto;
import lombok.Data;
import java.util.List;

@Data
public class ErxActivityDto {
    private String id;
    private String type;                  // "5" for Drug
    private String code;                  // Medication code
    private Double quantity;
    private Double duration;              // Duration in days
    private Integer unitId;
    private Integer refills;
    private String routeOfAdmin;
    private String instructions;
    private String start;
    private ErxFrequencyDto frequency;
    private List<ObservationDto> observations;
}
