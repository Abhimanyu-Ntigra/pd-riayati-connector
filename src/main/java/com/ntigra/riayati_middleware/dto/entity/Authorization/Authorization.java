package com.ntigra.riayati_middleware.dto.entity.Authorization;

import com.ntigra.riayati_middleware.dto.entity.Diagnosis;
import com.ntigra.riayati_middleware.dto.entity.Encounter;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class Authorization {
    private String type;              // "Authorization" or "Cancellation"
    private String id;                // Your authorization ID
    private String requestType;       // "New" or "Prescription"
    private String requestReferenceNumber;  // For Prescription type
    private String memberId;
    private String emiratesIdNumber;
    private String dateOrdered;
    private Double weight;
    private String dateOfBirth;
    private String gender;
    private String fullName;
    private String contactNumber;
    private String email;
    private Encounter encounter;
    private List<Diagnosis> diagnosis;
    private List<AuthorizationActivity> activity;
}
