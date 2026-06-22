package com.ntigra.riayati_middleware.dto.entity.Dispense;

import com.ntigra.riayati_middleware.dto.entity.Header;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DispenseSubmission {
    private Header header;
    private Dispense dispense;
}