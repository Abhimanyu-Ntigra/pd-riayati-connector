package com.ntigra.riayati_middleware.mapper;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.entity.Diagnosis;
import com.ntigra.riayati_middleware.dto.entity.DxInfo;
import com.ntigra.riayati_middleware.dto.entity.Eligibility.*;
import com.ntigra.riayati_middleware.dto.entity.Encounter;
import com.ntigra.riayati_middleware.dto.entity.Header;
import com.ntigra.riayati_middleware.dto.request.EligibilityRequestDto;
import com.ntigra.riayati_middleware.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EligibilityMapper {

    private final RiayatiProperties properties;
    private final DateUtil dateUtil;

    public EligibilitySubmission toEligibilitySubmission(EligibilityRequestDto dto) {
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
        List<Activity> activities = new ArrayList<>();
        if (dto.getActivities() != null) {
            for (var actDto : dto.getActivities()) {
                Activity activity = Activity.builder()
                        .id(actDto.getId())
                        .type(actDto.getType())
                        .code(actDto.getCode())
                        .quantity(actDto.getQuantity())
                        .net(actDto.getNet())
                        .clinician(actDto.getClinician())
                        .build();
                activities.add(activity);
            }
        }

        // ==================== BUILD AUTHORIZATION ====================
        EligibilityAuthorization authorization = EligibilityAuthorization.builder()
                .type("Eligibility")
                .id(dto.getTransactionId())
                .requestType("New")
                .memberId(dto.getMemberId())
                .emiratesIdNumber(dto.getEmiratesId())
                .dateOrdered(dto.getDateOrdered() != null ? dto.getDateOrdered() : currentDate)
                .weight(dto.getWeight() != null ? dto.getWeight() : 50.0)
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .fullName(dto.getFullName())
                .encounter(encounter)
                .diagnosis(diagnoses)
                .activity(activities)
                .build();

        // ==================== BUILD SUBMISSION ====================
        return EligibilitySubmission.builder()
                .header(header)
                .authorization(authorization)
                .build();
    }
}