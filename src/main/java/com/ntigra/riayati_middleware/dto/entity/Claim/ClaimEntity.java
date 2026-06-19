package com.ntigra.riayati_middleware.dto.entity.Claim;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClaimEntity {
    private Long id;
    private String claimId;
    private String entityId;
    private String memberId;
    private String emiratesId;
    private String patientName;
    private String patientGender;
    private String patientDob;
    private String senderId;
    private String receiverId;
    private String payerId;
    private String providerId;
    private String facilityId;
    private Integer encounterType;
    private String activitiesJson;
    private String diagnosesJson;
    private Double gross;
    private Double net;
    private Double patientShare;
    private String fileName;
    private byte[] fileContent;
    private String attachmentId;
    private String status;
    private Integer retryCount;
    private String errorMessage;
    private Double paymentAmount;
    private String paymentReference;
    private String remittanceData;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime processedAt;
}