package com.ntigra.riayati_middleware.mapper.authorization;

import com.ntigra.riayati_middleware.config.RiayatiProperties;
import com.ntigra.riayati_middleware.dto.request.Authorization.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthorizationRequestMapper {

    private final RiayatiProperties properties;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public AuthorizationRequest map(AuthorizationRequestDto dto) {
        RequestHeader header = buildHeader(dto);
        EncounterRequest encounter = buildEncounter(dto);
        Authorization authorization = buildAuthorization(dto, encounter);

        PriorRequest priorRequest = new PriorRequest();
        priorRequest.setHeader(header);
        priorRequest.setAuthorization(authorization);

        AuthorizationRequest request = new AuthorizationRequest();
        request.setPriorRequest(priorRequest);

        return request;
    }

    private RequestHeader buildHeader(AuthorizationRequestDto dto) {
        RequestHeader header = new RequestHeader();
        header.setSenderId(dto.getSenderId());
        header.setReceiverId(dto.getReceiverId());
        header.setTransactionDate(now());
        header.setRecordCount(1);
        header.setDispositionFlag(dto.getDispositionFlag());
        header.setPayerId(dto.getPayerId());
        return header;
    }

    private EncounterRequest buildEncounter(AuthorizationRequestDto dto) {
        EncounterRequest encounter = new EncounterRequest();
        encounter.setFacilityId(dto.getFacilityId());
        encounter.setType(dto.getEncounterType());
        encounter.setPatientId(null);
        encounter.setStart(dto.getEncounterStart());
        encounter.setEnd(dto.getEncounterEnd());
        encounter.setStartType(1);
        encounter.setEndType(1);
        return encounter;
    }

    private Authorization buildAuthorization(AuthorizationRequestDto dto, EncounterRequest encounter) {
        Authorization authorization = new Authorization();
        authorization.setType("Authorization");
        authorization.setId(generateRequestId());
        authorization.setRequestType(dto.getRequestReferenceNumber() != null ? "Prescription" : "New");
        authorization.setRequestReferenceNumber(dto.getRequestReferenceNumber());
        authorization.setMemberId(dto.getMemberId());
        authorization.setEmiratesIdNumber(dto.getEmiratesId());
        authorization.setDateOrdered(now());
        authorization.setWeight(dto.getWeight() != null ? dto.getWeight() : 50.0);
        authorization.setDateOfBirth(dto.getDateOfBirth() != null ?
                dto.getDateOfBirth().format(String.valueOf(DATE_TIME_FORMATTER)) : null);
        authorization.setGender(dto.getGender());
        authorization.setFullName(dto.getFullName());
        authorization.setContactNumber(dto.getContactNumber());
        authorization.setEmail(dto.getEmail());
        authorization.setEncounter(encounter);

        // Map Diagnoses
        if (dto.getDiagnoses() != null && !dto.getDiagnoses().isEmpty()) {
            authorization.setDiagnosis(dto.getDiagnoses().stream()
                    .map(this::mapDiagnosis)
                    .collect(Collectors.toList()));
        } else {
            authorization.setDiagnosis(Collections.emptyList());
        }

        // Map Activities
        if (dto.getActivities() != null && !dto.getActivities().isEmpty()) {
            authorization.setActivity(dto.getActivities().stream()
                    .map(this::mapActivity)
                    .collect(Collectors.toList()));
        } else {
            authorization.setActivity(Collections.emptyList());
        }

        return authorization;
    }

    private DiagnosisRequest mapDiagnosis(DiagnosisDto dto) {
        DiagnosisRequest diagnosis = new DiagnosisRequest();
        diagnosis.setType(dto.getType() != null ? dto.getType() : "Principal");
        diagnosis.setCode(dto.getCode());

        // DxInfo constant
        DxInfoRequest dxInfo = new DxInfoRequest();
        dxInfo.setType("POA");
        dxInfo.setCode("Y");
        diagnosis.setDxInfo(dxInfo);

        return diagnosis;
    }

    private ActivityRequest mapActivity(AuthorizationActivityDto dto) {
        ActivityRequest activity = new ActivityRequest();
        activity.setId(dto.getId());
        activity.setActivityReference(dto.getActivityReference());
        activity.setStart(dto.getStart() != null ? dto.getStart() : now());
        activity.setType(dto.getType() != null ? dto.getType() : "3");
        activity.setLocation(dto.getLocation() != null ? dto.getLocation() : "2");
        activity.setCode(dto.getCode());
        activity.setQuantity(dto.getQuantity());
        activity.setUnit(dto.getUnit());
        activity.setNet(dto.getNet());
        activity.setClinician(dto.getClinician() != null ? dto.getClinician() : "CONSTANT_CLINICIAN");
        activity.setDuration(dto.getDuration() != null ? dto.getDuration() : 0.0);

        if (dto.getObservations() != null && !dto.getObservations().isEmpty()) {
            activity.setObservation(dto.getObservations().stream()
                    .map(this::mapObservation)
                    .collect(Collectors.toList()));
        } else {
            activity.setObservation(Collections.emptyList());
        }

        return activity;
    }

    private ObservationRequest mapObservation(ObservationDto dto) {
        ObservationRequest observation = new ObservationRequest();
        observation.setType(dto.getType());
        observation.setCode(dto.getCode());
        observation.setValue(dto.getValue());
        observation.setValueType(dto.getValueType());
        return observation;
    }

    private String generateRequestId() {
        return "AUTH-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private String now() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
}