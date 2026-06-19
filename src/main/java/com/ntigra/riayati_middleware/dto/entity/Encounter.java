package com.ntigra.riayati_middleware.dto.entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@Builder
public class Encounter {
    private String facilityId;
    private Integer type;
    private String patientId;
    private String start;
    private String end;
    private Integer startType;
    private Integer endType;
    private String transferSource;
    private String transferDestination;
}
