package com.ntigra.riayati_middleware.dto.entity.Eligibility;

import com.ntigra.riayati_middleware.dto.entity.Header;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EligibilitySubmission {
    private Header header;
    private EligibilityAuthorization authorization;
}