package com.ntigra.riayati_middleware.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class DispenseRequestDto {

    // ==================== DATABASE TRACKING ====================
    private Long id;
    private String dispenseId;
    private String referenceNumber;          // ERX Reference Number
    private String authorizationId;
    private String priorRequestId;           // Prior Request ID
    private Integer retryCount;

    // ==================== HEADER INFORMATION ====================
    private String senderId;
    private String receiverId;
    private String payerId;
    private String facilityId;
    private String dispositionFlag;
    private Integer recordCount;

    // ==================== PATIENT INFORMATION ====================
    private String memberId;
    private String emiratesId;
    private String fullName;
    private String gender;
    private String dateOfBirth;
    private String contactNumber;
    private String email;

    // ==================== DISPENSE INFORMATION ====================
    private String dispenseDate;
    private String location;                 // 1=Inpatient, 2=Outpatient, 3=Emergency, 4=Home, 5=Ambulance

    // ==================== ACTIVITY ====================
    private DispenseActivityDto activity;
    private Integer encounterType;

    // ==================== ATTACHMENT ====================
    private String fileName;
    private byte[] fileContent;
}
