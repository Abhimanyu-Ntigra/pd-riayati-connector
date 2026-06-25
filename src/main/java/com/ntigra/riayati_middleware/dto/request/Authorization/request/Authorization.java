package com.ntigra.riayati_middleware.dto.request.Authorization.request;


import lombok.Data;
import java.util.List;

@Data
public class Authorization {

    private String type;
    private String id;
    private String requestType;
    private String requestReferenceNumber;
    private String memberId;
    private String emiratesIdNumber;
    private String dateOrdered;
    private Double weight;
    private String dateOfBirth;
    private String gender;
    private String fullName;
    private String contactNumber;
    private String email;
    private EncounterRequest encounter;
    private List<DiagnosisRequest> diagnosis;
    private List<ActivityRequest> activity;
}
