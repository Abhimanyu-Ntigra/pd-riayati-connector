package com.ntigra.riayati_middleware.dto.request.erx.request;

import com.ntigra.riayati_middleware.dto.request.Authorization.request.DiagnosisDto;
import lombok.Data;
import java.util.List;

@Data
public class ErxRequestDto {
    // Header
    private String senderId;
    private String receiverId;
    private String payerId;
    private String dispositionFlag;
    private Integer recordCount;

    // Prescription
    private String prescriptionId;
    private String transactionType;      // eRxRequest or eRxCancellation
    private String clinician;

    // Patient
    private String memberId;
    private String emiratesId;
    private String fullName;
    private String gender;
    private String dateOfBirth;
    private String contactNumber;
    private String email;
    private Double weight;

    // Encounter
    private String facilityId;
    private Integer encounterType;
    private String encounterStart;
    private String encounterEnd;

    // Clinical
    private List<ErxActivityDto> activities;
    private List<DiagnosisDto> diagnoses;

    // Attachment
    private String fileName;
    private byte[] fileContent;
}
