package com.ntigra.riayati_middleware.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ClaimRequestDto {
    //private String transactionType = "Claim";
    private Long id;
    private String claimId;
    private String memberId;
    private String emiratesId;
    private String patientName;
    private String patientGender;
    private String patientDateOfBirth;
    private Double weight;
    private String encounterStart;
    private String encounterEnd;
    private Integer startType;
    private Integer endType;
    private String referenceNumber;
    private String senderId;
    private String receiverId;
    private String payerId;
    private String providerId;
    private String facilityId;
    private Integer encounterType;
    private String mrn;
    private String payerAuthRef;
    private Double gross;
    private Double net;
    private Double patientShare;
    private String dispositionFlag = "PRODUCTION";
    private Integer recordCount = 1;
    private Integer retryCount;
    private String fileName;
    private byte[] fileContent;
    private List<ClaimActivityDto> activities;
    private List<DiagnosisDto> diagnoses;
}
