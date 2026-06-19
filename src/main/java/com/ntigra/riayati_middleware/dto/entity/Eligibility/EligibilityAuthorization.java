package com.ntigra.riayati_middleware.dto.entity.Eligibility;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class EligibilityAuthorization {

    private String type;              // "Eligibility" or "Cancellation"
    private String id;                // Your eligibility request ID
    private String requestType;       // "New"
    private String memberId;
    private String emiratesIdNumber;
    private String dateOrdered;
    private Double weight;
    private String dateOfBirth;
    private String gender;
    private String fullName;
    private String contactNumber;
    private String email;
    private EligibilityEncounter encounter;
    private List<EligibilityDiagnosis> diagnosis;
    private List<EligibilityActivity> activity;

}