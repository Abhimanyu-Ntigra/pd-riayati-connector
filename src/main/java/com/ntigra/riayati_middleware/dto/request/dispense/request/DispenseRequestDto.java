package com.ntigra.riayati_middleware.dto.request.dispense.request;

import lombok.Data;

@Data
public class DispenseRequestDto {
    // Header
    private String senderId;
    private String receiverId;
    private String payerId;
    private String dispositionFlag;
    private Integer recordCount;

    // Dispense
    private String dispenseId;
    private String referenceNumber;          // ERX Reference Number
    private String priorRequestId;
    private String dispenseDate;

    // Patient
    private String memberId;
    private String emiratesId;
    private String fullName;
    private String gender;
    private String dateOfBirth;
    private String contactNumber;
    private String email;

    // Encounter
    private String facilityId;
    private Integer encounterType;

    // Activity
    private DispenseActivityDto activity;
}
