package com.ntigra.riayati_middleware.dto.request.Authorization.request;

import lombok.Data;
import java.util.List;

@Data
public class AuthorizationRequestDto {
    // Database tracking
    private Long id;
    private String transactionId;
    private String requestReferenceNumber;  // For Prescription type

    // Header Information
    private String senderId;
    private String receiverId;
    private String payerId;
    private String dispositionFlag;
    private Integer recordCount;

    // Patient Information
    private String memberId;
    private String emiratesId;
    private String fullName;
    private String gender;
    private String dateOfBirth;
    private Double weight;
    private String contactNumber;
    private String email;
    private String dateOrdered;
    private String mrn;

    // Encounter Information
    private String facilityId;
    private Integer encounterType;
    private String encounterStart;
    private String encounterEnd;

    // Clinical Information
    private List<AuthorizationActivityDto> activities;
    private List<DiagnosisDto> diagnoses;

    // Status Tracking
    private Integer retryCount;
    private String status;

    private Double coverageLimit;
}
