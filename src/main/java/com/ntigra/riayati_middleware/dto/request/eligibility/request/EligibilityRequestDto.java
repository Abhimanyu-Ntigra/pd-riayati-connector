package com.ntigra.riayati_middleware.dto.request.eligibility.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EligibilityRequestDto {

    private String senderId;

    private String receiverId;

    private String payerId;

    private String dispositionFlag;

    private String memberId;

    private String emiratesId;

    private String gender;

    private LocalDateTime dateOfBirth;

    private Integer encounterType;

}