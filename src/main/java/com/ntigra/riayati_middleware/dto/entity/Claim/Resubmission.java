package com.ntigra.riayati_middleware.dto.entity.Claim;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Resubmission {
    private String attachment;
    private String comment;
    private String type;
}
