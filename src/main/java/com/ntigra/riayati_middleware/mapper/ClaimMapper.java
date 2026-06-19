package com.ntigra.riayati_middleware.mapper;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.entity.*;
import com.ntigra.riayati_middleware.dto.entity.Claim.Activity;
import com.ntigra.riayati_middleware.dto.entity.Claim.Claim;
import com.ntigra.riayati_middleware.dto.entity.Claim.ClaimSubmission;
import com.ntigra.riayati_middleware.dto.request.ClaimRequestDto;
import com.ntigra.riayati_middleware.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClaimMapper {

    private final RiayatiProperties properties;
    private final DateUtil dateUtil;

    /**
     * Convert ClaimRequestDto to Riayati API JSON format
     */
    public ClaimSubmission toClaimSubmission(ClaimRequestDto dto, String attachmentId) {
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
                        // Build DxInfo separately
                        DxInfo dxInfo = DxInfo.builder()
                                .type("POA")    // Constant
                                .code("Y")      // Constant: Yes
                                .build();

                        // Build Diagnosis with DxInfo
                        return Diagnosis.builder()
                                .type(diag.getType() != null ? diag.getType() : "Principal")
                                .code(diag.getCode())
                                .dxInfo(dxInfo)
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        // ==================== BUILD ACTIVITIES ====================
        List<Activity> activities = new ArrayList<>();
        boolean isFirstActivity = true;

        if (dto.getActivities() != null) {
            for (var actDto : dto.getActivities()) {
                List<Observation> observations = null;

                // Add attachment to first activity only
                if (isFirstActivity && attachmentId != null) {
                    observations = new ArrayList<>();
                    observations.add(Observation.builder()
                            .type("File")
                            .code("File")
                            .value(attachmentId)
                            .valueType("File")
                            .build());
                    isFirstActivity = false;
                }

                // Add activity observations from DB
                if (actDto.getObservations() != null && !actDto.getObservations().isEmpty()) {
                    if (observations == null) {
                        observations = new ArrayList<>();
                    }
                    for (var obsDto : actDto.getObservations()) {
                        observations.add(Observation.builder()
                                .type(obsDto.getType())
                                .code(obsDto.getCode())
                                .value(obsDto.getValue())
                                .valueType(obsDto.getValueType())
                                .build());
                    }
                }

                Activity activity = Activity.builder()
                        .id(actDto.getId())
                        .start(actDto.getStart() != null ? actDto.getStart() : currentDate)
                        .type(actDto.getType() != null ? actDto.getType() : "3")
                        .code(actDto.getCode())
                        .quantity(actDto.getQuantity())
                        .net(actDto.getNet())
                        .patientShare(actDto.getPatientShare() != null ? actDto.getPatientShare() : 0.0)
                        .clinician(actDto.getClinician())
                        .duration(0)  // Constant
                        .priorAuthorizationId(dto.getPayerAuthRef())
                        .observation(observations)
                        .build();
                activities.add(activity);
            }
        }

        // ==================== BUILD CLAIM ====================
        Claim claim = Claim.builder()
                .id(dto.getClaimId())
                .type("Submission")     // Constant
                .memberId(dto.getMemberId())
                .providerId(dto.getProviderId())
                .nationalIdNumber(dto.getEmiratesId())
                .dateOfBirth(dto.getPatientDateOfBirth())
                .gender(dto.getPatientGender() != null ? dto.getPatientGender() : "Male")
                .weight(50.0)           // Constant
                .gross(dto.getGross())
                .net(dto.getNet())
                .patientShare(dto.getPatientShare() != null ? dto.getPatientShare() : 0.0)
                .encounter(encounter)
                .diagnosis(diagnoses)
                .activity(activities)
                .build();

        // ==================== BUILD SUBMISSION ====================
        return ClaimSubmission.builder()
                .header(header)
                .claim(claim)
                .build();
    }
}