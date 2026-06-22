package com.ntigra.riayati_middleware.dto.entity.ERX;

import com.ntigra.riayati_middleware.dto.entity.Header;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErxSubmission {

    private Header header;
    private ErxPrescription prescription;

}
