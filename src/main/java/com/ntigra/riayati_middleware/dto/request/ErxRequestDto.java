package com.ntigra.riayati_middleware.dto.request;

import com.ntigra.riayati_middleware.dto.request.Authorization.request.DiagnosisDto;
import lombok.Data;
import java.util.List;

@Data
public class ErxRequestDto {
    private Long id;
    private String prescriptionId;      // PreAuthRef from PreAuthHead
    private String referenceNumber;
    private Integer retryCount;

    // Header
    private String senderId;
    private String receiverId;
    private String payerId;
    private String facilityId;
    private String dispositionFlag;
    private Integer recordCount;

    // Prescription
    private String clinician;
    private String transactionType;     // eRxRequest or eRxCancellation

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
    private Integer encounterType;
    private String encounterStart;
    private String encounterEnd;

    // Attachment
    private String fileName;
    private byte[] fileContent;

    // Activities
    private List<ErxActivityDto> activities;
    private List<DiagnosisDto> diagnoses;
}