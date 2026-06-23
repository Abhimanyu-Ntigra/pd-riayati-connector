package com.ntigra.riayati_middleware.mapper;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.entity.Diagnosis;
import com.ntigra.riayati_middleware.dto.entity.ERX.ErxPatient;
import com.ntigra.riayati_middleware.dto.entity.ERX.ErxPrescription;
import com.ntigra.riayati_middleware.dto.entity.ERX.ErxPrescriptionActivity;
import com.ntigra.riayati_middleware.dto.entity.ERX.ErxSubmission;
import com.ntigra.riayati_middleware.dto.entity.Encounter;
import com.ntigra.riayati_middleware.dto.entity.Header;
import com.ntigra.riayati_middleware.dto.entity.Observation;
import com.ntigra.riayati_middleware.dto.request.ErxRequestDto;
import com.ntigra.riayati_middleware.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ErxMapper {

    private final RiayatiProperties properties;
    private final DateUtil dateUtil;

    public ErxSubmission toErxSubmission(ErxRequestDto dto, String attachmentId) {
        String currentDate = dateUtil.getCurrentDateTime();

        // Header (REUSE)
        Header header = Header.builder()
                .senderId(dto.getSenderId() != null ? dto.getSenderId() : properties.getSenderId())
                .receiverId(dto.getReceiverId() != null ? dto.getReceiverId() : properties.getReceiverId())
                .transactionDate(currentDate)
                .recordCount(1)
                .dispositionFlag("PRODUCTION")
                .payerId(dto.getPayerId() != null ? dto.getPayerId() : properties.getPayerId())
                .build();

        // Encounter (REUSE)
        Encounter encounter = Encounter.builder()
                .facilityId(dto.getFacilityId())
                .type(dto.getEncounterType() != null ? dto.getEncounterType() : 1)
                .start(dto.getEncounterStart())
                .end(dto.getEncounterEnd())
                .build();

        // Patient (NEW for ERX)
        ErxPatient patient = ErxPatient.builder()
                .memberId(dto.getMemberId())
                .nationalIdNumber(dto.getEmiratesId())
                .dateOfBirth(dto.getDateOfBirth())
                .fullName(dto.getFullName())
                .gender(dto.getGender())
                .contactNumber(dto.getContactNumber())
                .weight(50.0)
                .email(dto.getEmail())
                .build();

        // Diagnosis (REUSE)
        List<Diagnosis> diagnoses = null;
        if (dto.getDiagnoses() != null && !dto.getDiagnoses().isEmpty()) {
            diagnoses = dto.getDiagnoses().stream()
                    .map(diag -> Diagnosis.builder()
                            .type(diag.getType() != null ? diag.getType() : "Principal")
                            .code(diag.getCode())
                            .build())
                    .collect(Collectors.toList());
        }

        // Activities (NEW for ERX)
        List<ErxPrescriptionActivity> activities = new ArrayList<>();
        boolean isFirst = true;

        for (var actDto : dto.getActivities()) {
            List<Observation> observations = null;

            if (isFirst && attachmentId != null) {
                observations = new ArrayList<>();
                observations.add(Observation.builder()
                        .type("File")
                        .code("Prescription")
                        .value(attachmentId)
                        .valueType("File")
                        .build());
                isFirst = false;
            }

            ErxPrescriptionActivity activity = ErxPrescriptionActivity.builder()
                    .id(actDto.getId())
                    .type(actDto.getType() != null ? actDto.getType() : "5")
                    .code(actDto.getCode())
                    .quantity(actDto.getQuantity())
                    .duration(0.0)
                    .unitId(1)
                    .refills(0)
                    .routeOfAdmin("001")
                    .instructions("Take as prescribed")
                    .start(actDto.getStart() != null ? actDto.getStart() : currentDate)
                    .observation(observations)
                    .build();
            activities.add(activity);
        }

        // Prescription (NEW for ERX)
        ErxPrescription prescription = ErxPrescription.builder()
                .id(dto.getPrescriptionId())
                .type(dto.getTransactionType() != null ? dto.getTransactionType() : "eRxRequest")
                .clinician(dto.getClinician() != null ? dto.getClinician() : "CONSTANT_CLINICIAN")
                .patient(patient)
                .encounter(encounter)
                .diagnosis(diagnoses)
                .activity(activities)
                .build();

        return ErxSubmission.builder()
                .header(header)
                .prescription(prescription)
                .build();
    }
}