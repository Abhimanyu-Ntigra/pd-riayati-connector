package com.ntigra.riayati_middleware.mapper;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.entity.Authorization.Authorization;
import com.ntigra.riayati_middleware.dto.entity.Authorization.AuthorizationActivity;
import com.ntigra.riayati_middleware.dto.entity.Authorization.AuthorizationSubmission;
import com.ntigra.riayati_middleware.dto.entity.Diagnosis;
import com.ntigra.riayati_middleware.dto.entity.Encounter;
import com.ntigra.riayati_middleware.dto.entity.Header;
import com.ntigra.riayati_middleware.dto.entity.Observation;
import com.ntigra.riayati_middleware.dto.request.AuthorizationRequestDto;
import com.ntigra.riayati_middleware.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthorizationRequestMapper {

    private final RiayatiProperties properties;
    private final DateUtil dateUtil;

    public AuthorizationSubmission toAuthorizationSubmission(AuthorizationRequestDto dto) {
        String currentDate = dateUtil.getCurrentDateTime();

        // Build Header (Reuse from Claim module)
        Header header = Header.builder()
                .senderId(dto.getSenderId() != null ? dto.getSenderId() : properties.getSenderId())
                .receiverId(dto.getReceiverId() != null ? dto.getReceiverId() : properties.getReceiverId())
                .transactionDate(currentDate)
                .recordCount(1)
                .dispositionFlag(dto.getDispositionFlag() != null ? dto.getDispositionFlag() : "PRODUCTION")
                .payerId(dto.getPayerId() != null ? dto.getPayerId() : properties.getPayerId())
                .build();

        // Build Encounter (Reuse from Claim module)
        Encounter encounter = Encounter.builder()
                .facilityId(dto.getFacilityId())
                .type(dto.getEncounterType() != null ? dto.getEncounterType() : 1)
                .start(dto.getEncounterStart())
                .end(dto.getEncounterEnd())
                .build();

        // Build Diagnosis (Reuse from Claim module)
        List<Diagnosis> diagnoses = null;
        if (dto.getDiagnoses() != null && !dto.getDiagnoses().isEmpty()) {
            diagnoses = dto.getDiagnoses().stream()
                    .map(diag -> Diagnosis.builder()
                            .type(diag.getType() != null ? diag.getType() : "Principal")
                            .code(diag.getCode())
                            .build())
                    .collect(Collectors.toList());
        }

        // Build Activities
        List<AuthorizationActivity> activities = new ArrayList<>();
        if (dto.getActivities() != null) {
            for (var actDto : dto.getActivities()) {
                List<Observation> observations = null;
                if (actDto.getObservations() != null) {
                    observations = actDto.getObservations().stream()
                            .map(obs -> Observation.builder()
                                    .type(obs.getType())
                                    .code(obs.getCode())
                                    .value(obs.getValue())
                                    .valueType(obs.getValueType())
                                    .build())
                            .collect(Collectors.toList());
                }

                AuthorizationActivity activity = AuthorizationActivity.builder()
                        .id(actDto.getId())
                        .activityReference(actDto.getActivityReference())
                        .start(actDto.getStart() != null ? actDto.getStart() : currentDate)
                        .type(actDto.getType() != null ? actDto.getType() : "3")
                        .location(actDto.getLocation())
                        .code(actDto.getCode())
                        .quantity(actDto.getQuantity())
                        .unit(actDto.getUnit())
                        .net(actDto.getNet())
                        .clinician(actDto.getClinician())
                        .duration(actDto.getDuration())
                        .observation(observations)
                        .build();
                activities.add(activity);
            }
        }

        // Build Authorization
        Authorization authorization = Authorization.builder()
                .type("Authorization")
                .id(dto.getTransactionId())
                .requestType(dto.getRequestReferenceNumber() != null ? "Prescription" : "New")
                .requestReferenceNumber(dto.getRequestReferenceNumber())
                .memberId(dto.getMemberId())
                .emiratesIdNumber(dto.getEmiratesId())
                .dateOrdered(dto.getDateOrdered() != null ? dto.getDateOrdered() : currentDate)
                .weight(dto.getWeight())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .fullName(dto.getFullName())
                .contactNumber(dto.getContactNumber())
                .email(dto.getEmail())
                .encounter(encounter)
                .diagnosis(diagnoses)
                .activity(activities)
                .build();

        return AuthorizationSubmission.builder()
                .header(header)
                .authorization(authorization)
                .build();
    }
}