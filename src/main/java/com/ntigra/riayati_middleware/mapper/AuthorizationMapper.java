package com.ntigra.riayati_middleware.mapper;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.entity.*;
import com.ntigra.riayati_middleware.dto.entity.Authorization.Authorization;
import com.ntigra.riayati_middleware.dto.entity.Authorization.AuthorizationActivity;
import com.ntigra.riayati_middleware.dto.entity.Authorization.AuthorizationSubmission;
import com.ntigra.riayati_middleware.dto.entity.Claim.Activity;
import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.dto.response.AuthorizationResponseDto;
import com.ntigra.riayati_middleware.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthorizationMapper {

    private final RiayatiProperties properties;
    private final DateUtil dateUtil;

    public AuthorizationSubmission toAuthorizationSubmission(AuthorizationRequestDto dto) {
        String currentDate = dateUtil.getCurrentDateTime();

        // ==================== BUILD HEADER ====================
        Header header = Header.builder()
                .senderId(dto.getSenderId() != null ? dto.getSenderId() : properties.getSenderId())
                .receiverId(dto.getReceiverId() != null ? dto.getReceiverId() : properties.getReceiverId())
                .transactionDate(currentDate)
                .recordCount(1)
                .dispositionFlag(dto.getDispositionFlag() != null ? dto.getDispositionFlag() : "PRODUCTION")
                .payerId(dto.getPayerId() != null ? dto.getPayerId() : properties.getPayerId())
                .build();

        // ==================== BUILD ENCOUNTER ====================
        Encounter encounter = Encounter.builder()
                .facilityId(dto.getFacilityId())
                .type(dto.getEncounterType() != null ? dto.getEncounterType() : 1)
                .patientId(dto.getMrn())
                .start(dto.getEncounterStart())
                .end(dto.getEncounterEnd())
                .startType(1)   // Constant: Elective
                .endType(1)     // Constant: Discharged with approval
                .build();

        // ==================== BUILD DIAGNOSIS ====================
        List<Diagnosis> diagnoses = null;
        if (dto.getDiagnoses() != null && !dto.getDiagnoses().isEmpty()) {
            diagnoses = dto.getDiagnoses().stream()
                    .map(diag -> {
                        DxInfo dxInfo = DxInfo.builder()
                                .type("POA")
                                .code("Y")
                                .build();

                        return Diagnosis.builder()
                                .type(diag.getType() != null ? diag.getType() : "Principal")
                                .code(diag.getCode())
                                .dxInfo(dxInfo)
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        // ==================== BUILD ACTIVITIES ====================
        List<AuthorizationActivity> activities = new ArrayList<>();
        if (dto.getActivities() != null) {
            for (var actDto : dto.getActivities()) {
                List<Observation> observations = null;

                // Add observations if present
                if (actDto.getObservations() != null && !actDto.getObservations().isEmpty()) {
                    observations = new ArrayList<>();
                    for (var obsDto : actDto.getObservations()) {
                        observations.add(Observation.builder()
                                .type(obsDto.getType())
                                .code(obsDto.getCode())
                                .value(obsDto.getValue())
                                .valueType(obsDto.getValueType())
                                .build());
                    }
                }

                AuthorizationActivity activity = AuthorizationActivity.builder()
                        .id(actDto.getId())
                        .activityReference(actDto.getActivityReference())
                        .start(actDto.getStart() != null ? actDto.getStart() : currentDate)
                        .type(actDto.getType() != null ? actDto.getType() : "3")
                        .location(actDto.getLocation() != null ? actDto.getLocation() : "2") // Default Outpatient
                        .code(actDto.getCode())
                        .quantity(actDto.getQuantity())
                        .unit(actDto.getUnit())
                        .net(actDto.getNet())
                        .clinician(actDto.getClinician() != null ? actDto.getClinician() : "CONSTANT_CLINICIAN")
                        .duration(actDto.getDuration() != null ? actDto.getDuration() : 0.0)
                        .observation(observations)
                        .build();
                activities.add(activity);
            }
        }

        // ==================== BUILD AUTHORIZATION ====================
        String requestType = (dto.getRequestReferenceNumber() != null && !dto.getRequestReferenceNumber().isEmpty())
                ? "Prescription"
                : "New";

        Authorization authorization = Authorization.builder()
                .type("Authorization")
                .id(dto.getTransactionId())
                .requestType(requestType)
                .requestReferenceNumber(dto.getRequestReferenceNumber())
                .memberId(dto.getMemberId())
                .emiratesIdNumber(dto.getEmiratesId())
                .dateOrdered(dto.getDateOrdered() != null ? dto.getDateOrdered() : currentDate)
                .weight(dto.getWeight() != null ? dto.getWeight() : 50.0)
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .fullName(dto.getFullName())
                .contactNumber(dto.getContactNumber())
                .email(dto.getEmail())
                .encounter(encounter)
                .diagnosis(diagnoses)
                .activity(activities)
                .build();

        // ==================== BUILD SUBMISSION ====================
        return AuthorizationSubmission.builder()
                .header(header)
                .authorization(authorization)
                .build();
    }
}