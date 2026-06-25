package com.ntigra.riayati_middleware.dto.request.dispense.request;

import lombok.Data;

@Data
public class DispenseData {
    private String id;
    private String type;                  // "Dispense"
    private String referenceNumber;
    private String dispenseDate;
    private String priorRequestId;
    private String contactNumber;
    private String email;
    private String fullName;
    private DispenseActivity activity;
}
