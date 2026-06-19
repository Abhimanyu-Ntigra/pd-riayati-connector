package com.ntigra.riayati_middleware.dto.entity.Eligibility;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EligibilityEncounter {
    private String facilityId;
    private Integer type;
}