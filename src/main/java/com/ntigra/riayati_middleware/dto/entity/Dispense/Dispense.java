package com.ntigra.riayati_middleware.dto.entity.Dispense;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Dispense {
    private String id;
    private String type;                    // "Dispense"
    private String referenceNumber;         // ERX Reference Number
    private String dispenseDate;
    private String priorRequestId;
    private String contactNumber;
    private String email;
    private String fullName;
    private DispenseActivity activity;
}
