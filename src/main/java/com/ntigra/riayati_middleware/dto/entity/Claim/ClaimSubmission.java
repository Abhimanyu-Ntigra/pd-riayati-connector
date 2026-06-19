package com.ntigra.riayati_middleware.dto.entity.Claim;

import com.ntigra.riayati_middleware.dto.entity.Claim.Claim;
import com.ntigra.riayati_middleware.dto.entity.Header;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ClaimSubmission {

    private Header header;
    private Claim claim;

}
