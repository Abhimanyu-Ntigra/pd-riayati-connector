package com.ntigra.riayati_middleware.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class EligibilityRequestDto {
    // Database tracking
    private Long id;                    // DB primary key
    private String transactionId;       // Your internal eligibility ID

    // Header Information
    private String senderId;            // Your facility license
    private String receiverId;          // Payer/TPA license
    private String payerId;             // Payer license
    private String dispositionFlag;     // "PRODUCTION" or "TEST"
    private Integer recordCount;        // Always 1

    // Patient Information
    private String memberId;            // Insurance member ID
    private String emiratesId;
    private String fullName;            // Patient full name
    private String gender;              // Male/Female
    private String dateOfBirth;         // DD/MM/YYYY
    private Double weight;
    private String contactNumber;
    private String email;

    // Encounter Information
    private String facilityId;          // Facility ID
    private Integer encounterType;      // 1=Outpatient, 2=Emergency, 3=Inpatient, etc.
    private String dateOrdered;

    // Clinical Information
    private List<EligibilityActivityDto> activities;
    private List<DiagnosisDto> diagnoses;

    // Status Tracking
    private Integer retryCount;
    private String status;
}
